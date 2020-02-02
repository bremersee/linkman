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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(description = "The specification of a link.")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Validated
public class LinkSpecification {

  @ApiModelProperty(value = "The id of the link.")
  @JsonProperty("id")
  private String id;

  @ApiModelProperty(
      value = "The access control list that specifies who can see the link.",
      required = true)
  @JsonProperty("acl")
  @NotNull
  private AccessControlList acl;

  @ApiModelProperty(value = "The sort order.", required = true)
  @JsonProperty(value = "order", required = true)
  private int order;

  @ApiModelProperty(value = "The linked resource.", required = true)
  @JsonProperty(value = "href", required = true)
  @NotBlank
  private String href;

  @ApiModelProperty(value = "The text that is displayed instead of the link.", required = true)
  @JsonProperty(value = "text", required = true)
  @NotBlank
  @Size(min = 3, max = 75)
  private String text;

  @ApiModelProperty(value = "The translations of the text.")
  @JsonProperty("textTranslations")
  private Map<String, String> textTranslations;

  @ApiModelProperty(value = "The description of the link.")
  @JsonProperty("description")
  @Size(max = 255)
  private String description;

  @ApiModelProperty(value = "The translations of the description.")
  @JsonProperty("descriptionTranslations")
  private Map<String, String> descriptionTranslations;

  /**
   * Instantiates a new link specification.
   *
   * @param id the id
   * @param acl the access control list that specifies who can see the link
   * @param order the sort order
   * @param href the linked resource (href)
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
      String text,
      Map<String, String> textTranslations,
      String description,
      Map<String, String> descriptionTranslations) {
    this.id = id;
    this.acl = acl;
    this.order = order;
    this.href = href;
    this.text = text;
    this.textTranslations = textTranslations;
    this.description = description;
    this.descriptionTranslations = descriptionTranslations;
  }
}
