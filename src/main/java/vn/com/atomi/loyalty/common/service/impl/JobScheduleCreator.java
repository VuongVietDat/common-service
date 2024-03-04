package vn.com.atomi.loyalty.common.service.impl;

import java.text.ParseException;
import java.util.Date;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.*;
import org.springframework.stereotype.Component;

/**
 * @author haidv
 * @version 1.0
 */
@Component
public class JobScheduleCreator {

  private static final Logger log = LoggerFactory.getLogger(JobScheduleCreator.class);

  private final SchedulerFactoryBean schedulerFactoryBean;

  private final ApplicationContext applicationContext;

  @Autowired
  public JobScheduleCreator(
      SchedulerFactoryBean schedulerFactoryBean, ApplicationContext applicationContext) {
    this.schedulerFactoryBean = schedulerFactoryBean;
    this.applicationContext = applicationContext;
  }

  public void scheduleJob(JobDetail jobDetail, Trigger trigger) throws SchedulerException {
    Scheduler scheduler = schedulerFactoryBean.getScheduler();
    scheduler.scheduleJob(jobDetail, trigger);
  }

  /**
   * Create Quartz Job.
   *
   * @param jobClass Class whose executeInternal() method needs to be called.
   * @param isDurable Job needs to be persisted even after completion. if true, job will be
   *     persisted, not otherwise.
   * @param jobName Job name.
   * @param jobGroup Job group.
   * @return JobDetail object
   */
  public JobDetail createJob(
      Class<? extends QuartzJobBean> jobClass, Boolean isDurable, String jobName, String jobGroup) {
    return this.createJob(jobClass, isDurable, jobName, jobGroup, null);
  }

  /**
   * Create Quartz Job.
   *
   * @param jobClass Class whose executeInternal() method needs to be called.
   * @param isDurable Job needs to be persisted even after completion. if true, job will be
   *     persisted, not otherwise.
   * @param jobName Job name.
   * @param jobGroup Job group.
   * @param jobDataMap Job data
   * @return JobDetail object
   */
  public JobDetail createJob(
      Class<? extends QuartzJobBean> jobClass,
      Boolean isDurable,
      String jobName,
      String jobGroup,
      JobDataMap jobDataMap) {
    JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
    factoryBean.setJobClass(jobClass);
    factoryBean.setDurability(isDurable == null || isDurable);
    factoryBean.setApplicationContext(applicationContext);
    factoryBean.setName(jobName);
    factoryBean.setGroup(jobGroup);
    factoryBean.setJobDataMap(jobDataMap);

    factoryBean.afterPropertiesSet();

    return factoryBean.getObject();
  }

  /**
   * Create cron trigger.
   *
   * @param triggerName Trigger name.
   * @param startTime Trigger start time.
   * @param cronExpression Cron expression.
   * @return {@link CronTrigger}
   */
  public CronTrigger createCronTrigger(
      String triggerName, String triggerGroup, Date startTime, String cronExpression) {
    CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
    factoryBean.setName(triggerName);
    factoryBean.setGroup(triggerGroup);
    factoryBean.setStartTime(startTime);
    factoryBean.setCronExpression(cronExpression);
    factoryBean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY);
    try {
      factoryBean.afterPropertiesSet();
    } catch (ParseException e) {
      log.error(e.getMessage(), e);
    }
    return factoryBean.getObject();
  }

  /**
   * Create simple trigger.
   *
   * @param triggerName Trigger name.
   * @param startTime Trigger start time.
   * @param repeatTime Job repeat period millsd
   * @return {@link SimpleTrigger}
   */
  public SimpleTrigger createSimpleTrigger(
      String triggerName, String triggerGroup, Date startTime, Long repeatTime, int repeatCount) {
    SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
    factoryBean.setGroup(triggerGroup);
    factoryBean.setName(triggerName);
    factoryBean.setStartTime(startTime);
    factoryBean.setRepeatInterval(repeatTime);
    factoryBean.setRepeatCount(repeatCount);
    factoryBean.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY);
    factoryBean.afterPropertiesSet();
    return factoryBean.getObject();
  }
}
