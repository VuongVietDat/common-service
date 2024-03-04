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
@Table(name = "cm_schedule_info")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ScheduleInfo extends BaseEntity {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cm_schedule_info_id_seq")
  @SequenceGenerator(
      name = "cm_schedule_info_id_seq",
      sequenceName = "cm_schedule_info_id_seq",
      allocationSize = 1)
  private Long id;

  @Column(name = "job_name")
  private String jobName;

  @Column(name = "job_group")
  private String jobGroup;

  @Column(name = "job_status")
  @Enumerated(value = EnumType.STRING)
  private StatusJob jobStatus;

  @Column(name = "job_class")
  private String jobClass;

  @Column(name = "cron_expression")
  private String cronExpression;

  @Column(name = "description")
  private String description;

  @Column(name = "interface_name")
  private String interfaceName;

  @Column(name = "repeat_time")
  private Long repeatTime;

  @Column(name = "cron_job")
  private Boolean cronJob;

  @Column(name = "repeat_count")
  private Integer repeatCount;

  @Column(name = "start_time")
  private Long startTime;

  @Column(name = "priority")
  private Integer priority;

  @Column(name = "is_durable")
  private Boolean isDurable;

  @Column(name = "data_map")
  private String dataMap = "{}";

  @Column(name = "execute_last_time")
  private LocalDateTime executeLastTime;
}
