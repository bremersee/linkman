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
import org.bremersee.common.model.AccessControlList;
import org.bremersee.common.model.TwoLetterLanguageCode;
import org.bremersee.security.access.AclBuilder;
import org.junit.jupiter.api.Test;

/**
 * The category specification test.
 *
 * @author Christian Bremer
 */
class CategorySpecTest {

  /**
   * Gets id.
   */
  @Test
  void getId() {
    CategorySpec model = new CategorySpec();
    String value = UUID.randomUUID().toString();
    model.setId(value);
    assertEquals(value, model.getId());

    assertEquals(model, model);
    assertEquals(model, CategorySpec.builder().id(value).build());
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
    CategorySpec model = new CategorySpec();
    AccessControlList value = AclBuilder.builder().owner(owner).buildAccessControlList();
    model.setAcl(value);
    assertEquals(value, model.getAcl());
    assertEquals(model, CategorySpec.builder().acl(value).build());
    assertTrue(model.toBuilder().build().toString().contains(owner));
  }

  /**
   * Gets order.
   */
  @Test
  void getOrder() {
    CategorySpec model = new CategorySpec();
    int value = 100;
    model.setOrder(value);
    assertEquals(value, model.getOrder());
    assertEquals(model, CategorySpec.builder().order(value).build());
    assertTrue(model.toBuilder().build().toString().contains(String.valueOf(value)));
  }

  /**
   * Gets name.
   */
  @Test
  void getName() {
    CategorySpec model = new CategorySpec();
    String value = UUID.randomUUID().toString();
    model.setName(value);
    assertEquals(value, model.getName());
    assertEquals(model, CategorySpec.builder().name(value).build());
    assertTrue(model.toBuilder().build().toString().contains(value));
  }

  /**
   * Gets translations.
   */
  @Test
  void getTranslations() {
    CategorySpec model = new CategorySpec();
    Set<Translation> value = Collections.singleton(new Translation("de", "value"));
    model.setTranslations(value);
    assertEquals(value, model.getTranslations());
    assertEquals(model, CategorySpec.builder().translations(value).build());
    assertTrue(model.toBuilder().build().toString().contains(value.toString()));

    assertEquals("value", model.getName(TwoLetterLanguageCode.DE));
    assertEquals("value", model.getName(new Locale("de")));
    assertEquals("value", model.getName("de"));
  }

}