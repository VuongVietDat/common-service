package vn.com.atomi.loyalty.common.repository.redis;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import vn.com.atomi.loyalty.base.utils.JsonUtils;
import vn.com.atomi.loyalty.common.dto.output.DictionaryOutput;

/**
 * @author haidv
 * @version 1.0
 */
@Repository
@RequiredArgsConstructor
public class MasterDataRepository {

  private static final String KEY_DICTIONARY_ALL = "LOYALTY_DICTIONARY_ALL";
  private final RedisTemplate<String, Object> redisTemplate;

  public List<DictionaryOutput> getDictionary() {
    var opt = (String) this.redisTemplate.opsForValue().get(KEY_DICTIONARY_ALL);
    return opt == null
        ? new ArrayList<>()
        : JsonUtils.fromJson(opt, List.class, DictionaryOutput.class);
  }

  public void putDictionary(List<DictionaryOutput> dictionaryOutputs) {
    redisTemplate
        .opsForValue()
        .set(KEY_DICTIONARY_ALL, JsonUtils.toJson(dictionaryOutputs), Duration.ofHours(12));
  }
}
