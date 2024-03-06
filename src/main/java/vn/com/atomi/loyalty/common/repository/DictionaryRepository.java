package vn.com.atomi.loyalty.common.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.atomi.loyalty.common.entity.Dictionary;
import vn.com.atomi.loyalty.common.enums.Status;

/**
 * @author haidv
 * @version 1.0
 */
@Repository
public interface DictionaryRepository extends JpaRepository<Dictionary, Long> {

  List<Dictionary> findByDeletedFalseAndStatus(Status status);
}
