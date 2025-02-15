package vn.com.atomi.loyalty.common.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import vn.com.atomi.loyalty.base.data.BaseEntity;
import vn.com.atomi.loyalty.common.enums.StatusJob;

/**
 * @author haidv
 * @version 1.0
 */
@Entity
@Table(name = "cm_schedule_log")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ScheduleLog extends BaseEntity {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cm_schedule_log_id_seq")
  @SequenceGenerator(
      name = "cm_schedule_log_id_seq",
      sequenceName = "cm_schedule_log_id_seq",
      allocationSize = 1)
  private Long id;

  @Column(name = "job_info_id")
  private Long jobId;

  @Column(name = "execute_id")
  private String executeId;

  @Column(name = "start_time")
  private LocalDateTime startTime;

  @Column(name = "end_time")
  private LocalDateTime endTime;

  @Column(name = "exception_info")
  private String exceptionInfo;

  @Column(name = "status")
  @Enumerated(value = EnumType.STRING)
  private StatusJob status;

  @Column(name = "extent_data")
  private String extentData;
}
