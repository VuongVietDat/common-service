package vn.com.atomi.loyalty.common.service;

import java.util.List;
import vn.com.atomi.loyalty.common.dto.output.DictionaryOutput;

/**
 * @author haidv
 * @version 1.0
 */
public interface DictionaryService {

  List<DictionaryOutput> getDictionaries(String type);
}
