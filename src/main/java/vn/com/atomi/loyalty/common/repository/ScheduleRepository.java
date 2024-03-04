package vn.com.atomi.loyalty.common.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.com.atomi.loyalty.common.entity.ScheduleInfo;

/**
 * @author haidv
 * @version 1.0
 */
@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleInfo, Long> {

  Optional<ScheduleInfo> findByIdAndDeletedFalse(Long id);

  Optional<ScheduleInfo> findByJobNameAndJobGroupAndDeletedFalse(String jobName, String jobGroup);

  @Modifying
  @Transactional
  @Query("update ScheduleInfo set lstExecuteAt = ?1 where id = ?2")
  void updateLstExecuteAt(LocalDateTime localDateTime, Long id);
}
