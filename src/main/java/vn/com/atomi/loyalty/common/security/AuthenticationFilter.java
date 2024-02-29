package vn.com.atomi.loyalty.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.com.atomi.loyalty.base.constant.RequestConstant;
import vn.com.atomi.loyalty.base.data.ResponseData;
import vn.com.atomi.loyalty.base.data.ResponseUtils;
import vn.com.atomi.loyalty.base.exception.BaseException;
import vn.com.atomi.loyalty.base.utils.RequestUtils;
import vn.com.atomi.loyalty.common.repository.redis.TokenBlackListRepository;
import vn.com.atomi.loyalty.common.utils.Utils;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  @Autowired private TokenProvider tokenProvider;

  @Autowired private TokenBlackListRepository tokenBlackListRepository;

  @Value("${spring.application.name}")
  private String serviceName;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      var requestId = request.getHeader(RequestConstant.REQUEST_ID);
      ThreadContext.put(
          RequestConstant.REQUEST_ID, requestId == null ? Utils.generateUniqueId() : requestId);
      ThreadContext.put(RequestConstant.SERVICE_NAME, serviceName);
      ThreadContext.put(RequestConstant.CLIENT_IP, RequestUtils.extractClientIpAddress(request));
      ThreadContext.put(RequestConstant.LOCAL_IP, request.getLocalAddr());
      ThreadContext.put(RequestConstant.DEVICE_ID, request.getHeader(RequestConstant.DEVICE_ID));
      ThreadContext.put(
          RequestConstant.DEVICE_NAME, request.getHeader(RequestConstant.DEVICE_NAME));
      ThreadContext.put(
          RequestConstant.DEVICE_TYPE, request.getHeader(RequestConstant.DEVICE_TYPE));
      ThreadContext.put(
          RequestConstant.BROWSER_NAME, request.getHeader(RequestConstant.BROWSER_NAME));
      ThreadContext.put(
          RequestConstant.APPLICATION_VERSION,
          request.getHeader(RequestConstant.APPLICATION_VERSION));
      request.setAttribute(RequestConstant.REQUEST_TIME_START, System.currentTimeMillis());
      SecurityContextHolder.getContext().setAuthentication(null);
      //      var jwt = getJwtFromRequest(request);
      //      if (StringUtils.hasText(jwt)
      //          && !RequestUtils.matches(
      //              request, Set.of(ApplicationSecurityConfig.IGNORE_AUTHENTICATION_PATTERN))) {
      //        var claims =
      //            tokenProvider.getClaimsFromRSAToken(
      //                jwt.substring(RequestConstant.BEARER_PREFIX.length()));
      //        var issueByUser = claims.get(RequestConstant.JWT_ISSUE_BY_USER, Boolean.class);
      //        var uid = claims.get(RequestConstant.JWT_CLAIM_USER_ID, String.class);
      //        var ssid = claims.get(RequestConstant.JWT_CLAIM_SESSION_ID, String.class);
      //        ThreadContext.put(RequestConstant.JWT_CLAIM_SESSION_ID, ssid);
      //        UserPrincipal userDetails;
      //        if (issueByUser) {
      //          if (tokenBlackListRepository.find(ssid).isPresent()) {
      //            throw new BaseException(CommonErrorCode.REFRESH_TOKEN_EXPIRED);
      //          }
      //          // var userinfo = authClientService.getUserinfo(uid, jwt);
      //          userDetails = new UserPrincipal(claims.getIssuer(), true, new UserOutput(), ssid,
      // jwt);
      //        } else {
      //          userDetails = new UserPrincipal(claims.getIssuer(), ssid, false);
      //        }
      //        var authentication =
      //            new UsernamePasswordAuthenticationToken(
      //                userDetails, null, userDetails.getAuthorities());
      //        authentication.setDetails(new
      // WebAuthenticationDetailsSource().buildDetails(request));
      //        SecurityContextHolder.getContext().setAuthentication(authentication);
      //      }

    } catch (BaseException ex) {
      LOGGER.error("Could not set user authentication in security context", ex);
      response.setStatus(ex.getHttpStatus().value());
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.setCharacterEncoding(StandardCharsets.UTF_8.name());
      ResponseData<?> responseData =
          ResponseUtils.error(ex.getCode(), ex.getMessage(), ex.getHttpStatus()).getBody();
      var writer = response.getWriter();
      ObjectMapper mapper =
          new ObjectMapper()
              .registerModule(new ParameterNamesModule())
              .registerModule(new Jdk8Module())
              .registerModule(new JavaTimeModule());
      writer.println(mapper.writeValueAsString(responseData));
      return;
    }
    filterChain.doFilter(request, response);
  }

  private String getJwtFromRequest(HttpServletRequest request) {
    var bearerToken = request.getHeader(RequestConstant.AUTHORIZATION);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(RequestConstant.BEARER_PREFIX)) {
      return bearerToken;
    }
    return null;
  }
}
