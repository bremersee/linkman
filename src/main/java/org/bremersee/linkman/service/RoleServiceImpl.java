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

package org.bremersee.linkman.service;

import org.bremersee.linkman.config.LinkmanProperties;
import org.bremersee.linkman.model.RoleRepresentation;
import org.bremersee.linkman.model.SelectOption;
import org.bremersee.security.authentication.AuthProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

/**
 * The role service implementation.
 *
 * @author Christian Bremer
 */
@Component
public class RoleServiceImpl implements RoleService {

  private final AuthProperties authProperties;

  private final LinkmanProperties linkmanProperties;

  private final KeycloakClientApi keycloakClient;

  /**
   * Instantiates a new role service.
   *
   * @param authProperties the authentication properties
   * @param linkmanProperties the properties
   * @param keycloakClient the keycloak client
   */
  public RoleServiceImpl(
      AuthProperties authProperties,
      LinkmanProperties linkmanProperties,
      KeycloakClientApi keycloakClient) {
    this.authProperties = authProperties;
    this.linkmanProperties = linkmanProperties;
    this.keycloakClient = keycloakClient;
  }

  @Override
  public Flux<SelectOption> getAllRoles() {
    return keycloakClient.getAllRoles(linkmanProperties.getKeycloakRealm())
        .filter(this::isValidRole)
        .map(role -> new SelectOption(getValue(role), getDisplayValue(role)));
  }

  private boolean isValidRole(RoleRepresentation role) {
    return StringUtils.hasText(role.getName())
        && !linkmanProperties.getExcludedRoles().contains(role.getName())
        && !linkmanProperties.getExcludedRoles().contains(
        authProperties.getRolePrefix() + role.getName());
  }

  private String getValue(RoleRepresentation role) {
    if (StringUtils.hasText(authProperties.getRolePrefix())) {
      if (role.getName().startsWith(authProperties.getRolePrefix())) {
        return role.getName();
      } else {
        return authProperties.getRolePrefix() + role.getName();
      }
    }
    return role.getName();
  }

  private String getDisplayValue(RoleRepresentation role) {
    return role.getName();
  }

}
