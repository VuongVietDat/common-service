package vn.com.atomi.loyalty.common.service.impl;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.com.atomi.loyalty.base.constant.RequestConstant;
import vn.com.atomi.loyalty.base.data.BaseService;
import vn.com.atomi.loyalty.base.event.MessageData;
import vn.com.atomi.loyalty.base.event.MessageInterceptor;
import vn.com.atomi.loyalty.common.dto.message.Lv24hCustomerMessage;
import vn.com.atomi.loyalty.common.dto.output.SourceDataMapOutput;
import vn.com.atomi.loyalty.common.enums.EventAction;
import vn.com.atomi.loyalty.common.enums.SourceGroup;
import vn.com.atomi.loyalty.common.feign.LoyaltyConfigClient;
import vn.com.atomi.loyalty.common.repository.Lv24hRepository;
import vn.com.atomi.loyalty.common.repository.redis.EtlLastCustomerRepository;
import vn.com.atomi.loyalty.common.service.Lv24hCustomerService;
import vn.com.atomi.loyalty.common.utils.Constants;

@Service
@RequiredArgsConstructor
public class Lv24hCustomerServiceImpl extends BaseService implements Lv24hCustomerService {

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

  private final LoyaltyConfigClient loyaltyConfigClient;

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
    if (list.isEmpty()) {
      return 0;
    }

    var lastID = (BigDecimal) CollectionUtils.lastElement(list).get("CUSTOMER_ID");
    redisLastCus.put(lastID.longValue());

    var msgData = this.fromOracleDb(list);

    var maps =
        loyaltyConfigClient
            .getAllSourceDataMap(ThreadContext.get(RequestConstant.REQUEST_ID), SourceGroup.LV24H)
            .getData();

    for (Map<String, Object> msgDatum : msgData) {
      this.mapDataLoyalty(msgDatum, maps);
    }

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

    var maps =
        loyaltyConfigClient
            .getAllSourceDataMap(ThreadContext.get(RequestConstant.REQUEST_ID), SourceGroup.LV24H)
            .getData();
    this.mapDataLoyalty(kafkaMsg, maps);
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
                                  if (o instanceof Timestamp ts) o = ts.toString();
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
        put("customerName", message.getFullName());
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

  private void mapDataLoyalty(Map<String, Object> msgDatum, List<SourceDataMapOutput> maps) {
    if (msgDatum.get("customerType") != null) {
      maps.stream()
          .filter(
              v ->
                  Constants.SOURCE_TYPE_CUSTOMER_TYPE.equals(v.getSourceType())
                      && v.getSourceId().equals(msgDatum.get("customerType").toString()))
          .findFirst()
          .ifPresentOrElse(
              sourceDataMapOutput ->
                  msgDatum.put("customerType", sourceDataMapOutput.getDestinationCode()),
              () -> msgDatum.put("customerType", null));
    }
    if (msgDatum.get("nationality") != null) {
      maps.stream()
          .filter(
              v ->
                  Constants.SOURCE_TYPE_NATIONALITY.equals(v.getSourceType())
                      && v.getSourceId().equals(msgDatum.get("nationality").toString()))
          .findFirst()
          .ifPresentOrElse(
              sourceDataMapOutput ->
                  msgDatum.put("nationality", sourceDataMapOutput.getDestinationCode()),
              () -> msgDatum.put("nationality", null));
    }

    if (msgDatum.get("uniqueType") != null) {
      maps.stream()
          .filter(
              v ->
                  Constants.SOURCE_TYPE_UNIQUE_TYPE.equals(v.getSourceType())
                      && v.getSourceId().equals(msgDatum.get("uniqueType").toString()))
          .findFirst()
          .ifPresentOrElse(
              sourceDataMapOutput ->
                  msgDatum.put("uniqueType", sourceDataMapOutput.getDestinationCode()),
              () -> msgDatum.put("uniqueType", null));
    }

    if (msgDatum.get("gender") != null) {
      maps.stream()
          .filter(
              v ->
                  Constants.SOURCE_TYPE_GENDER.equals(v.getSourceType())
                      && v.getSourceId().equals(msgDatum.get("gender").toString()))
          .findFirst()
          .ifPresentOrElse(
              sourceDataMapOutput ->
                  msgDatum.put("gender", sourceDataMapOutput.getDestinationCode()),
              () -> msgDatum.put("gender", null));
    }
  }
}
