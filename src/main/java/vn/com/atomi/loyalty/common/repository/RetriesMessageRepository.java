package vn.com.atomi.loyalty.common.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.com.atomi.loyalty.common.entity.RetriesMessage;

/**
 * @author haidv
 * @version 1.0
 */
@Repository
public interface RetriesMessageRepository extends JpaRepository<RetriesMessage, Long> {

  Optional<RetriesMessage> findByMessageId(String messageId);

  @Modifying
  @Transactional
  @Query("delete from RetriesMessage where messageId = ?1")
  void deleteByMessageId(String messageId);

  @Query(
      "select m from RetriesMessage m where m.retriesNo <= m.repeatCount and m.nextExecuteAt <= ?1 order by m.retriesNo")
  List<RetriesMessage> findByRetriesActivated(LocalDateTime now);
}
