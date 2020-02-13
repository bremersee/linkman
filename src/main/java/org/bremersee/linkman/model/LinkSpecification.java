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
import java.util.LinkedHashMap;
import java.util.Map;
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
public class LinkSpecification {

  @Schema(description = "Unique identifier of the link.", accessMode = AccessMode.READ_ONLY)
  @JsonProperty("id")
  private String id;

  @Schema(
      description = "The access control list that specifies who can see the link.",
      required = true)
  @JsonProperty(value = "acl", required = true)
  @NotNull
  private AccessControlList acl;

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
  private Map<String, String> textTranslations = new LinkedHashMap<>();

  @Schema(
      description = "The description of the link.",
      example = "On the example page you can view some examples.")
  @JsonProperty("description")
  @Size(max = 255)
  private String description;

  @Schema(description = "The translations of the description.")
  @JsonProperty("descriptionTranslations")
  private Map<String, String> descriptionTranslations = new LinkedHashMap<>();

  /**
   * Instantiates a new link specification.
   *
   * @param id the id
   * @param acl the access control list that specifies who can see the link
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
  public LinkSpecification(
      String id,
      AccessControlList acl,
      int order,
      String href,
      Boolean blank,
      String text,
      Map<String, String> textTranslations,
      String description,
      Map<String, String> descriptionTranslations) {
    this.id = id;
    this.acl = acl;
    this.order = order;
    this.href = href;
    setBlank(blank);
    this.text = text;
    setTextTranslations(textTranslations);
    this.description = description;
    setDescriptionTranslations(descriptionTranslations);
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
  public void setTextTranslations(Map<String, String> textTranslations) {
    this.textTranslations.clear();
    if (textTranslations != null) {
      this.textTranslations.putAll(textTranslations);
    }
  }

  /**
   * Sets description translations.
   *
   * @param descriptionTranslations the description translations
   */
  public void setDescriptionTranslations(
      Map<String, String> descriptionTranslations) {
    this.descriptionTranslations.clear();
    if (descriptionTranslations != null) {
      this.descriptionTranslations.putAll(descriptionTranslations);
    }
  }
}
