package vn.com.atomi.loyalty.common.service.job;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.quartz.*;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;
import vn.com.atomi.loyalty.base.constant.RequestConstant;
import vn.com.atomi.loyalty.base.exception.BaseException;
import vn.com.atomi.loyalty.base.exception.CommonErrorCode;
import vn.com.atomi.loyalty.common.entity.ScheduleInfo;
import vn.com.atomi.loyalty.common.entity.ScheduleLog;
import vn.com.atomi.loyalty.common.enums.ErrorCode;
import vn.com.atomi.loyalty.common.enums.StatusJob;
import vn.com.atomi.loyalty.common.repository.Lv24hRepository;
import vn.com.atomi.loyalty.common.repository.ScheduleLogRepository;
import vn.com.atomi.loyalty.common.repository.ScheduleRepository;
import vn.com.atomi.loyalty.common.service.Lv24hCustomerService;
import vn.com.atomi.loyalty.common.utils.Utils;

@Slf4j
@Component
@RequiredArgsConstructor
public class HandleEtlCustomerJob extends QuartzJobBean {
  private final SchedulerFactoryBean schedulerFactoryBean;
  private final ScheduleRepository scheduleRepository;
  private final ScheduleLogRepository scheduleLogRepository;
  private final Lv24hCustomerService lv24hCustomerService;

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
      var count = lv24hCustomerService.etl();
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
