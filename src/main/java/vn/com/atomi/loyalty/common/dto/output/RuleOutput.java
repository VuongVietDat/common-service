package vn.com.atomi.loyalty.common.dto.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import vn.com.atomi.loyalty.base.constant.DateConstant;
import vn.com.atomi.loyalty.common.enums.ExpirePolicyType;
import vn.com.atomi.loyalty.common.enums.PointType;
import vn.com.atomi.loyalty.common.enums.Status;

import java.time.LocalDate;
import java.util.List;

/**
 * @author haidv
 * @version 1.0
 */
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RuleOutput {

  @Schema(description = "ID bản ghi")
  private Long id;

  @Schema(description = "Loại quy tắc sinh điểm")
  private String type;

  @Schema(description = "Mã quy tắc sinh điểm")
  private String code;

  @Schema(description = "Tên quy tắc sinh điểm")
  private String name;

  @Schema(
      description =
          "Loại điểm:</br> ALL: Tất cả loại điẻm</br> RANK_POINT: Điểm xếp hạng</br> CONSUMPTION_POINT: Điểm tích lũy dùng để tiêu dùng")
  private PointType pointType;

  @Schema(description = "ID chiến dịch")
  private Long campaignId;

  @Schema(description = "Mã chiến dịch")
  private String campaignCode;

  @Schema(description = "ID ngân sách")
  private Long budgetId;

  @Schema(description = "Mã ngân sách")
  private String budgetCode;

  @Schema(description = "Ngày bắt đầu hiệu lực (dd/MM/yyyy)", example = "01/01/2024")
  @JsonFormat(pattern = DateConstant.STR_PLAN_DD_MM_YYYY_STROKE)
  private LocalDate startDate;

  @Schema(description = "Ngày kết thúc hiệu lực (dd/MM/yyyy)", example = "31/12/2024")
  @JsonFormat(pattern = DateConstant.STR_PLAN_DD_MM_YYYY_STROKE)
  private LocalDate endDate;

  @Schema(description = "Trạng thái:</br> ACTIVE: Hiệu lực</br> INACTIVE: Không hiệu lực")
  private Status status;

  @Schema(
      description =
          "Loại chính sách hết hạn điểm:</br> AFTER_DAY: Sau số ngày</br> AFTER_DATE: Sau ngày</br> FIRST_DATE_OF_MONTH: Ngày đầu tiên của tháng thứ N +")
  private ExpirePolicyType expirePolicyType;

  @Schema(
      description =
          "Giá trị của chính sách hết hạn điểm:</br> AFTER_DAY: number</br> AFTER_DATE: dd/MM/yyyy</br> FIRST_DATE_OF_MONTH: number")
  private String expirePolicyValue;

  @Schema(description = "Quy tắc chung phân bổ điểm")
  @NotEmpty
  private List<RuleAllocationOutput> ruleAllocationOutputs;

  @Schema(description = "Quy tắc tặng thêm điểm")
  private List<RuleBonusOutput> ruleBonusOutputs;

  @Schema(description = "Điều kiện áp dụng quy tắc")
  private List<RuleConditionOutput> ruleConditionOutputs;
}
