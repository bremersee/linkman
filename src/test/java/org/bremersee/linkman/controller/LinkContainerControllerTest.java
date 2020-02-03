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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.bremersee.groupman.api.GroupWebfluxControllerApi;
import org.bremersee.linkman.config.LinkmanProperties;
import org.bremersee.linkman.model.CategorySpecification;
import org.bremersee.linkman.model.LinkContainer;
import org.bremersee.linkman.model.LinkSpecification;
import org.bremersee.linkman.repository.CategoryEntity;
import org.bremersee.linkman.repository.CategoryRepository;
import org.bremersee.linkman.repository.LinkEntity;
import org.bremersee.linkman.repository.LinkRepository;
import org.bremersee.security.access.AclBuilder;
import org.bremersee.security.access.PermissionConstants;
import org.bremersee.test.security.authentication.WithJwtAuthenticationToken;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
class LinkContainerControllerTest {

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
   * Setup tests.
   */
  @BeforeAll
  void setUp() {
    // https://docs.spring.io/spring-security/site/docs/current/reference/html/test-webflux.html
    WebTestClient
        .bindToApplicationContext(this.context)
        .configureClient()
        .build();

    // create admin category (matches admin role)
    CategorySpecification adminCategory = CategorySpecification.builder()
        .id("adminCategory")
        .order(100)
        .name("Administration")
        .matchesRoles(Collections.singleton(ADMIN_ROLE_NAME))
        .build();
    CategoryEntity adminCategoryEntity = modelMapper.map(adminCategory, CategoryEntity.class);
    StepVerifier
        .create(categoryRepository.save(adminCategoryEntity))
        .assertNext(entry -> assertNotNull(entry.getId()))
        .verifyComplete();

    // create developer category (matches developer group)
    CategorySpecification developerCategory = CategorySpecification.builder()
        .id("developerCategory")
        .order(50)
        .name("Developer")
        .translations(Collections.singletonMap("fr", "Développeur"))
        .matchesGroups(Collections.singleton("developers"))
        .build();
    CategoryEntity developerCategoryEntity = modelMapper
        .map(developerCategory, CategoryEntity.class);
    StepVerifier
        .create(categoryRepository.save(developerCategoryEntity))
        .assertNext(entry -> assertNotNull(entry.getId()))
        .verifyComplete();

    // create anna's category (matches user anna)
    CategorySpecification annaCategory = CategorySpecification.builder()
        .id("annaCategory")
        .order(200)
        .name("Anna")
        .matchesUsers(Collections.singleton("anna"))
        .build();
    CategoryEntity annaCategoryEntity = modelMapper.map(annaCategory, CategoryEntity.class);
    StepVerifier
        .create(categoryRepository.save(annaCategoryEntity))
        .assertNext(entry -> assertNotNull(entry.getId()))
        .verifyComplete();

    // create public link
    LinkSpecification publicLink = LinkSpecification.builder()
        .id("publicLink")
        .href("http://public.example.org")
        .text("Public page")
        .description("The public page.")
        .acl(AclBuilder.builder()
            .guest(true, PermissionConstants.READ)
            .buildAccessControlList())
        .build();
    LinkEntity publicLinkEntity = modelMapper.map(publicLink, LinkEntity.class);
    StepVerifier
        .create(linkRepository.save(publicLinkEntity))
        .assertNext(entry -> assertNotNull(entry.getId()))
        .verifyComplete();

    // create admin link that can see users with admin role and stephen
    LinkSpecification adminLink = LinkSpecification.builder()
        .id("adminLink")
        .href("http://admin.example.org")
        .text("Admin page")
        .description("The admin page.")
        .acl(AclBuilder.builder()
            .addUser("stephen", PermissionConstants.READ)
            .addRole(ADMIN_ROLE_NAME, PermissionConstants.READ)
            .buildAccessControlList())
        .build();
    LinkEntity adminLinkEntity = modelMapper.map(adminLink, LinkEntity.class);
    StepVerifier
        .create(linkRepository.save(adminLinkEntity))
        .assertNext(entry -> assertNotNull(entry.getId()))
        .verifyComplete();

    // create developer link that can see users in group developers
    LinkSpecification developerLink = LinkSpecification.builder()
        .id("developerLink")
        .href("http://developer.example.org")
        .text("Developer page")
        .textTranslations(Collections.singletonMap("fr", "Page développeur"))
        .description("The developer page.")
        .descriptionTranslations(
            Collections.singletonMap("fr", "La page contient des liens vers les ressources."))
        .acl(AclBuilder.builder()
            .addGroup("developers", PermissionConstants.READ)
            .buildAccessControlList())
        .build();
    LinkEntity developerLinkEntity = modelMapper.map(developerLink, LinkEntity.class);
    StepVerifier
        .create(linkRepository.save(developerLinkEntity))
        .assertNext(entry -> assertNotNull(entry.getId()))
        .verifyComplete();

    // create anna's link that only can see anna
    LinkSpecification annaLink = LinkSpecification.builder()
        .id("annaLink")
        .href("http://anna.example.org")
        .text("Anna's page")
        .description("The page of anna.")
        .acl(AclBuilder.builder()
            .addUser("anna", PermissionConstants.READ)
            .buildAccessControlList())
        .build();
    LinkEntity annaLinkEntity = modelMapper.map(annaLink, LinkEntity.class);
    StepVerifier
        .create(linkRepository.save(annaLinkEntity))
        .assertNext(entry -> assertNotNull(entry.getId()))
        .verifyComplete();

    // create not mapped link
    LinkSpecification notMappedLink = LinkSpecification.builder()
        .id("notMappedLink")
        .href("http://not-mapped.example.org")
        .text("A not mapped link")
        .description("The page of a not mapped link.")
        .acl(AclBuilder.builder()
            .addUser("molly", PermissionConstants.READ)
            .buildAccessControlList())
        .build();
    LinkEntity notMappedLinkEntity = modelMapper.map(notMappedLink, LinkEntity.class);
    StepVerifier
        .create(linkRepository.save(notMappedLinkEntity))
        .assertNext(entry -> assertNotNull(entry.getId()))
        .verifyComplete();
  }

  /**
   * Sets up test.
   */
  @BeforeEach
  void setUpTest() {
    when(groupService.getMembershipIds()).thenReturn(Mono.just(Collections.emptySet()));
  }

  /**
   * Gets public links for guests.
   */
  @Test
  void getPublicLinksForGuests() {
    webTestClient
        .get()
        .uri("/api/public/links")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(LinkContainer.class)
        .value(this::assertThatPublicLinkIsPresent);
  }

  private void assertThatPublicLinkIsPresent(List<LinkContainer> list) {
    assertEquals(1L, list.stream().filter(LinkContainer::isPub).count());
    Optional<LinkContainer> optional = list.stream()
        .filter(LinkContainer::isPub)
        .findFirst();
    assertTrue(optional.isPresent());
    LinkContainer container = optional.get();
    assertEquals(properties.getPublicCategory().getName(), container.getCategory());
    assertNotNull(container.getLinks());
    assertEquals(1, container.getLinks().size());
    assertEquals("publicLink", container.getLinks().get(0).getId());
  }

  /**
   * Gets links with admin role.
   */
  @WithJwtAuthenticationToken(
      preferredUsername = "leopold",
      roles = {ADMIN_ROLE_NAME})
  @Test
  void getLinksWithAdminRole() {
    webTestClient
        .get()
        .uri("/api/public/links")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(LinkContainer.class)
        .value(list -> {
          assertThatAdminLinkIsPresent(list);
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
    webTestClient
        .get()
        .uri("/api/public/links")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(LinkContainer.class)
        .value(list -> {
          assertThatAdminLinkIsPresent(list);
          assertThatPublicLinkIsPresent(list);
        });
  }

  private void assertThatAdminLinkIsPresent(List<LinkContainer> list) {
    assertEquals(1L, list.stream()
        .filter(linkContainer -> "Administration".equals(linkContainer.getCategory()))
        .count());
    Optional<LinkContainer> optional = list.stream()
        .filter(linkContainer -> "Administration".equals(linkContainer.getCategory()))
        .findFirst();
    assertTrue(optional.isPresent());
    LinkContainer container = optional.get();
    assertNotNull(container.getLinks());
    assertEquals(1, container.getLinks().size());
    assertEquals("adminLink", container.getLinks().get(0).getId());
  }

  /**
   * Gets links for developers.
   */
  @WithJwtAuthenticationToken(
      preferredUsername = "dev",
      roles = {USER_ROLE_NAME})
  @Test
  void getLinksForDevelopers() {
    when(groupService.getMembershipIds())
        .thenReturn(Mono.just(Collections.singleton("developers")));
    webTestClient
        .get()
        .uri("/api/public/links")
        .accept(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.ACCEPT_LANGUAGE, "fr")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(LinkContainer.class)
        .value(list -> {
          assertThatDeveloperLinkIsPresent(list);
          assertThatPublicLinkIsPresent(list);
        });
  }

  private void assertThatDeveloperLinkIsPresent(List<LinkContainer> list) {
    assertEquals(1L, list.stream()
        .filter(linkContainer -> "Développeur".equals(linkContainer.getCategory()))
        .count());
    Optional<LinkContainer> optional = list.stream()
        .filter(linkContainer -> "Développeur".equals(linkContainer.getCategory()))
        .findFirst();
    assertTrue(optional.isPresent());
    LinkContainer container = optional.get();
    assertNotNull(container.getLinks());
    assertEquals(1, container.getLinks().size());
    assertEquals("developerLink", container.getLinks().get(0).getId());
    assertEquals("Page développeur", container.getLinks().get(0).getText());
    assertEquals(
        "La page contient des liens vers les ressources.",
        container.getLinks().get(0).getDescription());
  }

  /**
   * Gets links for anna.
   */
  @SuppressWarnings("DefaultAnnotationParam")
  @WithJwtAuthenticationToken(
      preferredUsername = "anna",
      roles = {USER_ROLE_NAME})
  @Test
  void getLinksForAnna() {
    webTestClient
        .get()
        .uri("/api/public/links")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(LinkContainer.class)
        .value(list -> {
          assertThatAnnasLinkIsPresent(list);
          assertThatPublicLinkIsPresent(list);
        });
  }

  private void assertThatAnnasLinkIsPresent(List<LinkContainer> list) {
    assertEquals(1L, list.stream()
        .filter(linkContainer -> "Anna".equals(linkContainer.getCategory()))
        .count());
    Optional<LinkContainer> optional = list.stream()
        .filter(linkContainer -> "Anna".equals(linkContainer.getCategory()))
        .findFirst();
    assertTrue(optional.isPresent());
    LinkContainer container = optional.get();
    assertNotNull(container.getLinks());
    assertEquals(1, container.getLinks().size());
    assertEquals("annaLink", container.getLinks().get(0).getId());
  }

  /**
   * Gets links for molly.
   */
  @WithJwtAuthenticationToken(
      preferredUsername = "molly",
      roles = {USER_ROLE_NAME})
  @Test
  void getLinksForMolly() {
    webTestClient
        .get()
        .uri("/api/public/links")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(LinkContainer.class)
        .value(list -> {
          assertThatMolliesLinkIsPresent(list);
          assertThatPublicLinkIsPresent(list);
        });
  }

  private void assertThatMolliesLinkIsPresent(List<LinkContainer> list) {
    assertEquals(1L, list.stream()
        .filter(linkContainer -> "Not categorized".equals(linkContainer.getCategory()))
        .count());
    Optional<LinkContainer> optional = list.stream()
        .filter(linkContainer -> "Not categorized".equals(linkContainer.getCategory()))
        .findFirst();
    assertTrue(optional.isPresent());
    LinkContainer container = optional.get();
    assertNotNull(container.getLinks());
    assertEquals(1, container.getLinks().size());
    assertEquals("notMappedLink", container.getLinks().get(0).getId());
  }

}