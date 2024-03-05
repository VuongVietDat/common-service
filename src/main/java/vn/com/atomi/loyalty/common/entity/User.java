package vn.com.atomi.loyalty.common.entity;

import jakarta.persistence.*;
import lombok.Data;
import vn.com.atomi.loyalty.base.data.BaseEntity;

@Data
@Entity
@Table(name = "cm_user")
public class User extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @Column(unique = true)
  private String username;

  private String password;
  private String displayName;
}
