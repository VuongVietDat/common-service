package vn.com.atomi.loyalty.base.swagger;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.UUID;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import vn.com.atomi.loyalty.base.constant.RequestConstant;

/**
 * @author haidv
 * @version 1.0
 */
@Configuration
public class SwaggerConfiguration {

  @Value("${spring.application.name}")
  private String serviceName;

  @Bean
  public OperationCustomizer customGlobalHeaders() {

    return (Operation operation, HandlerMethod handlerMethod) -> {
      operation.addParametersItem(
          new Parameter()
              .in(ParameterIn.HEADER.toString())
              .schema(new StringSchema())
              .name(RequestConstant.REQUEST_ID)
              .example(UUID.randomUUID().toString())
              .required(true));
      operation.addParametersItem(
          new Parameter()
              .in(ParameterIn.HEADER.toString())
              .schema(new StringSchema())
              .name(RequestConstant.CLIENT_IP)
              .example("127.0.0.1")
              .required(true));
      operation.addParametersItem(
          new Parameter()
              .in(ParameterIn.HEADER.toString())
              .schema(new StringSchema())
              .name(RequestConstant.CLIENT_TIME)
              .example(System.currentTimeMillis())
              .required(true));
      operation.addParametersItem(
          new Parameter()
              .in(ParameterIn.HEADER.toString())
              .schema(new StringSchema())
              .name(RequestConstant.CLIENT_PLATFORM)
              .example("LV24H_KHCN")
              .required(true));
      operation.addParametersItem(
          new Parameter()
              .in(ParameterIn.HEADER.toString())
              .schema(new StringSchema())
              .name(RequestConstant.DEVICE_ID)
              .example(UUID.randomUUID())
              .required(false));
      operation.addParametersItem(
          new Parameter()
              .in(ParameterIn.HEADER.toString())
              .schema(new StringSchema())
              .name(RequestConstant.DEVICE_NAME)
              .example("HAIDV")
              .required(false));
      operation.addParametersItem(
          new Parameter()
              .in(ParameterIn.HEADER.toString())
              .schema(new StringSchema())
              .name(RequestConstant.APPLICATION_VERSION)
              .example("1.0.0")
              .required(true));
      operation.addParametersItem(
          new Parameter()
              .in(ParameterIn.HEADER.toString())
              .schema(new StringSchema())
              .name(RequestConstant.DEVICE_TYPE)
              .example("IOS")
              .required(false));

      return operation;
    };
  }

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info().title(serviceName).version("1.0.0"))
        .components(
            new Components()
                .addSecuritySchemes(
                    "Authorization",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .name("Authorization")))
        .addSecurityItem(new SecurityRequirement().addList("Authorization"));
  }
}
