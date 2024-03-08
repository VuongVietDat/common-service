package vn.com.atomi.loyalty.base.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionOutput {
  private Long id;

  private String name;
}
