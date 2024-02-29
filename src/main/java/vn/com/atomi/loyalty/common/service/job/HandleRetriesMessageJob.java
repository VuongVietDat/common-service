package vn.com.atomi.loyalty.common.service.job;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.ThreadContext;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import vn.com.atomi.loyalty.base.constant.RequestConstant;
import vn.com.atomi.loyalty.base.exception.BaseException;
import vn.com.atomi.loyalty.common.entity.ScheduleInfo;
import vn.com.atomi.loyalty.common.entity.ScheduleLog;
import vn.com.atomi.loyalty.common.enums.ErrorCode;
import vn.com.atomi.loyalty.common.enums.StatusJob;
import vn.com.atomi.loyalty.common.event.MessageInterceptor;
import vn.com.atomi.loyalty.common.repository.RetriesMessageRepository;
import vn.com.atomi.loyalty.common.repository.ScheduleLogRepository;
import vn.com.atomi.loyalty.common.repository.ScheduleRepository;
import vn.com.atomi.loyalty.common.utils.Utils;

/**
 * @author haidv
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class HandleRetriesMessageJob extends QuartzJobBean {

  protected static final Logger LOGGER = LoggerFactory.getLogger(HandleRetriesMessageJob.class);

  private final ScheduleLogRepository scheduleLogRepository;

  private final ScheduleRepository scheduleRepository;

  private final RetriesMessageRepository retriesMessageRepository;

  private final MessageInterceptor messageInterceptor;

  @Override
  protected void executeInternal(JobExecutionContext context) {
    ThreadContext.put(RequestConstant.REQUEST_ID, Utils.generateUniqueId());
    JobKey jobKey = context.getJobDetail().getKey();
    ScheduleInfo scheduleInfo =
        scheduleRepository
            .findByJobNameAndJobGroupAndDeletedFalse(jobKey.getName(), jobKey.getGroup())
            .orElseThrow(() -> new BaseException(ErrorCode.JOB_NOT_EXISTED));
    ScheduleLog scheduleLog =
        ScheduleLog.builder()
            .jobId(scheduleInfo.getId())
            .status(StatusJob.STARTED)
            .startTime(LocalDateTime.now())
            .build();
    scheduleLog = scheduleLogRepository.save(scheduleLog);

    try {
      retriesMessageRepository
          .findByRetriesActivated(LocalDateTime.now())
          .forEach(messageInterceptor::convertAndSend);
    } catch (Exception e) {
      scheduleLog.setStatus(StatusJob.FAILED);
      scheduleLog.setExceptionInfo(e.getMessage());
      LOGGER.error(e.getMessage(), e);
    } finally {
      scheduleLog.setEndTime(LocalDateTime.now());
      scheduleLogRepository.save(scheduleLog);
      scheduleInfo.setJobStatus(StatusJob.STARTED);
      scheduleInfo.setExecuteLastTime(LocalDateTime.now());
      scheduleRepository.save(scheduleInfo);
    }
  }
}
