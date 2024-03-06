package vn.com.atomi.loyalty.common.dto.output;

import java.util.Set;
import lombok.Data;

@Data
public class UserOutput {
  private Long id;

  private String username;
  private String displayName;

  private Set<RoleOutput> roles;
}
