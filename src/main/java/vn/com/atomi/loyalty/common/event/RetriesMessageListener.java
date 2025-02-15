package vn.com.atomi.loyalty.common.event;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.ThreadContext;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import vn.com.atomi.loyalty.base.constant.RequestConstant;
import vn.com.atomi.loyalty.base.event.RetriesMessageData;
import vn.com.atomi.loyalty.base.redis.HistoryMessage;
import vn.com.atomi.loyalty.base.redis.HistoryMessageRepository;
import vn.com.atomi.loyalty.base.utils.JsonUtils;
import vn.com.atomi.loyalty.common.mapper.ModelMapper;
import vn.com.atomi.loyalty.common.repository.RetriesMessageRepository;
import vn.com.atomi.loyalty.common.utils.Utils;

/**
 * @author haidv
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class RetriesMessageListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(RetriesMessageListener.class);

  private final ModelMapper modelMapper = Mappers.getMapper(ModelMapper.class);

  private final RetriesMessageRepository retriesMessageRepository;

  private final HistoryMessageRepository historyMessageRepository;

  @KafkaListener(
      topics = "${custom.properties.kafka.topic.retries-event.name}",
      groupId = "${custom.properties.messaging.kafka.groupId}",
      concurrency = "1",
      containerFactory = "kafkaListenerContainerFactory")
  public void retriesEventListener(
      String data,
      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
      @Header(KafkaHeaders.RECEIVED_PARTITION) String partition,
      @Header(KafkaHeaders.OFFSET) String offset,
      Acknowledgment acknowledgment) {
    ThreadContext.put(RequestConstant.REQUEST_ID, Utils.generateUniqueId());
    ThreadContext.put(RequestConstant.BROKER_TYPE, RequestConstant.BROKER_KAFKA);
    ThreadContext.put(RequestConstant.MESSAGE_EVENT, topic);
    LOGGER.info("[KafkaConsumer][{}][{}][{}] Incoming: {}", topic, partition, offset, data);
    RetriesMessageData input = JsonUtils.fromJson(data, RetriesMessageData.class);
    if (input == null) {
      LOGGER.info("[KafkaConsumer][{}][{}][{}]  ignore!", topic, partition, offset);
      acknowledgment.acknowledge();
      ThreadContext.clearAll();
      return;
    }
    try {
      if (Boolean.FALSE.equals(
          historyMessageRepository.put(
              new HistoryMessage(input.getMessageId(), topic, RequestConstant.BROKER_KAFKA)))) {
        LOGGER.warn(
            "[KafkaConsumer][{}][{}][{}]  message has been processed", topic, partition, offset);
        return;
      }
      retriesMessageRepository
          .findByMessageId(input.getRetryMessageId())
          .ifPresentOrElse(
              v -> {
                if (input.getStatus().equals(RetriesMessageData.RetriesMessageDataStatus.DELETE)) {
                  retriesMessageRepository.deleteByMessageId(input.getMessageId());
                } else {
                  v.setNextExecuteAt(input.getPreExecuteAt().plusSeconds(v.getDelayTime()));
                  v.setRetriesNo(input.getRetriesNo());
                  retriesMessageRepository.save(v);
                }
              },
              () -> {
                var msg = modelMapper.convertToRetriesMessage(input);
                msg.setNextExecuteAt(input.getPreExecuteAt().plusSeconds(input.getDelayTime()));
                retriesMessageRepository.save(msg);
              });
      LOGGER.info("[KafkaConsumer][{}][{}][{}]  successful!", topic, partition, offset);
    } catch (Exception e) {
      LOGGER.error("[KafkaConsumer][{}][{}][{}]  Exception revert ", topic, partition, offset, e);
    } finally {
      acknowledgment.acknowledge();
      ThreadContext.clearAll();
    }
  }
}
