package vn.com.atomi.loyalty.base.redis;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author haidv
 * @version 1.0
 */
@Setter
@Getter
@NoArgsConstructor
public class TokenBlackList implements Serializable {
  private String sessionId;
  private Long expirationSeconds;
  private LocalDateTime expiration;

  public TokenBlackList(String sessionId, LocalDateTime expiration) {
    this.sessionId = sessionId;
    this.expirationSeconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), expiration);
    this.expiration = expiration;
  }
}
