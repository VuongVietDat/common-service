package vn.com.atomi.loyalty.common.security;

import io.jsonwebtoken.*;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.com.atomi.loyalty.base.exception.BaseException;
import vn.com.atomi.loyalty.base.exception.CommonErrorCode;
import vn.com.atomi.loyalty.common.utils.Utils;

@Component
public class TokenProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(TokenProvider.class);

  @Value("${custom.properties.rsa.private.key}")
  private String secretKey;

  public String issuerToken(String username, String sessionId, Date expiration) {
    var claims = Jwts.claims();
    return Jwts.builder()
        .setIssuer(username)
        .setSubject(sessionId)
        .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
        .setExpiration(expiration)
        .addClaims(claims)
        .signWith(SignatureAlgorithm.RS256, getPrivateKey())
        .setId(Utils.generateUniqueId())
        .compact();
  }

  public Claims getClaimsFromRSAToken(String token) {
    try {
      return Jwts.parser().setSigningKey(getPrivateKey()).parseClaimsJws(token).getBody();
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

  private PrivateKey getPrivateKey() throws BaseException {
    try {
      java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
      var encoded = Base64.getDecoder().decode(secretKey);
      var keyFactory = KeyFactory.getInstance("RSA");
      var keySpec = new PKCS8EncodedKeySpec(encoded);
      return keyFactory.generatePrivate(keySpec);
    } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
      LOGGER.error(e.getMessage(), e);
      throw new BaseException(CommonErrorCode.INTERNAL_SERVER_ERROR);
    }
  }
}
