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

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.TwoLetterLanguageCode;
import org.bremersee.linkman.model.Translation;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

/**
 * The category entity.
 *
 * @author Christian Bremer
 */
@Document(collection = "categories")
@TypeAlias("category")
@Getter
@Setter
@ToString
@NoArgsConstructor
@Validated
public class CategoryEntity implements Comparable<CategoryEntity> {

  @Id
  private String id;

  private int order;

  private String name;

  private Set<Translation> translations = new LinkedHashSet<>();

  @Indexed
  private Boolean matchesGuest;

  @Indexed
  private Set<String> matchesUsers = new LinkedHashSet<>();

  @Indexed
  private Set<String> matchesRoles = new LinkedHashSet<>();

  @Indexed
  private Set<String> matchesGroups = new LinkedHashSet<>();

  /**
   * Gets name.
   *
   * @param language the language
   * @return the name
   */
  public String getName(Locale language) {
    final TwoLetterLanguageCode code = TwoLetterLanguageCode
        .fromLocale(language, TwoLetterLanguageCode.EN);
    return Optional.ofNullable(translations)
        .flatMap(set -> set.stream().filter(entry -> code == entry.getLanguage()).findAny())
        .map(Translation::getValue)
        .orElse(name);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CategoryEntity entity = (CategoryEntity) o;
    if (StringUtils.hasText(id) && StringUtils.hasText(entity.id)) {
      return id.equals(entity.getId());
    }
    return order == entity.order
        && Objects.equals(name, entity.name)
        && Objects.equals(translations, entity.translations)
        && Objects.equals(matchesGuest, entity.matchesGuest)
        && Objects.equals(matchesUsers, entity.matchesUsers)
        && Objects.equals(matchesRoles, entity.matchesRoles)
        && Objects.equals(matchesGroups, entity.matchesGroups);
  }

  @Override
  public int hashCode() {
    if (StringUtils.hasText(id)) {
      return id.hashCode();
    }
    return Objects
        .hash(order, name, translations, matchesGuest, matchesUsers, matchesRoles, matchesGroups);
  }

  @Override
  public int compareTo(final CategoryEntity o) {
    return compareTo(o, null);
  }

  /**
   * Compare to.
   *
   * @param o the other category
   * @param language the language
   * @return the result
   */
  public int compareTo(final CategoryEntity o, final Locale language) {
    int result = Integer.compare(order, o.order);
    return result != 0
        ? result
        : String.valueOf(getName(language))
            .compareToIgnoreCase(String.valueOf(o.getName(language)));
  }

}
