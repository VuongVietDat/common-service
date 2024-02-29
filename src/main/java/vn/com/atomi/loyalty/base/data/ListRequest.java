package vn.com.atomi.loyalty.base.data;

import jakarta.validation.constraints.Pattern;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import vn.com.atomi.loyalty.base.annotations.ConditionString;
import vn.com.atomi.loyalty.base.utils.JsonUtils;

/**
 * Resource list acquisition request
 *
 * @author haidv
 * @version 1.0
 */
@Setter
@Getter
public class ListRequest {

  private static final Logger LOGGER = LoggerFactory.getLogger(ListRequest.class);

  @ConditionString private final String condition;

  private Integer pageSize;

  private Integer pageNo;

  @Pattern(regexp = "^[a-z0-9]+:(asc|desc)$", flags = Pattern.Flag.CASE_INSENSITIVE)
  private String sort;

  public ListRequest(Integer pageSize, Integer pageNo, String sort, String condition) {
    this.pageSize = (pageSize == null || pageSize <= 0 || pageSize > 1000) ? 1000 : pageSize;
    this.pageNo = (pageNo == null || pageNo <= 0) ? 1 : pageNo;
    this.sort = sort;
    this.condition = condition;
  }

  public Pageable pageable() {
    Sort srt = null;
    if (this.sort != null) {
      String[] part = this.sort.split("_");
      for (String s : part) {
        String[] tmp = s.split(":");
        if (tmp.length == 2) {
          if (srt == null) {
            srt = Sort.by(Sort.Direction.fromString(tmp[1].trim()), tmp[0].trim());
          } else {
            srt.and(Sort.by(Sort.Direction.fromString(tmp[1].trim()), tmp[0].trim()));
          }
        }
      }
    }
    if (srt == null) {
      srt = Sort.unsorted();
    }
    return PageRequest.of(this.pageNo - 1, this.pageSize, srt);
  }

  public <T> Optional<T> get(Class<T> valueType) {
    if (StringUtils.isEmpty(condition)) {
      return Optional.empty();
    }
    String json;
    try {
      json = new String(Base64.getDecoder().decode(condition), StandardCharsets.UTF_8);
    } catch (IllegalArgumentException e) {
      LOGGER.error(e.getMessage(), e);
      return Optional.empty();
    }
    T object = JsonUtils.fromJson(json, valueType);

    return Optional.of(object);
  }
}
