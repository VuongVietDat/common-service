package vn.com.atomi.loyalty.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.atomi.loyalty.common.entity.ScheduleLog;

/**
 * @author haidv
 * @version 1.0
 */
@Repository
public interface ScheduleLogRepository extends JpaRepository<ScheduleLog, Long> {}
