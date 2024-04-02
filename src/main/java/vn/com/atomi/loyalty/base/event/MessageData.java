package vn.com.atomi.loyalty.base.event;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author haidv
 * @version 1.0
 */
@Getter
@NoArgsConstructor
public class MessageData<T> {

  private String messageId;
  private String subject;
  private List<T> contents;

  public MessageData(String subject, T content) {
    this.messageId = UUID.randomUUID().toString();
    this.subject = subject;
    this.contents = Collections.singletonList(content);
  }

  public MessageData(String subject, List<T> contents) {
    this.messageId = UUID.randomUUID().toString();
    this.subject = subject;
    this.contents = contents;
  }

  public MessageData(T content) {
    this.messageId = UUID.randomUUID().toString();
    this.contents = Collections.singletonList(content);
  }

  public MessageData(List<T> contents) {
    this.messageId = UUID.randomUUID().toString();
    this.contents = contents;
  }

  public void updateMessageId(String messageId) {
    this.messageId = messageId;
  }
}
