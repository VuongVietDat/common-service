package vn.com.atomi.loyalty.common.dto.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author haidv
 * @version 1.0
 */
@Setter
@Getter
@AllArgsConstructor
public class LoginInput {

  private String username;

  private String password;
}
