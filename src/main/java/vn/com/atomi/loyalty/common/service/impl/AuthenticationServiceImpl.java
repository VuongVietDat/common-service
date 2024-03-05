package vn.com.atomi.loyalty.common.service.impl;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.com.atomi.loyalty.base.data.BaseService;
import vn.com.atomi.loyalty.base.exception.BaseException;
import vn.com.atomi.loyalty.base.exception.CommonErrorCode;
import vn.com.atomi.loyalty.base.redis.TokenBlackList;
import vn.com.atomi.loyalty.base.redis.TokenBlackListRepository;
import vn.com.atomi.loyalty.base.security.TokenProvider;
import vn.com.atomi.loyalty.common.dto.input.LoginInput;
import vn.com.atomi.loyalty.common.dto.output.LoginOutput;
import vn.com.atomi.loyalty.common.repository.UserRepository;
import vn.com.atomi.loyalty.common.service.AuthenticationService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl extends BaseService implements AuthenticationService {
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  private final UserRepository userRepository;
  private final RedissonClient redisson;

  private final TokenProvider tokenProvider;

  private final TokenBlackListRepository tokenBlackListRepository;

  @Value("${custom.properties.security.system.setting.brute-force-detection}")
  private boolean bruteForceDetection;

  @Value("${custom.properties.security.system.setting.max-login-failed}")
  private int maxLoginFailed;

  @Value("${custom.properties.security.session-lifespan}")
  private Duration sessionLifespan;

  @Value("${custom.properties.security.token-lifespan}")
  private Duration tokenLifespan;

  private RMapCache<Long, Integer> redisAuthFailureCount;

  @PostConstruct
  void onPost() {
    redisAuthFailureCount = redisson.getMapCache("Auth_Failure_Count");
  }

  @Transactional
  @Override
  public LoginOutput renewToken(String refreshToken) {
    LoginOutput output = new LoginOutput();
    var startAt = LocalDateTime.now();
    var tokenExpired = startAt.plusSeconds(tokenLifespan.getSeconds());
    output.setAccessToken(
        tokenProvider.issuerToken(
            "admin",
            refreshToken,
            Date.from(tokenExpired.atZone(ZoneId.systemDefault()).toInstant())));
    output.setAccessExpireIn(tokenLifespan.getSeconds());
    return output;
  }

  @Transactional
  @Override
  public LoginOutput login(LoginInput input) {
    val user = userRepository.findByUsernameAndDeletedFalse(input.getUsername());
    if (user == null) throw new BaseException(CommonErrorCode.USER_NOT_EXIST);

    Integer count = null;
    if (bruteForceDetection) {
      count = redisAuthFailureCount.get(user.getId());
      if (count != null && count >= maxLoginFailed) {
        throw new BaseException(CommonErrorCode.USER_LOCKED);
      }
    }

    if (!encoder.matches(input.getPassword(), user.getPassword())) {
      if (bruteForceDetection)
        redisAuthFailureCount.put(user.getId(), count == null ? 1 : count + 1);
      throw new BaseException(CommonErrorCode.PASSWORD_INCORRECT);
    }

    LoginOutput output = new LoginOutput();
    var startAt = LocalDateTime.now();
    var tokenExpired = startAt.plusSeconds(tokenLifespan.getSeconds());
    var sessionId = UUID.randomUUID().toString();
    output.setAccessToken(
        tokenProvider.issuerToken(
            input.getUsername(),
            sessionId,
            Date.from(tokenExpired.atZone(ZoneId.systemDefault()).toInstant())));
    output.setAccessExpireIn(tokenLifespan.getSeconds());
    output.setRefreshToken(sessionId);
    output.setRefreshExpireIn(sessionLifespan.getSeconds());
    return output;
  }

  @Override
  public void logout(String refreshToken) {
    tokenBlackListRepository.put(
        new TokenBlackList(
            refreshToken, LocalDateTime.now().plusSeconds(sessionLifespan.getSeconds())));
  }
}
