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

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.TwoLetterLanguageCode;
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

  private String keycloakBaseUri = "false";

  private String keycloakRealm = "master";

  private String rolePrefix = "ROLE_";

  private Set<String> excludedRoles = new HashSet<>();

  private Set<String> excludedGroups = new HashSet<>();

  private Set<TwoLetterLanguageCode> availableLanguages = new LinkedHashSet<>();

  private Category defaultCategory;

  private Category publicCategory;

  /**
   * Instantiates new linkman properties.
   */
  public LinkmanProperties() {
    availableLanguages.add(TwoLetterLanguageCode.BG);
    availableLanguages.add(TwoLetterLanguageCode.HR);
    availableLanguages.add(TwoLetterLanguageCode.CS);
    availableLanguages.add(TwoLetterLanguageCode.DA);
    availableLanguages.add(TwoLetterLanguageCode.NL);
    availableLanguages.add(TwoLetterLanguageCode.EN);
    availableLanguages.add(TwoLetterLanguageCode.ET);
    availableLanguages.add(TwoLetterLanguageCode.FI);
    availableLanguages.add(TwoLetterLanguageCode.FR);
    availableLanguages.add(TwoLetterLanguageCode.DE);
    availableLanguages.add(TwoLetterLanguageCode.EL);
    availableLanguages.add(TwoLetterLanguageCode.HU);
    availableLanguages.add(TwoLetterLanguageCode.GA);
    availableLanguages.add(TwoLetterLanguageCode.IT);
    availableLanguages.add(TwoLetterLanguageCode.LV);
    availableLanguages.add(TwoLetterLanguageCode.LT);
    availableLanguages.add(TwoLetterLanguageCode.MT);
    availableLanguages.add(TwoLetterLanguageCode.PL);
    availableLanguages.add(TwoLetterLanguageCode.PT);
    availableLanguages.add(TwoLetterLanguageCode.RO);
    availableLanguages.add(TwoLetterLanguageCode.SK);
    availableLanguages.add(TwoLetterLanguageCode.SL);
    availableLanguages.add(TwoLetterLanguageCode.ES);
    availableLanguages.add(TwoLetterLanguageCode.SV);

    defaultCategory = new Category();
    defaultCategory.setName("Not categorized");
    defaultCategory.getTranslations().put("de", "Nicht kategorisiert");
    defaultCategory.getTranslations().put("fr", "Non catégorisé");

    publicCategory = new Category();
    publicCategory.setName("Public");
    publicCategory.getTranslations().put("de", "Öffentlich");
  }

  /**
   * The init category.
   */
  @Getter
  @Setter
  @ToString
  @EqualsAndHashCode
  public static class Category {

    private String name;

    private Map<String, String> translations = new LinkedHashMap<>();
  }

}
