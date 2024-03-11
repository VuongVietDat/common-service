package vn.com.atomi.loyalty.base.exception;

import static vn.com.atomi.loyalty.base.exception.CommonErrorCode.FORBIDDEN;
import static vn.com.atomi.loyalty.base.logging.LoggingUtil.logRequest;
import static vn.com.atomi.loyalty.base.logging.LoggingUtil.logResponse;
import static vn.com.atomi.loyalty.base.utils.JsonUtils.toJson;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import vn.com.atomi.loyalty.base.data.ResponseUtils;
import vn.com.atomi.loyalty.base.logging.LoggingProperties;

/**
 * @author haidv
 * @version 1.0
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationEntryPoint.class);

  private final LoggingProperties loggingProperties;

  @Autowired
  public CustomAuthenticationEntryPoint(LoggingProperties loggingProperties) {
    this.loggingProperties = loggingProperties;
  }

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {
    logRequest(request, loggingProperties);
    LOGGER.warn("You need to login first in order to perform this action.");
    var errorCode = FORBIDDEN;
    response.setStatus(errorCode.getHttpStatus().value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");
    response
        .getWriter()
        .print(
            toJson(
                ResponseUtils.getResponseDataError(
                    errorCode.getCode(), errorCode.getMessage(), null)));
    logResponse(request, response, loggingProperties);
  }
}
