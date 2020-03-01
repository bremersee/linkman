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
import org.bremersee.security.core.AuthorityConstants;
import reactor.core.publisher.Flux;

/**
 * The keycloak client mock.
 *
 * @author Christian Bremer
 */
public class KeycloakClientMock implements KeycloakClientApi {

  @Override
  public Flux<RoleRepresentation> getAllRoles(String realm) {
    return Flux.fromArray(new RoleRepresentation[]{
        RoleRepresentation.builder()
            .name(AuthorityConstants.ADMIN_ROLE_NAME)
            .build(),
        RoleRepresentation.builder()
            .name(AuthorityConstants.USER_ROLE_NAME)
            .build(),
        RoleRepresentation.builder()
            .name(AuthorityConstants.LOCAL_USER_ROLE_NAME)
            .build(),
        RoleRepresentation.builder()
            .name("ROLE_LINK_ADMIN")
            .build()
    });
  }

  @Override
  public Flux<GroupRepresentation> getAllGroups(String realm) {
    return Flux.fromArray(new GroupRepresentation[]{
        GroupRepresentation.builder()
            .name("developer")
            .build(),
        GroupRepresentation.builder()
            .name("Company Admins")
            .build()
    });
  }
}
