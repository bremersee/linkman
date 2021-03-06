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

import java.time.Duration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.TwoLetterLanguageCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * The linkman properties.
 *
 * @author Christian Bremer
 */
@ConfigurationProperties(prefix = "bremersee.linkman")
@Validated
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class LinkmanProperties {

  @NotEmpty
  private String bucketName = "linkman";

  @NotNull
  private Duration presignedObjectUrlDuration = Duration.ofDays(1L);

  private String groupmanBaseUri;

  private String keycloakBaseUri;

  private String keycloakRealm = "master";

  private Set<String> excludedRoles = new HashSet<>();

  private Set<String> excludedGroups = new HashSet<>();

  private Set<TwoLetterLanguageCode> availableLanguages = new LinkedHashSet<>();

  private Category publicCategory;

  /**
   * Instantiates new linkman properties.
   */
  public LinkmanProperties() {
    availableLanguages.add(TwoLetterLanguageCode.EN);
    availableLanguages.add(TwoLetterLanguageCode.DE);

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
