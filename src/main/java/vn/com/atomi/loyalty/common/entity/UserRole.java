package vn.com.atomi.loyalty.common.entity;

import jakarta.persistence.*;
import lombok.Data;
import vn.com.atomi.loyalty.base.data.BaseEntity;

@Data
@Entity
@Table(name = "cm_user_role")
public class UserRole extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cm_user_role_id_seq")
  @SequenceGenerator(
      name = "cm_user_role_id_seq",
      sequenceName = "cm_user_role_id_seq",
      allocationSize = 1)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne
  @JoinColumn(name = "role_id")
  private Role role;
}
