package vn.com.atomi.loyalty.common.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.com.atomi.loyalty.common.dto.output.DictionaryOutput;
import vn.com.atomi.loyalty.base.security.PermissionOutput;
import vn.com.atomi.loyalty.base.security.RoleOutput;
import vn.com.atomi.loyalty.base.security.UserOutput;
import vn.com.atomi.loyalty.common.entity.*;
import vn.com.atomi.loyalty.common.event.RetriesMessageData;

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
}
