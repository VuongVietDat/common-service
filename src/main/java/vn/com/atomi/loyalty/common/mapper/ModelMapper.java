package vn.com.atomi.loyalty.common.mapper;

import java.time.LocalDate;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.com.atomi.loyalty.base.event.RetriesMessageData;
import vn.com.atomi.loyalty.base.security.PermissionOutput;
import vn.com.atomi.loyalty.base.security.RoleOutput;
import vn.com.atomi.loyalty.base.security.UserOutput;
import vn.com.atomi.loyalty.common.dto.input.TransactionInput;
import vn.com.atomi.loyalty.common.dto.message.AllocationPointTransactionInput;
import vn.com.atomi.loyalty.common.dto.output.DictionaryOutput;
import vn.com.atomi.loyalty.common.dto.output.RuleOutput;
import vn.com.atomi.loyalty.common.entity.*;
import vn.com.atomi.loyalty.common.enums.PointEventSource;
import vn.com.atomi.loyalty.common.enums.PointType;

/**
 * @author haidv
 * @version 1.0
 */
@Mapper
public interface ModelMapper {

  @Mapping(source = "retryMessageId", target = "messageId")
  RetriesMessage convertToRetriesMessage(RetriesMessageData retriesMessageData);

  UserOutput toUserOutput(User user);

  RoleOutput toRoleOutput(Role role);

  PermissionOutput toPermissionOutput(Permission permission);

  List<DictionaryOutput> convertToDictionaryOutputs(List<Dictionary> dictionaries);

  @Mapping(source = "messageId", target = "retryMessageId")
  @Mapping(target = "messageId", ignore = true)
  RetriesMessageData convertToRetriesMessageData(RetriesMessage retriesMessage);

  @Mapping(target = "eventSource", source = "eventSource")
  @Mapping(target = "expireAt", source = "expireAt")
  @Mapping(target = "customerId", source = "customerId")
  @Mapping(target = "ruleId", source = "ruleOutput.id")
  @Mapping(target = "ruleCode", source = "ruleOutput.code")
  @Mapping(target = "pointType", source = "pointType")
  @Mapping(target = "amount", source = "amount")
  @Mapping(target = "transactionAmount", source = "allocationPointTransactionInput.amount")
  TransactionInput convertToTransactionInput(
          AllocationPointTransactionInput allocationPointTransactionInput,
          PointType pointType,
          Long amount,
          Long customerId,
          RuleOutput ruleOutput,
          PointEventSource eventSource,
          LocalDate expireAt);
}
