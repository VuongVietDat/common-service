package vn.com.atomi.loyalty.common.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.com.atomi.loyalty.base.data.ResponseData;
import vn.com.atomi.loyalty.base.data.ResponseUtils;
import vn.com.atomi.loyalty.common.service.ExampleService;

/**
 * @author haidv
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
public class ExampleController {

  private final ExampleService exampleService;

  @PostMapping("/public/example")
  public ResponseEntity<ResponseData<String>> example() {
    return ResponseUtils.success(exampleService.example());
  }
}
