package vn.com.atomi.loyalty.base.data;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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

  protected void validateInput(Object input) {
    Set<ConstraintViolation<Object>> violations = validator.validate(input);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }

  protected Pageable pageable(Integer pageNo, Integer pageSize, String sort) {
    pageSize = (pageSize == null || pageSize <= 0 || pageSize > 200) ? 200 : pageSize;
    pageNo = (pageNo == null || pageNo <= 0) ? 1 : pageNo;
    Sort srt = null;
    if (sort != null) {
      String[] part = sort.split("_");
      for (String s : part) {
        String[] tmp = s.split(":");
        if (tmp.length == 2) {
          if (srt == null) {
            srt = Sort.by(Sort.Direction.fromString(tmp[1].trim()), tmp[0].trim());
          } else {
            srt.and(Sort.by(Sort.Direction.fromString(tmp[1].trim()), tmp[0].trim()));
          }
        }
      }
    }
    if (srt == null) {
      srt = Sort.unsorted();
    }
    return PageRequest.of(pageNo - 1, pageSize, srt);
  }
}
