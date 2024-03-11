package vn.com.atomi.loyalty.common.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

/**
 * @author haidv
 * @version 1.0
 */
@Setter
@Getter
@AllArgsConstructor
public class LoginInput {

  @Schema(example = "haidv")
  private @NotNull @Length(min = 4) String username;

  @Schema(example = "123456789")
  private @NotNull @Length(min = 8) String password;
}
