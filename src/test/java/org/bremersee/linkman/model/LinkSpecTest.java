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

package org.bremersee.linkman.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.bremersee.common.model.TwoLetterLanguageCode;
import org.junit.jupiter.api.Test;

/**
 * The link specification test.
 *
 * @author Christian Bremer
 */
class LinkSpecTest {

  /**
   * Gets id.
   */
  @Test
  void getId() {
    LinkSpec model = new LinkSpec();
    String value = UUID.randomUUID().toString();
    model.setId(value);
    assertEquals(value, model.getId());

    assertEquals(model, model);
    assertEquals(model, LinkSpec.builder().id(value).build());
    assertNotEquals(model, null);
    assertNotEquals(model, new Object());

    assertTrue(model.toBuilder().build().toString().contains(value));
  }

  /**
   * Gets order.
   */
  @Test
  void getOrder() {
    LinkSpec model = new LinkSpec();
    int value = 100;
    model.setOrder(value);
    assertEquals(value, model.getOrder());
    assertEquals(model, LinkSpec.builder().order(value).build());
    assertTrue(model.toBuilder().build().toString().contains(String.valueOf(value)));
  }

  /**
   * Gets href.
   */
  @Test
  void getHref() {
    LinkSpec model = new LinkSpec();
    String value = UUID.randomUUID().toString();
    model.setHref(value);
    assertEquals(value, model.getHref());
    assertEquals(model, LinkSpec.builder().href(value).build());
    assertTrue(model.toBuilder().build().toString().contains(value));
  }

  /**
   * Gets blank.
   */
  @Test
  void getBlank() {
    LinkSpec model = new LinkSpec();
    model.setBlank(true);
    assertEquals(Boolean.TRUE, model.getBlank());
    assertEquals(model, LinkSpec.builder().blank(true).build());
    assertTrue(model.toBuilder().build().toString().contains(String.valueOf(true)));
  }

  /**
   * Gets text.
   */
  @Test
  void getText() {
    LinkSpec model = new LinkSpec();
    String value = UUID.randomUUID().toString();
    model.setText(value);
    assertEquals(value, model.getText());
    assertEquals(model, LinkSpec.builder().text(value).build());
    assertTrue(model.toBuilder().build().toString().contains(value));
  }

  /**
   * Gets text translations.
   */
  @Test
  void getTextTranslations() {
    LinkSpec model = new LinkSpec();
    Set<Translation> value = Collections.singleton(new Translation("de", "value"));
    model.setTextTranslations(value);
    assertEquals(value, model.getTextTranslations());
    assertEquals(model, LinkSpec.builder().textTranslations(value).build());
    assertTrue(model.toBuilder().build().toString().contains(value.toString()));

    assertEquals("value", model.getText(TwoLetterLanguageCode.DE));
    assertEquals("value", model.getText(new Locale("de")));
    assertEquals("value", model.getText("de"));
  }

  /**
   * Gets description.
   */
  @Test
  void getDescription() {
    LinkSpec model = new LinkSpec();
    String value = UUID.randomUUID().toString();
    model.setDescription(value);
    assertEquals(value, model.getDescription());
    assertEquals(model, LinkSpec.builder().description(value).build());
    assertTrue(model.toBuilder().build().toString().contains(value));
  }

  /**
   * Gets description translations.
   */
  @Test
  void getDescriptionTranslations() {
    LinkSpec model = new LinkSpec();
    Set<Translation> value = Collections.singleton(new Translation("fr", "value"));
    model.setDescriptionTranslations(value);
    assertEquals(value, model.getDescriptionTranslations());
    assertEquals(model, LinkSpec.builder().descriptionTranslations(value).build());
    assertTrue(model.toBuilder().build().toString().contains(value.toString()));

    assertEquals("value", model.getDescription(TwoLetterLanguageCode.FR));
    assertEquals("value", model.getDescription(new Locale("fr")));
    assertEquals("value", model.getDescription("fr"));
  }

  /**
   * Gets category ids.
   */
  @Test
  void getCategoryIds() {
    LinkSpec model = new LinkSpec();
    Set<String> value = Collections.singleton("value");
    model.setCategoryIds(value);
    assertEquals(value, model.getCategoryIds());
    assertEquals(model, LinkSpec.builder().categoryIds(value).build());
    assertTrue(model.toBuilder().build().toString().contains(value.toString()));
  }
}