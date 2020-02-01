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

package org.bremersee.linkman.model;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

/**
 * The category specification.
 *
 * @author Christian Bremer
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Validated
public class CategorySpecification {

  private String id;

  private int order;

  private String name;

  private Map<String, String> translations = new LinkedHashMap<>();

  private Boolean matchesGuest;

  private Set<String> matchesUsers = new LinkedHashSet<>();

  private Set<String> matchesRoles = new LinkedHashSet<>();

  private Set<String> matchesGroups = new LinkedHashSet<>();

  /**
   * Instantiates a new category specification.
   *
   * @param id the id
   * @param order the order
   * @param name the name
   * @param translations the translations
   * @param matchesGuest the matches guest
   * @param matchesUsers the matches users
   * @param matchesRoles the matches roles
   * @param matchesGroups the matches groups
   */
  @Builder(toBuilder = true)
  @SuppressWarnings("unused")
  public CategorySpecification(
      String id,
      int order,
      String name,
      Map<String, String> translations,
      Boolean matchesGuest,
      Set<String> matchesUsers,
      Set<String> matchesRoles,
      Set<String> matchesGroups) {
    this.id = id;
    this.order = order;
    this.name = name;
    this.translations = translations;
    this.matchesGuest = matchesGuest;
    this.matchesUsers = matchesUsers;
    this.matchesRoles = matchesRoles;
    this.matchesGroups = matchesGroups;
  }
}
