package vn.com.atomi.loyalty.common.entity;

import jakarta.persistence.*;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.com.atomi.loyalty.base.data.BaseEntity;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cm_user")
public class User extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cm_user_id_seq")
  @SequenceGenerator(name = "cm_user_id_seq", sequenceName = "cm_user_id_seq", allocationSize = 1)
  private Long id;

  @Column(unique = true)
  private String username;

  private String password;
  private String displayName;

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(
      name = "cm_user_role",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles;
}
