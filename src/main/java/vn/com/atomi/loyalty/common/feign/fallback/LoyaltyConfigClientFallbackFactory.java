package vn.com.atomi.loyalty.common.feign.fallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import vn.com.atomi.loyalty.base.data.ResponseData;
import vn.com.atomi.loyalty.base.exception.BaseException;
import vn.com.atomi.loyalty.base.exception.CommonErrorCode;
import vn.com.atomi.loyalty.common.dto.output.RulePOCOutput;
import vn.com.atomi.loyalty.common.dto.output.SourceDataMapOutput;
import vn.com.atomi.loyalty.common.enums.SourceGroup;
import vn.com.atomi.loyalty.common.feign.LoyaltyConfigClient;

import java.util.List;

/**
 * @author haidv
 * @version 1.0
 */
@Component
public class LoyaltyConfigClientFallbackFactory implements FallbackFactory<LoyaltyConfigClient> {

  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  @Override
  public LoyaltyConfigClient create(Throwable cause) {
    LOGGER.error("An exception occurred when calling the LoyaltyCoreClient", cause);
    return new LoyaltyConfigClient() {
      @Override
      public ResponseData<String> automaticallyExpiresRule(String requestId) {
        throw new BaseException(CommonErrorCode.EXECUTE_THIRTY_SERVICE_ERROR, cause);
      }

      @Override
      public ResponseData<List<SourceDataMapOutput>> getAllSourceDataMap(
          String requestId, SourceGroup sourceGroup) {
        throw new BaseException(CommonErrorCode.EXECUTE_THIRTY_SERVICE_ERROR, cause);
      }
      @Override
      public ResponseData<RulePOCOutput> getRulePoc(String requestId, String type) {
        throw new BaseException(CommonErrorCode.EXECUTE_THIRTY_SERVICE_ERROR, cause);
      }
    };
  }
}
