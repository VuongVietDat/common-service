package vn.com.atomi.loyalty.common.service.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import vn.com.atomi.loyalty.common.feign.LoyaltyCollectDataClient;
import vn.com.atomi.loyalty.common.feign.LoyaltyConfigClient;
import vn.com.atomi.loyalty.common.feign.LoyaltyCoreClient;
import vn.com.atomi.loyalty.common.utils.Utils;

@Slf4j
@Component
@RequiredArgsConstructor
public class HandlePointCASAJob extends QuartzJobBean {

    protected static final Logger LOGGER = LoggerFactory.getLogger(HandlePointExpirationJob.class);

    private final LoyaltyCollectDataClient loyaltyCollectDataClient;

    private final LoyaltyConfigClient loyaltyConfigClient;

    private final LoyaltyCoreClient loyaltyCoreClient;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        String executeId = Utils.generateUniqueId();

    }
}
