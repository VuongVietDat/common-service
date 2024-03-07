package vn.com.atomi.loyalty.common.service.impl;

import static vn.com.atomi.loyalty.common.enums.ErrorCode.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;
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
import vn.com.atomi.loyalty.common.dto.output.RoleOutput;
import vn.com.atomi.loyalty.common.dto.output.UserOutput;
import vn.com.atomi.loyalty.common.entity.Session;
import vn.com.atomi.loyalty.common.entity.User;
import vn.com.atomi.loyalty.common.entity.UserRole;
import vn.com.atomi.loyalty.common.repository.RoleRepository;
import vn.com.atomi.loyalty.common.repository.SessionRepository;
import vn.com.atomi.loyalty.common.repository.UserRepository;
import vn.com.atomi.loyalty.common.repository.UserRoleRepository;
import vn.com.atomi.loyalty.common.repository.redis.CacheUserRepository;
import vn.com.atomi.loyalty.common.repository.redis.LoginFailureCountRepository;
import vn.com.atomi.loyalty.common.service.AuthenticationService;
import vn.com.atomi.loyalty.common.utils.Utils;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl extends BaseService implements AuthenticationService {
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  private final UserRepository userRepository;
  private final UserRoleRepository userRoleRepository;
  private final RoleRepository roleRepository;
  private final SessionRepository sessionRepository;

  private final LoginFailureCountRepository redisAuthFailureCount;
  private final CacheUserRepository cacheUserRepository;

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

  @Override
  public UserOutput getUser(String token) {
    val session = validSession(token);

    // check has cache
    val cache = cacheUserRepository.get(token);
    if (cache.isPresent()) return cache.get();

    // load DB
    val userOptional = userRepository.findByIdAndDeletedFalse(session.getUserId());
    val user = userOptional.orElseThrow(() -> new BaseException(USER_NOT_EXIST));
    val userOutput = modelMapper.toUserOutput(user);
    val roleIds =
        userRoleRepository.findByUserIdAndDeletedFalse(user.getId()).stream()
            .map(UserRole::getRoleId)
            .toList();
    val roles =
        roleRepository.findAllById(roleIds).stream()
            .map(role -> new RoleOutput(role.getId(), role.getName()))
            .collect(Collectors.toSet());
    userOutput.setRoles(roles);

    //save cache
    cacheUserRepository.put(token, userOutput);

    return userOutput;
  }

  private User validLogin(LoginInput input) {
    // Check user exist
    val user =
        userRepository
            .findByUsernameAndDeletedFalse(input.getUsername())
            .orElseThrow(() -> new BaseException(USER_NOT_EXIST));

    // count login failure
    Integer count = null;
    if (bruteForceDetection) {
      val countOptional = redisAuthFailureCount.get(user.getId());
      if (countOptional.isPresent()) {
        count = countOptional.get();
        if (countOptional.get() >= maxLoginFailed) {
          throw new BaseException(USER_LOCKED);
        }
      }
    }

    // check password
    if (!encoder.matches(input.getPassword(), user.getPassword())) {
      if (bruteForceDetection)
        redisAuthFailureCount.put(user.getId(), count == null ? 1 : count + 1);
      throw new BaseException(PASSWORD_INCORRECT);
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
