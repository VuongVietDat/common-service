package vn.com.atomi.loyalty.common.repository.redis;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LoginFailureCountRepository {

  private final RedisTemplate<String, Object> redisTemplate;

  @Value("${custom.properties.security.system.setting.lifespan}")
  private Duration userLockLifespan;

  private String composeHeader(String username) {
    return String.format("LOYALTY_LOGIN_FAILURE_COUNT:%s", username);
  }

  public void put(String username, int count) {
    redisTemplate.opsForValue().set(composeHeader(username), count, userLockLifespan);
  }

  public Optional<Pair<Long, Integer>> get(String username) {
    val key = composeHeader(username);
    val ttl = redisTemplate.getExpire(key, TimeUnit.MINUTES);
    val count = redisTemplate.opsForValue().get(key);
    if (ttl == null || count == null) return Optional.empty();

    return Optional.of(Pair.of(ttl, (Integer) count));
  }

  public void remove(String username) {
    redisTemplate.delete(composeHeader(username));
  }
}
