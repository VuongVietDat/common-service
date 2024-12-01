package vn.com.atomi.loyalty.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.com.atomi.loyalty.common.entity.GiftPartner;

import java.util.List;

/**
 * @author nghiatd
 * @version 1.0
 */
@Repository
public interface GiftPartnerRepository extends JpaRepository<GiftPartner, Long> {

  @Query(value = """
        SELECT ggp.*
        FROM C_MISSION_SEQUENTIAL cms
        JOIN GS_MISSION_GIFT mgt ON (cms.ID = mgt.MISSION_SEQ_ID AND QTY_ASSIGN > 0)
        JOIN GS_GIFT_PARTNER ggp ON mgt.GIFT_PARTNER_ID = ggp.ID
        WHERE EXISTS
            (
            SELECT 1 FROM C_CUST_MISSION_PROGRESS cmps
            WHERE cmps.CUSTOMER_ID = :customerId
            AND cms.MISSION_ID = cmps.MISSION_ID
            AND STATUS = :status)
        AND (cms.IS_DELETED IS NULL OR cms.IS_DELETED = 'N')
        AND (mgt.IS_DELETED IS NULL OR mgt.IS_DELETED = 0)
        AND (ggp.IS_DELETED IS NULL OR ggp.IS_DELETED = 0)
  """, nativeQuery = true)
  List<GiftPartner> findByCondition(Long customerId, String status);
}
