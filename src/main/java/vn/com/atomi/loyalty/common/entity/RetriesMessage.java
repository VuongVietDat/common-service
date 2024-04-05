package vn.com.atomi.loyalty.common.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import vn.com.atomi.loyalty.base.data.BaseEntity;

/**
 * @author haidv
 * @version 1.0
 */
@Entity
@Table(name = "cm_retries_message")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetriesMessage extends BaseEntity {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cm_retries_message_id_seq")
  @SequenceGenerator(
      name = "cm_retries_message_id_seq",
      sequenceName = "cm_retries_message_id_seq",
      allocationSize = 1)
  private Long id;

  @Column(name = "message_id")
  private String messageId;

  @Lob
  @Column(name = "data")
  private String data;

  @Column(name = "source")
  private String source;

  @Column(name = "retries_no")
  private int retriesNo;

  @Column(name = "topic")
  private String topic;

  @Column(name = "destination")
  private String destination;

  @Column(name = "delay_time")
  private Long delayTime;

  @Column(name = "repeat_count")
  private Integer repeatCount;

  @Column(name = "next_execute_at")
  private LocalDateTime nextExecuteAt;
}
