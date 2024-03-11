package vn.com.atomi.loyalty.base.data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import vn.com.atomi.loyalty.base.constant.DateConstant;
import vn.com.atomi.loyalty.base.utils.RequestUtils;

/**
 * @author haidv
 * @version 1.0
 */
@ToString
@Getter
public class ResponseData<T> implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  private final String timestamp;

  private final String service;

  private final String sessionId;

  private final String requestId;

  private int code;

  private String message;

  @Setter private T data;

  public ResponseData() {
    this.code = 0;
    this.timestamp =
        LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern(DateConstant.STR_PLAN_DD_MM_YYYY_HH_MM_SS_STROKE));
    this.message = "Successful!";
    this.service = RequestUtils.extractServiceName();
    this.requestId = RequestUtils.extractRequestId();
    this.sessionId = RequestUtils.extractSessionId();
  }

  public ResponseData<T> success(T data) {
    this.data = data;
    return this;
  }

  public ResponseData<T> error(int code, String message) {
    this.code = code;
    this.message = message;
    return this;
  }

  public ResponseData<T> error(int code, String message, T data) {
    this.data = data;
    this.code = code;
    this.message = message;
    return this;
  }
}
