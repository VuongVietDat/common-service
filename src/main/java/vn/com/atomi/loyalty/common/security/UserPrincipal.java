package vn.com.atomi.loyalty.common.security;

import java.util.ArrayList;
import java.util.Collection;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;
import vn.com.atomi.loyalty.common.dto.output.UserOutput;

@Getter
public class UserPrincipal implements UserDetails {

  private final String sessionId;
  private final boolean issueByUser;
  private final Collection<SimpleGrantedAuthority> authorities;
  private final String clientId;
  private UserOutput userOutput;
  private String authHeader;

  public UserPrincipal(String clientId, String sessionId, boolean issueByUser) {
    this.clientId = clientId;
    this.sessionId = sessionId;
    this.issueByUser = issueByUser;
    authorities = new ArrayList<>();
  }

  public UserPrincipal(
      String clientId,
      boolean issueByUser,
      UserOutput userOutput,
      String sessionId,
      String authHeader) {
    this.clientId = clientId;
    this.userOutput = userOutput;
    this.sessionId = sessionId;
    this.authHeader = authHeader;
    this.issueByUser = issueByUser;
    authorities = new ArrayList<>();
    if (!CollectionUtils.isEmpty(userOutput.getUserRoleOutputs())) {
      userOutput
          .getUserRoleOutputs()
          .forEach(v2 -> authorities.add(new SimpleGrantedAuthority("ROLE_" + v2.getCode())));
    }
  }

  @Override
  public String getPassword() {
    return null;
  }

  @Override
  public String getUsername() {
    return issueByUser ? userOutput.getId() : clientId;
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
