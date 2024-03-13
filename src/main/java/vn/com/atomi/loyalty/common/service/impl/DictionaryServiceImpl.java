package vn.com.atomi.loyalty.common.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
  public List<DictionaryOutput> getDictionaries(String type, Status status, boolean isSubLeaf) {
    List<Dictionary> dictionaries = dictionaryRepository.findByDeletedFalse();
    var node = super.modelMapper.convertToDictionaryOutputs(dictionaries);
    if (!CollectionUtils.isEmpty(node)) {
      masterDataRepository.putDictionary(node);
    }
    List<DictionaryOutput> leafs =
        node.stream()
            .filter(
                v ->
                    (StringUtils.isEmpty(type) || type.equals(v.getParentCode()))
                        && (status == null || v.getStatus().equals(status)))
            .collect(Collectors.toList());
    if (StringUtils.isNotBlank(type) && isSubLeaf) {
      List<String> leafCode = leafs.stream().map(DictionaryOutput::getCode).toList();
      var subLeaf = node.stream().filter(v -> leafCode.contains(v.getParentCode())).toList();
      leafs.addAll(subLeaf);
    }
    return leafs;
  }
}
