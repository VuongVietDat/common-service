package vn.com.atomi.loyalty.base.swagger;

import lombok.Getter;
import lombok.Setter;

/**
 * @author haidv
 * @version 1.0
 */
@Setter
@Getter
public class GlobalOperationParameter {

  private String name;

  private String description;

  private String modelRef;

  private String parameterType;

  private boolean required;
}
