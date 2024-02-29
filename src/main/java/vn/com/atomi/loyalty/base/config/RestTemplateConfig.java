package vn.com.atomi.loyalty.base.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.client.RestTemplate;

/**
 * @author haidv
 * @version 1.0
 */
@Configuration
public class RestTemplateConfig {

  @Bean
  public CustomRestTemplateCustomizer customRestTemplateCustomizer() {
    return new CustomRestTemplateCustomizer();
  }

  @Bean
  @DependsOn(value = {"customRestTemplateCustomizer"})
  public RestTemplateBuilder restTemplateBuilder() {
    return new RestTemplateBuilder(customRestTemplateCustomizer());
  }

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.build();
  }
}
