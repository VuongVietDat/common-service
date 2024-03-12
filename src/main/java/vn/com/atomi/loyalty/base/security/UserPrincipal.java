package vn.com.atomi.loyalty.base.security;

import java.util.Collection;
import java.util.stream.Collectors;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

@Getter
public class UserPrincipal implements UserDetails {

  private final String sessionId;
  private final String username;
  private UserOutput userOutput;
  private Collection<SimpleGrantedAuthority> authorities;

  public UserPrincipal(String sessionId, String username, UserOutput userOutput) {
    this.sessionId = sessionId;
    this.username = username;
    this.userOutput = userOutput;
    if (!CollectionUtils.isEmpty(userOutput.getPermissions())) {
      this.authorities =
          userOutput.getPermissions().stream()
              .map(v -> new SimpleGrantedAuthority(v.getName()))
              .collect(Collectors.toList());
    }
  }

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
