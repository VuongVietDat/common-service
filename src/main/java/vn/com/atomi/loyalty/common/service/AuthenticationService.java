package vn.com.atomi.loyalty.common.service;

import vn.com.atomi.loyalty.common.dto.input.LoginInput;
import vn.com.atomi.loyalty.common.dto.output.LoginOutput;
import vn.com.atomi.loyalty.common.dto.output.UserOutput;

/**
 * @author haidv
 * @version 1.0
 */
public interface AuthenticationService {

  LoginOutput login(LoginInput loginInput);

  LoginOutput renewToken(String token);

  void logout(String token);

  UserOutput getUser();
}
