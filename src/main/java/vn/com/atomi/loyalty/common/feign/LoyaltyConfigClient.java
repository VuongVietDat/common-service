package vn.com.atomi.loyalty.common.feign;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import vn.com.atomi.loyalty.base.constant.RequestConstant;
import vn.com.atomi.loyalty.base.data.ResponseData;
import vn.com.atomi.loyalty.common.dto.output.RulePOCOutput;
import vn.com.atomi.loyalty.common.dto.output.SourceDataMapOutput;
import vn.com.atomi.loyalty.common.enums.SourceGroup;
import vn.com.atomi.loyalty.common.feign.fallback.LoyaltyConfigClientFallbackFactory;

import java.util.List;

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

  @Operation(summary = "Api (nội bộ) lấy cấu hình chuyển data nguồn thành loyalty data")
  @GetMapping("/internal/source-data-map-all")
  ResponseData<List<SourceDataMapOutput>> getAllSourceDataMap(
      @RequestHeader(RequestConstant.REQUEST_ID) String requestId,
      @RequestParam SourceGroup sourceGroup);

  @Operation(summary = "Api (nội bộ) lấy tất cả quy tắc theo kịch bản phục vụ POC")
  @GetMapping("/internal/rules/poc")
  ResponseData<RulePOCOutput> getRulePoc(
          @RequestHeader(RequestConstant.REQUEST_ID) String requestId,
          @RequestParam String type);

}
