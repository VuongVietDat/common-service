package vn.com.atomi.loyalty.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.com.atomi.loyalty.common.entity.CCustMissionProgress;

import java.util.List;

public interface CCustMissionProgressRepository extends JpaRepository<CCustMissionProgress, Long> {

    @Query(value= """
        SELECT cmps FROM CCustMissionProgress cmps 
        WHERE cmps.status = :status
        AND cmps.missionType != :missionType
    """)
    List<CCustMissionProgress> findMissionProgressByCondition (String status, String missionType);

    @Query(value= """
        SELECT count(1) FROM CCustMissionProgress cmps 
        WHERE cmps.customer = :customerId
        AND cmps.parentId = :parentId
        AND cmps.status != :status
    """)
    Integer checkGroupCompletion(Long parentId, Long customerId, String status);

    @Query(value= """
        SELECT count(1) FROM CCustMissionProgress cmps 
        WHERE cmps.customer = :customerId
        AND cmps.parentId = :parentId
        AND cmps.status = :status
    """)
    Integer checkMissionCompletion(Long parentId, Long customerId, String status);
    @Query(value= """
        UPDATE CCustMissionProgress cmps 
        SET cmps.status = :status
        WHERE cmps.customer = :customerId
        AND cmps.missionId = :missionId
    """)
    void updateParentMission(Long missionId, Long customerId, String status);

    @Query(value= """
        UPDATE CCustMissionProgress cmps 
        SET cmps.status = :status
        WHERE cmps.customer = :customerId
        AND cmps.parentId = :parentId
    """)
    void updateGroupMission(Long parentId, Long customerId, String status);

}