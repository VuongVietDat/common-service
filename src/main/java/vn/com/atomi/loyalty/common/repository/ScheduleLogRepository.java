package vn.com.atomi.loyalty.common.repository;

import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.com.atomi.loyalty.common.entity.ScheduleLog;
import vn.com.atomi.loyalty.common.enums.StatusJob;

/**
 * @author haidv
 * @version 1.0
 */
@Repository
public interface ScheduleLogRepository extends JpaRepository<ScheduleLog, Long> {

  @Modifying
  @Transactional
  @Query("update ScheduleLog set endTime = ?1, exceptionInfo = ?2, status = ?3 where id = ?4")
  void updateLogEndJob(LocalDateTime localDateTime, String msg, StatusJob statusJob, Long id);
}
