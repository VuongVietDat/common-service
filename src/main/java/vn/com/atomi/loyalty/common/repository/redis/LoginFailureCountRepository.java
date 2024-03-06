package vn.com.atomi.loyalty.common.repository.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LoginFailureCountRepository {

  private final RedisTemplate<String, Object> redisTemplate;

  private String composeHeader(long key) {
    return String.format("LoginFailureCount:%d", key);
  }

  public void put(long userId, int count) {
    redisTemplate.opsForValue().set(composeHeader(userId), count);
  }

  public Optional<Integer> get(long userId) {
    return Optional.ofNullable((Integer) redisTemplate.opsForValue().get(composeHeader(userId)));
  }

  public void remove(long userId){
    redisTemplate.delete(composeHeader(userId));
  }
}
