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
 * The category specification test.
 *
 * @author Christian Bremer
 */
class CategorySpecificationTest {

  /**
   * Gets id.
   */
  @Test
  void getId() {
    CategorySpecification model = new CategorySpecification();
    String value = UUID.randomUUID().toString();
    model.setId(value);
    assertEquals(value, model.getId());

    assertEquals(model, model);
    assertEquals(model, CategorySpecification.builder().id(value).build());
    assertNotEquals(model, null);
    assertNotEquals(model, new Object());

    assertTrue(model.toBuilder().build().toString().contains(value));
  }

  /**
   * Gets order.
   */
  @Test
  void getOrder() {
    CategorySpecification model = new CategorySpecification();
    int value = 100;
    model.setOrder(value);
    assertEquals(value, model.getOrder());
    assertEquals(model, CategorySpecification.builder().order(value).build());
    assertTrue(model.toBuilder().build().toString().contains(String.valueOf(value)));
  }

  /**
   * Gets name.
   */
  @Test
  void getName() {
    CategorySpecification model = new CategorySpecification();
    String value = UUID.randomUUID().toString();
    model.setName(value);
    assertEquals(value, model.getName());
    assertEquals(model, CategorySpecification.builder().name(value).build());
    assertTrue(model.toBuilder().build().toString().contains(value));
  }

  /**
   * Gets translations.
   */
  @Test
  void getTranslations() {
    CategorySpecification model = new CategorySpecification();
    Set<Translation> value = Collections.singleton(new Translation("de", "value"));
    model.setTranslations(value);
    assertEquals(value, model.getTranslations());
    assertEquals(model, CategorySpecification.builder().translations(value).build());
    assertTrue(model.toBuilder().build().toString().contains(value.toString()));

    assertEquals("value", model.getName(TwoLetterLanguageCode.DE));
    assertEquals("value", model.getName(new Locale("de")));
    assertEquals("value", model.getName("de"));
  }

  /**
   * Gets matches guest.
   */
  @Test
  void getMatchesGuest() {
    CategorySpecification model = new CategorySpecification();
    model.setMatchesGuest(true);
    assertEquals(Boolean.TRUE, model.getMatchesGuest());
    assertEquals(model, CategorySpecification.builder().matchesGuest(true).build());
    assertTrue(model.toBuilder().build().toString().contains(String.valueOf(true)));
  }

  /**
   * Gets matches users.
   */
  @Test
  void getMatchesUsers() {
    CategorySpecification model = new CategorySpecification();
    Set<String> value = Collections.singleton(UUID.randomUUID().toString());
    model.setMatchesUsers(value);
    assertEquals(value, model.getMatchesUsers());
    assertEquals(model, CategorySpecification.builder().matchesUsers(value).build());
    assertTrue(model.toBuilder().build().toString().contains(value.toString()));
  }

  /**
   * Gets matches roles.
   */
  @Test
  void getMatchesRoles() {
    CategorySpecification model = new CategorySpecification();
    Set<String> value = Collections.singleton(UUID.randomUUID().toString());
    model.setMatchesRoles(value);
    assertEquals(value, model.getMatchesRoles());
    assertEquals(model, CategorySpecification.builder().matchesRoles(value).build());
    assertTrue(model.toBuilder().build().toString().contains(value.toString()));
  }

  /**
   * Gets matches groups.
   */
  @Test
  void getMatchesGroups() {
    CategorySpecification model = new CategorySpecification();
    Set<String> value = Collections.singleton(UUID.randomUUID().toString());
    model.setMatchesGroups(value);
    assertEquals(value, model.getMatchesGroups());
    assertEquals(model, CategorySpecification.builder().matchesGroups(value).build());
    assertTrue(model.toBuilder().build().toString().contains(value.toString()));
  }
}