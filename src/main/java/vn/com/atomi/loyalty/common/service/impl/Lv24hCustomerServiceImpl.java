package vn.com.atomi.loyalty.common.service.impl;

import java.math.BigDecimal;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.com.atomi.loyalty.base.data.BaseService;
import vn.com.atomi.loyalty.base.event.MessageData;
import vn.com.atomi.loyalty.base.utils.JsonUtils;
import vn.com.atomi.loyalty.common.dto.message.Lv24hCustomerMessage;
import vn.com.atomi.loyalty.common.enums.EventAction;
import vn.com.atomi.loyalty.common.mapper.CustomerMapper;
import vn.com.atomi.loyalty.common.repository.Lv24hRepository;
import vn.com.atomi.loyalty.common.repository.redis.EtlLastCustomerRepository;
import vn.com.atomi.loyalty.common.service.Lv24hCustomerService;

@SuppressWarnings({"unchecked"})
@Service
@RequiredArgsConstructor
public class Lv24hCustomerServiceImpl extends BaseService implements Lv24hCustomerService {
  private final Lv24hRepository lv24hRepository;
  private final EtlLastCustomerRepository redisLastCus;
  private final CustomerMapper customerMapper;

  @SuppressWarnings("rawtypes")
  private final KafkaTemplate kafkaTemplate;

  @Value("${custom.properties.kafka.topic.customer-create.name}")
  String topicCreate;

  @Value("${custom.properties.kafka.topic.customer-update.name}")
  String topicUpdate;

  @Value("${custom.properties.kafka.topic.customer-delete.name}")
  String topicDelete;

  @Override
  public int etl() {
    // load data from
    var list = lv24hRepository.selects(redisLastCus.get());

    var lastID = (BigDecimal) CollectionUtils.lastElement(list).get("CUSTOMER_ID");
    redisLastCus.put(lastID.longValue());

    var msgData = customerMapper.fromOracleDb(list);
    kafkaTemplate.send(topicCreate, JsonUtils.toJson(new MessageData<>(msgData)));

    return list.size();
  }

  @Override
  public void syncFromQueue(Lv24hCustomerMessage message, String messageId) {
    // valid input
    var action = message.getAction();
    if (action == null) return;

    var topic =
        Map.of(
                EventAction.INSERT,
                topicCreate,
                EventAction.UPDATE,
                topicUpdate,
                EventAction.DELETE,
                topicDelete)
            .get(action);
    if (topic == null) return;

    // mapping and publish msg
    var kafkaMsg = customerMapper.fromQueue(message);
    kafkaTemplate.send(topic, messageId, JsonUtils.toJson(new MessageData<>(kafkaMsg)));
  }
}
