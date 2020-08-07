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

import static org.bremersee.security.core.AuthorityConstants.ADMIN_ROLE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;
import org.bremersee.common.model.JavaLocaleDescription;
import org.bremersee.common.model.TwoLetterLanguageCode;
import org.bremersee.test.security.authentication.WithJwtAuthenticationToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * The language controller test.
 *
 * @author Christian Bremer
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost/jwk",
    "bremersee.linkman.available-languages[0]=en",
    "bremersee.linkman.available-languages[1]=de",
    "bremersee.linkman.available-languages[2]=fr",
    "bremersee.linkman.available-languages[3]=bg"
})
@ActiveProfiles({"default"})
@TestInstance(Lifecycle.PER_CLASS)
class LanguageControllerTest {

  /**
   * The web test client.
   */
  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private WebTestClient webTestClient;

  /**
   * Gets available languages.
   */
  @WithJwtAuthenticationToken(roles = {ADMIN_ROLE_NAME})
  @Test
  void getAvailableLanguages() {
    JavaLocaleDescription expected = new JavaLocaleDescription(
        TwoLetterLanguageCode.BG.toString(),
        TwoLetterLanguageCode.BG.toLocale().getDisplayLanguage(Locale.FRANCE));
    webTestClient
        .get()
        .uri("/api/languages")
        .accept(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.ACCEPT_LANGUAGE, Locale.FRENCH.getLanguage())
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(JavaLocaleDescription.class)
        .value(list -> {
          assertFalse(list.isEmpty());
          assertEquals(Locale.FRENCH.getLanguage(), list.get(0).getLocale());
          assertTrue(list.contains(expected));
        });
  }
}