package vn.com.atomi.loyalty.common.dto.output;

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
@Table(name = "C_CUSTOMER_CASA")
public class CustomerCasa extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "C_CUSTOMER_CASA_ID_SEQ")
	@SequenceGenerator(name = "C_CUSTOMER_CASA_ID_SEQ", sequenceName = "C_CUSTOMER_CASA_ID_SEQ", allocationSize = 1)
	private Long id;

	@Column(name = "CUSTOMER_ID")
	private long customerId;

	@Column(name = "CIF_BANK")
	private String cifBank;

	@Column(name = "CUSTOMER_NAME")
	private String customerName;
	
	@Column(name = "PHONE")
	private String phone;
	
	@Column(name = "CASA_AMOUNT")
	private long casaAmount;
	
	@Column(name = "CASA_DATE")
	private LocalDate casaDate;
	
	@Column(name = "STATUS")
	@Enumerated(EnumType.STRING)
	private Status status;
}
