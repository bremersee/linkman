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
import static org.bremersee.security.core.AuthorityConstants.USER_ROLE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import eu.maxschuster.dataurl.DataUrl;
import eu.maxschuster.dataurl.DataUrlBuilder;
import eu.maxschuster.dataurl.DataUrlEncoding;
import eu.maxschuster.dataurl.DataUrlSerializer;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import org.bremersee.linkman.model.CategorySpec;
import org.bremersee.linkman.model.LinkSpec;
import org.bremersee.linkman.model.Translation;
import org.bremersee.linkman.repository.CategoryEntity;
import org.bremersee.linkman.repository.CategoryRepository;
import org.bremersee.linkman.repository.LinkEntity;
import org.bremersee.linkman.repository.LinkRepository;
import org.bremersee.security.access.AclBuilder;
import org.bremersee.security.access.PermissionConstants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.test.StepVerifier;

/**
 * The link controller test.
 *
 * @author Christian Bremer
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"in-memory"})
@TestInstance(Lifecycle.PER_CLASS) // allows us to use @BeforeAll with a non-static method
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LinkControllerTest {

  private static final String categoryId = UUID.randomUUID().toString();

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
   * The category repository.
   */
  @Autowired
  CategoryRepository categoryRepository;

  /**
   * The link repository.
   */
  @Autowired
  LinkRepository linkRepository;

  /**
   * The model mapper.
   */
  @Autowired
  ModelMapper modelMapper;

  /**
   * The test category.
   */
  final CategorySpec testCategory = CategorySpec.builder()
      .id(categoryId)
      .order(100)
      .name("Administration")
      .translations(Collections.singleton(new Translation("de", "Verwaltung")))
      .acl(AclBuilder.builder()
          .guest(false, PermissionConstants.READ)
          .addUser("stephen", PermissionConstants.READ)
          .addRole(ADMIN_ROLE_NAME, PermissionConstants.READ)
          .addGroup("Admins", PermissionConstants.READ)
          .buildAccessControlList())
      .build();

  /**
   * The test link.
   */
  final LinkSpec testLink = LinkSpec.builder()
      .id(UUID.randomUUID().toString())
      .categoryIds(Collections.singleton(categoryId))
      .order(100)
      .href("http://admin.example.org")
      .blank(true)
      .text("Admin page")
      .textTranslations(Collections.singleton(new Translation("de", "Verwaltungsseite")))
      .description("On the admin page you can do anything as admin.")
      .descriptionTranslations(Collections.singleton(new Translation("de", "Bla bla")))
      .build();

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

    CategoryEntity testCategoryEntity = modelMapper.map(testCategory, CategoryEntity.class);
    StepVerifier
        .create(categoryRepository.save(testCategoryEntity))
        .assertNext(entry -> assertEquals(testCategoryEntity.getId(), entry.getId()))
        .verifyComplete();

    LinkEntity testLinkEntity = modelMapper.map(testLink, LinkEntity.class);
    StepVerifier
        .create(linkRepository.save(testLinkEntity))
        .assertNext(entry -> {
          assertEquals(testLink.getId(), entry.getId());
          assertEquals(testLink.getOrder(), entry.getOrder());
          assertEquals("http://admin.example.org", entry.getHref());
          assertEquals(Boolean.TRUE, entry.getBlank());
          assertEquals("Admin page", entry.getText());
          assertEquals("Verwaltungsseite", entry.getText(Locale.GERMANY));
          assertEquals(
              "On the admin page you can do anything as admin.",
              entry.getDescription());
          assertEquals("Bla bla", entry.getDescription(Locale.GERMAN));
        })
        .verifyComplete();
  }

  /**
   * Gets links.
   */
  @WithMockUser(
      username = "admin",
      password = "admin",
      authorities = {ADMIN_ROLE_NAME})
  @Order(10)
  @Test
  void getLinks() {
    webTestClient
        .get()
        .uri("/api/links")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(LinkSpec.class)
        .value(list -> {
          assertEquals(1, list.size());
          assertTrue(list.stream().anyMatch(entry -> testLink.getId().equals(entry.getId())));
        });
  }

  /**
   * Gets links.
   */
  @WithMockUser(
      username = "admin",
      password = "admin",
      authorities = {ADMIN_ROLE_NAME})
  @Order(12)
  @Test
  void getLinksOfCategory() {
    webTestClient
        .get()
        .uri("/api/links?categoryId={id}", categoryId)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(LinkSpec.class)
        .value(list -> {
          assertEquals(1, list.size());
          assertTrue(list.stream().anyMatch(entry -> testLink.getId().equals(entry.getId())));
        });
  }

  /**
   * Gets links and expect forbidden.
   */
  @WithMockUser(
      username = "user",
      password = "user",
      authorities = {USER_ROLE_NAME})
  @Order(15)
  @Test
  void getLinksAndExpectForbidden() {
    webTestClient
        .get()
        .uri("/api/links")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isForbidden();
  }

  /**
   * Add link.
   */
  @WithMockUser(
      username = "admin",
      password = "admin",
      authorities = {ADMIN_ROLE_NAME})
  @Order(20)
  @Test
  void addLink() {
    LinkSpec model = LinkSpec.builder()
        .order(99)
        .href("http://developers.example.org")
        .text("Developer page")
        .textTranslations(Collections.singleton(new Translation("de", "Entwicklerseite")))
        .description("The developer page contains ...")
        .build();
    webTestClient
        .post()
        .uri("/api/links")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(model))
        .exchange()
        .expectBody(LinkSpec.class)
        .value((Consumer<LinkSpec>) entry -> {
          assertNotNull(entry);
          assertNotNull(entry.getId());
          assertEquals("http://developers.example.org", entry.getHref());
          assertEquals("Developer page", entry.getText());
          assertEquals("Entwicklerseite", entry.getText("de"));
          assertEquals("The developer page contains ...", entry.getDescription());
        });
  }

  /**
   * Gets link.
   */
  @WithMockUser(
      username = "admin",
      password = "admin",
      authorities = {ADMIN_ROLE_NAME})
  @Order(30)
  @Test
  void getLink() {
    webTestClient
        .get()
        .uri("/api/links/{id}", testLink.getId())
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody(LinkSpec.class)
        .value((Consumer<LinkSpec>) entry -> {
          assertEquals(testLink.getId(), entry.getId());
          assertEquals(testLink.getOrder(), entry.getOrder());
        });
  }

  /**
   * Update link.
   */
  @WithMockUser(
      username = "admin",
      password = "admin",
      authorities = {ADMIN_ROLE_NAME})
  @Order(40)
  @Test
  void updateLink() {
    Set<Translation> newTranslations = new LinkedHashSet<>(testLink.getTextTranslations());
    newTranslations.add(new Translation("fr", "Page d'administration"));
    LinkSpec update = testLink.toBuilder()
        .textTranslations(newTranslations)
        .build();
    webTestClient
        .put()
        .uri("/api/links/{id}", testLink.getId())
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(update))
        .exchange()
        .expectBody(LinkSpec.class)
        .value((Consumer<LinkSpec>) entry -> assertEquals(
            "Page d'administration", entry.getText("fr")));
  }

  @WithMockUser(
      username = "admin",
      password = "admin",
      authorities = {ADMIN_ROLE_NAME})
  @Order(45)
  @Test
  void updateAndDeleteLinkImages() {

    final byte[] cardImage = "cardImage".getBytes(StandardCharsets.UTF_8);
    final byte[] menuImage = "menuImage".getBytes(StandardCharsets.UTF_8);
    DataUrl menuImageDataUrl = new DataUrlBuilder()
        .setData(menuImage)
        .setCharset(StandardCharsets.UTF_8.name())
        .setEncoding(DataUrlEncoding.BASE64)
        .setMimeType(MediaType.IMAGE_GIF_VALUE)
        .build();
    String menuImageValue;
    try {
      menuImageValue = new DataUrlSerializer().serialize(menuImageDataUrl);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }

    MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
    bodyBuilder.part(LinkSpec.CARD_IMAGE_NAME, cardImage, MediaType.IMAGE_PNG)
        .header(
            "Content-Disposition",
            "form-data; name=cardImage; filename=test.png");
    bodyBuilder.part(LinkSpec.MENU_IMAGE_NAME, menuImageValue);

    webTestClient
        .post()
        .uri("/api/links/{id}/images", testLink.getId())
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
        .exchange()
        .expectBody(LinkSpec.class)
        .value((Consumer<LinkSpec>) entry -> {
          assertNotNull(entry);
          assertNotNull(entry.getCardImageUrl());
          assertNotNull(entry.getMenuImageUrl());
        });

    webTestClient
        .delete()
        .uri("/api/links/{id}/images?name={0}&name={1}",
            testLink.getId(), LinkSpec.CARD_IMAGE_NAME, LinkSpec.MENU_IMAGE_NAME)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectBody(LinkSpec.class)
        .value((Consumer<LinkSpec>) entry -> {
          assertNotNull(entry);
          assertNull(entry.getCardImageUrl());
          assertNull(entry.getMenuImageUrl());
        });
  }

  /**
   * Delete link.
   */
  @WithMockUser(
      username = "admin",
      password = "admin",
      authorities = {ADMIN_ROLE_NAME})
  @Order(50)
  @Test
  void deleteLink() {
    webTestClient
        .delete()
        .uri("/api/links/{id}", testLink.getId())
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk();

    webTestClient
        .get()
        .uri("/api/links/{id}", testLink.getId())
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isNotFound();
  }

}