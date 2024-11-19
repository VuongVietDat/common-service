package vn.com.atomi.loyalty.common.service.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
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
import vn.com.atomi.loyalty.common.feign.LoyaltyCoreClient;
import vn.com.atomi.loyalty.common.repository.ScheduleLogRepository;
import vn.com.atomi.loyalty.common.repository.ScheduleRepository;
import vn.com.atomi.loyalty.common.utils.Utils;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class HandlePointCardJob extends QuartzJobBean {

    protected static final Logger LOGGER = LoggerFactory.getLogger(HandlePointExpirationJob.class);

    private final ScheduleLogRepository scheduleLogRepository;

    private final ScheduleRepository scheduleRepository;

    private final LoyaltyCoreClient loyaltyCoreClient;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        String executeId = Utils.generateUniqueId();
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
            loyaltyCoreClient.calculatePointCard(executeId,"01/01/2024", "31/12/2024");
            status = StatusJob.SUCCESS;
        } catch (Exception e) {
            msg = e.getMessage();
            status = StatusJob.FAILED;
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (scheduleLog != null) {
                scheduleLogRepository.updateLogEndJob(
                        LocalDateTime.now(), msg, status, scheduleLog.getId());
            }
            scheduleRepository.updateLstExecuteAt(LocalDateTime.now(), scheduleInfo.getId());
        }
    }
}
