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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Locale;
import java.util.UUID;
import org.bremersee.groupman.api.GroupWebfluxControllerApi;
import org.bremersee.linkman.config.LinkmanProperties;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

/**
 * The type Link container controller test.
 *
 * @author Christian Bremer
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "bremersee.security.authentication.enable-jwt-support=true",
    "bremersee.linkman.public-category.name=Guest",
    "bremersee.linkman.default-category.name=Not categorized"
})
@ActiveProfiles({"default"})
@TestInstance(Lifecycle.PER_CLASS) // allows us to use @BeforeAll with a non-static method
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MenuControllerTest {

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
   * The properties.
   */
  @Autowired
  LinkmanProperties properties;

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
   * The group service.
   */
  @MockBean
  GroupWebfluxControllerApi groupService;

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
   * The test link.
   */
  final LinkSpec publicTestLink = LinkSpec.builder()
      .id(UUID.randomUUID().toString())
      .order(1)
      .href("http://web.example.org")
      .blank(true)
      .text("Admin page")
      .textTranslations(Collections.singleton(new Translation("de", "Verwaltungsseite")))
      .description("On the admin page you can do anything as admin.")
      .descriptionTranslations(Collections.singleton(new Translation("de", "Bla bla")))
      .build();

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
        .assertNext(entry -> assertEquals(testLink.getId(), entry.getId()))
        .verifyComplete();
  }

//
//  /**
//   * Gets public links for guests.
//   */
//  @Test
//  void getPublicLinksForGuests() {
//    webTestClient
//        .get()
//        .uri("/api/links")
//        .accept(MediaType.APPLICATION_JSON)
//        .exchange()
//        .expectStatus().isOk()
//        .expectBodyList(MenuEntry.class)
//        .value(this::assertThatPublicLinkIsPresent);
//  }
//
//  private void assertThatPublicLinkIsPresent(List<MenuEntry> list) {
//    assertEquals(1L, list.stream().filter(MenuEntry::isPub).count());
//    Optional<MenuEntry> optional = list.stream()
//        .filter(MenuEntry::isPub)
//        .findFirst();
//    assertTrue(optional.isPresent());
//    MenuEntry container = optional.get();
//    assertEquals(properties.getPublicCategory().getName(), container.getCategory());
//    assertNotNull(container.getLinks());
//    assertEquals(1, container.getLinks().size());
//    assertEquals("publicLink", container.getLinks().get(0).getId());
//  }
//
//  /**
//   * Gets links with admin role.
//   */
//  @WithJwtAuthenticationToken(
//      preferredUsername = "leopold",
//      roles = {ADMIN_ROLE_NAME})
//  @Test
//  void getLinksWithAdminRole() {
//    webTestClient
//        .get()
//        .uri("/api/links")
//        .accept(MediaType.APPLICATION_JSON)
//        .exchange()
//        .expectStatus().isOk()
//        .expectBodyList(MenuEntry.class)
//        .value(list -> {
//          assertThatAdminLinkIsPresent(list);
//          assertThatPublicLinkIsPresent(list);
//        });
//  }
//
//  /**
//   * Gets links for stephen.
//   */
//  @WithJwtAuthenticationToken(
//      preferredUsername = "stephen",
//      roles = {USER_ROLE_NAME})
//  @Test
//  void getLinksForStephen() {
//    webTestClient
//        .get()
//        .uri("/api/links")
//        .accept(MediaType.APPLICATION_JSON)
//        .exchange()
//        .expectStatus().isOk()
//        .expectBodyList(MenuEntry.class)
//        .value(list -> {
//          assertThatAdminLinkIsPresent(list);
//          assertThatPublicLinkIsPresent(list);
//        });
//  }
//
//  private void assertThatAdminLinkIsPresent(List<MenuEntry> list) {
//    assertEquals(1L, list.stream()
//        .filter(linkContainer -> "Administration".equals(linkContainer.getCategory()))
//        .count());
//    Optional<MenuEntry> optional = list.stream()
//        .filter(linkContainer -> "Administration".equals(linkContainer.getCategory()))
//        .findFirst();
//    assertTrue(optional.isPresent());
//    MenuEntry container = optional.get();
//    assertNotNull(container.getLinks());
//    assertEquals(1, container.getLinks().size());
//    assertEquals("adminLink", container.getLinks().get(0).getId());
//  }
//
//  /**
//   * Gets links for developers.
//   */
//  @WithJwtAuthenticationToken(
//      preferredUsername = "dev",
//      roles = {USER_ROLE_NAME})
//  @Test
//  void getLinksForDevelopers() {
//    when(groupService.getMembershipIds())
//        .thenReturn(Mono.just(Collections.singleton("developers")));
//    webTestClient
//        .get()
//        .uri("/api/links")
//        .accept(MediaType.APPLICATION_JSON)
//        .header(HttpHeaders.ACCEPT_LANGUAGE, "fr")
//        .exchange()
//        .expectStatus().isOk()
//        .expectBodyList(MenuEntry.class)
//        .value(list -> {
//          assertThatDeveloperLinkIsPresent(list);
//          assertThatPublicLinkIsPresent(list);
//        });
//  }
//
//  private void assertThatDeveloperLinkIsPresent(List<MenuEntry> list) {
//    assertEquals(1L, list.stream()
//        .filter(linkContainer -> "Développeur".equals(linkContainer.getCategory()))
//        .count());
//    Optional<MenuEntry> optional = list.stream()
//        .filter(linkContainer -> "Développeur".equals(linkContainer.getCategory()))
//        .findFirst();
//    assertTrue(optional.isPresent());
//    MenuEntry container = optional.get();
//    assertNotNull(container.getLinks());
//    assertEquals(1, container.getLinks().size());
//    assertEquals("developerLink", container.getLinks().get(0).getId());
//    assertEquals("Page développeur", container.getLinks().get(0).getText());
//    assertEquals(
//        "La page contient des liens vers les ressources.",
//        container.getLinks().get(0).getDescription());
//  }
//
//  /**
//   * Gets links for anna.
//   */
//  @SuppressWarnings("DefaultAnnotationParam")
//  @WithJwtAuthenticationToken(
//      preferredUsername = "anna",
//      roles = {USER_ROLE_NAME})
//  @Test
//  void getLinksForAnna() {
//    webTestClient
//        .get()
//        .uri("/api/links")
//        .accept(MediaType.APPLICATION_JSON)
//        .exchange()
//        .expectStatus().isOk()
//        .expectBodyList(MenuEntry.class)
//        .value(list -> {
//          assertThatAnnasLinkIsPresent(list);
//          assertThatPublicLinkIsPresent(list);
//        });
//  }
//
//  private void assertThatAnnasLinkIsPresent(List<MenuEntry> list) {
//    assertEquals(1L, list.stream()
//        .filter(linkContainer -> "Anna".equals(linkContainer.getCategory()))
//        .count());
//    Optional<MenuEntry> optional = list.stream()
//        .filter(linkContainer -> "Anna".equals(linkContainer.getCategory()))
//        .findFirst();
//    assertTrue(optional.isPresent());
//    MenuEntry container = optional.get();
//    assertNotNull(container.getLinks());
//    assertEquals(1, container.getLinks().size());
//    assertEquals("annaLink", container.getLinks().get(0).getId());
//  }
//
//  /**
//   * Gets links for molly.
//   */
//  @WithJwtAuthenticationToken(
//      preferredUsername = "molly",
//      roles = {USER_ROLE_NAME})
//  @Test
//  void getLinksForMolly() {
//    webTestClient
//        .get()
//        .uri("/api/links")
//        .accept(MediaType.APPLICATION_JSON)
//        .exchange()
//        .expectStatus().isOk()
//        .expectBodyList(MenuEntry.class)
//        .value(list -> {
//          assertThatMolliesLinkIsPresent(list);
//          assertThatPublicLinkIsPresent(list);
//        });
//  }
//
//  private void assertThatMolliesLinkIsPresent(List<MenuEntry> list) {
//    assertEquals(1L, list.stream()
//        .filter(linkContainer -> "Not categorized".equals(linkContainer.getCategory()))
//        .count());
//    Optional<MenuEntry> optional = list.stream()
//        .filter(linkContainer -> "Not categorized".equals(linkContainer.getCategory()))
//        .findFirst();
//    assertTrue(optional.isPresent());
//    MenuEntry container = optional.get();
//    assertNotNull(container.getLinks());
//    assertEquals(1, container.getLinks().size());
//    assertEquals("notMappedLink", container.getLinks().get(0).getId());
//  }

}