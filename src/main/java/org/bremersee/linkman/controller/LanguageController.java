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

package org.bremersee.linkman.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Locale;
import org.bremersee.common.model.JavaLocaleDescription;
import org.bremersee.linkman.config.LinkmanProperties;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * The language controller.
 *
 * @author Christian Bremer
 */
@Tag(name = "language-controller", description = "The available languages API.")
@RestController
public class LanguageController {

  private LinkmanProperties properties;

  /**
   * Instantiates a new language controller.
   *
   * @param properties the properties
   */
  public LanguageController(LinkmanProperties properties) {
    this.properties = properties;
  }

  /**
   * Gets available languages.
   *
   * @param inLocale the language of the description
   * @return the available languages
   */
  @Operation(
      summary = "Get available languages.",
      operationId = "getAvailableLanguages",
      tags = {"language-controller"})
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "The available languages.",
          content = @Content(
              array = @ArraySchema(
                  schema = @Schema(implementation = JavaLocaleDescription.class))))
  })
  @GetMapping(path = "/api/languages", produces = MediaType.APPLICATION_JSON_VALUE)
  public Flux<JavaLocaleDescription> getAvailableLanguages(
      @Parameter(hidden = true) Locale inLocale) {

    return Flux.fromStream(properties.getAvailableLanguages().stream()
        .map(code -> new JavaLocaleDescription(
            code.toString(),
            code.toLocale().getDisplayLanguage(inLocale)))
        .filter(locale -> StringUtils.hasText(locale.getDescription()))
        .sorted((o1, o2) -> {
          if (o1.getLocale().equals(inLocale.getLanguage())) {
            return -1;
          } else if (o2.getLocale().equals(inLocale.getLanguage())) {
            return 1;
          }
          return o1.getDescription().compareToIgnoreCase(o2.getDescription());
        })
    );

  }

}
