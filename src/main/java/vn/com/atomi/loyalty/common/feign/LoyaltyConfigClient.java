package vn.com.atomi.loyalty.common.feign;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import vn.com.atomi.loyalty.base.constant.RequestConstant;
import vn.com.atomi.loyalty.base.data.ResponseData;
import vn.com.atomi.loyalty.common.feign.fallback.LoyaltyConfigClientFallbackFactory;

/**
 * @author haidv
 * @version 1.0
 */
@FeignClient(
    name = "loyalty-config-service",
    url = "${custom.properties.loyalty-config-service-url}",
    fallbackFactory = LoyaltyConfigClientFallbackFactory.class)
public interface LoyaltyConfigClient {

  @Operation(
      summary = "Api (nội bộ) tự động chuyển trạng thái hết hiệu lực quy tắc khi hết ngày kết thúc")
  @PutMapping("/internal/rules/automatically-expires")
  ResponseData<String> automaticallyExpiresRule(
      @RequestHeader(RequestConstant.REQUEST_ID) String requestId);
}
