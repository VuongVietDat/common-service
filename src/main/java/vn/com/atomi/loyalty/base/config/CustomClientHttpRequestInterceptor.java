package vn.com.atomi.loyalty.base.config;

import static org.springframework.http.converter.StringHttpMessageConverter.DEFAULT_CHARSET;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMessage;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

/**
 * @author haidv
 * @version 1.0
 */
public class CustomClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(CustomClientHttpRequestInterceptor.class);

  @Override
  public ClientHttpResponse intercept(
      HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    traceRequest(request, body);
    var response = execution.execute(request, body);
    traceResponse(response, request);
    return response;
  }

  private void traceRequest(HttpRequest request, byte[] body) throws IOException {
    LOGGER.info(
        "RestTemplate request logging: URI: {} Method: {} Headers: {} Body: {}",
        request.getURI(),
        request.getMethod(),
        request.getHeaders(),
        new String(body, StandardCharsets.UTF_8));
  }

  private void traceResponse(ClientHttpResponse response, HttpRequest request) throws IOException {
    LOGGER.info(
        "RestTemplate response logging: URI: {} Method: {} Status code: {} Headers: {} Body: {}",
        request.getURI(),
        request.getMethod(),
        response.getStatusCode(),
        response.getHeaders(),
        new String(StreamUtils.copyToByteArray(response.getBody()), getCharset(response)));
  }

  private Charset getCharset(HttpMessage message) {
    return Optional.ofNullable(message.getHeaders().getContentType())
        .map(MediaType::getCharset)
        .orElse(DEFAULT_CHARSET);
  }
}
