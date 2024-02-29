package vn.com.atomi.loyalty.common.utils;

import vn.com.atomi.loyalty.base.utils.Snowflake;

/**
 * @author haidv
 * @version 1.0
 */
public class Utils {

  private Utils() {
    throw new IllegalStateException("Utility class");
  }

  public static String generateUniqueId() {
    return String.valueOf(Snowflake.getInstance().nextId());
  }

  public static String makeLikeParameter(String param) {
    return "%|" + param + "|%";
  }
}
