package vn.com.atomi.loyalty.base.redis;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author haidv
 * @version 1.0
 */
@Repository
@RequiredArgsConstructor
public class HistoryMessageRepository {

  private final RedisTemplate<String, Object> redisTemplate;

  @Value("${spring.application.name}")
  private String applicationName;

  public Boolean put(HistoryMessage historyMessage) {
    return redisTemplate
        .opsForValue()
        .setIfAbsent(composeHeader(historyMessage), historyMessage, Duration.ofHours(12));
  }

  private String composeHeader(HistoryMessage historyMessage) {
    return String.format(
        "LOYALTY_HISTORY_MESSAGE:%s:%s:%s:%s",
        historyMessage.getBrokerType().toUpperCase(),
        applicationName.toUpperCase(),
        historyMessage.getDestination().toUpperCase(),
        historyMessage.getMessageId());
  }
}
