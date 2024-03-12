package vn.com.atomi.loyalty.base.annotations;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.apache.logging.log4j.util.Strings;
import vn.com.atomi.loyalty.base.constant.DateConstant;

/**
 * @author haidv
 * @version 1.0
 */
@Target({
  METHOD,
  FIELD,
  ANNOTATION_TYPE,
  CONSTRUCTOR,
  PARAMETER,
})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = DateTimeValidator.Validator.class)
public @interface DateTimeValidator {

  String charset() default "UTF-8";

  boolean required() default true;

  String pattern() default DateConstant.STR_PLAN_DD_MM_YYYY_HH_MM_SS_STROKE;

  String message() default "{validation.constraints.DateTimeValidator.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  class Validator implements ConstraintValidator<DateTimeValidator, String> {

    private DateTimeValidator annotation;

    @Override
    public void initialize(DateTimeValidator annotation) {
      this.annotation = annotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
      if (Strings.isBlank(value) && !annotation.required()) {
        return true; // skipped.
      }
      if (Strings.isBlank(value) && annotation.required()) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("must not be blank").addConstraintViolation();
        return false;
      }
      try {
        var dateFormatter = DateTimeFormatter.ofPattern(annotation.pattern());
        dateFormatter.parse(value);
      } catch (DateTimeParseException e) {
        context.disableDefaultConstraintViolation();
        context
            .buildConstraintViolationWithTemplate(
                String.format(
                    "Text '%s' could not be match format %s", value, annotation.pattern()))
            .addConstraintViolation();
        return false;
      }
      return true;
    }
  }
}
