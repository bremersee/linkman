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

import java.util.Map;
import javax.validation.constraints.NotBlank;
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
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Validated
public class LinkSpecification {

  private String id;

  private AccessControlList acl;

  private int order;

  @NotBlank
  private String href;

  @NotBlank
  @Size(min = 3, max = 75)
  private String text;

  private Map<String, String> textTranslations;

  @NotBlank
  @Size(max = 255)
  private String description;

  private Map<String, String> descriptionTranslations;

  /**
   * Instantiates a new link specification.
   *
   * @param id the id
   * @param acl the acl
   * @param order the order
   * @param href the href
   * @param text the text
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
