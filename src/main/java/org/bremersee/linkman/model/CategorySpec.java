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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.AccessControlList;
import org.bremersee.common.model.TwoLetterLanguageCode;
import org.bremersee.security.access.PermissionConstants;
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
public class CategorySpec {

  @Schema(description = "Unique identifier of the category.", accessMode = AccessMode.READ_ONLY)
  @JsonProperty("id")
  private String id;

  @Schema(
      description = "The access control list that specifies who can see the category.",
      required = true)
  @JsonProperty(value = "acl", required = true)
  @NotNull
  private AccessControlList acl;

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

  /**
   * Instantiates a new category specification.
   *
   * @param id the id
   * @param order the order
   * @param name the name
   * @param translations the translations
   */
  @Builder(toBuilder = true)
  @SuppressWarnings("unused")
  public CategorySpec(
      String id,
      AccessControlList acl,
      int order,
      String name,
      Set<Translation> translations) {
    this.id = id;
    this.acl = acl;
    this.order = order;
    this.name = name;
    setTranslations(translations);
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

  @Schema(hidden = true)
  @JsonIgnore
  public boolean isPublic() {
    return Optional.ofNullable(getAcl())
        .map(AccessControlList::getEntries)
        .flatMap(accessControlEntries -> accessControlEntries.stream()
            .filter(ace -> PermissionConstants.READ.equals(ace.getPermission()))
            .findAny()
            .map(ace -> Boolean.TRUE.equals(ace.getGuest())))
        .orElse(false);
  }

}
