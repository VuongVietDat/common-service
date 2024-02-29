package vn.com.atomi.loyalty.base.logging;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import vn.com.atomi.loyalty.base.constant.RequestConstant;

/**
 * Configuration properties for the logging abstraction.
 *
 * @author haidv
 * @version 1.0
 */
@Setter
@ConfigurationProperties(prefix = "custom.properties.logging")
public class LoggingProperties {

  @Getter private int requestMaxPayloadLength = 10000;

  @Getter private int responseMaxPayloadLength = 1000;

  @Getter private boolean defaultIgnoreLogUri = true;

  private Set<String> ignoreLogUri = new HashSet<>();

  @Getter private boolean excludeResponseBody = true;

  public Set<String> getIgnoreLogUri() {
    if (defaultIgnoreLogUri) {
      ignoreLogUri.addAll(RequestConstant.getWhiteListRequest());
    }
    return ignoreLogUri;
  }
}
