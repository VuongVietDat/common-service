package vn.com.atomi.loyalty.common.utils;

/**
 * @author haidv
 * @version 1.0
 */
public class Constants {

  public static final String SOURCE_TYPE_CUSTOMER_TYPE = "CUSTOMER_TYPE";

  public static final String SOURCE_TYPE_NATIONALITY = "NATIONALITY";

  public static final String SOURCE_TYPE_UNIQUE_TYPE = "UNIQUE_TYPE";

  public static final String SOURCE_TYPE_GENDER = "GENDER";

  public static final int BATCH_SIZE = 100;

  private Constants() {
    throw new IllegalStateException("Utility class");
  }
}
