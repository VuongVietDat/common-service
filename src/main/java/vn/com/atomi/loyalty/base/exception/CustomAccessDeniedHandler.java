package vn.com.atomi.loyalty.base.exception;

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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import vn.com.atomi.loyalty.base.data.ResponseUtils;
import vn.com.atomi.loyalty.base.logging.LoggingProperties;

/**
 * @author haidv
 * @version 1.0
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(AccessDeniedHandler.class);

  private final LoggingProperties loggingProperties;

  @Autowired
  public CustomAccessDeniedHandler(LoggingProperties loggingProperties) {
    this.loggingProperties = loggingProperties;
  }

  @Override
  public void handle(
      HttpServletRequest request, HttpServletResponse response, AccessDeniedException exc)
      throws IOException {
    logRequest(request, loggingProperties);
    var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null) {
      LOGGER.warn(
          String.format(
              "User: %s attempted to access the protected URL: %s",
              auth.getName(), request.getRequestURI()));
    }
    var errorCode = CommonErrorCode.FORBIDDEN;
    response.setCharacterEncoding("UTF-8");
    response.setStatus(errorCode.getHttpStatus().value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response
        .getWriter()
        .write(
            toJson(
                ResponseUtils.getResponseDataError(
                    errorCode.getCode(), errorCode.getMessage(), null)));
    logResponse(request, response, loggingProperties);
  }
}
