package vn.com.atomi.loyalty.common.service.impl;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Service;
import vn.com.atomi.loyalty.base.constant.RequestConstant;
import vn.com.atomi.loyalty.base.data.BaseService;
import vn.com.atomi.loyalty.base.utils.RequestUtils;
import vn.com.atomi.loyalty.common.dto.input.TransactionInput;
import vn.com.atomi.loyalty.common.dto.message.AllocationPointTransactionInput;
import vn.com.atomi.loyalty.common.dto.output.RuleOutput;
import vn.com.atomi.loyalty.common.dto.output.RulePOCOutput;
import vn.com.atomi.loyalty.common.entity.CCustMissionProgress;
import vn.com.atomi.loyalty.common.entity.GiftPartner;
import vn.com.atomi.loyalty.common.entity.GsGiftClaim;
import vn.com.atomi.loyalty.common.enums.ExpirePolicyType;
import vn.com.atomi.loyalty.common.enums.PointEventSource;
import vn.com.atomi.loyalty.common.enums.PointType;
import vn.com.atomi.loyalty.common.feign.LoyaltyConfigClient;
import vn.com.atomi.loyalty.common.feign.LoyaltyCoreClient;
import vn.com.atomi.loyalty.common.repository.CCustMissionProgressRepository;
import vn.com.atomi.loyalty.common.repository.GiftClaimRepository;
import vn.com.atomi.loyalty.common.repository.GiftPartnerRepository;
import vn.com.atomi.loyalty.common.service.MissionClaimRewardService;
import vn.com.atomi.loyalty.common.utils.Constants;
import vn.com.atomi.loyalty.common.utils.Utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionClaimRewardServiceImpl extends BaseService implements MissionClaimRewardService {

    private final CCustMissionProgressRepository progressRepository;

    private final GiftPartnerRepository giftPartnerRepository;

    private final GiftClaimRepository giftClaimRepository;

    private final LoyaltyCoreClient loyaltyCoreClient;

    private final LoyaltyConfigClient loyaltyConfigClient;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Integer execute() {
    // truy van toan bo nhiem vu co trang thai la complete
    List<CCustMissionProgress> custMissionProgresses = progressRepository.
            findMissionProgressByCondition(Constants.Mission.STATUS_COMPLETED);
    if(custMissionProgresses != null) {
      // xu ly khi co ban ghi thanh cong
      for(CCustMissionProgress mission : custMissionProgresses) {
          if(!Constants.Mission.TYPE_CHAIN.
                  equalsIgnoreCase(mission.getMissionType())) { // truong hop la (G) group hoac (M) mission
              boolean canCompleteParent =
                      Constants.Mission.GROUP_TYPE_AND.equals(mission.getGroupType())
                              ? checkGroupCompletion(mission)
                              : checkAnyMissionCompleted(mission);
              if (canCompleteParent) {
                  // neu thoa man dieu kien thi cap nhat parent id = COMPLETED
                  progressRepository.updateParentMission(
                          Constants.Mission.STATUS_COMPLETED,
                          mission.getCustomerId(),
                          mission.getParentId());
              }
          }
          this.handleMissionClaim(mission);
      }
    }
    return 0;
    }

    public void handleMissionClaim(CCustMissionProgress mission){

        // kiem tra nhiem vu co mapping voi qua gi khong
        List<GiftPartner> lstGift = giftPartnerRepository.findByCondition(mission.getCustomerId(), Constants.Mission.STATUS_COMPLETED);
        if(lstGift != null) {
            // tra thuong theo Gift
            for (GiftPartner giftPartner : lstGift) {
                if(Constants.Gift.TYPE_POINT.
                        equalsIgnoreCase(giftPartner.getGiftType())) {
                    // tra thuong cong point
                    plusPoint(mission, giftPartner);
                } else {
                    // dua vao bang gift claim
                    this.claimGift(mission, giftPartner);
                }
            }
        }
        mission.setStatus(Constants.Mission.STATUS_CLAIMED);
        progressRepository.save(mission);
    }

    public void plusPoint(CCustMissionProgress mission, GiftPartner giftPartner) {
        // kiem tra lai rule cho nay xem co the dung theo truong hop tao rule khong?????
        String executeId = Utils.generateUniqueId();
        ThreadContext.put(RequestConstant.REQUEST_ID, executeId);
        RulePOCOutput rulePOC = loyaltyConfigClient.getRulePoc(RequestUtils.extractRequestId(), "CASA").getData();
        RuleOutput rule = this.convertToRuleOutput(rulePOC);

        AllocationPointTransactionInput allocationTransaction = new AllocationPointTransactionInput();
        allocationTransaction.setAmount(Long.valueOf(String.valueOf(giftPartner.getPrice())));
        allocationTransaction.setRefNo(mission.getTxnRefNo());
        allocationTransaction.setTransactionAt(mission.getCompletedAt().atStartOfDay());
        allocationTransaction.setCurrency(giftPartner.getUnit());
        allocationTransaction.setTransactionType(PointEventSource.MISSION.name());
        allocationTransaction.setTransactionGroup("FUNDTF");
        // convert data
        var transaction = super.modelMapper.convertToTransactionInput(
                allocationTransaction,
                PointType.CONSUMPTION_POINT,
                giftPartner.getPrice(),
                mission.getCustomerId(),
                rule,
                PointEventSource.MISSION,
                this.getExpireDate(rule));
        // cong diem
        loyaltyCoreClient.plusAmount(executeId, transaction);
    }
    public void claimGift(CCustMissionProgress mission, GiftPartner giftPartner) {
        GsGiftClaim giftClaim = new GsGiftClaim();
        giftClaim.setCustomerId(mission.getCustomerId());
        giftClaim.setCifBank(mission.getCifNo()); // tam thoi chua co
        giftClaim.setGiftId(giftPartner.getId());
        giftClaim.setQuantity(1);
        giftClaim.setRefNo(mission.getTxnRefNo());
        giftClaimRepository.save(giftClaim);
    }
    private boolean checkGroupCompletion(CCustMissionProgress mission) {
      // check all mission in chain mission has any mission not completed
        Integer countCompleted = progressRepository.checkGroupCompletion(
                mission.getParentId(),
                mission.getCustomerId(),
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
                mission.getCustomerId(),
                Constants.Mission.STATUS_COMPLETED);
        if(countCompleted > 0) {
            return true;
        }
        return false;
    }
    public static RuleOutput convertToRuleOutput(RulePOCOutput rulePOC) {
        return RuleOutput.builder()
                .id(rulePOC.getId())
                .type(rulePOC.getType())
                .code(rulePOC.getCode())
                .name(rulePOC.getName())
                .pointType(rulePOC.getPointType())
                .campaignId(rulePOC.getCampaignId())
                .campaignCode(rulePOC.getCampaignCode())
                .budgetId(rulePOC.getBudgetId())
                .budgetCode(rulePOC.getCode())
                .startDate(rulePOC.getStartDate())
                .endDate(rulePOC.getEndDate())
                .status(rulePOC.getStatus())
                .expirePolicyType(ExpirePolicyType.NEVER)
                .expirePolicyValue(null)
                .ruleAllocationOutputs(List.of())
                .ruleBonusOutputs(List.of())
                .ruleConditionOutputs(List.of())
                .build();
    }

    private LocalDate getExpireDate(RuleOutput ruleOutput) {
        return switch (ruleOutput.getExpirePolicyType()) {
            case AFTER_DATE -> Utils.convertToLocalDate(ruleOutput.getExpirePolicyValue());
            case AFTER_DAY -> LocalDate.now().plusDays(Long.parseLong(ruleOutput.getExpirePolicyValue()));
            case FIRST_DATE_OF_MONTH -> LocalDate.now()
                    .plusMonths(Long.parseLong(ruleOutput.getExpirePolicyValue()))
                    .with(TemporalAdjusters.firstDayOfMonth());
            case NEVER -> null;
        };
    }
    @Transactional
    public void updateParentStatus(CCustMissionProgress mission) { // not used
      try {
            String sql = "UPDATE C_CUST_MISSION_PROGRESS SET STATUS =  ? " +
                    " WHERE CUSTOMER_ID = ? AND MISSION_ID = ? ";
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, Constants.Mission.STATUS_COMPLETED);
            query.setParameter(2, mission.getCustomerId());
            query.setParameter(3, mission.getParentId());
            int updatedCount = query.executeUpdate();
            if(updatedCount > 0) {
                System.out.println("Update Sucessfully MissionId; " + mission.getMissionId() + " | customerId:" + mission.getCustomerId());
            }
      } catch (Exception ex) {
          ex.printStackTrace();
      }
    }

}
