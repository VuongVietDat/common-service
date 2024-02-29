package vn.com.atomi.loyalty.base.data;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This is the super interface for the service class of applications. <br>
 * The service provides methods for read / write operations on multiple entities using resource
 * classes. <br>
 * This interface defines the most common methods that should be supported in all service classes.
 *
 * @author haidv
 * @version 1.0
 */
public abstract class BaseController {

  @Autowired private Validator validator;

  public void validateInput(Object input) {
    Set<ConstraintViolation<Object>> violations = validator.validate(input);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }
}
