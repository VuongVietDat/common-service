package vn.com.atomi.loyalty.common.service.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import vn.com.atomi.loyalty.base.utils.RequestUtils;
import vn.com.atomi.loyalty.common.dto.output.CustomerCasa;
import vn.com.atomi.loyalty.common.feign.LoyaltyCollectDataClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HandlePointCASAJob extends QuartzJobBean {

    protected static final Logger LOGGER = LoggerFactory.getLogger(HandlePointExpirationJob.class);

    private final LoyaltyCollectDataClient loyaltyCollectDataClient;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        List<CustomerCasa> customerCasas = loyaltyCollectDataClient.getLstCurrentCasa(RequestUtils.extractRequestId()).getData();
    }
}
