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
  public class Mission {
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_INPROGRESS = "INPROGRESS";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String TYPE_CHAIN = "C";
    public static final String TYPE_GROUP = "G";
    public static final String TYPE_MISSION = "M";
    public static final String GROUP_TYPE_AND = "1";

  }
  private Constants() {
    throw new IllegalStateException("Utility class");
  }
}
