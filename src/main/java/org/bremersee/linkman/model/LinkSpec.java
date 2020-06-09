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
 * The link specification.
 *
 * @author Christian Bremer
 */
@Schema(description = "The specification of a link.")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Validated
public class LinkSpec {

  @Schema(description = "Unique identifier of the link.", accessMode = AccessMode.READ_ONLY)
  @JsonProperty("id")
  private String id;

  @Schema(description = "The category IDs.")
  @JsonProperty("categoryIds")
  private Set<String> categoryIds = new LinkedHashSet<>();

  @Schema(description = "The sort order.", required = true, example = "100")
  @JsonProperty(value = "order", required = true)
  private int order;

  @Schema(description = "The linked resource.", required = true, example = "http://example.org")
  @JsonProperty(value = "href", required = true)
  @NotBlank
  private String href;

  @Schema(
      description = "Specified whether to open the link in a blank target (default is false).",
      defaultValue = "false")
  @JsonProperty("blank")
  private Boolean blank = Boolean.FALSE;

  @Schema(
      description = "The text that is displayed instead of the link.",
      required = true,
      example = "The example page")
  @JsonProperty(value = "text", required = true)
  @NotBlank
  @Size(min = 3, max = 75)
  private String text;

  @Schema(description = "The translations of the text.")
  @JsonProperty("textTranslations")
  private Set<Translation> textTranslations = new LinkedHashSet<>();

  @Schema(
      description = "The description of the link.",
      example = "On the example page you can view some examples.")
  @JsonProperty("description")
  @Size(max = 255)
  private String description;

  @Schema(description = "The translations of the description.")
  @JsonProperty("descriptionTranslations")
  private Set<Translation> descriptionTranslations = new LinkedHashSet<>();

  /**
   * Instantiates a new link specification.
   *
   * @param id the id
   * @param categoryIds the category IDs
   * @param order the sort order
   * @param href the linked resource (href)
   * @param blank specified whether to open the link in a blank target (default is false)
   * @param text the text that is displayed instead of the link
   * @param textTranslations the text translations
   * @param description the description
   * @param descriptionTranslations the description translations
   */
  @Builder(toBuilder = true)
  @SuppressWarnings("unused")
  public LinkSpec(
      String id,
      Set<String> categoryIds,
      int order,
      String href,
      Boolean blank,
      String text,
      Set<Translation> textTranslations,
      String description,
      Set<Translation> descriptionTranslations) {
    this.id = id;
    setCategoryIds(categoryIds);
    this.order = order;
    this.href = href;
    setBlank(blank);
    this.text = text;
    setTextTranslations(textTranslations);
    this.description = description;
    setDescriptionTranslations(descriptionTranslations);
  }

  /**
   * Sets category ids.
   *
   * @param categoryIds the category ids
   */
  public void setCategoryIds(Set<String> categoryIds) {
    this.categoryIds.clear();
    if (categoryIds != null) {
      this.categoryIds.addAll(categoryIds);
    }
  }

  /**
   * Sets blank.
   *
   * @param blank the blank
   */
  public void setBlank(Boolean blank) {
    this.blank = Boolean.TRUE.equals(blank);
  }

  /**
   * Sets text translations.
   *
   * @param textTranslations the text translations
   */
  public void setTextTranslations(Set<Translation> textTranslations) {
    this.textTranslations.clear();
    if (textTranslations != null) {
      this.textTranslations.addAll(textTranslations);
    }
  }

  /**
   * Sets description translations.
   *
   * @param descriptionTranslations the description translations
   */
  public void setDescriptionTranslations(
      Set<Translation> descriptionTranslations) {
    this.descriptionTranslations.clear();
    if (descriptionTranslations != null) {
      this.descriptionTranslations.addAll(descriptionTranslations);
    }
  }

  /**
   * Gets text.
   *
   * @param language the language
   * @return the text
   */
  public String getText(TwoLetterLanguageCode language) {
    if (language == null) {
      return text;
    }
    return Optional.ofNullable(textTranslations)
        .flatMap(set -> set.stream().filter(entry -> language == entry.getLanguage()).findAny())
        .map(Translation::getValue)
        .orElse(text);
  }

  /**
   * Gets text.
   *
   * @param language the language
   * @return the text
   */
  public String getText(Locale language) {
    return getText(TwoLetterLanguageCode.fromLocale(language));
  }

  /**
   * Gets text.
   *
   * @param language the language
   * @return the text
   */
  public String getText(String language) {
    return getText(TwoLetterLanguageCode.fromValue(language));
  }

  /**
   * Gets description.
   *
   * @param language the language
   * @return the description
   */
  public String getDescription(TwoLetterLanguageCode language) {
    if (language == null) {
      return description;
    }
    return Optional.ofNullable(descriptionTranslations)
        .flatMap(set -> set.stream().filter(entry -> language == entry.getLanguage()).findAny())
        .map(Translation::getValue)
        .orElse(description);
  }

  /**
   * Gets description.
   *
   * @param language the language
   * @return the description
   */
  public String getDescription(Locale language) {
    return getDescription(TwoLetterLanguageCode.fromLocale(language));
  }

  /**
   * Gets description.
   *
   * @param language the language
   * @return the description
   */
  public String getDescription(String language) {
    return getDescription(TwoLetterLanguageCode.fromValue(language));
  }

}
