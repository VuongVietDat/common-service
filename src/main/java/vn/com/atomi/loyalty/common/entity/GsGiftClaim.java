package vn.com.atomi.loyalty.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.generator.Generator;
import vn.com.atomi.loyalty.base.data.BaseEntity;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "GS_GIFT_CLAIM")
public class GsGiftClaim extends BaseEntity {
    public static final String GENERATOR = "GS_GIFT_CLAIM_ID_SEQ";
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GENERATOR)
    @SequenceGenerator(name = GENERATOR, sequenceName = GENERATOR, allocationSize = 1)
    private Long id;

    @Column(name = "CIF_BANK", columnDefinition = "VARCHAR2(50)")
    private String cifBank;

    @Column(name = "CIF_WALLET", columnDefinition = "VARCHAR2(50)")
    private String cifWallet;

    @Column(name = "CUSTOMER_ID")
    private Long customerId;

    @Column(name = "GIFT_ID")
    private Long giftId;

    @Column(name = "QUANTITY", columnDefinition = "NUMBER(19, 0)")
    private Integer quantity;

    @Column(name = "REF_NO", columnDefinition = "VARCHAR2(50)")
    private String refNo;

}