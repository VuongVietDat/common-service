package vn.com.atomi.loyalty.common.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.com.atomi.loyalty.base.data.BaseEntity;
import vn.com.atomi.loyalty.common.enums.Status;

import java.time.LocalDate;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "GS_GIFT_PARTNER")
public class GiftPartner extends BaseEntity {

    public static final String GENERATOR = "GS_GIFT_PARTNER_ID_SEQ";

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GENERATOR)
    @SequenceGenerator(name = GENERATOR, sequenceName = GENERATOR, allocationSize = 1)
    private Long id;

    @Column(name = "PARTNER_ID")
    private Long partnerId;

    @Column(name = "CATEGORY_ID")
    private Long categoryId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "GIFT_TYPE")
    private String giftType;

    @Column(name = "QTY_INIT")
    private Long qtyInit;

    @Column(name = "QTY_REMAIN")
    private Long qtyRemain;

    @Column(name = "PRICE")
    private Long price;

    @Column(name = "UNIT")
    private String unit;

    @Column(name = "START_DATE")
    private LocalDate startDate;

    @Column(name = "END_DATE")
    private LocalDate endDate;

    @Column(name = "EFFECTIVE_DATE")
    private LocalDate effectiveDate;

    @Column(name = "EXPIRED_DATE")
    private LocalDate expiredDate;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "VISIBLE")
    private String visible;

    @Column(name = "NOTES")
    private String notes;

    @Column(name = "GUIDE")
    private String guide;

    @Column(name = "CONDITION")
    private String condition;

    @Column(name = "IMAGE")
    private String image;

    @Column(name = "THUMBNAIL")
    private String thumbnail;

    @Column(name = "LIMIT_GIFT_PER_USER")
    private Long limitGiftPerUser;

    @Column(name = "QTY_AVAIL")
    private Long qtyAvail;

    @Column(name = "QTY_ASSIGN")
    private Long qtyAssign;


}
