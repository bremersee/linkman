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

package org.bremersee.linkman.config;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * The linkman properties.
 *
 * @author Christian Bremer
 */
@ConfigurationProperties(prefix = "bremersee.linkman")
@Component
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class LinkmanProperties {

  private String groupmanBaseUri;

  private DefaultCategory defaultCategory = new DefaultCategory();

  /**
   * The default category.
   */
  @Getter
  @Setter
  @ToString
  @EqualsAndHashCode
  public static class DefaultCategory {

    private String name;

    private Map<String, String> translations = new LinkedHashMap<>();

    /**
     * Instantiates a new default category.
     */
    public DefaultCategory() {
      name = "Not categorized";
      translations.put("de", "Nicht kategorisiert");
      translations.put("fr", "Non catégorisé");
    }
  }

}
