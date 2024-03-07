package vn.com.atomi.loyalty.common.entity;

import jakarta.persistence.*;
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
@Table(name = "cm_role")
public class Role extends BaseEntity {
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cm_role_id_seq")
  @SequenceGenerator(name = "cm_role_id_seq", sequenceName = "cm_role_id_seq", allocationSize = 1)
  private Long id;

  @Column(name = "name", unique = true)
  private String name;
}
