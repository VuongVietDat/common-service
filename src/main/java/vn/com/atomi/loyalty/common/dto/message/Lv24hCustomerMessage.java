package vn.com.atomi.loyalty.common.dto.message;

import lombok.Data;
import vn.com.atomi.loyalty.common.enums.EventAction;

/**
 * @author haidv
 * @version 1.0
 */
@Data
public class Lv24hCustomerMessage {
  private EventAction action;
  private String messageId;
  private String userName;
  private String custNo;
  private String mobilePhone;
  private String dateOfBirth;
  private String sex;
  private String branchCode;
  private String regBranch;
  private String address;
  private String uniqueId;
  private String uniqueValue;
  private String dateOfIssue;
  private String placeOfIssue;
  private String nationality;
  private String packageDefault;
}
