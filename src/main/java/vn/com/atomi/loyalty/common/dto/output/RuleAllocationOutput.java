package vn.com.atomi.loyalty.common.dto.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import vn.com.atomi.loyalty.common.enums.Frequency;

/**
 * @author haidv
 * @version 1.0
 */
@Setter
@Getter
public class RuleAllocationOutput {

  @Schema(description = "ID bản ghi")
  private Long id;

  @Schema(
      description = "true = Quy đổi theo giá trị giao dịch / false = Quy đổi theo số lần giao dịch")
  private Boolean isExchangeByValue;

  @Schema(description = "Số điểm cố định")
  private String fixPointAmount;

  @Schema(description = "Giá trị quy đổi (VND)")
  private String exchangeValue;

  @Schema(description = "Giá trị điểm")
  private String exchangePoint;

  @Schema(description = "Giá trị giao dịch tối thiểu")
  private String minTransaction;

  @Schema(description = "Tích điểm với số tiền thực khách hàng thanh toán (sau khi trừ khuyến mãi)")
  private Boolean isNetValue;

  @Schema(description = "Giới hạn số điểm tối đa phân bổ trên một giao dịch")
  private String limitPointPerTransaction;

  @Schema(description = "Giới hạn số điểm tối đa phân bổ trên một khách hàng")
  private String limitPointPerUser;

  @Deprecated
  @Schema(
      description =
          "Tần suất giới hạn số điểm tối đa phân bổ trên một khách hàng:</br> HOURS: Giờ</br> DAY: Ngày</br> WEEK: Tuần</br> MONTH: Tháng</br> YEAR: Năm")
  private Frequency frequencyLimitPointPerUser;

  @Schema(description = "Giới hạn số lần tối đa phân bổ trên một khách hàng")
  private String limitEventPerUser;

  @Schema(
      description =
          "Tần suất giới hạn số lần tối đa phân bổ trên một khách hàng:</br> HOURS: Giờ</br> DAY: Ngày</br> WEEK: Tuần</br> MONTH: Tháng</br> YEAR: Năm")
  private Frequency frequencyLimitEventPerUser;

  @Schema(description = "Thời gian chờ giữa 2 lần")
  private String timeWait;

  @Schema(
      description =
          "Đơn vị thời gian chờ giữa 2 lần:</br> MINUTE: Phút</br>HOURS: Giờ</br> DAY: Ngày</br> WEEK: Tuần</br> MONTH: Tháng</br> YEAR: Năm")
  private Frequency frequencyTimeWait;
}
