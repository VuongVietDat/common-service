package vn.com.atomi.loyalty.base.security;

import java.util.Collection;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class UserPrincipal implements UserDetails {

  private final String sessionId;
  private final String username;
  private final Collection<SimpleGrantedAuthority> authorities;

  public UserPrincipal(
      String sessionId, String username, Collection<SimpleGrantedAuthority> authorities) {
    this.sessionId = sessionId;
    this.username = username;
    this.authorities = authorities;
  }

  @Override
  public String getPassword() {
    return null;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return false;
  }

  @Override
  public boolean isAccountNonLocked() {
    return false;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return false;
  }

  @Override
  public boolean isEnabled() {
    return false;
  }
}
