package vn.com.atomi.loyalty.common.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.com.atomi.loyalty.base.constant.RequestConstant;
import vn.com.atomi.loyalty.base.data.BaseController;
import vn.com.atomi.loyalty.base.data.ResponseData;
import vn.com.atomi.loyalty.base.data.ResponseUtils;
import vn.com.atomi.loyalty.base.security.Authority;
import vn.com.atomi.loyalty.common.dto.output.DictionaryOutput;
import vn.com.atomi.loyalty.common.enums.Status;
import vn.com.atomi.loyalty.common.service.DictionaryService;

/**
 * @author haidv
 * @version 1.0
 */
@RequiredArgsConstructor
@RestController
public class DictionaryController extends BaseController {

  private final DictionaryService dictionaryService;

  @Operation(summary = "Api (nội bộ) lấy danh sách cấu hình đang hiệu lực")
  @PreAuthorize(Authority.ROLE_SYSTEM)
  @GetMapping("/internal/dictionaries")
  public ResponseEntity<ResponseData<List<DictionaryOutput>>> getDictionaries(
      @Parameter(
              description = "Chuỗi xác thực khi gọi api nội bộ",
              example = "eb6b9f6fb84a45d9c9b2ac5b2c5bac4f36606b13abcb9e2de01fa4f066968cd0")
          @RequestHeader(RequestConstant.SECURE_API_KEY)
          @SuppressWarnings("unused")
          String apiKey,
      @RequestParam(required = false) String type,
      @RequestParam(required = false) Status status,
      @RequestParam(required = false, defaultValue = "true") Boolean isSubLeaf) {
    return ResponseUtils.success(dictionaryService.getDictionaries(type, status, isSubLeaf));
  }

  @Operation(summary = "Api lấy danh sách tất cả cấu hình đang hiệu lực")
  @GetMapping("/all-dictionaries")
  public ResponseEntity<ResponseData<List<DictionaryOutput>>> getAllDictionaries(
      @Parameter(description = "Loại cấu hình", example = "RULE_TYPE")
          @RequestParam(required = false)
          String type,
      @RequestParam(required = false) Status status,
      @RequestParam(required = false, defaultValue = "true") Boolean isSubLeaf) {
    return ResponseUtils.success(dictionaryService.getDictionaries(type, status, isSubLeaf));
  }
}
