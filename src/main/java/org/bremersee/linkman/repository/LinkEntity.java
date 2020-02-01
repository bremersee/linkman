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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
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
@EqualsAndHashCode
@NoArgsConstructor
@Validated
public class LinkEntity implements Comparable<LinkEntity> {

  @Id
  private String id;

  private AclEntity acl;

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
    int result = order - o.order;
    return result != 0
        ? result
        : String.valueOf(getText(language))
            .compareToIgnoreCase(String.valueOf(o.getText(language)));
  }

}
