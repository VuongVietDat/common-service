package vn.com.atomi.loyalty.common.entity;

import jakarta.persistence.*;
import lombok.Data;
import vn.com.atomi.loyalty.base.data.BaseEntity;

@Data
@Entity
@Table(name = "cm_user")
public class User extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cm_user_id_seq")
  @SequenceGenerator(
          name = "cm_user_id_seq",
          sequenceName = "cm_user_id_seq",
          allocationSize = 1)
  private Long id;

  @Column(unique = true)
  private String username;

  private String password;
  private String displayName;
}
