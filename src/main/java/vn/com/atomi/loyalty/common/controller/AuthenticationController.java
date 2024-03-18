package vn.com.atomi.loyalty.common.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.com.atomi.loyalty.base.constant.RequestConstant;
import vn.com.atomi.loyalty.base.data.ResponseData;
import vn.com.atomi.loyalty.base.data.ResponseUtils;
import vn.com.atomi.loyalty.base.security.Authority;
import vn.com.atomi.loyalty.base.security.UserOutput;
import vn.com.atomi.loyalty.common.dto.input.LoginInput;
import vn.com.atomi.loyalty.common.dto.output.LoginOutput;
import vn.com.atomi.loyalty.common.service.AuthenticationService;

/**
 * @author haidv
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService authenticationService;

  @Operation(summary = "Tạo token xác thực")
  @PostMapping("/public/auth/token")
  public ResponseEntity<ResponseData<LoginOutput>> login(
      @RequestBody @Valid LoginInput loginInput) {
    return ResponseUtils.success(authenticationService.login(loginInput));
  }

  @Operation(summary = "Renew token xác thực")
  @PostMapping("/public/auth/renew-token")
  public ResponseEntity<ResponseData<LoginOutput>> renewToken(
      @Parameter(description = "refresh token") @RequestParam String token) {
    return ResponseUtils.success(authenticationService.renewToken(token));
  }

  @Operation(summary = "Đăng xuất")
  @DeleteMapping("/public/auth/token")
  public ResponseEntity<ResponseData<Void>> logout(
      @Parameter(description = "refresh token") @RequestParam String token,
      HttpServletRequest servletRequest) {
    authenticationService.logout(token);
    return ResponseUtils.success();
  }

  @Operation(summary = "Lấy thông tin người dùng hiện tại")
  @GetMapping("/auth/user")
  public ResponseEntity<ResponseData<UserOutput>> getUser() {
    return ResponseUtils.success(authenticationService.getUser());
  }

  @Operation(summary = "Api (nội bộ) lấy thông tin người dùng theo username")
  @PreAuthorize(Authority.ROLE_SYSTEM)
  @GetMapping("/internal/auth/user")
  public ResponseEntity<ResponseData<UserOutput>> getUser(
      @Parameter(
              description = "Chuỗi xác thực khi gọi api nội bộ",
              example = "eb6b9f6fb84a45d9c9b2ac5b2c5bac4f36606b13abcb9e2de01fa4f066968cd0")
          @RequestHeader(RequestConstant.SECURE_API_KEY)
          @SuppressWarnings("unused")
          String apiKey,
      @RequestParam String username) {
    return ResponseUtils.success(authenticationService.getUser(username));
  }
}
