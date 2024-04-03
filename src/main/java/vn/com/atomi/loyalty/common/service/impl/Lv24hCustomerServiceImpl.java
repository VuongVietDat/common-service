package vn.com.atomi.loyalty.common.service.impl;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.com.atomi.loyalty.base.data.BaseService;
import vn.com.atomi.loyalty.base.event.MessageData;
import vn.com.atomi.loyalty.base.utils.JsonUtils;
import vn.com.atomi.loyalty.common.dto.message.Lv24hCustomerMessage;
import vn.com.atomi.loyalty.common.mapper.CustomerMapper;
import vn.com.atomi.loyalty.common.repository.Lv24hRepository;
import vn.com.atomi.loyalty.common.repository.redis.EtlLastCustomerRepository;
import vn.com.atomi.loyalty.common.service.Lv24hCustomerService;

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

  @SuppressWarnings({"unchecked"})
  @Override
  public int etl() {
    var list = lv24hRepository.selects(redisLastCus.get());

    var lastID = (BigDecimal) CollectionUtils.lastElement(list).get("CUSTOMER_ID");
    redisLastCus.put(lastID.longValue());

    var msgData = customerMapper.fromOracleDb(list);
    kafkaTemplate.send(topicCreate, JsonUtils.toJson(new MessageData<>(msgData)));

    return list.size();
  }

  @SuppressWarnings({"unchecked"})
  @Override
  public void syncFromQueue(Lv24hCustomerMessage message, String messageId) {
    var kafkaMsg = customerMapper.fromQueue(message);
    kafkaTemplate.send(topicCreate, messageId, JsonUtils.toJson(new MessageData<>(kafkaMsg)));
  }
}
