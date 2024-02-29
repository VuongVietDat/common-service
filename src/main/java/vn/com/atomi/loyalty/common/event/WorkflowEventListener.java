package vn.com.atomi.loyalty.common.event;

import java.util.LinkedHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * @author haidv
 * @version 1.0
 */
@SuppressWarnings({"rawtypes"})
@RequiredArgsConstructor
@Component
public class WorkflowEventListener extends MessageListener<LinkedHashMap> {

  //  @KafkaListener(
  //      topics = "${custom.properties.kafka.topic.workflow-event.name}",
  //      groupId = "${custom.properties.messaging.kafka.groupId}",
  //      concurrency = "${custom.properties.kafka.topic.workflow-event.concurrent.thread}",
  //      containerFactory = "kafkaListenerContainerFactory")
  public void workflowEventListener(
      String data,
      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
      @Header(KafkaHeaders.RECEIVED_PARTITION) String partition,
      @Header(KafkaHeaders.OFFSET) String offset,
      Acknowledgment acknowledgment) {
    super.messageListener(data, topic, partition, offset, acknowledgment, 300, 10);
  }

  //  @KafkaListener(
  //      topics = "${custom.properties.kafka.topic.workflow-event-retries.name}",
  //      groupId = "${custom.properties.messaging.kafka.groupid}",
  //      concurrency = "1",
  //      containerFactory = "kafkaListenerContainerFactory")
  public void workflowEventRetriesListener(
      String data,
      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
      @Header(KafkaHeaders.RECEIVED_PARTITION) String partition,
      @Header(KafkaHeaders.OFFSET) String offset,
      Acknowledgment acknowledgment) {
    super.messageRetriesListener(data, topic, partition, offset, acknowledgment);
  }

  @Override
  protected void handleMessageEvent(
      String topic,
      String partition,
      String offset,
      MessageData<LinkedHashMap> input,
      String messageId) {}
}
