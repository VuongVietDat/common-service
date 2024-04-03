package vn.com.atomi.loyalty.common.event;

import java.util.LinkedHashMap;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import vn.com.atomi.loyalty.base.constant.RequestConstant;
import vn.com.atomi.loyalty.base.event.BaseRetriesMessageListener;
import vn.com.atomi.loyalty.base.event.MessageData;
import vn.com.atomi.loyalty.base.event.RetriesMessageData;
import vn.com.atomi.loyalty.base.redis.HistoryMessage;
import vn.com.atomi.loyalty.base.utils.JsonUtils;
import vn.com.atomi.loyalty.common.dto.message.Lv24hCustomerMessage;
import vn.com.atomi.loyalty.common.service.Lv24hCustomerService;
import vn.com.atomi.loyalty.common.utils.Utils;

/**
 * @author haidv
 * @version 1.0
 */
@SuppressWarnings({"rawtypes"})
@RequiredArgsConstructor
@Component
public class Lv24hCustomerEventListener extends BaseRetriesMessageListener<LinkedHashMap> {
  private final Lv24hCustomerService lv24hCustomerService;

  @RabbitListener(queues = "${custom.properties.rabbitmq.queue.lv24h-customer-event.name}")
  public void lv24hTransactionEvent(
      String data,
      @Header("amqp_consumerQueue") String queue,
      @Header("timestamp") String timestamp) {
    ThreadContext.put(RequestConstant.REQUEST_ID, Utils.generateUniqueId());
    ThreadContext.put(RequestConstant.BROKER_TYPE, RequestConstant.BROKER_RABBIT);
    ThreadContext.put(RequestConstant.MESSAGE_EVENT, queue);
    LOGGER.info("[RabbitConsumer][{}][{}] Incoming: {}", queue, timestamp, data);
    Lv24hCustomerMessage input = JsonUtils.fromJson(data, Lv24hCustomerMessage.class);
    if (input == null) {
      LOGGER.info("[RabbitConsumer][{}][{}]  ignore!", queue, timestamp);
      ThreadContext.clearAll();
      return;
    }
    var messageId = input.getMessageId();
    if (StringUtils.isBlank(messageId))
      messageId = String.format("%s_%s_%s", queue, timestamp, input.getCustNo());

    try {
      if (Boolean.FALSE.equals(
          super.historyMessageRepository.put(
              new HistoryMessage(messageId, queue, RequestConstant.BROKER_RABBIT)))) {
        LOGGER.warn("[RabbitConsumer][{}][{}]  message has been processed", queue, timestamp);
        return;
      }
      handleMessageEvent(input, messageId);
      LOGGER.info("[RabbitConsumer][{}][{}]  successful!", queue, timestamp);
    } catch (Exception e) {
      LOGGER.error("[RabbitConsumer][{}][{}]  Exception revert ", queue, timestamp, e);
      var retryData = new MessageData<>(input);
      retryData.updateMessageId(messageId);
      messageInterceptor.convertAndSendRetriesEvent(
          new RetriesMessageData(messageId, JsonUtils.toJson(retryData), queue, 300, 15));
    } finally {
      ThreadContext.clearAll();
    }
  }

  @KafkaListener(
      topics = "${custom.properties.kafka.topic.lv24h-customer-event-retries.name}",
      groupId = "${custom.properties.messaging.kafka.groupId}",
      concurrency = "1",
      containerFactory = "kafkaListenerContainerFactory")
  public void workflowEventRetriesListener(
      String data,
      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
      @Header(KafkaHeaders.RECEIVED_PARTITION) String partition,
      @Header(KafkaHeaders.OFFSET) String offset,
      Acknowledgment acknowledgment) {
    super.messageRetriesListener(data, topic, partition, offset, acknowledgment);
  }

  private void handleMessageEvent(Lv24hCustomerMessage input, String messageId) {
    lv24hCustomerService.syncFromQueue(input, messageId);
  }

  @Override
  protected void handleMessageEvent(
      String topic,
      String partition,
      String offset,
      MessageData<LinkedHashMap> input,
      String messageId) {
    handleMessageEvent(
        JsonUtils.fromJson(input.getContents(), Lv24hCustomerMessage.class), messageId);
  }
}
