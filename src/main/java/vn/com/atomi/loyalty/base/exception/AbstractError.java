package vn.com.atomi.loyalty.base.exception;

import org.springframework.http.HttpStatus;

/**
 * @author haidv
 * @version 1.0
 */
public interface AbstractError {

  String getMessage();

  int getCode();

  HttpStatus getHttpStatus();
}
