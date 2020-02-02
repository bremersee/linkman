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
import java.util.Map;
import java.util.UUID;
import org.bremersee.common.model.AccessControlList;
import org.bremersee.security.access.AclBuilder;
import org.junit.jupiter.api.Test;

/**
 * The link specification test.
 *
 * @author Christian Bremer
 */
class LinkSpecificationTest {

  /**
   * Gets id.
   */
  @Test
  void getId() {
    LinkSpecification model = new LinkSpecification();
    String value = UUID.randomUUID().toString();
    model.setId(value);
    assertEquals(value, model.getId());

    assertEquals(model, model);
    assertEquals(model, LinkSpecification.builder().id(value).build());
    assertNotEquals(model, null);
    assertNotEquals(model, new Object());

    assertTrue(model.toBuilder().build().toString().contains(value));
  }

  /**
   * Gets acl.
   */
  @Test
  void getAcl() {
    String owner = UUID.randomUUID().toString();
    LinkSpecification model = new LinkSpecification();
    AccessControlList value = AclBuilder.builder().owner(owner).buildAccessControlList();
    model.setAcl(value);
    assertEquals(value, model.getAcl());
    assertEquals(model, LinkSpecification.builder().acl(value).build());
    assertTrue(model.toBuilder().build().toString().contains(owner));
  }

  /**
   * Gets order.
   */
  @Test
  void getOrder() {
    LinkSpecification model = new LinkSpecification();
    int value = 100;
    model.setOrder(value);
    assertEquals(value, model.getOrder());
    assertEquals(model, LinkSpecification.builder().order(value).build());
    assertTrue(model.toBuilder().build().toString().contains(String.valueOf(value)));
  }

  /**
   * Gets href.
   */
  @Test
  void getHref() {
    LinkSpecification model = new LinkSpecification();
    String value = UUID.randomUUID().toString();
    model.setHref(value);
    assertEquals(value, model.getHref());
    assertEquals(model, LinkSpecification.builder().href(value).build());
    assertTrue(model.toBuilder().build().toString().contains(value));
  }

  /**
   * Gets blank.
   */
  @Test
  void getBlank() {
    LinkSpecification model = new LinkSpecification();
    model.setBlank(true);
    assertEquals(Boolean.TRUE, model.getBlank());
    assertEquals(model, LinkSpecification.builder().blank(true).build());
    assertTrue(model.toBuilder().build().toString().contains(String.valueOf(true)));
  }

  /**
   * Gets text.
   */
  @Test
  void getText() {
    LinkSpecification model = new LinkSpecification();
    String value = UUID.randomUUID().toString();
    model.setText(value);
    assertEquals(value, model.getText());
    assertEquals(model, LinkSpecification.builder().text(value).build());
    assertTrue(model.toBuilder().build().toString().contains(value));
  }

  /**
   * Gets text translations.
   */
  @Test
  void getTextTranslations() {
    LinkSpecification model = new LinkSpecification();
    Map<String, String> value = Collections.singletonMap("key", "value");
    model.setTextTranslations(value);
    assertEquals(value, model.getTextTranslations());
    assertEquals(model, LinkSpecification.builder().textTranslations(value).build());
    assertTrue(model.toBuilder().build().toString().contains(value.toString()));
  }

  /**
   * Gets description.
   */
  @Test
  void getDescription() {
    LinkSpecification model = new LinkSpecification();
    String value = UUID.randomUUID().toString();
    model.setDescription(value);
    assertEquals(value, model.getDescription());
    assertEquals(model, LinkSpecification.builder().description(value).build());
    assertTrue(model.toBuilder().build().toString().contains(value));
  }

  /**
   * Gets description translations.
   */
  @Test
  void getDescriptionTranslations() {
    LinkSpecification model = new LinkSpecification();
    Map<String, String> value = Collections.singletonMap("key", "value");
    model.setDescriptionTranslations(value);
    assertEquals(value, model.getDescriptionTranslations());
    assertEquals(model, LinkSpecification.builder().descriptionTranslations(value).build());
    assertTrue(model.toBuilder().build().toString().contains(value.toString()));
  }
}