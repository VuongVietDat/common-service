package vn.com.atomi.loyalty.base.graceful;

import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author haidv
 * @version 1.0
 */
@Configuration
@ConditionalOnClass(HealthIndicator.class)
public class GracefulShutdownAutoConfiguration {

  @Bean
  HealthIndicator gracefulShutdownHealthCheck() {
    return new GracefulShutdownHealthIndicator();
  }
}
