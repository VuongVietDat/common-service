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
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cm_session_id_seq")
  @SequenceGenerator(
      name = "cm_session_id_seq",
      sequenceName = "cm_session_id_seq",
      allocationSize = 1)
  private Long id;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "refresh_token")
  private String refreshToken;

  @Column(name = "expire")
  private LocalDateTime expire;

  @Column(name = "client_ip")
  private String clientIp;

  @Column(name = "client_time")
  private String clientTime;

  @Column(name = "client_platform")
  private String clientPlatform;

  @Column(name = "device_id")
  private String deviceId;

  @Column(name = "device_name")
  private String deviceName;

  @Column(name = "device_type")
  private String deviceType;

  @Column(name = "app_version")
  private String appVersion;
}
