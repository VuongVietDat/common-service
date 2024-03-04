package vn.com.atomi.loyalty.common.utils;

import java.util.UUID;

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
}
