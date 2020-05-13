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
import org.bremersee.linkman.model.SelectOption;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

/**
 * The group service implementation.
 *
 * @author Christian Bremer
 */
@Component
public class GroupServiceImpl implements GroupService {

  private final LinkmanProperties properties;

  private final KeycloakClientApi keycloakClient;

  /**
   * Instantiates a new group service.
   *
   * @param properties the properties
   * @param keycloakClient the keycloak client
   */
  public GroupServiceImpl(
      LinkmanProperties properties,
      KeycloakClientApi keycloakClient) {
    this.properties = properties;
    this.keycloakClient = keycloakClient;
  }

  @Override
  public Flux<SelectOption> getAllGroups() {
    return keycloakClient.getAllGroups(properties.getKeycloakRealm())
        .filter(group -> StringUtils.hasText(group.getName())
            && !properties.getExcludedGroups().contains(group.getName()))
        .map(group -> new SelectOption(group.getName(), group.getName()));
  }

}
