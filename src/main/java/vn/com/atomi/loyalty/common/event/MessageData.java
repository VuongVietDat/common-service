package vn.com.atomi.loyalty.common.event;

import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.atomi.loyalty.common.utils.Utils;

/**
 * @author haidv
 * @version 1.0
 */
@Getter
@NoArgsConstructor
public class MessageData<T> {

  public static final String FINISH_TASK = "FINISH_TASK";

  public static final String START_WORKFLOW = "START_WORKFLOW";

  private String messageId;
  private String subject;
  private List<T> contents;

  public MessageData(String subject, T content) {
    this.messageId = Utils.generateUniqueId();
    this.subject = subject;
    this.contents = Collections.singletonList(content);
  }

  public MessageData(String subject, List<T> contents) {
    this.messageId = Utils.generateUniqueId();
    this.subject = subject;
    this.contents = contents;
  }

  public MessageData(T content) {
    this.messageId = Utils.generateUniqueId();
    this.contents = Collections.singletonList(content);
  }

  public MessageData(List<T> contents) {
    this.messageId = Utils.generateUniqueId();
    this.contents = contents;
  }

  public void updateMessageId(String messageId) {
    this.messageId = messageId;
  }
}
