package vn.com.atomi.loyalty.common.dao;

import lombok.Data;

@Data
public class UserRawInput {
  private Long id;

  private String username;
  private String displayName;
  private Long roleId;
  private String name;
}
