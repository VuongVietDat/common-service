package vn.com.atomi.loyalty.base.event;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/**
 * @author haidv
 * @version 1.0
 */
@Getter
@Setter
public class RetriesMessageData {

  private String messageId;

  private String retryMessageId;

  private String data;

  private String topic;

  private String source;

  private String destination;

  private Integer retriesNo;

  private Integer repeatCount;

  private Long delayTime;

  private LocalDateTime preExecuteAt;

  private RetriesMessageDataStatus status;

  public RetriesMessageData() {
    this.messageId = UUID.randomUUID().toString();
    this.retriesNo = 1;
    this.status = RetriesMessageDataStatus.INSERT;
  }

  public RetriesMessageData(
      String retryMessageId, String data, String topic, long delayTime, Integer repeatCount) {
    this();
    this.retryMessageId = retryMessageId;
    this.data = data;
    this.topic = topic;
    this.delayTime = delayTime;
    this.repeatCount = repeatCount;
    this.preExecuteAt = LocalDateTime.now();
  }

  public RetriesMessageData incrementRetriesNo() {
    this.messageId = UUID.randomUUID().toString();
    this.retriesNo = this.retriesNo + 1;
    this.data = null;
    this.source = null;
    this.destination = null;
    this.topic = null;
    this.repeatCount = null;
    this.preExecuteAt = LocalDateTime.now();
    this.status = RetriesMessageDataStatus.UPDATE;
    return this;
  }

  public RetriesMessageData deleteRetries() {
    this.messageId = UUID.randomUUID().toString();
    this.retriesNo = null;
    this.data = null;
    this.source = null;
    this.destination = null;
    this.topic = null;
    this.delayTime = null;
    this.repeatCount = null;
    this.status = RetriesMessageDataStatus.DELETE;
    return this;
  }

  public enum RetriesMessageDataStatus {
    INSERT,
    DELETE,
    UPDATE
  }
}
