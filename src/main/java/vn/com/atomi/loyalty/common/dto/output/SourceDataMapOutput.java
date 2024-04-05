package vn.com.atomi.loyalty.common.dto.output;

import lombok.*;
import vn.com.atomi.loyalty.common.enums.SourceGroup;
import vn.com.atomi.loyalty.common.enums.Status;

/**
 * @author haidv
 * @version 1.0
 */
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SourceDataMapOutput {

  private Long id;

  private String sourceId;

  private String sourceCode;

  private String sourceName;

  private String destinationId;

  private String destinationCode;

  private String sourceType;

  private SourceGroup sourceGroup;

  private Status status;
}
