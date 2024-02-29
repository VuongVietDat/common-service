package vn.com.atomi.loyalty.base.graceful;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

/**
 * @author haidv
 * @version 1.0
 */
public class GracefulShutdownHealthIndicator implements HealthIndicator {

  private static final String GRACEFUL_SHUTDOWN = "GracefulShutdown";

  private static final Logger logger =
      LoggerFactory.getLogger(GracefulShutdownHealthIndicator.class);

  private Health health;

  GracefulShutdownHealthIndicator() {
    this.setReady(true);
  }

  /**
   * Return an indication of health.
   *
   * @return the health
   */
  @Override
  public Health health() {
    return this.health;
  }

  public void setReady(final boolean ready) {
    if (ready) {
      this.health =
          new Health.Builder().withDetail(GRACEFUL_SHUTDOWN, "application up").up().build();
      logger.info("GracefulShutdown healthCheck up");
    } else {
      this.health =
          new Health.Builder()
              .withDetail(GRACEFUL_SHUTDOWN, "gracefully shutting down")
              .down()
              .build();
      logger.info("GracefulShutdown healthCheck down");
    }
  }
}
