package vn.com.atomi.loyalty.common.dto.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.*;
import vn.com.atomi.loyalty.base.constant.DateConstant;

/**
 * @author haidv
 * @version 1.0
 */
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserOutput {

  private String id;

  @JsonIgnore private String password;

  private String fullName;

  private String email;

  private String mobile;

  private String address;

  private String postalCode;

  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonFormat(pattern = DateConstant.STR_PLAN_DD_MM_YYYY)
  private LocalDate birthday;

  private String country;

  private Boolean enabled;

  private Boolean emailVerified;

  private Long loginFailed;

  private String avatar;

  private ZonedDateTime lockExpired;

  private List<UserClientOutput> userClientOutputs;

  private List<UserRoleOutput> userRoleOutputs;

  private String introducer;
}
