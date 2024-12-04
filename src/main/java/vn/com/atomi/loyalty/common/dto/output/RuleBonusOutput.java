package vn.com.atomi.loyalty.common.dto.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import vn.com.atomi.loyalty.common.enums.BonusType;
import vn.com.atomi.loyalty.common.enums.PlusType;

/**
 * @author haidv
 * @version 1.0
 */
@Setter
@Getter
public class RuleBonusOutput {

  @Schema(description = "ID bản ghi")
  private Long id;

  @Schema(description = "Loại thưởng thêm")
  private BonusType type;

  @Schema(description = "Giá trị thưởng")
  private String value;

  @Schema(
      description =
          "Loại giá trị thưởng:</br> PERCENTAGE: Phần trăm base điểm</br> FIX: Số điểm cụ thể")
  private PlusType plusType;

  @Schema(description = "Điều kiện cha nhận thưởng thêm")
  private String condition;

  @Schema(description = "Điều kiện con nhận thưởng thêm")
  private String childCondition;
}
