package vn.com.atomi.loyalty.base.exception;

import org.springframework.http.HttpStatus;

/**
 * @author haidv
 * @version 1.0
 */
public enum CommonErrorCode implements AbstractError {
  BAD_REQUEST(
      1, "Máy chủ không thể hoặc sẽ không xử lý yêu cầu do lỗi máy khách.", HttpStatus.BAD_REQUEST),
  UNAUTHORIZED(
      2, "Đã xảy ra lỗi trong quá trình xác thực, yêu cầu đăng nhập lại.", HttpStatus.UNAUTHORIZED),
  FORBIDDEN(3, "Bạn không có quyền thực hiện thao tác này.", HttpStatus.FORBIDDEN),
  NOT_FOUND(4, "Không tìm thấy trình xử lý nào cho %s %s.", HttpStatus.NOT_FOUND),
  METHOD_NOT_ALLOWED(
      5,
      "Phương thức yêu cầu được máy chủ biết nhưng không được tài nguyên đích hỗ trợ.",
      HttpStatus.METHOD_NOT_ALLOWED),
  DATA_INTEGRITY_VIOLATION(
      6,
      "Việc cố gắng chèn hoặc cập nhật dữ liệu sẽ vi phạm ràng buộc về tính toàn vẹn.",
      HttpStatus.CONFLICT),
  MISSING_REQUEST_PARAMETER(7, "Tham số %s bắt buộc '%s' không tồn tại.", HttpStatus.BAD_REQUEST),
  UNSUPPORTED_MEDIA_TYPE(
      8,
      "Máy chủ từ chối chấp nhận yêu cầu vì định dạng payload không được hỗ trợ.",
      HttpStatus.UNSUPPORTED_MEDIA_TYPE),
  MEDIA_TYPE_NOT_ACCEPTABLE(
      9,
      "Trình xử lý yêu cầu không thể tạo ra phản hồi được máy khách chấp nhận.",
      HttpStatus.UNSUPPORTED_MEDIA_TYPE),
  ARGUMENT_TYPE_MISMATCH(10, "Loại thông số bắt buộc %s '%s' không khớp", HttpStatus.BAD_REQUEST),
  ARGUMENT_NOT_VALID(11, "Lỗi nhập liệu", HttpStatus.BAD_REQUEST),
  ENTITY_NOT_FOUND(12, "Đối tượng không còn tồn tại trong cơ sở dữ liệu.", HttpStatus.NOT_FOUND),
  INTERNAL_SERVER_ERROR(
      13,
      "Đã xảy ra lỗi khi xử lý yêu cầu, vui lòng liên hệ với quản trị viên.",
      HttpStatus.INTERNAL_SERVER_ERROR),
  ACCESS_TOKEN_INVALID(
      14,
      "Mã thông báo truy cập không hợp lệ, hãy tạo mã thông báo truy cập mới hoặc đăng nhập lại.",
      HttpStatus.UNAUTHORIZED),
  ACCESS_TOKEN_EXPIRED(
      15,
      "Mã thông báo truy cập đã hết hạn, hãy tạo mã thông báo truy cập mới hoặc đăng nhập lại.",
      HttpStatus.UNAUTHORIZED),
  REFRESH_TOKEN_INVALID(
      16, "Mã thông báo làm mới không hợp lệ, yêu cầu đăng nhập lại.", HttpStatus.UNAUTHORIZED),
  REFRESH_TOKEN_EXPIRED(
      17, "Mã thông báo làm mới đã hết hạn, yêu cầu đăng nhập lại.", HttpStatus.UNAUTHORIZED),
  EXECUTE_THIRTY_SERVICE_ERROR(
      18, "Đã xảy ra lỗi khi thực thi api của bên thứ 3.", HttpStatus.INTERNAL_SERVER_ERROR),
  ;

  private final int code;

  private final String message;

  private final HttpStatus httpStatus;

  CommonErrorCode(int code, String message, HttpStatus httpStatus) {
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
