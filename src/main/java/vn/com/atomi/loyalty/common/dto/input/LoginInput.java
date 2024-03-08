package vn.com.atomi.loyalty.common.dto.input;

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

  private @NotNull @Length(min = 4) String username;

  private @NotNull @Length(min = 8) String password;
}
