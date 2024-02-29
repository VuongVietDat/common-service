package vn.com.atomi.loyalty.base.data;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

/**
 * @author haidv
 * @version 1.0
 */
@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

  @Override
  public String convertToDatabaseColumn(List<String> list) {
    return CollectionUtils.isEmpty(list) ? null : String.format("|%s|", String.join("|", list));
  }

  @Override
  public List<String> convertToEntityAttribute(String joined) {
    return StringUtils.isBlank(joined)
        ? new ArrayList<>()
        : List.of(joined.substring(1, joined.length() - 1).split("\\|"));
  }
}
