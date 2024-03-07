package vn.com.atomi.loyalty.common.enums;

import org.springframework.http.HttpStatus;
import vn.com.atomi.loyalty.base.exception.AbstractError;

/**
 * @author haidv
 * @version 1.0
 */
public enum ErrorCode implements AbstractError {
  JOB_NOT_EXISTED(1, "Không tìm thấy tiến trình với id = %s", HttpStatus.BAD_REQUEST),
  USER_LOCKED(1002, "User is locked because over 5 time login failure", HttpStatus.UNAUTHORIZED),
  USER_NOT_EXIST(1003, "User is not exist", HttpStatus.UNAUTHORIZED),
  PASSWORD_INCORRECT(1004, "Wrong password", HttpStatus.UNAUTHORIZED),
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
