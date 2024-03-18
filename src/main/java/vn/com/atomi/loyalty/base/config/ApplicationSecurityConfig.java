package vn.com.atomi.loyalty.base.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementPortType;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.web.*;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ServletEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import vn.com.atomi.loyalty.base.exception.CustomAccessDeniedHandler;
import vn.com.atomi.loyalty.base.exception.CustomAuthenticationEntryPoint;
import vn.com.atomi.loyalty.base.security.AuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class ApplicationSecurityConfig {

  public static final String[] IGNORE_AUTHENTICATION_PATTERN =
      new String[] {
        "/",
        "/public/**",
        "/internal/**",
        "/error",
        "/favicon.ico",
        "/**/*.png",
        "/**/*.gif",
        "/**/*.svg",
        "/**/*.jpg",
        "/**/*.html",
        "/**/*.css",
        "/**/*.js",
        "/swagger-ui.html/**",
        "/swagger-ui/**",
        "/swagger-resources/**",
        "/v3/api-docs",
        "/v3/api-docs/**",
        "/webjars/**",
        "/csrf",
        "/actuator",
        "/actuator/**"
      };
  private final AuthenticationFilter jwtRequestFilter;
  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
  private final CustomAccessDeniedHandler customAccessDeniedHandler;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.cors(
            cors ->
                cors.configurationSource(
                    request -> {
                      CorsConfiguration configuration = new CorsConfiguration();
                      configuration.setAllowedOrigins(List.of("*"));
                      configuration.setAllowedMethods(List.of("*"));
                      configuration.setAllowedHeaders(List.of("*"));
                      return configuration;
                    }))
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            authorizationManagerRequestMatcherRegistry ->
                authorizationManagerRequestMatcherRegistry
                    .requestMatchers(IGNORE_AUTHENTICATION_PATTERN)
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .exceptionHandling(
            (exceptionHandling) ->
                exceptionHandling
                    .accessDeniedHandler(customAccessDeniedHandler)
                    .authenticationEntryPoint(customAuthenticationEntryPoint));

    http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
    return http.getSharedObject(AuthenticationManagerBuilder.class).build();
  }

  @Bean
  public RequestContextListener requestContextListener() {
    return new RequestContextListener();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping(
      WebEndpointsSupplier webEndpointsSupplier,
      ServletEndpointsSupplier servletEndpointsSupplier,
      ControllerEndpointsSupplier controllerEndpointsSupplier,
      EndpointMediaTypes endpointMediaTypes,
      CorsEndpointProperties corsProperties,
      WebEndpointProperties webEndpointProperties,
      Environment environment) {
    List<ExposableEndpoint<?>> allEndpoints = new ArrayList();
    Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier.getEndpoints();
    allEndpoints.addAll(webEndpoints);
    allEndpoints.addAll(servletEndpointsSupplier.getEndpoints());
    allEndpoints.addAll(controllerEndpointsSupplier.getEndpoints());
    String basePath = webEndpointProperties.getBasePath();
    EndpointMapping endpointMapping = new EndpointMapping(basePath);
    boolean shouldRegisterLinksMapping =
        this.shouldRegisterLinksMapping(webEndpointProperties, environment, basePath);
    return new WebMvcEndpointHandlerMapping(
        endpointMapping,
        webEndpoints,
        endpointMediaTypes,
        corsProperties.toCorsConfiguration(),
        new EndpointLinksResolver(allEndpoints, basePath),
        shouldRegisterLinksMapping);
  }

  @Bean
  public WebMvcConfigurer corsMappingConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        registry
            .addResourceHandler("swagger-ui.html")
            .addResourceLocations("classpath:/META-INF/resources/");
        registry
            .addResourceHandler("/webjars/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/");
      }
    };
  }

  private boolean shouldRegisterLinksMapping(
      WebEndpointProperties webEndpointProperties, Environment environment, String basePath) {
    return webEndpointProperties.getDiscovery().isEnabled()
        && (StringUtils.hasText(basePath)
            || ManagementPortType.get(environment).equals(ManagementPortType.DIFFERENT));
  }
}
