package vn.com.atomi.loyalty.base.security;

import io.jsonwebtoken.*;
import java.util.Date;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.com.atomi.loyalty.base.exception.BaseException;
import vn.com.atomi.loyalty.base.exception.CommonErrorCode;

@Component
public class TokenProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(TokenProvider.class);

  @Value("${custom.properties.private.key}")
  private String secretKey;

  public String issuerToken(String username, String sessionId, Date expiration) {
    var claims = Jwts.claims();
    return Jwts.builder()
        .setIssuer(username)
        .setSubject(sessionId)
        .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
        .setExpiration(expiration)
        .addClaims(claims)
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .setId(UUID.randomUUID().toString())
        .compact();
  }

  public Claims getClaimsFromToken(String token) {
    try {
      return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    } catch (MalformedJwtException
        | UnsupportedJwtException
        | IllegalArgumentException
        | SignatureException ex) {
      LOGGER.error("Token invalid", ex);
      throw new BaseException(CommonErrorCode.ACCESS_TOKEN_INVALID);
    } catch (ExpiredJwtException e) {
      LOGGER.error("Token expired", e);
      throw new BaseException(CommonErrorCode.ACCESS_TOKEN_EXPIRED);
    }
  }
}
