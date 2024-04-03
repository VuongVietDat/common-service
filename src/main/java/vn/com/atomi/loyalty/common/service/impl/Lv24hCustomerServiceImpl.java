package vn.com.atomi.loyalty.common.service.impl;

import static vn.com.atomi.loyalty.base.constant.DateConstant.STR_PLAN_DD_MM_YYYY_STROKE;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
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
import vn.com.atomi.loyalty.common.repository.Lv24hRepository;
import vn.com.atomi.loyalty.common.repository.redis.EtlLastCustomerRepository;
import vn.com.atomi.loyalty.common.service.Lv24hCustomerService;

@Service
@RequiredArgsConstructor
public class Lv24hCustomerServiceImpl extends BaseService implements Lv24hCustomerService {
  private final Lv24hRepository lv24hRepository;
  private final EtlLastCustomerRepository redisLastCus;

  @SuppressWarnings("rawtypes")
  private final KafkaTemplate kafkaTemplate;

  @Value("${custom.properties.kafka.topic.customer-create.name}")
  String topic;

  private final SimpleDateFormat dateFormat = new SimpleDateFormat(STR_PLAN_DD_MM_YYYY_STROKE);
  private final Map<String, String> mappingInfo =
      new HashMap<>() {
        {
          put("CUSTOMER_NO", "cifBank");
          put("CIF_WALLET", "cifWallet");
          put("CUSTOMER_NAME", "customerName");
          put("DATE_OF_BIRTH", "dob");
          put("FULL_ADDRESS", "currentAddress");
          put("USER_TYPE", "customerType");
          put("SEX", "gender");
          put("NATIONALITY_ID", "nationality");
          put("BRANCH_CODE", "ownerBranch");
          put("MOBILE_PHONE", "phone");
          put("UNIQUE_ID", "uniqueType");
          put("UNIQUE_VALUE", "uniqueValue");
          put("DATE_OF_ISSUE", "issueDate");
          put("PLACE_OF_ISSUE", "issuePlace");
          put("REG_BRANCH", "registerBranch");
          //            put("FULL_ADDRESS", "residentialAddress");
          //            put("", "rank");
          //            put("", "rmCode");
          //            put("", "rmName");
          //            put("", "segment");
        }
      };

  @SuppressWarnings({"unchecked"})
  @Override
  public int etl() {
    var list = lv24hRepository.selects(redisLastCus.get());

    var lastID = (BigDecimal) CollectionUtils.lastElement(list).get("CUSTOMER_ID");
    redisLastCus.put(lastID.longValue());

    var msgData = mappingData(list);
    kafkaTemplate.send(topic, JsonUtils.toJson(new MessageData<>(msgData)));

    return list.size();
  }

  /*public void test() {
  new Thread(
          () -> {
            var lastId = BigDecimal.valueOf(0);
            var count = 0;
            var size = 0;
            do {
              count++;
              var list = lv24hRepository.selects(lastId.longValue());
              size = list.size();
              lastId = (BigDecimal) CollectionUtils.lastElement(list).get("CUSTOMER_ID");

              System.out.printf("\nzxc %d | %d | %d", count, size, lastId.longValue());
            } while (size == Lv24hRepository.batchSize);
            System.out.println("\nzxc done");
            */
  /*
  zxc 687 | 100 | 317588
  zxc 688 | 33 | 522726
  zxc done
  */
  /*
            })
        .start();
  }*/
  private List<Map<String, Object>> mappingData(List<Map<String, Object>> list) {
    return list.stream()
        .map(
            map ->
                (Map<String, Object>)
                    new HashMap<String, Object>() {
                      {
                        map.forEach(
                            (s, o) -> {
                              if (o != null) {
                                var key = mappingInfo.get(s);
                                if (key != null) {
                                  if (o instanceof Timestamp ts) o = dateFormat.format(ts);
                                  put(key, o);
                                }
                              }
                            });
                      }
                    })
        .toList();
  }

  @Override
  public void syncFromQueue(Lv24hCustomerMessage message) {}
}
