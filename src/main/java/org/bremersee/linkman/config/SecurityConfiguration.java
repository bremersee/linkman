/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bremersee.linkman.config;

import lombok.extern.slf4j.Slf4j;
import org.bremersee.security.authentication.AuthenticationProperties;
import org.bremersee.security.authentication.JsonPathReactiveJwtConverter;
import org.bremersee.security.authentication.PasswordFlowReactiveAuthenticationManager;
import org.bremersee.security.core.AuthorityConstants;
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;

/**
 * The security configuration.
 *
 * @author Christian Bremer
 */
@ConditionalOnWebApplication
@Configuration
@EnableWebFluxSecurity
@Slf4j
public class SecurityConfiguration {

  /**
   * The jwt login.
   */
  @ConditionalOnWebApplication
  @ConditionalOnProperty(
      prefix = "bremersee.security.authentication",
      name = "enable-jwt-support",
      havingValue = "true")
  @Configuration
  static class JwtLogin {

    private String[] adminRoles;

    private JsonPathReactiveJwtConverter jwtConverter;

    private PasswordFlowReactiveAuthenticationManager passwordFlowAuthenticationManager;

    /**
     * Instantiates a new jwt login.
     *
     * @param properties the properties
     * @param jwtConverter the jwt converter
     * @param passwordFlowAuthenticationManager the password flow authentication manager
     */
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public JwtLogin(
        LinkmanProperties properties,
        JsonPathReactiveJwtConverter jwtConverter,
        PasswordFlowReactiveAuthenticationManager passwordFlowAuthenticationManager) {
      this.adminRoles = properties.getAdminRoles().toArray(new String[0]);
      this.jwtConverter = jwtConverter;
      this.passwordFlowAuthenticationManager = passwordFlowAuthenticationManager;
    }

    /**
     * Builds the OAuth2 resource server filter chain.
     *
     * @param http the http
     * @return the security web filter chain
     */
    @Bean
    @Order(51)
    public SecurityWebFilterChain oauth2ResourceServerFilterChain(ServerHttpSecurity http) {

      log.info("msg=[Creating resource server filter chain.]");
      http
          .securityMatcher(new NegatedServerWebExchangeMatcher(EndpointRequest.toAnyEndpoint()))
          .csrf().disable()
          .oauth2ResourceServer()
          .jwt()
          .jwtAuthenticationConverter(jwtConverter);

      http
          .authorizeExchange()
          .pathMatchers(HttpMethod.OPTIONS).permitAll()
          // .pathMatchers("/swagger-ui.html").permitAll()
          // .pathMatchers("/webjars/**").permitAll()
          .pathMatchers("/v3/**", "/swagger-ui.html", "/webjars/**").permitAll()
          .pathMatchers("/api/menu").permitAll()
          .pathMatchers("/api/**").hasAnyAuthority(adminRoles)
          .anyExchange().authenticated();

      return http.build();
    }

    /**
     * Builds the actuator filter chain.
     *
     * @param http the http security configuration object
     * @return the security web filter chain
     */
    @Bean
    @Order(52)
    public SecurityWebFilterChain actuatorFilterChain(ServerHttpSecurity http) {

      log.info("msg=[Creating actuator filter chain.]");
      http
          .securityMatcher(EndpointRequest.toAnyEndpoint())
          .csrf().disable()
          .httpBasic()
          .authenticationManager(passwordFlowAuthenticationManager);

      http
          .authorizeExchange()
          .pathMatchers(HttpMethod.OPTIONS).permitAll()
          .matchers(EndpointRequest.to(HealthEndpoint.class)).permitAll()
          .matchers(EndpointRequest.to(InfoEndpoint.class)).permitAll()
          .anyExchange().hasAuthority(AuthorityConstants.ACTUATOR_ROLE_NAME);

      return http.build();
    }
  }

  /**
   * The basic auth login.
   */
  @ConditionalOnWebApplication
  @ConditionalOnProperty(
      prefix = "bremersee.security.authentication",
      name = "enable-jwt-support",
      havingValue = "false", matchIfMissing = true)
  @Configuration
  @EnableConfigurationProperties(AuthenticationProperties.class)
  static class BasicAuthLogin {

    private AuthenticationProperties properties;

    private String[] adminRoles;

    /**
     * Instantiates a new basic auth login.
     *
     * @param authenticationProperties the authentication properties
     * @param linkmanProperties the linkman properties
     */
    public BasicAuthLogin(
        AuthenticationProperties authenticationProperties,
        LinkmanProperties linkmanProperties) {
      this.properties = authenticationProperties;
      this.adminRoles = linkmanProperties.getAdminRoles().toArray(new String[0]);
    }

    /**
     * User details service map reactive user details service.
     *
     * @return the map reactive user details service
     */
    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
      return new MapReactiveUserDetailsService(properties.buildBasicAuthUserDetails());
    }

    /**
     * Builds the basic auth resource server filter chain.
     *
     * @param http the http
     * @return the security web filter chain
     */
    @Bean
    @Order(51)
    public SecurityWebFilterChain oauth2ResourceServerFilterChain(ServerHttpSecurity http) {

      log.info("msg=[Creating resource server filter chain.]");
      return http
          .securityMatcher(new NegatedServerWebExchangeMatcher(EndpointRequest.toAnyEndpoint()))
          .csrf().disable()
          .authorizeExchange()
          .pathMatchers(HttpMethod.OPTIONS).permitAll()
          // .pathMatchers("/swagger-ui.html").permitAll()
          // .pathMatchers("/webjars/**").permitAll()
          .pathMatchers("/v3/**", "/swagger-ui.html", "/webjars/**").permitAll()
          .pathMatchers("/api/menu").permitAll()
          .pathMatchers("/api/**").hasAnyAuthority(adminRoles)
          .anyExchange().authenticated()
          .and()
          .httpBasic()
          .and()
          .formLogin().disable()
          .build();
    }

    /**
     * Builds the actuator filter chain.
     *
     * @param http the http security configuration object
     * @return the security web filter chain
     */
    @Bean
    @Order(52)
    public SecurityWebFilterChain actuatorFilterChain(ServerHttpSecurity http) {

      log.info("msg=[Creating actuator filter chain.]");
      return http
          .securityMatcher(EndpointRequest.toAnyEndpoint())
          .csrf().disable()
          .authorizeExchange()
          .pathMatchers(HttpMethod.OPTIONS).permitAll()
          .matchers(EndpointRequest.to(HealthEndpoint.class)).permitAll()
          .matchers(EndpointRequest.to(InfoEndpoint.class)).permitAll()
          .anyExchange().hasAuthority(AuthorityConstants.ACTUATOR_ROLE_NAME)
          .and()
          .httpBasic()
          .and()
          .formLogin().disable()
          .build();
    }
  }
}