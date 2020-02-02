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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import org.bremersee.linkman.model.LinkSpecification;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.test.StepVerifier;

/**
 * The type Link controller test.
 *
 * @author Christian Bremer
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "bremersee.security.authentication.enable-jwt-support=false"
})
@ActiveProfiles({"basic-auth"})
@TestInstance(Lifecycle.PER_CLASS) // allows us to use @BeforeAll with a non-static method
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LinkControllerTest {

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
   * The test entry.
   */
  final LinkSpecification testEntry = LinkSpecification.builder()
      .id(UUID.randomUUID().toString())
      .order(100)
      .href("http://admin.example.org")
      .blank(true)
      .text("Admin page")
      .textTranslations(Collections.singletonMap("de", "Verwaltungsseite"))
      .description("On the admin page you can do anything as admin.")
      .descriptionTranslations(Collections.singletonMap("de", "Bla bla"))
      .acl(AclBuilder.builder()
          .guest(false, PermissionConstants.READ)
          .addUser("stephen", PermissionConstants.READ)
          .addRole(ADMIN_ROLE_NAME, PermissionConstants.READ)
          .addGroup("Admins", PermissionConstants.READ)
          .buildAccessControlList())
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

    LinkEntity testEntity = modelMapper.map(testEntry, LinkEntity.class);
    StepVerifier
        .create(linkRepository.save(testEntity))
        .assertNext(entry -> {
          assertEquals(testEntry.getId(), entry.getId());
          assertEquals(testEntry.getOrder(), entry.getOrder());
          assertEquals("http://admin.example.org", entry.getHref());
          assertEquals(Boolean.TRUE, entry.getBlank());
          assertEquals("Admin page", entry.getText());
          assertEquals("Verwaltungsseite", entry.getText(Locale.GERMANY));
          assertEquals(
              "On the admin page you can do anything as admin.",
              entry.getDescription());
          assertEquals("Bla bla", entry.getDescription(Locale.GERMAN));
          assertNotNull(entry.getAcl());
          assertNotNull(entry.getAcl().entryMap());
          assertNotNull(entry.getAcl().entryMap().get(PermissionConstants.READ));
          assertNotNull(entry.getAcl().entryMap().get(PermissionConstants.READ).getUsers());
          assertNotNull(entry.getAcl().entryMap().get(PermissionConstants.READ).getRoles());
          assertNotNull(entry.getAcl().entryMap().get(PermissionConstants.READ).getGroups());
          assertFalse(entry.getAcl().entryMap().get(PermissionConstants.READ).isGuest());
          assertTrue(entry.getAcl().entryMap().get(PermissionConstants.READ)
              .getUsers().contains("stephen"));
          assertTrue(entry.getAcl().entryMap().get(PermissionConstants.READ)
              .getRoles().contains(ADMIN_ROLE_NAME));
          assertTrue(entry.getAcl().entryMap().get(PermissionConstants.READ)
              .getGroups().contains("Admins"));
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
        .uri("/api/admin/links")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(LinkSpecification.class)
        .value(list -> {
          assertEquals(1, list.size());
          assertTrue(list.stream().anyMatch(entry -> testEntry.getId().equals(entry.getId())));
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
        .uri("/api/admin/links")
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
    LinkSpecification model = LinkSpecification.builder()
        .order(99)
        .href("http://developers.example.org")
        .text("Developer page")
        .textTranslations(Collections.singletonMap("de", "Entwicklerseite"))
        .description("The developer page contains ...")
        .acl(AclBuilder.builder()
            .addGroup("developers", PermissionConstants.READ)
            .buildAccessControlList())
        .build();
    webTestClient
        .post()
        .uri("/api/admin/links")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(model))
        .exchange()
        .expectBody(LinkSpecification.class)
        .value((Consumer<LinkSpecification>) entry -> {
          assertNotNull(entry);
          assertNotNull(entry.getId());
          assertEquals("http://developers.example.org", entry.getHref());
          assertEquals("Developer page", entry.getText());
          assertEquals("Entwicklerseite", entry.getTextTranslations().get("de"));
          assertEquals("The developer page contains ...", entry.getDescription());
          assertNotNull(entry.getAcl());
          assertNotNull(entry.getAcl().getEntries());
          assertTrue(entry.getAcl().getEntries().stream()
              .anyMatch(ace -> ace.getGroups().contains("developers")));
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
        .uri("/api/admin/links/{id}", testEntry.getId())
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody(LinkSpecification.class)
        .value((Consumer<LinkSpecification>) entry -> {
          assertEquals(testEntry.getId(), entry.getId());
          assertEquals(testEntry.getOrder(), entry.getOrder());
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
    Map<String, String> newTranslations = new LinkedHashMap<>(testEntry.getTextTranslations());
    newTranslations.put("fr", "Page d'administration");
    LinkSpecification update = testEntry.toBuilder()
        .textTranslations(newTranslations)
        .build();
    webTestClient
        .put()
        .uri("/api/admin/links/{id}", testEntry.getId())
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(update))
        .exchange()
        .expectBody(LinkSpecification.class)
        .value((Consumer<LinkSpecification>) entry -> assertEquals(
            "Page d'administration", entry.getTextTranslations().get("fr")));
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
        .uri("/api/admin/links/{id}", testEntry.getId())
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk();

    webTestClient
        .get()
        .uri("/api/admin/links/{id}", testEntry.getId())
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isNotFound();
  }

}