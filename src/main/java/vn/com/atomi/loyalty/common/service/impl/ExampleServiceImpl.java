package vn.com.atomi.loyalty.common.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.com.atomi.loyalty.base.data.BaseService;
import vn.com.atomi.loyalty.common.dto.input.LoginInput;
import vn.com.atomi.loyalty.common.feign.LoyaltyCoreClient;
import vn.com.atomi.loyalty.common.service.ExampleService;
import vn.com.atomi.loyalty.common.utils.Utils;

/**
 * @author haidv
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class ExampleServiceImpl extends BaseService implements ExampleService {

  private final LoyaltyCoreClient loyaltyCoreClient;

  @Override
  public String example() {
    return loyaltyCoreClient
        .exampleSuccess(
            super.getRequestId(), Utils.generateUniqueId(), new LoginInput("test", "test"))
        .getData();
    //    var r = RandomUtils.nextInt(1, 4);
    //    if (r == 1) {
    //      return loyaltyCoreClient.exampleSuccess().getData();
    //    }
    //    if (r == 2) {
    //      return loyaltyCoreClient.exampleError().getData();
    //    }
    //    if (r == 3) {
    //      return loyaltyCoreClient.exampleErrorFallBack().getData();
    //    }
    //    return null;
  }
}
