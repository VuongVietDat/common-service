package vn.com.atomi.loyalty.common.feign.fallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import vn.com.atomi.loyalty.base.data.ResponseData;
import vn.com.atomi.loyalty.common.dto.output.CustomerCasa;
import vn.com.atomi.loyalty.common.feign.LoyaltyCollectDataClient;

import java.util.ArrayList;
import java.util.List;

/**
 * @author haidv
 * @version 1.0
 */
@Component
public class LoyaltyCollectDataClientFallbackFactory implements FallbackFactory<LoyaltyCollectDataClient> {

  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  @Override
  public LoyaltyCollectDataClient create(Throwable cause) {
    LOGGER.error("An exception occurred when calling the LoyaltyCollectDataClient", cause);
    return new LoyaltyCollectDataClient() {
      @Override
      public ResponseData<List<CustomerCasa>> getLstCurrentCasa(String requestId) {
        LOGGER.info("getLstCurrentCasa: set default empty array");
        return new ResponseData<List<CustomerCasa>>().success(new ArrayList<>());
      }
    };
  }
}
