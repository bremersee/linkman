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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

/**
 * The link.
 *
 * @author Christian Bremer
 */
@Schema(description = "A link description.")
@Validated
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class Link {

  @Schema(description = "Unique identifier of the link.", accessMode = AccessMode.READ_ONLY)
  @JsonProperty("id")
  private String id;

  @Schema(description = "The linked resource.", required = true, example = "http://example.org")
  @JsonProperty(value = "href", required = true)
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
  @JsonProperty("text")
  private String text;

  @Schema(
      description = "Specifies whether the text should be displayed or not.",
      defaultValue = "true")
  @JsonProperty("displayText")
  private Boolean displayText = Boolean.TRUE;

  @Schema(
      description = "The description of the link.",
      example = "On the example page you can view some examples.")
  @JsonProperty("description")
  private String description;

  @Schema(description = "The image URL of the card.")
  @JsonProperty("cardImageUrl")
  private String cardImageUrl;

  @Schema(description = "The image URL of the menu entry.")
  @JsonProperty("menuImageUrl")
  private String menuImageUrl;

  /**
   * Instantiates a new link.
   *
   * @param id the id
   * @param href the href
   * @param blank the blank
   * @param text the text
   * @param displayText the display text
   * @param description the description
   * @param cardImageUrl the card image url
   * @param menuImageUrl the menu image url
   */
  @Builder(toBuilder = true)
  public Link(String id, String href, Boolean blank, String text, Boolean displayText,
      String description, String cardImageUrl, String menuImageUrl) {
    this.id = id;
    this.href = href;
    setBlank(blank);
    this.text = text;
    setDisplayText(displayText);
    this.description = description;
    this.cardImageUrl = cardImageUrl;
    this.menuImageUrl = menuImageUrl;
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
   * Sets display text.
   *
   * @param displayText the display text
   */
  public void setDisplayText(Boolean displayText) {
    this.displayText = displayText == null || displayText;
  }

}
