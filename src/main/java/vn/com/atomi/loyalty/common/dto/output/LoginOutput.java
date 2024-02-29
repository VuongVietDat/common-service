package vn.com.atomi.loyalty.common.dto.output;

import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * @author haidv
 * @version 1.0
 */
@Setter
@Getter
public class LoginOutput {

  private String accessToken;

  private Long expiresIn;

  private ZonedDateTime expiredAt;
}
