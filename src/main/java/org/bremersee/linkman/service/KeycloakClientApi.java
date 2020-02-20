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

import org.bremersee.linkman.model.GroupRepresentation;
import org.bremersee.linkman.model.RoleRepresentation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Flux;

/**
 * The keycloak client api.
 */
public interface KeycloakClientApi {

  /**
   * Gets all roles.
   *
   * @param realm the realm
   * @return all roles
   */
  @GetMapping(path = "/admin/realms/{realm}/roles", produces = MediaType.APPLICATION_JSON_VALUE)
  Flux<RoleRepresentation> getAllRoles(@PathVariable("realm") String realm);

  /**
   * Gets all groups.
   *
   * @param realm the realm
   * @return all groups
   */
  @GetMapping(path = "/admin/realms/{realm}/groups", produces = MediaType.APPLICATION_JSON_VALUE)
  Flux<GroupRepresentation> getAllGroups(@PathVariable("realm") String realm);

}
