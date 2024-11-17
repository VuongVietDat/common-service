package vn.com.atomi.loyalty.common.feign;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import vn.com.atomi.loyalty.base.constant.RequestConstant;
import vn.com.atomi.loyalty.base.data.ResponseData;
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

    @Operation(summary = "Api (nội bộ) thực hiện hết hạn điểm")
    @PostMapping("/internal/points-expiration")
    ResponseData<String> executePointExpiration(
            @RequestHeader(RequestConstant.REQUEST_ID) String requestId);

    @Operation(summary = "Api (nội bộ) thực hiện tính điểm dựa vào số dư CASA bình quân")
    @PostMapping("/internal/customers/points/casa")
    ResponseData<String> calculatePointCasa(
            @RequestHeader(RequestConstant.REQUEST_ID) String requestId);

    @Operation(summary = "Api (nội bộ) thực hiện tính điểm cho giao dịch mua bán ngoại tệ tại quầy")
    @PostMapping("/internal/customers/points/currencyTransaction")
    ResponseData<String> calculatePointCurrencyTransaction(
            @RequestHeader(RequestConstant.REQUEST_ID) String requestId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate);
}
