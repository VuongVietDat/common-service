package vn.com.atomi.loyalty.base.event;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.kafka.support.Acknowledgment;
import vn.com.atomi.loyalty.base.constant.RequestConstant;
import vn.com.atomi.loyalty.base.redis.HistoryMessage;
import vn.com.atomi.loyalty.base.utils.JsonUtils;

/**
 * @author haidv
 * @version 1.0
 */
public abstract class BaseRetriesMessageListener<T> extends BaseMessageListener<T> {

  @SuppressWarnings("unchecked")
  protected void messageRetriesListener(
      String data, String topic, String partition, String offset, Acknowledgment acknowledgment) {
    super.initListener(topic, partition, offset, data);
    RetriesMessageData input = JsonUtils.fromJson(data, RetriesMessageData.class);
    if (input == null) {
      LOGGER.info("[KafkaConsumer][{}][{}][{}]  ignore!", topic, partition, offset);
      acknowledgment.acknowledge();
      ThreadContext.clearAll();
      return;
    }
    MessageData<T> retryData = JsonUtils.fromJson(input.getData(), MessageData.class);
    if (retryData == null) {
      LOGGER.info("[KafkaConsumer][{}][{}][{}]  retryData ignore!", topic, partition, offset);
      acknowledgment.acknowledge();
      ThreadContext.clearAll();
      return;
    }
    try {
      if (Boolean.FALSE.equals(
          super.historyMessageRepository.put(
              new HistoryMessage(input.getMessageId(), topic, RequestConstant.BROKER_KAFKA)))) {
        LOGGER.warn(
            "[KafkaConsumer][{}][{}][{}]  message has been processed", topic, partition, offset);
        return;
      }
      handleMessageEvent(topic, partition, offset, retryData, input.getMessageId());
      messageInterceptor.convertAndSendRetriesEvent(input.deleteRetries());
      LOGGER.info("[KafkaConsumer][{}][{}][{}]  successful!", topic, partition, offset);
    } catch (Exception e) {
      LOGGER.error("[KafkaConsumer][{}][{}][{}]  Exception revert ", topic, partition, offset, e);
      messageInterceptor.convertAndSendRetriesEvent(input.incrementRetriesNo());
    } finally {
      acknowledgment.acknowledge();
      ThreadContext.clearAll();
    }
  }

  protected abstract void handleMessageEvent(
      String topic, String partition, String offset, MessageData<T> input, String messageId);
}
