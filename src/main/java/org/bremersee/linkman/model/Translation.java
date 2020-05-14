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
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.TwoLetterLanguageCode;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

/**
 * The type Translation.
 *
 * @author Christian Bremer
 */
@Schema(description = "The translation.")
@TypeAlias("translation")
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"language"})
@NoArgsConstructor
@Validated
public class Translation {

  @Schema(description = "The language.", required = true)
  @JsonProperty(value = "language", required = true)
  @NotNull
  private TwoLetterLanguageCode language;

  @Schema(description = "The translated value.", required = true)
  @JsonProperty(value = "value", required = true)
  @NotNull
  private String value;

  /**
   * Instantiates a new Translation.
   *
   * @param language the language
   * @param value the value
   */
  @Builder(toBuilder = true)
  public Translation(
      @NotNull TwoLetterLanguageCode language,
      @NotNull String value) {
    this.language = language;
    this.value = value;
  }

  /**
   * Instantiates a new Translation.
   *
   * @param language the language
   * @param value the value
   */
  public Translation(
      @NotNull String language,
      @NotNull String value) {
    this.language = TwoLetterLanguageCode.fromValue(language);
    this.value = value;
    Assert.notNull(this.language, "Language must not be null.");
  }

  /**
   * To translation set.
   *
   * @param translations the translations as map
   * @return the translation set
   */
  public static Set<Translation> toTranslations(Map<String, String> translations) {
    return Optional.ofNullable(translations)
        .map(map -> map.entrySet().stream()
            .map(Translation::fromMapEntry)
            .collect(Collectors.toSet()))
        .orElse(Collections.emptySet());
  }

  private static Translation fromMapEntry(Map.Entry<String, String> entry) {
    return Translation.builder()
        .language(TwoLetterLanguageCode.fromValue(entry.getKey(), TwoLetterLanguageCode.EN))
        .value(entry.getValue())
        .build();
  }

}
