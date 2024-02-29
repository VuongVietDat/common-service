package vn.com.atomi.loyalty.common.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.atomi.loyalty.common.entity.ScheduleInfo;

/**
 * @author haidv
 * @version 1.0
 */
@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleInfo, Long> {

  boolean existsByJobNameAndJobGroupAndDeletedFalse(String jobName, String jobGroup);

  Page<ScheduleInfo> findByDeletedFalseOrderByStartTimeDescPriorityDesc(Pageable pageable);

  Optional<ScheduleInfo> findByIdAndDeletedFalse(Long id);

  Optional<ScheduleInfo> findByJobNameAndJobGroupAndDeletedFalse(String jobName, String jobGroup);
}
