package vn.com.atomi.loyalty.common.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.atomi.loyalty.base.data.BaseController;
import vn.com.atomi.loyalty.base.data.ResponseData;
import vn.com.atomi.loyalty.base.data.ResponseUtils;
import vn.com.atomi.loyalty.common.repository.Lv24hRepository;
import vn.com.atomi.loyalty.common.service.ScheduleService;

/**
 * @author haidv
 * @version 1.0
 */
@RequiredArgsConstructor
@RestController
public class ScheduleController extends BaseController {
  private final Lv24hRepository repo;
  private final ScheduleService scheduleService;

  @Operation(summary = "Bắt đầu một tiến trình")
  @PutMapping("/schedule/{id}/start-now")
  public ResponseEntity<ResponseData<Void>> startJobNow(
      @Parameter(description = "ID tiến trình") @PathVariable Long id)
      throws SchedulerException, ClassNotFoundException {
    scheduleService.startJobNow(id);
    return ResponseUtils.success();
  }

  @Operation(summary = "Dừng một tiến trình")
  @PutMapping("/schedule/{id}/pause")
  public ResponseEntity<ResponseData<Void>> pauseJob(
      @Parameter(description = "ID tiến trình") @PathVariable Long id) throws SchedulerException {
    scheduleService.pauseJob(id);
    return ResponseUtils.success();
  }

  @Operation(summary = "Xóa một tiến trình")
  @DeleteMapping("/schedule/{id}")
  public ResponseEntity<ResponseData<Void>> deleteJob(
      @Parameter(description = "ID tiến trình") @PathVariable Long id) throws SchedulerException {
    scheduleService.deleteJob(id);
    return ResponseUtils.success();
  }

  @GetMapping("/public/test")
  public void test() {
     repo.selects(21059);
  }
}
