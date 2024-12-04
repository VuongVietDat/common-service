package vn.com.atomi.loyalty.common.dto.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import vn.com.atomi.loyalty.common.enums.ConditionProperties;
import vn.com.atomi.loyalty.common.enums.Operators;

/**
 * @author haidv
 * @version 1.0
 */
@Setter
@Getter
public class RuleConditionOutput {

  @Schema(description = "ID bản ghi")
  private Long id;

  @Schema(description = "Thuộc tính điều kiện áp dụng quy tắc")
  private ConditionProperties properties;

  @Schema(
      description =
          "Điều kiện:</br> EQUAL: Bằng</br> LESS_THAN: Nhỏ hơn</br> LESS_THAN_EQUAL: Nhỏ hơn hoặc bằng</br> GREATER_THAN: Lớn hơn</br> GREATER_THAN_EQUAL: Lớn hơn hoặc bằng</br> CONTAIN: Chứa</br> START_WITH: Bắt đầu bằng</br> END_WITH: Kết thúc bằng</br> IN: Nằm trong</br> NOT_IN: Không nằm trong")
  private Operators operators;

  @Schema(description = "Giá trị thuộc tính")
  private String value;
}
