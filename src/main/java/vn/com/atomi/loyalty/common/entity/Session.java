package vn.com.atomi.loyalty.common.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import vn.com.atomi.loyalty.base.data.BaseEntity;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cm_session")
public class Session extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cm_session_id_seq")
  @SequenceGenerator(
      name = "cm_session_id_seq",
      sequenceName = "cm_session_id_seq",
      allocationSize = 1)
  private Long id;

  private Long userId;

  private String refreshToken;

  private LocalDateTime expire;

  private String clientIp;
  private String clientTime;
  private String clientPlatform;
  private String deviceId;
  private String deviceName;
  private String deviceType;
  private String appVersion;
}
