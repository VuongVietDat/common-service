package vn.com.atomi.loyalty.common.service.job;

import static vn.com.atomi.loyalty.base.constant.DateConstant.STR_PLAN_DD_MM_YYYY_STROKE;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import vn.com.atomi.loyalty.base.constant.RequestConstant;
import vn.com.atomi.loyalty.base.event.MessageData;
import vn.com.atomi.loyalty.base.exception.BaseException;
import vn.com.atomi.loyalty.base.exception.CommonErrorCode;
import vn.com.atomi.loyalty.base.utils.JsonUtils;
import vn.com.atomi.loyalty.common.entity.ScheduleInfo;
import vn.com.atomi.loyalty.common.entity.ScheduleLog;
import vn.com.atomi.loyalty.common.enums.ErrorCode;
import vn.com.atomi.loyalty.common.enums.StatusJob;
import vn.com.atomi.loyalty.common.repository.Lv24hRepository;
import vn.com.atomi.loyalty.common.repository.ScheduleLogRepository;
import vn.com.atomi.loyalty.common.repository.ScheduleRepository;
import vn.com.atomi.loyalty.common.repository.redis.EtlLastCustomerRepository;
import vn.com.atomi.loyalty.common.utils.Utils;

@Slf4j
@Component
@RequiredArgsConstructor
public class HandleEtlCustomerJob extends QuartzJobBean {
  private final SchedulerFactoryBean schedulerFactoryBean;
  private final ScheduleRepository scheduleRepository;
  private final ScheduleLogRepository scheduleLogRepository;
  private final Lv24hRepository lv24hRepository;
  private final EtlLastCustomerRepository redisLastCus;

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
      var count = process();
      if (count < Lv24hRepository.batchSize) finishJob(scheduleInfo);
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
              *//*
              zxc 687 | 100 | 317588
              zxc 688 | 33 | 522726
              zxc done
              *//*
            })
        .start();
  }*/

  @SuppressWarnings({"unchecked"})
  private int process() {
    var list = lv24hRepository.selects(redisLastCus.get());

    var lastID = (BigDecimal) CollectionUtils.lastElement(list).get("CUSTOMER_ID");
    redisLastCus.put(lastID.longValue());

    var msgData = mappingData(list);
    kafkaTemplate.send(topic, JsonUtils.toJson(new MessageData<>(msgData)));

    return list.size();
  }

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

  private void finishJob(ScheduleInfo scheduleInfo) {
    try {
      JobKey jobKey = new JobKey(scheduleInfo.getJobName(), scheduleInfo.getJobGroup());
      Scheduler scheduler = schedulerFactoryBean.getScheduler();
      JobDetail jobDetail = scheduler.getJobDetail(jobKey);
      if (jobDetail == null) throw new BaseException(CommonErrorCode.BAD_REQUEST);

      scheduler.deleteJob(jobKey);
      scheduleInfo.setJobStatus(StatusJob.SUCCESS);
      scheduleRepository.save(scheduleInfo);
    } catch (SchedulerException e) {
      throw new BaseException(new Object[] {scheduleInfo.getId()}, ErrorCode.JOB_NOT_EXISTED);
    }
  }
}
