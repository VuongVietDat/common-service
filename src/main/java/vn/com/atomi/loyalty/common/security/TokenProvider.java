package vn.com.atomi.loyalty.common.security;

import io.jsonwebtoken.*;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.com.atomi.loyalty.base.exception.BaseException;
import vn.com.atomi.loyalty.base.exception.CommonErrorCode;

@Component
public class TokenProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(TokenProvider.class);

  @Value("${custom.properties.rsa.public.key}")
  private String secretKey;

  public Claims getClaimsFromRSAToken(String token) {
    try {
      return Jwts.parser().verifyWith(getPublicKey()).build().parseSignedClaims(token).getPayload();
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

  private PublicKey getPublicKey() throws BaseException {
    try {
      java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
      var encoded = Base64.getDecoder().decode(secretKey);
      var keyFactory = KeyFactory.getInstance("RSA");
      var keySpec = new X509EncodedKeySpec(encoded);
      return keyFactory.generatePublic(keySpec);
    } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
      LOGGER.error(e.getMessage(), e);
      throw new BaseException(CommonErrorCode.INTERNAL_SERVER_ERROR);
    }
  }
}
