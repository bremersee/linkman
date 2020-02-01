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

package org.bremersee.linkman.controller;

import java.util.Set;
import lombok.Getter;

/**
 * The user context.
 *
 * @author Christian Bremer
 */
class UserContext {

  @Getter
  private final String userId;

  @Getter
  private final Set<String> roles;

  @Getter
  private final Set<String> groups;

  /**
   * Instantiates a new user context.
   */
  UserContext() {
    this(null, null, null);
  }

  /**
   * Instantiates a new user context.
   *
   * @param userId the user id
   * @param roles the roles
   * @param groups the groups
   */
  UserContext(String userId, Set<String> roles, Set<String> groups) {
    this.userId = userId;
    this.roles = roles;
    this.groups = groups;
  }
}
