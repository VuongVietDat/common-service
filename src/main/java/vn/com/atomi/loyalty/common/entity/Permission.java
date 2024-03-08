package vn.com.atomi.loyalty.common.entity;

import jakarta.persistence.*;
import lombok.Data;
import vn.com.atomi.loyalty.base.data.BaseEntity;

@Data
@Entity
@Table(name = "cm_permission")
public class Permission extends BaseEntity {
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cm_permission_id_seq")
  @SequenceGenerator(
      name = "cm_permission_id_seq",
      sequenceName = "cm_permission_id_seq",
      allocationSize = 1)
  private Long id;

  @Column(name = "name", unique = true)
  private String name;
}
