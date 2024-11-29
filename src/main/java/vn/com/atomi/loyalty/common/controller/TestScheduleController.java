package vn.com.atomi.loyalty.common.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.com.atomi.loyalty.base.data.BaseController;
import vn.com.atomi.loyalty.base.data.ResponseData;
import vn.com.atomi.loyalty.base.data.ResponseUtils;
import vn.com.atomi.loyalty.common.service.ScheduleService;
import vn.com.atomi.loyalty.common.service.impl.MissionClaimRewardServiceImpl;

/**
 * @author haidv
 * @version 1.0
 */
@RequiredArgsConstructor
@RestController
public class TestScheduleController extends BaseController {
  private final MissionClaimRewardServiceImpl scheduleService;

  @Operation(summary = "Bắt đầu một tiến trình")
  @PutMapping("/test-schedule")
  public ResponseEntity<ResponseData<Void>> startJobNow()
      throws SchedulerException, ClassNotFoundException {
    scheduleService.execute();
    return ResponseUtils.success();
  }

}
