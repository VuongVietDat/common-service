package vn.com.atomi.loyalty.base.feign;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author haidv
 * @version 1.0
 */
@Configuration
public class FeignLoggingConfiguration {

  @Bean
  public FeignLogging customFeignRequestLogging() {
    return new FeignLogging();
  }

  @Bean
  public Logger.Level feignLoggerLevel() {
    return Logger.Level.BASIC;
  }
}
