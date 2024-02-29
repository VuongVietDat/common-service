package vn.com.atomi.loyalty.common.event;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.Acknowledgment;
import vn.com.atomi.loyalty.base.constant.RequestConstant;
import vn.com.atomi.loyalty.base.utils.JsonUtils;
import vn.com.atomi.loyalty.common.entity.redis.HistoryKafkaMessage;
import vn.com.atomi.loyalty.common.repository.redis.HistoryKafkaMessageRepository;
import vn.com.atomi.loyalty.common.utils.Utils;

public abstract class MessageListener<T> {

  protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  private MessageInterceptor messageInterceptor;

  private HistoryKafkaMessageRepository historyKafkaMessageRepository;

  @Autowired
  public final void setMessageInterceptor(MessageInterceptor messageInterceptor) {
    this.messageInterceptor = messageInterceptor;
  }

  @Autowired
  public final void setHistoryKafkaMessageRepository(
      HistoryKafkaMessageRepository historyKafkaMessageRepository) {
    this.historyKafkaMessageRepository = historyKafkaMessageRepository;
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
          historyKafkaMessageRepository.put(
              topic, new HistoryKafkaMessage(input.getMessageId())))) {
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

  @SuppressWarnings("unchecked")
  protected void messageRetriesListener(
      String data, String topic, String partition, String offset, Acknowledgment acknowledgment) {
    this.initListener(topic, partition, offset, data);
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
          historyKafkaMessageRepository.put(
              topic, new HistoryKafkaMessage(input.getMessageId())))) {
        LOGGER.warn(
            "[KafkaConsumer][{}][{}][{}]  message has been processed", topic, partition, offset);
        return;
      }
      handleMessageEvent(topic, partition, offset, retryData, input.getMessageId());
      messageInterceptor.convertAndSendRetriesEvent(input.deleteRetries());
      LOGGER.info("[KafkaConsumer][{}][{}][{}]  successful!", topic, partition, offset);
    } catch (Exception e) {
      LOGGER.error("[KafkaConsumer][{}][{}][{}]  Exception revert ", topic, partition, offset, e);
      this.handleExceptionRetries(input);
    } finally {
      acknowledgment.acknowledge();
      ThreadContext.clearAll();
    }
  }

  protected abstract void handleMessageEvent(
      String topic, String partition, String offset, MessageData<T> input, String messageId);

  private void initListener(String topic, String partition, String offset, String data) {
    ThreadContext.put(RequestConstant.REQUEST_ID, Utils.generateUniqueId());
    ThreadContext.put(RequestConstant.KAFKA_EVENT, topic);
    LOGGER.info("[KafkaConsumer][{}][{}][{}] Incoming: {}", topic, partition, offset, data);
  }

  private void handleExceptionRetries(RetriesMessageData input) {
    messageInterceptor.convertAndSendRetriesEvent(input.incrementRetriesNo());
  }
}
