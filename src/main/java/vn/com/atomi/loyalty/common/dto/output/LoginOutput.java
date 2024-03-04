package vn.com.atomi.loyalty.common.dto.output;

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

  private String refreshToken;

  private Long accessExpireIn;

  private Long refreshExpireIn;
}
