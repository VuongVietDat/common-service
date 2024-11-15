package vn.com.atomi.loyalty.common.feign;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import vn.com.atomi.loyalty.base.constant.RequestConstant;
import vn.com.atomi.loyalty.base.data.ResponseData;
import vn.com.atomi.loyalty.common.dto.output.CustomerCasa;
import vn.com.atomi.loyalty.common.feign.fallback.LoyaltyCollectDataClientFallbackFactory;

import java.util.List;

/**
 * @author haidv
 * @version 1.0
 */
@FeignClient(
    name = "loyalty-collectdata-service",
    url = "${custom.properties.loyalty-collectdata-service-url}",
    fallbackFactory = LoyaltyCollectDataClientFallbackFactory.class)
public interface LoyaltyCollectDataClient {

  @Operation(summary = "Api (nội bộ) lấy danh sach số dư casa hiện tại cua khách hàng")
  @GetMapping("/internal/customers/casa")
  ResponseData<List<CustomerCasa>> getLstCurrentCasa( @RequestHeader(RequestConstant.REQUEST_ID) String requestId);

}
