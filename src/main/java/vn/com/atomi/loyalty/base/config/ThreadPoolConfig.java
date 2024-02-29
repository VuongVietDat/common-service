package vn.com.atomi.loyalty.base.config;

import java.util.concurrent.ThreadPoolExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.web.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class ThreadPoolConfig {

  @Value("${custom.properties.service.thread.pool.task.executor.core.pool.size}")
  private int corePoolSize;

  @Value("${custom.properties.service.thread.pool.task.executor.max.pool.size}")
  private int maxPoolSize;

  @Value("${custom.properties.service.thread.pool.task.executor.queue.capacity}")
  private int queueCapacity;

  @Bean
  @ConditionalOnProperty("custom.properties.service.thread.pool.task.executor")
  public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
    var executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(corePoolSize);
    executor.setMaxPoolSize(maxPoolSize);
    executor.setQueueCapacity(queueCapacity);
    executor.setThreadNamePrefix("service-task_executor-");
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.initialize();
    return executor;
  }
}
