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

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Arrays;
import java.util.Locale;
import org.bremersee.common.model.JavaLocaleDescription;
import org.bremersee.common.model.TwoLetterLanguageCode;
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
@RestController
public class LanguageController {

  /**
   * Gets available languages.
   *
   * @param inLocale the language of the description
   * @return the available languages
   */
  @ApiOperation(
      value = "Get available languages.",
      nickname = "getAvailableLanguages",
      response = JavaLocaleDescription.class,
      responseContainer = "List",
      tags = {"language-controller"})
  @ApiResponses(value = {
      @ApiResponse(
          code = 200,
          message = "OK",
          response = JavaLocaleDescription.class,
          responseContainer = "List")
  })
  @GetMapping(path = "/api/public/languages", produces = MediaType.APPLICATION_JSON_VALUE)
  public Flux<JavaLocaleDescription> getAvailableLanguages(Locale inLocale) {
    return Flux.fromStream(Arrays.stream(TwoLetterLanguageCode.values())
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
