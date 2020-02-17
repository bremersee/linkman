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

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.TwoLetterLanguageCode;
import org.springframework.validation.annotation.Validated;

/**
 * The category specification.
 *
 * @author Christian Bremer
 */
@Schema(description = "The specification of a category.")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Validated
public class CategorySpecification {

  @Schema(description = "Unique identifier of the category.", accessMode = AccessMode.READ_ONLY)
  @JsonProperty("id")
  private String id;

  @Schema(description = "The sort order value.", required = true, example = "134")
  @JsonProperty(value = "order", required = true)
  private int order;

  @Schema(description = "The default name.", required = true, example = "Administration")
  @JsonProperty(value = "name", required = true)
  @NotBlank
  @Size(min = 1, max = 75)
  private String name;

  @Schema(description = "The translations of the name.")
  @JsonProperty("translations")
  private Set<Translation> translations = new LinkedHashSet<>();

  @Schema(description = "Specifies whether the links of this category can be seen without "
      + "authentication. Default is false.", defaultValue = "false")
  @JsonProperty("matchesGuest")
  private Boolean matchesGuest = Boolean.FALSE;

  @Schema(description = "Specifies the users that can see the links of this category.")
  @JsonProperty("matchesUsers")
  private Set<String> matchesUsers = new LinkedHashSet<>();

  @Schema(description = "Specifies the roles of the users that can see the links of this "
      + "category.")
  @JsonProperty("matchesRoles")
  private Set<String> matchesRoles = new LinkedHashSet<>();

  @Schema(description = "Specifies the groups of the users that can see the links of this "
      + "category.")
  @JsonProperty("matchesGroups")
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
      Set<Translation> translations,
      Boolean matchesGuest,
      Set<String> matchesUsers,
      Set<String> matchesRoles,
      Set<String> matchesGroups) {
    this.id = id;
    this.order = order;
    this.name = name;
    setTranslations(translations);
    setMatchesGuest(matchesGuest);
    setMatchesUsers(matchesUsers);
    setMatchesRoles(matchesRoles);
    setMatchesGroups(matchesGroups);
  }

  /**
   * Sets translations.
   *
   * @param translations the translations
   */
  public void setTranslations(Set<Translation> translations) {
    this.translations.clear();
    if (translations != null) {
      this.translations.addAll(translations);
    }
  }

  /**
   * Sets matches guest.
   *
   * @param matchesGuest the matches guest
   */
  public void setMatchesGuest(Boolean matchesGuest) {
    this.matchesGuest = Boolean.TRUE.equals(matchesGuest);
  }

  /**
   * Sets matches users.
   *
   * @param matchesUsers the matches users
   */
  public void setMatchesUsers(Set<String> matchesUsers) {
    this.matchesUsers.clear();
    if (matchesUsers != null) {
      this.matchesUsers.addAll(matchesUsers);
    }
  }

  /**
   * Sets matches roles.
   *
   * @param matchesRoles the matches roles
   */
  public void setMatchesRoles(Set<String> matchesRoles) {
    this.matchesRoles.clear();
    if (matchesRoles != null) {
      this.matchesRoles.addAll(matchesRoles);
    }
  }

  /**
   * Sets matches groups.
   *
   * @param matchesGroups the matches groups
   */
  public void setMatchesGroups(Set<String> matchesGroups) {
    this.matchesGroups.clear();
    if (matchesGroups != null) {
      this.matchesGroups.addAll(matchesGroups);
    }
  }

  /**
   * Gets name.
   *
   * @param language the language
   * @return the name
   */
  public String getName(TwoLetterLanguageCode language) {
    if (language == null) {
      return name;
    }
    return Optional.ofNullable(translations)
        .flatMap(set -> set.stream().filter(entry -> language == entry.getLanguage()).findAny())
        .map(Translation::getValue)
        .orElse(name);
  }

  /**
   * Gets name.
   *
   * @param language the language
   * @return the name
   */
  public String getName(Locale language) {
    return getName(TwoLetterLanguageCode.fromLocale(language));
  }

  /**
   * Gets name.
   *
   * @param language the language
   * @return the name
   */
  public String getName(String language) {
    return getName(TwoLetterLanguageCode.fromValue(language));
  }

}
