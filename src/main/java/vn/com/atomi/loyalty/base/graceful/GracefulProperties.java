package vn.com.atomi.loyalty.base.graceful;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author haidv
 * @version 1.0
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "custom.properties.graceful.shutdown")
public class GracefulProperties {

  private Duration timeout = Duration.ZERO;

  private Duration wait = Duration.ZERO;
}
