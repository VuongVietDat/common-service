package vn.com.atomi.loyalty.common.event;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import vn.com.atomi.loyalty.common.utils.Utils;

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
    this.messageId = Utils.generateUniqueId();
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
    this.messageId = Utils.generateUniqueId();
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
    this.messageId = Utils.generateUniqueId();
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
