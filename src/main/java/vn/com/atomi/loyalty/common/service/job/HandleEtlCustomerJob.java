package vn.com.atomi.loyalty.common.service.job;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import vn.com.atomi.loyalty.base.constant.RequestConstant;
import vn.com.atomi.loyalty.base.exception.BaseException;
import vn.com.atomi.loyalty.base.utils.JsonUtils;
import vn.com.atomi.loyalty.common.entity.ScheduleInfo;
import vn.com.atomi.loyalty.common.entity.ScheduleLog;
import vn.com.atomi.loyalty.common.enums.ErrorCode;
import vn.com.atomi.loyalty.common.enums.StatusJob;
import vn.com.atomi.loyalty.common.event.MessageData;
import vn.com.atomi.loyalty.common.repository.Lv24hRepository;
import vn.com.atomi.loyalty.common.repository.ScheduleLogRepository;
import vn.com.atomi.loyalty.common.repository.ScheduleRepository;
import vn.com.atomi.loyalty.common.repository.redis.EtlLastCustomerRepository;
import vn.com.atomi.loyalty.common.utils.Utils;

@Slf4j
@Component
@RequiredArgsConstructor
public class HandleEtlCustomerJob extends QuartzJobBean {
  private final ScheduleRepository scheduleRepository;
  private final ScheduleLogRepository scheduleLogRepository;
  private final Lv24hRepository lv24hRepository;
  private final EtlLastCustomerRepository redisLastCus;

  @SuppressWarnings("rawtypes")
  private final KafkaTemplate kafkaTemplate;

  @Override
  protected void executeInternal(JobExecutionContext context) {
    var executeId = Utils.generateUniqueId();
    ThreadContext.put(RequestConstant.REQUEST_ID, executeId);
    JobKey jobKey = context.getJobDetail().getKey();
    ScheduleInfo scheduleInfo =
        scheduleRepository
            .findByJobNameAndJobGroupAndDeletedFalse(jobKey.getName(), jobKey.getGroup())
            .orElseThrow(() -> new BaseException(ErrorCode.JOB_NOT_EXISTED));
    ScheduleLog scheduleLog = null;
    StatusJob status = null;
    String msg = null;
    if (scheduleInfo.getIsLog()) {
      scheduleLog =
          ScheduleLog.builder()
              .executeId(executeId)
              .jobId(scheduleInfo.getId())
              .status(StatusJob.STARTED)
              .startTime(LocalDateTime.now())
              .build();
      scheduleLog = scheduleLogRepository.save(scheduleLog);
    }
    try {
      process();
      status = StatusJob.SUCCESS;
    } catch (Exception e) {
      msg = e.getMessage();
      status = StatusJob.FAILED;
      log.error(e.getMessage(), e);
    } finally {
      if (scheduleLog != null) {
        scheduleLogRepository.updateLogEndJob(
            LocalDateTime.now(), msg, status, scheduleLog.getId());
      }
      scheduleRepository.updateLstExecuteAt(LocalDateTime.now(), scheduleInfo.getId());
    }
  }

  @SuppressWarnings({"unchecked"})
  private void process() {
    var list = lv24hRepository.selects(redisLastCus.get());
    // TODO: 01/04/2024 if list <100 break

    var lastID = (BigDecimal) CollectionUtils.lastElement(list).get("CUSTOMER_ID");
    redisLastCus.put(lastID.longValue());

    var msgData = mappingData(list);
    kafkaTemplate.send("CUSTOMER_CREATE_EVENT", JsonUtils.toJson(new MessageData<>(msgData)));
  }

  private List<Map<String, Object>> mappingData(List<Map<String, Object>> list) {
    var mapping =
        new HashMap<String, String>() {
          {
            put("CUSTOMER_NO", "cifBank");
            put("CIF_NO", "cifWallet");
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
//            put("REG_BRANCH", "registerBranch");
            //            put("FULL_ADDRESS", "residentialAddress");
            //            put("", "rank");
            //            put("", "rmCode");
            //            put("", "rmName");
            //            put("", "segment");
          }
        };

    return list.stream()
        .map(
            map ->
                (Map<String, Object>)
                    new HashMap<String, Object>() {
                      {
                        map.forEach(
                            (s, o) -> {
                              if (o != null) {
                                var key = mapping.get(s);
                                if (key != null) put(key, o);
                              }
                            });
                      }
                    })
        .toList();
  }
}
