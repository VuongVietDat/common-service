package vn.com.atomi.loyalty.common.dto.output;

import java.util.Set;
import lombok.Data;
import vn.com.atomi.loyalty.common.entity.Role;

@Data
public class UserOutput {
  private Long id;

  private String username;
  private String displayName;

  private Set<Role> roles;
}
