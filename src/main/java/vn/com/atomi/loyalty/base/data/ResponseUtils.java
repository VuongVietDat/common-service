package vn.com.atomi.loyalty.base.data;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author haidv
 * @version 1.0
 */
public class ResponseUtils {

  private ResponseUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static <T> ResponseEntity<ResponseData<T>> success() {
    return success(null);
  }

  public static <T> ResponseEntity<ResponseData<T>> success(T o) {
    return ResponseEntity.ok(new ResponseData<T>().success(o));
  }

  public static <T> ResponseEntity<ResponseData<T>> created() {
    return created(null);
  }

  public static <T> ResponseEntity<ResponseData<T>> created(T o) {
    return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseData<T>().success(o));
  }

  public static <T> ResponseEntity<ResponseData<T>> error(
      int code, String message, HttpStatus status) {
    return error(code, message, null, status);
  }

  public static <T> ResponseEntity<ResponseData<T>> error(
      int code, String message, T data, HttpStatus status) {
    return ResponseEntity.status(status).body(getResponseDataError(code, message, data));
  }

  public static <T> ResponseData<T> getResponseDataError(int code, String message, T data) {
    return new ResponseData<T>().error(code, message, data);
  }
}
