package vn.com.atomi.loyalty.common.service.impl;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.com.atomi.loyalty.base.constant.RequestConstant;
import vn.com.atomi.loyalty.base.data.BaseService;
import vn.com.atomi.loyalty.base.event.MessageData;
import vn.com.atomi.loyalty.base.event.MessageInterceptor;
import vn.com.atomi.loyalty.common.dto.message.Lv24hCustomerMessage;
import vn.com.atomi.loyalty.common.dto.output.SourceDataMapOutput;
import vn.com.atomi.loyalty.common.entity.CCustMissionProgress;
import vn.com.atomi.loyalty.common.enums.EventAction;
import vn.com.atomi.loyalty.common.enums.SourceGroup;
import vn.com.atomi.loyalty.common.enums.Status;
import vn.com.atomi.loyalty.common.feign.LoyaltyConfigClient;
import vn.com.atomi.loyalty.common.repository.CCustMissionProgressRepository;
import vn.com.atomi.loyalty.common.repository.Lv24hRepository;
import vn.com.atomi.loyalty.common.repository.redis.EtlLastCustomerRepository;
import vn.com.atomi.loyalty.common.service.Lv24hCustomerService;
import vn.com.atomi.loyalty.common.service.MissionClaimRewardService;
import vn.com.atomi.loyalty.common.utils.Constants;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MissionClaimRewardServiceImpl extends BaseService implements MissionClaimRewardService {

  private final CCustMissionProgressRepository progressRepository;

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public Integer execute() {
    // truy van toan bo nhiem vu co trang thai la complete
    List<CCustMissionProgress> custMissionProgresses = progressRepository.
            findMissionProgressByCondition(Constants.Mission.STATUS_COMPLETED, Constants.Mission.TYPE_CHAIN);
    if(custMissionProgresses != null) {
      // xu ly khi co ban ghi thanh cong
      for(CCustMissionProgress mission : custMissionProgresses) {
          boolean canCompleteParent =
                  Constants.Mission.GROUP_TYPE_AND.equals(mission.getGroupType())
                      ? checkGroupCompletion(mission)
                      : checkAnyMissionCompleted(mission);
          if(canCompleteParent) {
              this.updateParentStatus(mission);
          }
      }
    }

    return 0;
  }
    private boolean checkGroupCompletion(CCustMissionProgress mission) {
      // check all mission in chain mission has any mission not completed
        Integer countCompleted = progressRepository.checkGroupCompletion(
                mission.getParentId(),
                mission.getCustomer(),
                Constants.Mission.STATUS_COMPLETED);
        if(countCompleted == 0) {
            return true;
        }
        return false;
    }
    private boolean checkAnyMissionCompleted(CCustMissionProgress mission) {
        // Check if at least one mission is 'COMPLETED' for OR type
        Integer countCompleted = progressRepository.checkMissionCompletion(
                mission.getParentId(),
                mission.getCustomer(),
                Constants.Mission.STATUS_COMPLETED);
        if(countCompleted > 0) {
            return true;
        }
        return false;
    }

    @Transactional
    public void updateParentStatus(CCustMissionProgress mission) {
      try {
            String sql = "UPDATE C_CUST_MISSION_PROGRESS SET STATUS =  ? " +
                    " WHERE CUSTOMER_ID = ? AND MISSION_ID = ? ";
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, Constants.Mission.STATUS_COMPLETED);
            query.setParameter(2, mission.getCustomer());
            query.setParameter(3, mission.getParentId());
            int updatedCount = query.executeUpdate();
            if(updatedCount > 0) {
                System.out.println("Update Sucessfully MissionId; " + mission.getMissionId() + " | customerId:" + mission.getCustomer());
            }
      } catch (Exception ex) {
          ex.printStackTrace();
      }
    }

}
