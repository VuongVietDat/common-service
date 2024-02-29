package vn.com.atomi.loyalty.base.logging;

import static vn.com.atomi.loyalty.base.logging.LoggingUtil.logRequest;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

/**
 * @author haidv
 * @version 1.0
 */
@ControllerAdvice
public class FilterRequestBodyAdviceAdapter extends RequestBodyAdviceAdapter {

  private final HttpServletRequest httpServletRequest;
  private final LoggingProperties loggingProperties;

  @Autowired
  public FilterRequestBodyAdviceAdapter(
      HttpServletRequest httpServletRequest, LoggingProperties loggingProperties) {
    this.httpServletRequest = httpServletRequest;
    this.loggingProperties = loggingProperties;
  }

  /**
   * Invoked first to determine if this interceptor applies.
   *
   * @param methodParameter the method parameter
   * @param targetType the target type, not necessarily the same as the method parameter type, e.g.
   *     for {@code HttpEntity<String>}.
   * @param converterType the selected converter type
   * @return whether this interceptor should be invoked or not
   */
  @Override
  public boolean supports(
      MethodParameter methodParameter,
      Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    return true;
  }

  /**
   * Invoked second before the request body is read and converted.
   *
   * @param inputMessage the request
   * @param parameter the target method parameter
   * @param targetType the target type, not necessarily the same as the method parameter type, e.g.
   *     for {@code HttpEntity<String>}.
   * @param converterType the converter used to deserialize the body
   * @return the input request or a new instance, never {@code null}
   */
  @Override
  public Object afterBodyRead(
      Object body,
      HttpInputMessage inputMessage,
      MethodParameter parameter,
      Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    logRequest(httpServletRequest, loggingProperties, body);
    return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
  }
}
