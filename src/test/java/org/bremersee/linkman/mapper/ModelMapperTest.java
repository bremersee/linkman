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

package org.bremersee.linkman.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.UUID;
import org.bremersee.common.model.TwoLetterLanguageCode;
import org.bremersee.data.minio.UrlSigner;
import org.bremersee.linkman.model.LinkSpec;
import org.bremersee.linkman.model.Translation;
import org.bremersee.linkman.repository.LinkEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

/**
 * The model mapper test.
 *
 * @author Christian Bremer
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost/jwk"
})
@ActiveProfiles({"default"})
@TestInstance(Lifecycle.PER_CLASS) // allows us to use @BeforeAll with a non-static method
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ModelMapperTest {

  private static final String IMAGE_URL = "http://somewhere/12345";

  @Autowired
  private ModelMapper modelMapper;

  @MockBean
  private UrlSigner urlSigner;

  /**
   * Sets up.
   */
  @BeforeEach
  void setUp() {
    when(urlSigner.apply(Mockito.any()))
        .thenAnswer((Answer<String>) invocation -> invocation.getArgument(0) == null
            ? null
            : IMAGE_URL);
  }

  /**
   * Link entity to link spec.
   */
  @Test
  void linkEntityToLinkSpec() {
    LinkEntity entity = new LinkEntity();
    entity.setId(UUID.randomUUID().toString());
    entity.setOrder(123);
    entity.setHref("http://localhost");
    entity.setText("Local Service");
    entity.setTextTranslations(Set.of(
        Translation.builder()
            .language(TwoLetterLanguageCode.DE)
            .value("Lokaler Service")
            .build()));
    entity.setDescription("This is the local host service.");
    entity.setDescriptionTranslations(Set.of(
        Translation.builder()
            .language(TwoLetterLanguageCode.DE)
            .value("Das ist der Dienst auf der lokalen Maschine.")
            .build()));
    entity.setCategoryIds(Set.of("3", "4", "5"));
    entity.setCardImage("card");
    entity.setMenuImage(null);

    LinkSpec spec = modelMapper.map(entity, LinkSpec.class);
    assertEquals(entity.getId(), spec.getId());
    assertEquals(entity.getOrder(), spec.getOrder());
    assertEquals(entity.getHref(), spec.getHref());
    assertEquals(entity.getText(), spec.getText());
    assertEquals(entity.getTextTranslations(), spec.getTextTranslations());
    assertEquals(entity.getDescription(), spec.getDescription());
    assertEquals(entity.getDescriptionTranslations(), spec.getDescriptionTranslations());
    assertEquals(IMAGE_URL, spec.getCardImageUrl());
    assertNull(spec.getMenuImageUrl());
  }

  /**
   * Link spec to entity.
   */
  @Test
  void linkSpecToEntity() {
    LinkSpec spec = LinkSpec.builder()
        .id(UUID.randomUUID().toString())
        .order(123)
        .href("http://localhost")
        .text("Local Service")
        .textTranslations(Set.of(
            Translation.builder()
                .language(TwoLetterLanguageCode.DE)
                .value("Lokaler Service")
                .build()))
        .description("This is the local host service.")
        .descriptionTranslations(Set.of(
            Translation.builder()
                .language(TwoLetterLanguageCode.DE)
                .value("Das ist der Dienst auf der lokalen Maschine.")
                .build()))
        .categoryIds(Set.of("3", "4", "5"))
        .cardImageUrl("http://somehost/presigned")
        .menuImageUrl(null)
        .build();

    LinkEntity entity = new LinkEntity();
    entity.setCardImage("card");
    entity.setMenuImage("menu");

    modelMapper.map(spec, entity);

    assertEquals(spec.getId(), entity.getId());
    assertEquals(spec.getOrder(), entity.getOrder());
    assertEquals(spec.getHref(), entity.getHref());
    assertEquals(spec.getText(), entity.getText());
    assertEquals(spec.getTextTranslations(), entity.getTextTranslations());
    assertEquals(spec.getDescription(), entity.getDescription());
    assertEquals(spec.getDescriptionTranslations(), entity.getDescriptionTranslations());
    assertEquals("card", entity.getCardImage());
    assertEquals("menu", entity.getMenuImage());
  }

}
