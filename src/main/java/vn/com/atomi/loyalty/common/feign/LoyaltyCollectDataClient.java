package vn.com.atomi.loyalty.common.feign;

import org.springframework.cloud.openfeign.FeignClient;
import vn.com.atomi.loyalty.common.feign.fallback.LoyaltyCollectDataClientFallbackFactory;

/**
 * @author haidv
 * @version 1.0
 */
@FeignClient(
        name = "loyalty-collectdata-service",
        url = "${custom.properties.loyalty-collectdata-service-url}",
        fallbackFactory = LoyaltyCollectDataClientFallbackFactory.class)
public interface LoyaltyCollectDataClient {
}
