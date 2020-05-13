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
import org.bremersee.linkman.service.KeycloakClientApi;
import org.bremersee.linkman.service.KeycloakClientMock;
import org.bremersee.security.authentication.AuthProperties;
import org.bremersee.security.authentication.ReactiveAccessTokenProviders;
import org.bremersee.web.reactive.function.client.AccessTokenAppender;
import org.bremersee.web.reactive.function.client.DefaultWebClientErrorDecoder;
import org.bremersee.web.reactive.function.client.proxy.InvocationFunctions;
import org.bremersee.web.reactive.function.client.proxy.WebClientProxyBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * The keycloak client configuration.
 *
 * @author Christian Bremer
 */
@Configuration
@Slf4j
public class KeycloakClientConfiguration {

  private final AuthProperties AuthProperties;

  private final LinkmanProperties linkmanProperties;

  /**
   * Instantiates a new keycloak client configuration.
   *
   * @param AuthProperties the authentication properties
   * @param linkmanProperties the linkman properties
   */
  public KeycloakClientConfiguration(
      AuthProperties AuthProperties,
      LinkmanProperties linkmanProperties) {
    this.AuthProperties = AuthProperties;
    this.linkmanProperties = linkmanProperties;
  }

  /**
   * Creates keycloak client api bean.
   *
   * @param parser the rest api exception parser
   * @return the keycloak client api
   */
  @Bean
  public KeycloakClientApi keycloakClientApi(RestApiExceptionParser parser) {

    final String baseUri = linkmanProperties.getKeycloakBaseUri();
    if (!StringUtils.hasText(baseUri) || "false".equalsIgnoreCase(baseUri.trim())) {
      log.warn("Using keycloak client mock, because no keycloak base uri was specified.");
      return new KeycloakClientMock();
    }
    log.info("Using keycloak client with base uri {}", linkmanProperties.getKeycloakBaseUri());
    final WebClient webClient = WebClient.builder()
        .baseUrl(linkmanProperties.getKeycloakBaseUri())
        .filter(new AccessTokenAppender(ReactiveAccessTokenProviders
            .withAccessTokenRetriever(AuthProperties.getClientCredentialsFlow())))
        .build();
    return WebClientProxyBuilder.defaultBuilder()
        .webClient(webClient)
        .commonFunctions(InvocationFunctions.builder()
            .errorDecoder(new DefaultWebClientErrorDecoder(parser))
            .build())
        .build(KeycloakClientApi.class);
  }

}
