package vn.com.atomi.loyalty.base.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.com.atomi.loyalty.base.constant.DateConstant;
import vn.com.atomi.loyalty.base.constant.RequestConstant;
import vn.com.atomi.loyalty.base.utils.Snowflake;

/**
 * @author haidv
 * @version 1.0
 */
@Component
public class CustomFeignRequestInterceptorConfiguration implements RequestInterceptor {

  @Value("${spring.application.name}")
  private String serviceName;

  @Override
  public void apply(RequestTemplate template) {
    String requestID;
    if (template.headers().get(RequestConstant.REQUEST_ID).isEmpty()) {
      requestID = String.valueOf(Snowflake.getInstance().nextId());
      template.header(RequestConstant.REQUEST_ID, requestID);
    } else {
      requestID = (String) template.headers().get(RequestConstant.REQUEST_ID).toArray()[0];
    }
    ThreadContext.put(RequestConstant.REQUEST_ID, requestID);
    try {
      template.header(RequestConstant.CLIENT_IP, InetAddress.getLocalHost().getHostAddress());
    } catch (UnknownHostException e) {
      template.header(RequestConstant.CLIENT_IP, "127.0.0.1");
    }
    template.header(
        RequestConstant.CLIENT_TIME,
        LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern(DateConstant.STR_PLAN_YYYY_MM_DD_HH_MM_SS_SSS)));
    template.header(RequestConstant.CLIENT_PLATFORM, serviceName);
  }
}
