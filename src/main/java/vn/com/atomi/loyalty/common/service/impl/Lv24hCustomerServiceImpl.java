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
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.com.atomi.loyalty.base.data.BaseService;
import vn.com.atomi.loyalty.base.event.MessageData;
import vn.com.atomi.loyalty.base.event.MessageInterceptor;
import vn.com.atomi.loyalty.common.dto.message.Lv24hCustomerMessage;
import vn.com.atomi.loyalty.common.enums.EventAction;
import vn.com.atomi.loyalty.common.repository.Lv24hRepository;
import vn.com.atomi.loyalty.common.repository.redis.EtlLastCustomerRepository;
import vn.com.atomi.loyalty.common.service.Lv24hCustomerService;

@Service
@RequiredArgsConstructor
public class Lv24hCustomerServiceImpl extends BaseService implements Lv24hCustomerService {

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

  private final Lv24hRepository lv24hRepository;

  private final EtlLastCustomerRepository redisLastCus;

  private final MessageInterceptor messageInterceptor;

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

    var msgData = this.fromOracleDb(list);
    messageInterceptor.convertAndSend(topicCreate, new MessageData<>(msgData));

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
    var kafkaMsg = this.fromQueue(message);
    messageInterceptor.convertAndSend(topic, messageId, new MessageData<>(kafkaMsg));
  }

  private List<Map<String, Object>> fromOracleDb(List<Map<String, Object>> list) {
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

  private Map<String, Object> fromQueue(Lv24hCustomerMessage message) {
    return new HashMap<>() {
      {
        put("cifBank", message.getCustNo());
        //        put( "cifWallet",message.);
        //        put("CUSTOMER_NAME", "customerName");
        put("currentAddress", message.getAddress());
        put("customerType", message.getUserType());
        put("gender", message.getSex());
        put("nationality", message.getNationality());
        put("ownerBranch", message.getBranchCode());
        put("phone", message.getMobilePhone());
        put("uniqueType", message.getUniqueId());
        put("uniqueValue", message.getUniqueValue());
        put("issuePlace", message.getPlaceOfIssue());
        put("registerBranch", message.getRegBranch());
        put("issueDate", message.getDateOfIssue());
        put("dob", message.getDateOfBirth());
        /*var issueD = message.getDateOfIssue();
        if (issueD != null) put("DATE_OF_ISSUE", dateFormat.format(issueD));
        var bod = message.getDateOfBirth();
        if (bod != null) put("DATE_OF_BIRTH", dateFormat.format(bod));*/
      }
    };
  }
}
