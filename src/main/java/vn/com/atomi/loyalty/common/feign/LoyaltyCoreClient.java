package vn.com.atomi.loyalty.common.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import vn.com.atomi.loyalty.base.constant.RequestConstant;
import vn.com.atomi.loyalty.base.data.ResponseData;
import vn.com.atomi.loyalty.common.dto.input.LoginInput;
import vn.com.atomi.loyalty.common.feign.fallback.LoyaltyCoreClientFallbackFactory;

/**
 * @author haidv
 * @version 1.0
 */
@FeignClient(
    name = "loyalty-core-service",
    url = "${custom.properties.loyalty-core-service-url}",
    fallbackFactory = LoyaltyCoreClientFallbackFactory.class)
public interface LoyaltyCoreClient {

  @PostMapping("/public/example-success")
  ResponseData<String> exampleSuccess(
      @RequestHeader(RequestConstant.REQUEST_ID) String requestId,
      @RequestParam String query,
      @RequestBody LoginInput loginInput);

  @GetMapping("/public/example-error")
  ResponseData<String> exampleError(@RequestHeader(RequestConstant.REQUEST_ID) String requestId);

  @GetMapping("/public/example-error-fallback")
  ResponseData<String> exampleErrorFallBack(
      @RequestHeader(RequestConstant.REQUEST_ID) String requestId);
}
