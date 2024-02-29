package vn.com.atomi.loyalty.common.entity.redis;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author haidv
 * @version 1.0
 */
@Setter
@Getter
@NoArgsConstructor
public class HistoryKafkaMessage implements Serializable {
  private String messageId;
  private LocalDateTime consumeAt;

  public HistoryKafkaMessage(String messageId) {
    this.messageId = messageId;
    this.consumeAt = LocalDateTime.now();
  }
}
