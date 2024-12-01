package vn.com.atomi.loyalty.common.utils;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import vn.com.atomi.loyalty.base.constant.DateConstant;

/**
 * @author haidv
 * @version 1.0
 */
public class Utils {
  public static DateTimeFormatter LOCAL_DATE_FORMATTER =
          DateTimeFormatter.ofPattern(DateConstant.STR_PLAN_DD_MM_YYYY_STROKE);
  private Utils() {
    throw new IllegalStateException("Utility class");
  }

  public static String generateUniqueId() {
    return UUID.randomUUID().toString();
  }

  public static String makeLikeParameter(String param) {
    return "%" + param + "%";
  }

  public static LocalDate convertToLocalDate(String date) {
    return StringUtils.isEmpty(date) ? null : LocalDate.parse(date, LOCAL_DATE_FORMATTER);
  }
  public static Optional<HttpServletRequest> getCurrentRequest() {
    val requestAttributes = RequestContextHolder.getRequestAttributes();
    if (requestAttributes instanceof ServletRequestAttributes) {
      return Optional.of(((ServletRequestAttributes) requestAttributes).getRequest());
    }
    return Optional.empty();
  }
}
