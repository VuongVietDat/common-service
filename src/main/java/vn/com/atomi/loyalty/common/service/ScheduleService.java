package vn.com.atomi.loyalty.common.service;

import org.quartz.SchedulerException;

/**
 * @author haidv
 * @version 1.0
 */
public interface ScheduleService {

  void startJobNow(Long id) throws SchedulerException, ClassNotFoundException;

  void pauseJob(Long id) throws SchedulerException;

  void deleteJob(Long id) throws SchedulerException;
}
