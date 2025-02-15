package vn.com.atomi.loyalty.common.enums;

import org.springframework.http.HttpStatus;
import vn.com.atomi.loyalty.base.exception.AbstractError;

/**
 * @author haidv
 * @version 1.0
 */
public enum ErrorCode implements AbstractError {
  JOB_NOT_EXISTED(2000, "Không tìm thấy tiến trình với id = %s", HttpStatus.BAD_REQUEST),
  USER_LOCKED(
      2001,
      "Tài khoản của bạn đã bị khóa do nhập sai quá 5 lần liên tiếp."
          + " Vui lòng thử lại sau %d phút hoặc liên hệ với quản trị viên hệ thống.",
      HttpStatus.UNAUTHORIZED),
  USER_NOT_EXIST(2002, "Người dùng không tồn tại", HttpStatus.UNAUTHORIZED),
  USER_INCORRECT(
      2003,
      "Tài khoản bạn nhập không tồn tại. Bạn vui lòng kiểm tra lại thông tin đăng nhập.",
      HttpStatus.UNAUTHORIZED),
  PASS_INCORRECT(
      2004,
      "Sai tên đăng nhập/ mật khẩu quá 5 lần sẽ bị khóa. Bạn còn %d lần thử lại.",
      HttpStatus.UNAUTHORIZED),
  ;

  private final int code;

  private final String message;

  private final HttpStatus httpStatus;

  ErrorCode(int code, String message, HttpStatus httpStatus) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }

  @Override
  public int getCode() {
    return code;
  }

  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public HttpStatus getHttpStatus() {
    return httpStatus;
  }
}
