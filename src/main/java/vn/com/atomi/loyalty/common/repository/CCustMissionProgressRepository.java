package vn.com.atomi.loyalty.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.com.atomi.loyalty.common.entity.CCustMissionProgress;

import java.util.List;

public interface CCustMissionProgressRepository extends JpaRepository<CCustMissionProgress, Long> {

    @Query(value= """
        SELECT cmps FROM CCustMissionProgress cmps 
        WHERE cmps.status = :status
    """)
    List<CCustMissionProgress> findMissionProgressByCondition (String status);

    @Query(value= """
        SELECT count(1) FROM CCustMissionProgress cmps 
        WHERE cmps.customerId = :customerId
        AND cmps.parentId = :parentId
        AND cmps.status != :status
    """)
    Integer checkGroupCompletion(Long parentId, Long customerId, String status);

    @Query(value= """
        SELECT count(1) FROM CCustMissionProgress cmps 
        WHERE cmps.customerId = :customerId
        AND cmps.parentId = :parentId
        AND cmps.status = :status
    """)
    Integer checkMissionCompletion(Long parentId, Long customerId, String status);
    @Query(value= """
        UPDATE CCustMissionProgress cmps 
        SET cmps.status = :status
        WHERE cmps.customerId = :customerId
        AND cmps.missionId = :missionId
    """)
    void updateParentMission1(Long missionId, Long customerId, String status);

    @Query(value= """
        UPDATE C_CUST_MISSION_PROGRESS SET STATUS = :status
        WHERE CUSTOMER_ID = :customerId 
        AND MISSION_ID = :missionId
    """, nativeQuery = true)
    void updateParentMission(String status,Long customerId, Long missionId );

}