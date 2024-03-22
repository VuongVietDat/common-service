package vn.com.atomi.loyalty.common.service.impl;

import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import vn.com.atomi.loyalty.base.data.BaseService;
import vn.com.atomi.loyalty.base.exception.BaseException;
import vn.com.atomi.loyalty.base.exception.CommonErrorCode;
import vn.com.atomi.loyalty.common.entity.ScheduleInfo;
import vn.com.atomi.loyalty.common.enums.ErrorCode;
import vn.com.atomi.loyalty.common.enums.StatusJob;
import vn.com.atomi.loyalty.common.repository.ScheduleRepository;
import vn.com.atomi.loyalty.common.service.ScheduleService;

/**
 * @author haidv
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl extends BaseService implements ScheduleService {

  private final ScheduleRepository scheduleRepository;

  private final SchedulerFactoryBean schedulerFactoryBean;

  private final JobScheduleCreator scheduleCreator;

  @Override
  @SuppressWarnings("unchecked")
  public void startJobNow(Long id) throws SchedulerException, ClassNotFoundException {
    ScheduleInfo scheduleInfo =
        scheduleRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new BaseException(new Object[] {id}, ErrorCode.JOB_NOT_EXISTED));
    Scheduler scheduler = schedulerFactoryBean.getScheduler();
    TriggerKey triggerKey =
        TriggerKey.triggerKey(scheduleInfo.getJobName(), scheduleInfo.getJobGroup());
    JobKey jobKey = new JobKey(scheduleInfo.getJobName(), scheduleInfo.getJobGroup());

    if (scheduler.checkExists(jobKey) && scheduler.checkExists(triggerKey)) {
      Trigger trigger = scheduler.getTrigger(triggerKey);
      trigger = trigger.getTriggerBuilder().startNow().build();
      scheduler.rescheduleJob(triggerKey, trigger);
    } else {
      Class<?> clazz = Class.forName(scheduleInfo.getJobClass());

      JobDetail jobDetail =
          scheduleCreator.createJob(
              (Class<? extends QuartzJobBean>) clazz,
              scheduleInfo.getIsDurable(),
              scheduleInfo.getJobName(),
              scheduleInfo.getJobGroup());

      Trigger trigger;
      if (scheduleInfo.getCronJob()) {
        trigger =
            scheduleCreator.createCronTrigger(
                scheduleInfo.getJobName(),
                scheduleInfo.getJobGroup(),
                new Date(scheduleInfo.getStartTime()),
                scheduleInfo.getCronExpression());
      } else {
        trigger =
            scheduleCreator.createSimpleTrigger(
                scheduleInfo.getJobName(),
                scheduleInfo.getJobGroup(),
                new Date(scheduleInfo.getStartTime()),
                scheduleInfo.getRepeatTime(),
                scheduleInfo.getRepeatCount());
      }
      trigger = trigger.getTriggerBuilder().startNow().build();
      scheduleCreator.scheduleJob(jobDetail, trigger);
    }
    scheduleInfo.setJobStatus(StatusJob.STARTED);
    scheduleRepository.save(scheduleInfo);
  }

  public void pauseJob(Long id) throws SchedulerException {
    ScheduleInfo scheduleInfo =
        scheduleRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new BaseException(new Object[] {id}, ErrorCode.JOB_NOT_EXISTED));
    JobKey jobKey = new JobKey(scheduleInfo.getJobName(), scheduleInfo.getJobGroup());
    Scheduler scheduler = schedulerFactoryBean.getScheduler();
    JobDetail jobDetail = scheduler.getJobDetail(jobKey);
    if (jobDetail == null) {
      throw new BaseException(CommonErrorCode.BAD_REQUEST);
    } else {
      scheduler.deleteJob(jobKey);
      scheduleInfo.setJobStatus(StatusJob.STOPPED);
      scheduleRepository.save(scheduleInfo);
    }
  }

  public void deleteJob(Long id) throws SchedulerException {
    ScheduleInfo scheduleInfo =
        scheduleRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new BaseException(new Object[] {id}, ErrorCode.JOB_NOT_EXISTED));
    JobKey jobKey = new JobKey(scheduleInfo.getJobName(), scheduleInfo.getJobGroup());
    Scheduler scheduler = schedulerFactoryBean.getScheduler();
    JobDetail jobDetail = scheduler.getJobDetail(jobKey);
    if (jobDetail != null) {
      scheduler.deleteJob(jobKey);
    }
    scheduleInfo.setJobStatus(StatusJob.STOPPED);
    scheduleInfo.setDeleted(true);
    scheduleRepository.save(scheduleInfo);
  }
}
