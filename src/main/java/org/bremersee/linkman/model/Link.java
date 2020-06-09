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

  @JsonProperty("id")
  private String id;

  @JsonProperty(value = "href", required = true)
  private String href;

  @JsonProperty("blank")
  private Boolean blank = Boolean.FALSE;

  @JsonProperty("text")
  private String text;

  @JsonProperty("description")
  private String description;

  @JsonProperty("cardImageUrl")
  private String cardImageUrl;

  @JsonProperty("menuImageUrl")
  private String menuImageUrl;

  /**
   * Instantiates a new link.
   *
   * @param id the id
   * @param href the href
   * @param blank the blank
   * @param text the text
   * @param description the description
   * @param cardImageUrl the card image url
   * @param menuImageUrl the menu image url
   */
  @Builder(toBuilder = true)
  public Link(String id, String href, Boolean blank, String text, String description,
      String cardImageUrl, String menuImageUrl) {
    this.id = id;
    this.href = href;
    this.blank = blank;
    this.text = text;
    this.description = description;
    this.cardImageUrl = cardImageUrl;
    this.menuImageUrl = menuImageUrl;
  }
}
