package vn.com.atomi.loyalty.common.repository.redis;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import vn.com.atomi.loyalty.common.entity.redis.HistoryKafkaMessage;

/**
 * @author haidv
 * @version 1.0
 */
@Repository
@RequiredArgsConstructor
public class HistoryKafkaMessageRepository {

  private final RedisTemplate<String, Object> redisTemplate;

  @Value("${spring.application.name}")
  private String applicationName;

  public Boolean put(String topic, HistoryKafkaMessage historyKafkaMessage) {
    return redisTemplate
        .opsForValue()
        .setIfAbsent(
            composeHeader(topic, historyKafkaMessage.getMessageId()),
            historyKafkaMessage,
            Duration.ofHours(12));
  }

  private String composeHeader(String topic, String key) {
    return String.format("HistoryKafkaMessage:%s:%s:%s", applicationName, topic, key);
  }
}
