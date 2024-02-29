package vn.com.atomi.loyalty.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.com.atomi.loyalty.common.entity.RetriesMessage;
import vn.com.atomi.loyalty.common.event.RetriesMessageData;

/**
 * @author haidv
 * @version 1.0
 */
@Mapper
public interface ModelMapper {

  @Mapping(source = "retryMessageId", target = "messageId")
  RetriesMessage convertToRetriesMessage(RetriesMessageData retriesMessageData);
}
