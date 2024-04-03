package vn.com.atomi.loyalty.base.event;

import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.Acknowledgment;
import vn.com.atomi.loyalty.base.constant.RequestConstant;
import vn.com.atomi.loyalty.base.redis.HistoryMessage;
import vn.com.atomi.loyalty.base.redis.HistoryMessageRepository;
import vn.com.atomi.loyalty.base.utils.JsonUtils;

/**
 * @author haidv
 * @version 1.0
 */
public abstract class BaseMessageListener<T> {

  protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  protected HistoryMessageRepository historyMessageRepository;

  protected MessageInterceptor messageInterceptor;

  @Autowired
  public final void setMessageInterceptor(MessageInterceptor messageInterceptor) {
    this.messageInterceptor = messageInterceptor;
  }

  @Autowired
  public final void setHistoryMessageRepository(HistoryMessageRepository historyMessageRepository) {
    this.historyMessageRepository = historyMessageRepository;
  }

  @SuppressWarnings("unused")
  protected void messageListener(
      String data, String topic, String partition, String offset, Acknowledgment acknowledgment) {
    messageListener(data, topic, partition, offset, acknowledgment, 0, 0);
  }

  @SuppressWarnings("unchecked")
  protected void messageListener(
      String data,
      String topic,
      String partition,
      String offset,
      Acknowledgment acknowledgment,
      long delayTime,
      Integer repeatCount) {
    this.initListener(topic, partition, offset, data);
    MessageData<T> input = JsonUtils.fromJson(data, MessageData.class);
    if (input == null) {
      LOGGER.info("[KafkaConsumer][{}][{}][{}]  ignore!", topic, partition, offset);
      acknowledgment.acknowledge();
      ThreadContext.clearAll();
      return;
    }
    if (StringUtils.isBlank(input.getMessageId())) {
      input.updateMessageId(String.format("%s_%s_%s", topic, partition, offset));
    }
    try {
      if (Boolean.FALSE.equals(
          historyMessageRepository.put(
              new HistoryMessage(input.getMessageId(), topic, RequestConstant.BROKER_KAFKA)))) {
        LOGGER.warn(
            "[KafkaConsumer][{}][{}][{}]  message has been processed", topic, partition, offset);
        return;
      }
      handleMessageEvent(topic, partition, offset, input, input.getMessageId());
      LOGGER.info("[KafkaConsumer][{}][{}][{}]  successful!", topic, partition, offset);
    } catch (Exception e) {
      LOGGER.error("[KafkaConsumer][{}][{}][{}]  Exception revert ", topic, partition, offset, e);
      if (repeatCount > 0) {
        messageInterceptor.convertAndSendRetriesEvent(
            new RetriesMessageData(input.getMessageId(), data, topic, delayTime, repeatCount));
      }
    } finally {
      acknowledgment.acknowledge();
      ThreadContext.clearAll();
    }
  }

  protected abstract void handleMessageEvent(
      String topic, String partition, String offset, MessageData<T> input, String messageId);

  protected void initListener(String topic, String partition, String offset, String data) {
    ThreadContext.put(RequestConstant.REQUEST_ID, UUID.randomUUID().toString());
    ThreadContext.put(RequestConstant.BROKER_TYPE, RequestConstant.BROKER_KAFKA);
    ThreadContext.put(RequestConstant.MESSAGE_EVENT, topic);
    LOGGER.info("[KafkaConsumer][{}][{}][{}] Incoming: {}", topic, partition, offset, data);
  }
}
