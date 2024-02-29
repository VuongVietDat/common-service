package vn.com.atomi.loyalty.common.dto.output;

import java.time.ZonedDateTime;
import lombok.*;

/**
 * @author haidv
 * @version 1.0
 */
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserClientOutput {

  private String name;

  private String clientId;

  private ZonedDateTime joinAt;
}
