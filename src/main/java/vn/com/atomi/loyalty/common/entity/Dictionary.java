package vn.com.atomi.loyalty.common.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.com.atomi.loyalty.base.data.BaseEntity;
import vn.com.atomi.loyalty.common.enums.Status;

/**
 * @author haidv
 * @version 1.0
 */
@Entity
@Table(name = "cm_dictionary")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dictionary extends BaseEntity {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cm_dictionary_id_seq")
  @SequenceGenerator(
      name = "cm_dictionary_id_seq",
      sequenceName = "cm_dictionary_id_seq",
      allocationSize = 1)
  private Long id;

  @Column(name = "type")
  private String type;

  @Column(name = "code")
  private String code;

  @Column(name = "name")
  private String name;

  @Column(name = "order_no")
  private int orderNo;

  @Column(name = "value")
  private String value;

  @Column(name = "status")
  @Enumerated(value = EnumType.STRING)
  private Status status;
}
