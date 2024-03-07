package vn.com.atomi.loyalty.common.repository;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import vn.com.atomi.loyalty.common.dao.UserRawInput;
import vn.com.atomi.loyalty.common.dto.output.RoleOutput;
import vn.com.atomi.loyalty.common.dto.output.UserOutput;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserDetailRepository {
  private final JdbcTemplate jdbc;
  private final ObjectMapper mapper =
      new ObjectMapper()
          .setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_SNAKE_CASE)
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  public Optional<UserOutput> get(Long id) {
    val mapList =
        jdbc.queryForList(
            """
                    select * from LOYALTY_COMMON.cm_user u
                     join LOYALTY_COMMON.cm_user_role ur on u.id = ur.user_id
                     join LOYALTY_COMMON.cm_role r on ur.role_id = r.id
                     where u.id = ? and u.is_deleted = 0""",
            id);
    if (mapList.isEmpty()) return Optional.empty();

    val rawList =
        mapList.stream().map(map -> mapper.convertValue(map, UserRawInput.class)).toList();

    val userOutput = mapper.convertValue(mapList.get(0), UserOutput.class);
    userOutput.setRoles(
        rawList.stream()
            .map(raw -> new RoleOutput(raw.getRoleId(), raw.getName()))
            .collect(Collectors.toSet()));

    return Optional.of(userOutput);
  }
}
