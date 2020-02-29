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

import java.util.Locale;
import java.util.Set;
import org.bremersee.linkman.model.MenuEntry;
import reactor.core.publisher.Flux;

/**
 * The menu service.
 *
 * @author Christian Bremer
 */
public interface MenuService {

  /**
   * Gets menu entries.
   *
   * @param language the language
   * @param userId the user id
   * @param roles the roles
   * @param groups the groups
   * @return the menu entries
   */
  Flux<MenuEntry> getMenuEntries(
      Locale language,
      String userId,
      Set<String> roles,
      Set<String> groups);

}
