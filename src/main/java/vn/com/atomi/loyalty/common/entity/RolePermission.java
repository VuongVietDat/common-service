package vn.com.atomi.loyalty.common.entity;

import jakarta.persistence.*;
import lombok.Data;
import vn.com.atomi.loyalty.base.data.BaseEntity;

@Data
@Entity
@Table(name = "cm_role_permission")
public class RolePermission extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cm_role_permission_id_seq")
  @SequenceGenerator(
      name = "cm_role_permission_id_seq",
      sequenceName = "cm_role_permission_id_seq",
      allocationSize = 1)
  private Long id;

  private Long roleId;

  private Long permissionId;
}
