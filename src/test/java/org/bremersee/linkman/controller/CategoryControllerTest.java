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
import static org.bremersee.security.core.AuthorityConstants.LOCAL_USER_ROLE_NAME;
import static org.bremersee.security.core.AuthorityConstants.USER_ROLE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import org.bremersee.exception.model.RestApiException;
import org.bremersee.linkman.model.CategorySpec;
import org.bremersee.linkman.model.Translation;
import org.bremersee.linkman.repository.CategoryEntity;
import org.bremersee.linkman.repository.CategoryRepository;
import org.bremersee.security.access.AclBuilder;
import org.bremersee.security.access.PermissionConstants;
import org.bremersee.test.security.authentication.WithJwtAuthenticationToken;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.test.StepVerifier;

/**
 * The category controller test.
 *
 * @author Christian Bremer
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost/jwk"
})
@ActiveProfiles({"default"})
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CategoryControllerTest {

  /**
   * The web test client.
   */
  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private WebTestClient webTestClient;

  /**
   * The category repository.
   */
  @Autowired
  private CategoryRepository categoryRepository;

  /**
   * The model mapper.
   */
  @Autowired
  private ModelMapper modelMapper;

  /**
   * The test entry.
   */
  private final CategorySpec testEntry = CategorySpec.builder()
      .id(UUID.randomUUID().toString())
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
   * Setup tests.
   */
  @BeforeAll
  void setUp() {
    CategoryEntity testEntity = modelMapper.map(testEntry, CategoryEntity.class);
    StepVerifier
        .create(categoryRepository.save(testEntity))
        .assertNext(entry -> {
          assertEquals(testEntry.getId(), entry.getId());
          assertEquals(testEntry.getOrder(), entry.getOrder());
          assertEquals(testEntry.getName(), entry.getName());
          assertEquals("Verwaltung", entry.getName(new Locale("de")));
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
   * Assert that public category exists.
   */
  @WithJwtAuthenticationToken(roles = {ADMIN_ROLE_NAME})
  @Order(5)
  @Test
  void assertThatPublicCategoryExists() {
    webTestClient
        .get()
        .uri("/api/categories/f/public-exists")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Boolean.class)
        .value(Assertions::assertTrue);
  }

  /**
   * Gets categories and expect default exists.
   */
  @WithJwtAuthenticationToken(roles = {ADMIN_ROLE_NAME})
  @Order(10)
  @Test
  void getCategoriesAndExpectDefaultExists() {
    webTestClient
        .get()
        .uri("/api/categories")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(CategorySpec.class)
        .value(list -> {
          assertEquals(2, list.size());
          assertTrue(list.stream().anyMatch(entry -> Boolean.TRUE.equals(entry.isPublic())));
          assertTrue(list.stream().anyMatch(entry -> testEntry.getId().equals(entry.getId())));
        });
  }

  /**
   * Gets categories and expect forbidden.
   */
  @WithJwtAuthenticationToken(roles = {USER_ROLE_NAME})
  @Order(15)
  @Test
  void getCategoriesAndExpectForbidden() {
    webTestClient
        .get()
        .uri("/api/categories")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isForbidden();
  }

  /**
   * Add category.
   */
  @WithJwtAuthenticationToken(roles = {ADMIN_ROLE_NAME})
  @Order(20)
  @Test
  void addCategory() {
    webTestClient
        .post()
        .uri("/api/categories")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(CategorySpec.builder()
            .name("For local users")
            .order(200)
            .acl(AclBuilder.builder()
                .guest(false, PermissionConstants.READ)
                .addUser("remote-admin", PermissionConstants.READ)
                .addRole(LOCAL_USER_ROLE_NAME, PermissionConstants.READ)
                .addGroup("Company Admins", PermissionConstants.READ)
                .buildAccessControlList())
            .build()))
        .exchange()
        .expectBody(CategorySpec.class)
        .value((Consumer<CategorySpec>) entry -> {
          assertNotNull(entry);
          assertNotNull(entry.getId());
          assertEquals("For local users", entry.getName());
          assertEquals(200, entry.getOrder());
          assertNotNull(entry.getAcl());
          assertNotNull(entry.getAcl().getEntries());
          assertTrue(entry.getAcl().getEntries().stream()
              .anyMatch(ace -> PermissionConstants.READ.equals(ace.getPermission())));
          assertTrue(entry.getAcl().getEntries().stream()
              .anyMatch(ace -> PermissionConstants.READ.equals(ace.getPermission())
                  && Boolean.FALSE.equals(ace.getGuest())));
          assertTrue(entry.getAcl().getEntries().stream()
              .anyMatch(ace -> PermissionConstants.READ.equals(ace.getPermission())
                  && ace.getUsers().contains("remote-admin")));
          assertTrue(entry.getAcl().getEntries().stream()
              .anyMatch(ace -> PermissionConstants.READ.equals(ace.getPermission())
                  && ace.getRoles().contains(LOCAL_USER_ROLE_NAME)));
          assertTrue(entry.getAcl().getEntries().stream()
              .anyMatch(ace -> PermissionConstants.READ.equals(ace.getPermission())
                  && ace.getGroups().contains("Company Admins")));
        });
  }

  /**
   * Add public category and expect error.
   */
  @WithJwtAuthenticationToken(roles = {ADMIN_ROLE_NAME})
  @Order(25)
  @Test
  void addPublicCategoryAndExpectError() {
    webTestClient
        .post()
        .uri("/api/categories")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(CategorySpec.builder()
            .name("More public links")
            .order(-200)
            .acl(AclBuilder.builder()
                .guest(true, PermissionConstants.READ)
                .buildAccessControlList())
            .build()))
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody(RestApiException.class)
        .value((Consumer<RestApiException>) entry -> assertEquals(
            "ONLY_ONE_PUBLIC_CATEGORY_IS_ALLOWED", entry.getErrorCode()));
  }

  /**
   * Gets category.
   */
  @WithJwtAuthenticationToken(roles = {ADMIN_ROLE_NAME})
  @Order(30)
  @Test
  void getCategory() {
    webTestClient
        .get()
        .uri("/api/categories/{id}", testEntry.getId())
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody(CategorySpec.class)
        .value((Consumer<CategorySpec>) entry -> {
          assertEquals(testEntry.getOrder(), entry.getOrder());
          assertEquals(testEntry.getName(), entry.getName());
        });
  }

  /**
   * Update category.
   */
  @WithJwtAuthenticationToken(roles = {ADMIN_ROLE_NAME})
  @Order(40)
  @Test
  void updateCategory() {
    Set<Translation> newTranslations = new LinkedHashSet<>(testEntry.getTranslations());
    newTranslations.add(new Translation("fr", "Gestion"));
    CategorySpec update = testEntry.toBuilder()
        .translations(newTranslations)
        .build();
    webTestClient
        .put()
        .uri("/api/categories/{id}", testEntry.getId())
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(update))
        .exchange()
        .expectBody(CategorySpec.class)
        .value((Consumer<CategorySpec>) entry -> assertEquals(
            "Gestion", entry.getName("fr")));
  }

  /**
   * Make category public and expect error.
   */
  @WithJwtAuthenticationToken(roles = {ADMIN_ROLE_NAME})
  @Order(45)
  @Test
  void makeCategoryPublicAndExpectError() {
    CategorySpec update = testEntry.toBuilder()
        .acl(AclBuilder.builder()
            .guest(true, PermissionConstants.READ)
            .buildAccessControlList())
        .build();
    webTestClient
        .put()
        .uri("/api/categories/{id}", testEntry.getId())
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(update))
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody(RestApiException.class)
        .value((Consumer<RestApiException>) entry -> assertEquals(
            "ONLY_ONE_PUBLIC_CATEGORY_IS_ALLOWED", entry.getErrorCode()));
  }

  /**
   * Delete category.
   */
  @WithJwtAuthenticationToken(roles = {ADMIN_ROLE_NAME})
  @Order(50)
  @Test
  void deleteCategory() {
    webTestClient
        .delete()
        .uri("/api/categories/{id}", testEntry.getId())
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk();

    webTestClient
        .get()
        .uri("/api/categories/{id}", testEntry.getId())
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isNotFound();
  }

}