package vn.com.atomi.loyalty.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.atomi.loyalty.common.entity.GsGiftClaim;

/**
 * @author nghiatd
 * @version 1.0
 */
@Repository
public interface GiftClaimRepository extends JpaRepository<GsGiftClaim, Long> {

}
