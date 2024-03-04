package vn.com.atomi.loyalty.base.logging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author haidv
 * @version 1.0
 */
@ControllerAdvice
public class LoggingResponseBodyAdviceAdapter implements ResponseBodyAdvice<Object> {

  private final LoggingProperties loggingProperties;

  @Autowired
  public LoggingResponseBodyAdviceAdapter(LoggingProperties loggingProperties) {
    this.loggingProperties = loggingProperties;
  }

  /**
   * Whether this component supports the given controller method return type and the selected {@code
   * HttpMessageConverter} type.
   *
   * @param returnType the return type
   * @param converterType the selected converter type
   * @return {@code true} if {@link #beforeBodyWrite} should be invoked; {@code false} otherwise
   */
  @Override
  public boolean supports(
      MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
    return true;
  }

  /**
   * Invoked after an {@code HttpMessageConverter} is selected and just before its write method is
   * invoked.
   *
   * @param body the body to be written
   * @param returnType the return type of the controller method
   * @param selectedContentType the content type selected through content negotiation
   * @param selectedConverterType the converter type selected to write to the response
   * @param request the current request
   * @param response the current response
   * @return the body that was passed in or a modified (possibly new) instance
   */
  @Override
  public Object beforeBodyWrite(
      Object body,
      MethodParameter returnType,
      MediaType selectedContentType,
      Class<? extends HttpMessageConverter<?>> selectedConverterType,
      ServerHttpRequest request,
      ServerHttpResponse response) {

    if (request instanceof ServletServerHttpRequest
        && response instanceof ServletServerHttpResponse) {
      LoggingUtil.logResponse(
          ((ServletServerHttpRequest) request).getServletRequest(),
          ((ServletServerHttpResponse) response).getServletResponse(),
          loggingProperties,
          body);
    }
    return body;
  }
}
