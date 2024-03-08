package vn.com.atomi.loyalty.base.security;

import lombok.Data;

import java.util.Set;

@Data
public class UserOutput {
  private Long id;

  private String username;

  private String displayName;

  private Set<RoleOutput> roles;

  private Set<PermissionOutput> permissions;
}
