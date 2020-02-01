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

package org.bremersee.linkman.repository;

import java.util.Set;
import reactor.core.publisher.Flux;

/**
 * The custom link repository.
 *
 * @author Christian Bremer
 */
public interface LinkRepositoryCustom {

  /**
   * Find readable links.
   *
   * @param userId the user id
   * @param roles the roles
   * @param groups the groups
   * @return the readable links
   */
  Flux<LinkEntity> findReadableLinks(String userId, Set<String> roles, Set<String> groups);

}
