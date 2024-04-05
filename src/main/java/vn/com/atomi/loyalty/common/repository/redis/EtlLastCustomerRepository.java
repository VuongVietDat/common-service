package vn.com.atomi.loyalty.common.repository.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EtlLastCustomerRepository {
  private static final String key = "LOYALTY_LAST_CUSTOMER";
  private final RedisTemplate<String, Object> redisTemplate;

  public void put(long id) {
    redisTemplate.opsForValue().set(key, id);
  }

  public long get() {
    var value = (Integer) redisTemplate.opsForValue().get(key);
    return value == null ? 0 : Long.valueOf(value);
  }
}
