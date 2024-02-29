package vn.com.atomi.loyalty.base.feign;

import static feign.Util.UTF_8;
import static feign.Util.decodeOrDefault;

import feign.Logger;
import feign.Request;
import feign.Response;
import feign.Util;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author haidv
 * @version 1.0
 */
@Slf4j
public class FeignLogging extends Logger {

  @Override
  protected void logRequest(String configKey, Level logLevel, Request request) {

    int bodyLength = 0;
    String bodyText = null;
    if (request.body() != null) {
      bodyLength = request.body().length;
      bodyText = request.charset() != null ? new String(request.body(), request.charset()) : null;
    }
    log(
        configKey,
        "---> %s %s %s %s (%s-byte body) %s",
        request.httpMethod().name(),
        request.url(),
        resolveProtocolVersion(request.protocolVersion()),
        request.headers(),
        bodyLength,
        bodyText != null ? bodyText : "Binary data");
  }

  @Override
  protected Response logAndRebufferResponse(
      String configKey, Level logLevel, Response response, long elapsedTime) throws IOException {
    int status = response.status();
    int bodyLength = 0;
    if (response.body() != null && !(status == 204 || status == 205)) {
      // HTTP 204 No Content "...response MUST NOT include a message-body"
      // HTTP 205 Reset Content "...response MUST NOT include an entity"
      byte[] bodyData = Util.toByteArray(response.body().asInputStream());
      bodyLength = bodyData.length;
      if (bodyLength > 0) {
        log(
            configKey,
            "<--- %s %s%s (%sms) %s (%s-byte body) %s",
            resolveProtocolVersion(response.protocolVersion()),
            status,
            response.reason() != null && logLevel.compareTo(Level.NONE) > 0
                ? " " + response.reason()
                : "",
            elapsedTime,
            response.headers(),
            bodyLength,
            decodeOrDefault(bodyData, UTF_8, "Binary data"));
      }
      return response.toBuilder().body(bodyData).build();
    } else {
      log(
          configKey,
          "<--- %s %s%s (%sms) %s (%s-byte body) %s",
          resolveProtocolVersion(response.protocolVersion()),
          status,
          response.reason() != null && logLevel.compareTo(Level.NONE) > 0
              ? " " + response.reason()
              : "",
          elapsedTime,
          response.headers(),
          bodyLength);
    }
    return response;
  }

  @Override
  protected void log(String configKey, String format, Object... args) {
    log.info(format(configKey, format, args));
  }

  protected String format(String configKey, String format, Object... args) {
    return String.format(methodTag(configKey) + format, args);
  }
}
