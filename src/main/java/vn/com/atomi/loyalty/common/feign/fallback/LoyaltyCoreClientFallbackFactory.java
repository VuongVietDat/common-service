package vn.com.atomi.loyalty.common.feign.fallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import vn.com.atomi.loyalty.base.data.ResponseData;
import vn.com.atomi.loyalty.base.exception.BaseException;
import vn.com.atomi.loyalty.base.exception.CommonErrorCode;
import vn.com.atomi.loyalty.common.feign.LoyaltyCoreClient;

/**
 * @author haidv
 * @version 1.0
 */
@Component
public class LoyaltyCoreClientFallbackFactory implements FallbackFactory<LoyaltyCoreClient> {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    public LoyaltyCoreClient create(Throwable cause) {
        LOGGER.error("An exception occurred when calling the LoyaltyCoreClient", cause);
        return new LoyaltyCoreClient() {
            @Override
            public ResponseData<String> executePointExpiration(String requestId) {
                throw new BaseException(CommonErrorCode.EXECUTE_THIRTY_SERVICE_ERROR, cause);
            }

            @Override
            public ResponseData<String> calculatePointCasa(String requestId) {
                throw new BaseException(CommonErrorCode.EXECUTE_THIRTY_SERVICE_ERROR, cause);
            }

            @Override
            public ResponseData<String> calculatePointCurrencyTransaction(String requestId, String startDate, String endDate) {
                throw new BaseException(CommonErrorCode.EXECUTE_THIRTY_SERVICE_ERROR, cause);
            }
        };
    }
}
