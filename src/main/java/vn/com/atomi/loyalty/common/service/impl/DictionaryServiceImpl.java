package vn.com.atomi.loyalty.common.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.com.atomi.loyalty.base.data.BaseService;
import vn.com.atomi.loyalty.common.dto.output.DictionaryOutput;
import vn.com.atomi.loyalty.common.entity.Dictionary;
import vn.com.atomi.loyalty.common.enums.Status;
import vn.com.atomi.loyalty.common.repository.DictionaryRepository;
import vn.com.atomi.loyalty.common.repository.redis.MasterDataRepository;
import vn.com.atomi.loyalty.common.service.DictionaryService;

/**
 * @author haidv
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class DictionaryServiceImpl extends BaseService implements DictionaryService {

  private final DictionaryRepository dictionaryRepository;

  private final MasterDataRepository masterDataRepository;

  @Override
  public List<DictionaryOutput> getDictionaries(String type) {
    List<Dictionary> dictionaries = dictionaryRepository.findByDeletedFalseAndStatus(Status.ACTIVE);
    var out = super.modelMapper.convertToDictionaryOutputs(dictionaries);
    if (!CollectionUtils.isEmpty(out)) {
      masterDataRepository.putDictionary(out);
    }
    return out;
  }
}
