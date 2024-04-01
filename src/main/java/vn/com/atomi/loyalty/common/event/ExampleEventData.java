package vn.com.atomi.loyalty.common.event;

import lombok.*;
import vn.com.atomi.loyalty.base.event.EventData;

/**
 * @author haidv
 * @version 1.0
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExampleEventData extends EventData {

  private String example;
}
