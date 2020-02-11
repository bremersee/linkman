package org.bremersee.linkman.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;
import org.bremersee.common.model.JavaLocaleDescription;
import org.bremersee.common.model.TwoLetterLanguageCode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * The type Language controller test.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "bremersee.security.authentication.enable-jwt-support=true",
    "bremersee.linkman.public-category.name=Guest",
    "bremersee.linkman.default-category.name=Not categorized"
})
@ActiveProfiles({"default"})
@TestInstance(Lifecycle.PER_CLASS)
    // allows us to use @BeforeAll with a non-static method
class LanguageControllerTest {

  /**
   * The application context.
   */
  @Autowired
  ApplicationContext context;

  /**
   * The web test client.
   */
  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  WebTestClient webTestClient;

  /**
   * Setup tests.
   */
  @BeforeAll
  void setUp() {
    // https://docs.spring.io/spring-security/site/docs/current/reference/html/test-webflux.html
    WebTestClient
        .bindToApplicationContext(this.context)
        .configureClient()
        .build();
  }

  /**
   * Gets available languages.
   */
  @Test
  void getAvailableLanguages() {
    JavaLocaleDescription expected = new JavaLocaleDescription(
        TwoLetterLanguageCode.BG.toString(),
        TwoLetterLanguageCode.BG.toLocale().getDisplayLanguage(Locale.FRANCE));
    webTestClient
        .get()
        .uri("/api/public/languages")
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