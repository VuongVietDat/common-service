package vn.com.atomi.loyalty.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import vn.com.atomi.loyalty.base.event.CoreEvent;

/**
 * @author haidv
 * @version 1.0
 */
@Getter
@AllArgsConstructor
public enum ServiceEvent implements CoreEvent {
  EXAMPLE("EXAMPLE", "serviceDrivenEventListener", "example"),
  ;

  private final String eventName;

  private final String handleEventBeanName;

  private final String handleEventFunctionName;
}
