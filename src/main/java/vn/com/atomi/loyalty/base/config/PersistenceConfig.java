package vn.com.atomi.loyalty.base.config;

import java.time.ZonedDateTime;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;
import vn.com.atomi.loyalty.base.constant.RequestConstant;

/**
 * @author haidv
 * @version 1.0
 */
@Configuration
@EnableJpaAuditing(
    auditorAwareRef = "auditorProvider",
    dateTimeProviderRef = "auditingDateTimeProvider")
public class PersistenceConfig {

  @Bean(name = "auditingDateTimeProvider")
  public DateTimeProvider dateTimeProvider() {
    return () -> Optional.of(ZonedDateTime.now());
  }

  @Bean
  AuditorAware<String> auditorProvider() {
    return new AuditorAwareImpl();
  }

  /**
   * Class implement for components that are aware of the application's current auditor. This will
   * be some kind of user mostly.
   *
   * @author haidv
   * @version 1.0
   */
  public static class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
      try {
        var userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return userId == null || userId.equals("anonymousUser")
            ? Optional.of(RequestConstant.SYSTEM)
            : Optional.of(userId);
      } catch (Exception e) {
        return Optional.of(RequestConstant.SYSTEM);
      }
    }
  }
}
