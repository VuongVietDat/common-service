package vn.com.atomi.loyalty.base.graceful;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;

/**
 * @author haidv
 * @version 1.0
 */
@Configuration
@EnableConfigurationProperties(GracefulProperties.class)
public class GracefulShutdownConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(GracefulShutdownConfig.class);

  private static Object LOCK;

  private static boolean IS_PREVENT_SHUTDOWN;

  static {
    GracefulShutdownConfig.LOCK = new Object();
    GracefulShutdownConfig.IS_PREVENT_SHUTDOWN = false;
  }

  @Bean
  public GracefulShutdown gracefulShutdown(
      GracefulProperties gracefulProperties, ConfigurableApplicationContext applicationContext) {
    return new GracefulShutdown(gracefulProperties, applicationContext);
  }

  //  @PreDestroy
  //  public void shutdown() {
  //    LOGGER.info("@PreDestroy shutdown");
  //    Schedulers.shutdown();
  //  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public static class GracefulShutdown
      implements TomcatConnectorCustomizer, ApplicationListener<ContextClosedEvent> {

    private final GracefulProperties gracefulProperties;

    private final ConfigurableApplicationContext applicationContext;

    private Connector connector;

    public GracefulShutdown(
        GracefulProperties gracefulProperties, ConfigurableApplicationContext applicationContext) {
      this.gracefulProperties = gracefulProperties;
      this.applicationContext = applicationContext;
    }

    public void onApplicationEvent(final ContextClosedEvent event) {
      try {
        boolean needWaitDNS = false;
        synchronized (GracefulShutdownConfig.LOCK) {
          if (!GracefulShutdownConfig.IS_PREVENT_SHUTDOWN) {
            needWaitDNS = true;
            GracefulShutdownConfig.IS_PREVENT_SHUTDOWN = true;
          }
        }
        if (needWaitDNS) {
          setReadiesToFalse();
          delayShutdownSpringContext();
        }
        this.connector.pause();
        final Executor executor = this.connector.getProtocolHandler().getExecutor();
        if (executor instanceof ThreadPoolExecutor) {
          delayShutdownSpringContext(executor);
        }
      } catch (Exception ex) {
        LOGGER.error("Error while gracefulShutdown", ex);
        Thread.currentThread().interrupt();
      }
    }

    /**
     * Customize the connector.
     *
     * @param connector the connector to customize
     */
    public void customize(final Connector connector) {
      this.connector = connector;
    }

    private void delayShutdownSpringContext() {
      try {
        long shutdownWaitMillis = this.gracefulProperties.getTimeout().toMillis();
        LOGGER.info(
            "Gonna wait for {} milliseconds before shutdown SpringContext!", shutdownWaitMillis);
        Thread.sleep(shutdownWaitMillis);
      } catch (InterruptedException ex) {
        LOGGER.error("Error while gracefulShutdown Thread.sleep", ex);
        Thread.currentThread().interrupt();
      }
    }

    private void delayShutdownSpringContext(Executor executor) {
      try {
        final ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
        threadPoolExecutor.shutdown();
        long shutdownWaitSeconds = this.gracefulProperties.getWait().getSeconds();
        if (!threadPoolExecutor.awaitTermination(shutdownWaitSeconds, TimeUnit.SECONDS)) {
          LOGGER.error(
              "Tomcat thread pool did not shut down gracefully within {} seconds. Proceeding with forceful shutdown",
              shutdownWaitSeconds);
        }
      } catch (InterruptedException ex) {
        LOGGER.error("Error while gracefulShutdown threadPoolExecutor.awaitTermination", ex);
        Thread.currentThread().interrupt();
      }
    }

    private void setReadiesToFalse() {
      LOGGER.info(
          "Setting ready for application to false, so the application doesn't receive new connections from Openshift");
      final Map<String, GracefulShutdownHealthIndicator> probeControllers =
          this.applicationContext.getBeansOfType((Class) GracefulShutdownHealthIndicator.class);
      if (probeControllers.size() < 1) {
        LOGGER.error("Could not find a ProbeController Bean.");
      }
      if (probeControllers.size() > 1) {
        LOGGER.warn(
            "You have more than one ProbeController for Ready-Check registered. Most probably one as Rest service and one in automatically configured as Actuator health check.");
      }
      for (final GracefulShutdownHealthIndicator probeController : probeControllers.values()) {
        probeController.setReady(false);
      }
    }
  }
}
