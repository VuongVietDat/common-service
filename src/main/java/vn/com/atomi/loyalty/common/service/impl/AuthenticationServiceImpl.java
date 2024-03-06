package vn.com.atomi.loyalty.common.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.com.atomi.loyalty.base.constant.RequestConstant;
import vn.com.atomi.loyalty.base.data.BaseService;
import vn.com.atomi.loyalty.base.exception.BaseException;
import vn.com.atomi.loyalty.base.exception.CommonErrorCode;
import vn.com.atomi.loyalty.base.redis.TokenBlackList;
import vn.com.atomi.loyalty.base.redis.TokenBlackListRepository;
import vn.com.atomi.loyalty.base.security.TokenProvider;
import vn.com.atomi.loyalty.base.utils.RequestUtils;
import vn.com.atomi.loyalty.common.dto.input.LoginInput;
import vn.com.atomi.loyalty.common.dto.output.LoginOutput;
import vn.com.atomi.loyalty.common.entity.Session;
import vn.com.atomi.loyalty.common.entity.User;
import vn.com.atomi.loyalty.common.repository.SessionRepository;
import vn.com.atomi.loyalty.common.repository.UserRepository;
import vn.com.atomi.loyalty.common.repository.redis.LoginFailureCountRepository;
import vn.com.atomi.loyalty.common.service.AuthenticationService;
import vn.com.atomi.loyalty.common.utils.Utils;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl extends BaseService implements AuthenticationService {
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  private final UserRepository userRepository;
  private final SessionRepository sessionRepository;
  private final LoginFailureCountRepository redisAuthFailureCount;

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

  @Transactional
  @Override
  public LoginOutput renewToken(String refreshToken) {
    val session = validSession(refreshToken);
    val user = userRepository.findById(session.getUserId()).get();

    LoginOutput output = new LoginOutput();
    var startAt = LocalDateTime.now();
    var tokenExpired = startAt.plusSeconds(tokenLifespan.getSeconds());
    output.setAccessToken(
        tokenProvider.issuerToken(
            user.getUsername(),
            refreshToken,
            Date.from(tokenExpired.atZone(ZoneId.systemDefault()).toInstant())));
    output.setAccessExpireIn(tokenLifespan.getSeconds());
    return output;
  }

  @Transactional
  @Override
  public LoginOutput login(LoginInput input) {
    val user = validLogin(input);

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

    // save Session
    val builder =
        Session.builder()
            .userId(user.getId())
            .refreshToken(sessionId)
            .expire(LocalDateTime.now().plusSeconds(sessionLifespan.getSeconds()));
    Utils.getCurrentRequest()
        .ifPresent(
            request ->
                builder
                    .clientIp(RequestUtils.extractClientIpAddress(request))
                    .clientTime(request.getHeader(RequestConstant.CLIENT_TIME))
                    .clientPlatform(request.getHeader(RequestConstant.CLIENT_PLATFORM))
                    .deviceId(request.getHeader(RequestConstant.DEVICE_ID))
                    .deviceName(request.getHeader(RequestConstant.DEVICE_NAME))
                    .deviceType(request.getHeader(RequestConstant.DEVICE_TYPE))
                    .appVersion(request.getHeader(RequestConstant.APPLICATION_VERSION)));
    sessionRepository.save(builder.build());

    return output;
  }

  @Override
  public void logout(String refreshToken) {
    tokenBlackListRepository.put(
        new TokenBlackList(
            refreshToken, LocalDateTime.now().plusSeconds(sessionLifespan.getSeconds())));
  }

  private User validLogin(LoginInput input) {
    // Check user exist
    val user =
        userRepository
            .findByUsernameAndDeletedFalse(input.getUsername())
            .orElseThrow(() -> new BaseException(CommonErrorCode.USER_NOT_EXIST));

    // count login failure
    Integer count = null;
    if (bruteForceDetection) {
      val countOptional = redisAuthFailureCount.get(user.getId());
      if (countOptional.isPresent()) {
        count = countOptional.get();
        if (countOptional.get() >= maxLoginFailed) {
          throw new BaseException(CommonErrorCode.USER_LOCKED);
        }
      }
    }

    // check password
    if (!encoder.matches(input.getPassword(), user.getPassword())) {
      if (bruteForceDetection)
        redisAuthFailureCount.put(user.getId(), count == null ? 1 : count + 1);
      throw new BaseException(CommonErrorCode.PASSWORD_INCORRECT);
    }

    // login success clear counter
    redisAuthFailureCount.remove(user.getId());

    return user;
  }

  private Session validSession(String refreshToken) {
    // check logout
    if (tokenBlackListRepository.find(refreshToken).isPresent())
      throw new BaseException(CommonErrorCode.REFRESH_TOKEN_EXPIRED);

    // check session exist
    val session =
        sessionRepository
            .findByRefreshToken(refreshToken)
            .orElseThrow(() -> new BaseException(CommonErrorCode.REFRESH_TOKEN_INVALID));
    // check session expired
    if (LocalDateTime.now().isAfter(session.getExpire()))
      throw new BaseException(CommonErrorCode.REFRESH_TOKEN_EXPIRED);

    return session;
  }
}
