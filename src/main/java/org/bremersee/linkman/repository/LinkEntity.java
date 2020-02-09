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

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

/**
 * The link entity.
 *
 * @author Christian Bremer
 */
@Document(collection = "links")
@TypeAlias("link")
@Getter
@Setter
@ToString
@NoArgsConstructor
@Validated
public class LinkEntity implements Comparable<LinkEntity> {

  @Id
  private String id;

  private AclEntity acl;

  private int order;

  @NotBlank
  private String href;

  private Boolean blank = Boolean.FALSE;

  @NotBlank
  @Size(min = 3, max = 75)
  private String text;

  private Map<String, String> textTranslations;

  @NotBlank
  @Size(max = 255)
  private String description;

  private Map<String, String> descriptionTranslations;

  /**
   * Gets text.
   *
   * @param language the language
   * @return the text
   */
  public String getText(Locale language) {
    if (language == null || textTranslations == null) {
      return text;
    }
    return textTranslations.getOrDefault(language.getLanguage(), text);
  }

  /**
   * Gets description.
   *
   * @param language the language
   * @return the description
   */
  public String getDescription(Locale language) {
    if (language == null || descriptionTranslations == null) {
      return description;
    }
    return descriptionTranslations.getOrDefault(language.getLanguage(), description);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LinkEntity that = (LinkEntity) o;
    if (StringUtils.hasText(id) && StringUtils.hasText(that.id)) {
      return id.equals(that.getId());
    }
    return order == that.order &&
        Objects.equals(acl, that.acl) &&
        Objects.equals(href, that.href) &&
        Objects.equals(blank, that.blank) &&
        Objects.equals(text, that.text) &&
        Objects.equals(textTranslations, that.textTranslations) &&
        Objects.equals(description, that.description) &&
        Objects.equals(descriptionTranslations, that.descriptionTranslations);
  }

  @Override
  public int hashCode() {
    if (StringUtils.hasText(id)) {
      return id.hashCode();
    }
    return Objects.hash(
        acl, order, href, blank, text, textTranslations, description, descriptionTranslations);
  }

  @Override
  public int compareTo(final LinkEntity o) {
    return compareTo(o, null);
  }

  /**
   * Compare to.
   *
   * @param o the other link
   * @param language the language
   * @return the result
   */
  public int compareTo(final LinkEntity o, final Locale language) {
    int result = Integer.compare(order, o.order);
    return result != 0
        ? result
        : String.valueOf(getText(language))
            .compareToIgnoreCase(String.valueOf(o.getText(language)));
  }

}
