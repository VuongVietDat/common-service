package vn.com.atomi.loyalty.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;
import lombok.val;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author haidv
 * @version 1.0
 */
public class Utils {

  private Utils() {
    throw new IllegalStateException("Utility class");
  }

  public static String generateUniqueId() {
    return UUID.randomUUID().toString();
  }

  public static String makeLikeParameter(String param) {
    return "%|" + param + "|%";
  }

  public static Optional<HttpServletRequest> getCurrentRequest() {
    val requestAttributes = RequestContextHolder.getRequestAttributes();
    if (requestAttributes instanceof ServletRequestAttributes) {
      return Optional.of(((ServletRequestAttributes) requestAttributes).getRequest());
    }
    return Optional.empty();
  }
}
