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
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.bremersee.common.model.Link;
import org.bremersee.groupman.api.GroupWebfluxControllerApi;
import org.bremersee.linkman.config.LinkmanProperties;
import org.bremersee.linkman.model.CategorySpec;
import org.bremersee.linkman.model.LinkSpec;
import org.bremersee.linkman.model.MenuEntry;
import org.bremersee.linkman.model.Translation;
import org.bremersee.linkman.repository.CategoryEntity;
import org.bremersee.linkman.repository.CategoryRepository;
import org.bremersee.linkman.repository.LinkEntity;
import org.bremersee.linkman.repository.LinkRepository;
import org.bremersee.security.access.AclBuilder;
import org.bremersee.security.access.PermissionConstants;
import org.bremersee.test.security.authentication.WithJwtAuthenticationToken;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * The menu controller test.
 *
 * @author Christian Bremer
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "bremersee.security.authentication.enable-jwt-support=true"
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
          .addGroup("developer", PermissionConstants.READ)
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
      .textTranslations(Set.of(
          new Translation("de", "Verwaltungsseite"),
          new Translation("fr", "Page d'administration")))
      .description("That is the administrative area.")
      .descriptionTranslations(Set.of(
          new Translation("de", "Das ist der administrative Bereich."),
          new Translation("fr", "C'est le domaine administratif.")))
      .build();

  /**
   * The public test link.
   */
  final LinkSpec publicTestLink = LinkSpec.builder()
      .id(UUID.randomUUID().toString())
      .order(1)
      .href("http://web.example.org")
      .blank(true)
      .text("A public web page")
      .textTranslations(Collections.singleton(
          new Translation("de", "Eine öffentliche Seite")))
      .description("Have a look.")
      .descriptionTranslations(Collections.singleton(
          new Translation("de", "Schaue es dir an.")))
      .build();

  /**
   * Sets up.
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
        .assertNext(entry -> assertEquals(testLink.getId(), entry.getId()))
        .verifyComplete();

    final LinkEntity publicTestLinkEntity = modelMapper.map(publicTestLink, LinkEntity.class);
    StepVerifier
        .create(categoryRepository.findPublicCategory().flatMap(category -> {
          publicTestLinkEntity.setCategoryIds(Collections.singleton(category.getId()));
          return linkRepository.save(publicTestLinkEntity);
        }))
        .assertNext(entry -> assertEquals(publicTestLinkEntity.getId(), entry.getId()))
        .verifyComplete();
  }


  /**
   * Gets public links for guests.
   */
  @Test
  void getPublicLinksForGuests() {
    webTestClient
        .get()
        .uri("/api/menu")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(MenuEntry.class)
        .value(list -> {
          assertThatPublicLinkIsPresent(list);
          assertThatTestLinkIsNotPresent(list);
        });
  }

  private void assertThatPublicLinkIsPresent(List<MenuEntry> list) {
    assertEquals(1L, list.stream().filter(MenuEntry::isPub).count());
    Optional<MenuEntry> optional = list.stream()
        .filter(MenuEntry::isPub)
        .findFirst();
    assertTrue(optional.isPresent());
    MenuEntry container = optional.get();
    assertNotNull(container.getLinks());
    assertEquals(1, container.getLinks().size());
    assertEquals(publicTestLink.getId(), container.getLinks().get(0).getId());
  }

  /**
   * Gets links with admin role.
   */
  @WithJwtAuthenticationToken(
      preferredUsername = "leopold",
      roles = {ADMIN_ROLE_NAME})
  @Test
  void getLinksWithAdminRole() {
    when(groupService.getMembershipIds()).thenReturn(Mono.just(Collections.emptySet()));

    webTestClient
        .get()
        .uri("/api/menu")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(MenuEntry.class)
        .value(list -> {
          assertThatTestLinkIsPresent(list);
          assertThatPublicLinkIsPresent(list);
        });
  }

  /**
   * Gets links for stephen.
   */
  @WithJwtAuthenticationToken(
      preferredUsername = "stephen",
      roles = {USER_ROLE_NAME})
  @Test
  void getLinksForStephen() {
    when(groupService.getMembershipIds()).thenReturn(Mono.just(Collections.emptySet()));

    webTestClient
        .get()
        .uri("/api/menu")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(MenuEntry.class)
        .value(list -> {
          assertThatTestLinkIsPresent(list);
          assertThatPublicLinkIsPresent(list);
        });
  }

  /**
   * Gets links for stephen in french.
   */
  @WithJwtAuthenticationToken(
      preferredUsername = "stephen",
      roles = {USER_ROLE_NAME})
  @Test
  void getLinksForStephenInFrench() {
    when(groupService.getMembershipIds()).thenReturn(Mono.just(Collections.emptySet()));

    webTestClient
        .get()
        .uri("/api/menu")
        .accept(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.ACCEPT_LANGUAGE, "fr")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(MenuEntry.class)
        .value(list -> {
          assertThatPublicLinkIsPresent(list);
          Optional<Link> link = list.stream()
              .flatMap(entry -> entry.getLinks().stream()
                  .filter(l -> testLink.getId().equals(l.getId())))
              .findFirst();
          assertTrue(link.isPresent());
          assertEquals("Page d'administration", link.get().getText());
          assertEquals("C'est le domaine administratif.", link.get().getDescription());
        });
  }

  /**
   * Gets links for developers.
   */
  @WithJwtAuthenticationToken(
      preferredUsername = "molly",
      roles = {USER_ROLE_NAME})
  @Test
  void getLinksForDevelopers() {
    when(groupService.getMembershipIds()).thenReturn(Mono.just(Collections.singleton("developer")));

    webTestClient
        .get()
        .uri("/api/menu")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(MenuEntry.class)
        .value(list -> {
          assertThatTestLinkIsPresent(list);
          assertThatPublicLinkIsPresent(list);
        });
  }

  /**
   * Gets links for leopold.
   */
  @WithJwtAuthenticationToken(
      preferredUsername = "leopold",
      roles = {USER_ROLE_NAME})
  @Test
  void getLinksFoLeopold() {
    when(groupService.getMembershipIds()).thenReturn(Mono.just(Collections.emptySet()));

    webTestClient
        .get()
        .uri("/api/menu")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(MenuEntry.class)
        .value(list -> {
          assertThatTestLinkIsNotPresent(list);
          assertThatPublicLinkIsPresent(list);
        });
  }

  private void assertThatTestLinkIsPresent(List<MenuEntry> list) {
    assertTrue(list.stream()
        .anyMatch(menuEntry -> menuEntry.getLinks().stream()
            .anyMatch(link -> testLink.getId().equals(link.getId()))));
  }

  private void assertThatTestLinkIsNotPresent(List<MenuEntry> list) {
    assertFalse(list.stream()
        .anyMatch(menuEntry -> menuEntry.getLinks().stream()
            .anyMatch(link -> testLink.getId().equals(link.getId()))));
  }

}