package vn.com.atomi.loyalty.common.repository.redis;

import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import vn.com.atomi.loyalty.common.dto.output.UserOutput;

@Repository
@RequiredArgsConstructor
public class CacheUserRepository {

  private final RedisTemplate<String, Object> redisTemplate;

  @Value("${custom.properties.security.session-lifespan}")
  private Duration sessionLifespan;

  private String composeHeader(String key) {
    return String.format("CacheUser:%s", key);
  }

  public void put(String token, UserOutput user) {
    redisTemplate.opsForValue().set(composeHeader(token), user, sessionLifespan);
  }

  public Optional<UserOutput> get(String token) {
    return Optional.ofNullable((UserOutput) redisTemplate.opsForValue().get(composeHeader(token)));
  }
}
