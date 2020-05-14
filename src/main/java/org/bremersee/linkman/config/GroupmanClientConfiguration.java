/*
 * Copyright 2020 the original author or authors.
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
import org.bremersee.exception.RestApiExceptionParser;
import org.bremersee.groupman.api.GroupWebfluxControllerApi;
import org.bremersee.groupman.mock.GroupWebfluxControllerMock;
import org.bremersee.security.authentication.ReactiveAccessTokenProviders;
import org.bremersee.web.reactive.function.client.AccessTokenAppender;
import org.bremersee.web.reactive.function.client.DefaultWebClientErrorDecoder;
import org.bremersee.web.reactive.function.client.proxy.InvocationFunctions;
import org.bremersee.web.reactive.function.client.proxy.WebClientProxyBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * The groupman client configuration.
 *
 * @author Christian Bremer
 */
@Configuration
@Slf4j
public class GroupmanClientConfiguration {

  private final LinkmanProperties properties;

  /**
   * Instantiates a new groupman client configuration.
   *
   * @param properties the properties
   */
  public GroupmanClientConfiguration(LinkmanProperties properties) {
    this.properties = properties;
  }

  /**
   * Group service group webflux controller api.
   *
   * @param parser the parser
   * @return the group webflux controller api
   */
  @ConditionalOnProperty(name = "eureka.client.enabled", havingValue = "false")
  @Bean("groupService")
  public GroupWebfluxControllerApi groupService(RestApiExceptionParser parser) {

    final String baseUri = properties.getGroupmanBaseUri();
    if (!StringUtils.hasText(baseUri) || "false".equalsIgnoreCase(baseUri.trim())) {
      return new GroupWebfluxControllerMock();
    }

    final WebClient webClient = WebClient.builder()
        .baseUrl(baseUri)
        .filter(new AccessTokenAppender(ReactiveAccessTokenProviders.fromAuthentication()))
        .build();
    return groupService(parser, webClient);
  }

  @ConditionalOnProperty(
      name = "eureka.client.enabled",
      havingValue = "true",
      matchIfMissing = true)
  @Bean("groupService")
  public GroupWebfluxControllerApi loadBalancedGroupService(
      RestApiExceptionParser parser,
      ReactorLoadBalancerExchangeFilterFunction lbFunction) {

    final String baseUri = properties.getGroupmanBaseUri();
    if (!StringUtils.hasText(baseUri) || "false".equalsIgnoreCase(baseUri.trim())) {
      return new GroupWebfluxControllerMock();
    }

    final WebClient webClient = WebClient.builder()
        .baseUrl(baseUri)
        .filter(new AccessTokenAppender(ReactiveAccessTokenProviders.fromAuthentication()))
        .filter(lbFunction)
        .build();
    return groupService(parser, webClient);
  }

  private GroupWebfluxControllerApi groupService(
      RestApiExceptionParser parser,
      WebClient webClient) {

    final String baseUri = properties.getGroupmanBaseUri();
    if (!StringUtils.hasText(baseUri) || "false".equalsIgnoreCase(baseUri.trim())) {
      log.warn("Using groupman mock, because no groupman base uri was specified.");
      return new GroupWebfluxControllerMock();
    }
    log.info("Using groupman client with base uri {}", properties.getGroupmanBaseUri());
    return WebClientProxyBuilder.defaultBuilder()
        .webClient(webClient)
        .commonFunctions(InvocationFunctions.builder()
            .errorDecoder(new DefaultWebClientErrorDecoder(parser))
            .build())
        .build(GroupWebfluxControllerApi.class);
  }

}
