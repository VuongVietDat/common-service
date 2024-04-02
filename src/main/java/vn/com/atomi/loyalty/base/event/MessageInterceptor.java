package vn.com.atomi.loyalty.base.event;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import vn.com.atomi.loyalty.base.utils.JsonUtils;

/**
 * @author haidv
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class MessageInterceptor {

  private static final Logger LOGGER = LoggerFactory.getLogger(MessageInterceptor.class);

  @SuppressWarnings("rawtypes")
  private final KafkaTemplate kafkaTemplate;

  @Value("${custom.properties.kafka.topic.retries-event.name}")
  private String retriesEventTopic;

  @Value("${spring.application.name}")
  private String applicationName;

  @SuppressWarnings({"unchecked"})
  public void convertAndSendRetriesEvent(RetriesMessageData payload) {
    LOGGER.info(
        "Start push message to queue: {} messageId: {} with payload: {}",
        retriesEventTopic,
        payload.getMessageId(),
        JsonUtils.toJson(payload));
    if (payload.getStatus().equals(RetriesMessageData.RetriesMessageDataStatus.INSERT)) {
      payload.setSource(applicationName.toUpperCase());
      payload.setDestination(
          String.format("%s.%s.%s", payload.getTopic(), payload.getSource(), "RETRIES"));
    }
    kafkaTemplate.send(retriesEventTopic, payload.getMessageId(), JsonUtils.toJson(payload));
    LOGGER.info(
        "End push message to queue: {} messageId: {}", retriesEventTopic, payload.getMessageId());
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public void convertAndSend(String queueName, MessageData payload) {
    var payloadJson = JsonUtils.toJson(payload);
    LOGGER.info(
        "Start push message to queue: {} messageId: {} with payload: {}",
        queueName,
        payload.getMessageId(),
        payloadJson);
    kafkaTemplate.send(queueName, payloadJson);
    LOGGER.info("End push message to queue: {} messageId: {}", queueName, payload.getMessageId());
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public void convertAndSend(String queueName, String key, MessageData payload) {
    var payloadJson = JsonUtils.toJson(payload);
    LOGGER.info(
        "Start push message to queue: {} messageId: {} with payload: {}",
        queueName,
        payload.getMessageId(),
        payloadJson);
    kafkaTemplate.send(queueName, key, payloadJson);
    LOGGER.info("End push message to queue: {} messageId: {}", queueName, payload.getMessageId());
  }

  @SuppressWarnings({"unchecked"})
  public void convertAndSend(RetriesMessageData payload) {
    var payloadJson = JsonUtils.toJson(payload);
    LOGGER.info(
        "Start push message to queue: {} messageId: {} with payload: {}",
        payload.getDestination(),
        payload.getMessageId(),
        payloadJson);
    kafkaTemplate.send(payload.getDestination(), payload.getRetryMessageId(), payloadJson);
    LOGGER.info(
        "End push message to queue: {} messageId: {}",
        payload.getDestination(),
        payload.getMessageId());
  }
}
