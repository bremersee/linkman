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

package org.bremersee.linkman.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import org.bremersee.linkman.model.Translation;
import org.junit.jupiter.api.Test;

class CategoryEntityTest {

  @Test
  void isPublic() {
    CategoryEntity expected = new CategoryEntity();
    assertFalse(expected.isPublic());

    AceEntity aceEntity = new AceEntity();
    aceEntity.setGuest(true);
    AclEntity aclEntity = new AclEntity();
    aclEntity.setRead(aceEntity);
    expected.setAcl(aclEntity);
    assertTrue(expected.isPublic());
  }

  @Test
  void getName() {
    CategoryEntity expected = new CategoryEntity();
    assertNull(expected.getName());

    expected.setName("Welcome");
    assertEquals("Welcome", expected.getName());
    assertEquals("Welcome", expected.getName(Locale.FRENCH));

    Set<Translation> translations = Collections.singleton(new Translation("de", "Willkommen"));
    expected.setTranslations(translations);
    assertEquals(translations, expected.getTranslations());

    assertEquals("Willkommen", expected.getName(Locale.GERMANY));
  }

  @SuppressWarnings({"SimplifiableJUnitAssertion", "ConstantConditions", "EqualsWithItself"})
  @Test
  void compareTo() {
    CategoryEntity a = new CategoryEntity();
    a.setOrder(1);
    CategoryEntity b = new CategoryEntity();
    b.setOrder(2);
    assertTrue(a.compareTo(b) < 0);

    a.setOrder(2);
    assertEquals(0, a.compareTo(b));

    a.setName("Hello");
    b.setName("Welcome");
    assertTrue(a.compareTo(b) < 0);

    a.setName("nil");
    b.setName("nil");
    Set<Translation> at = Collections.singleton(new Translation("de", "Hallo"));
    a.setTranslations(at);
    Set<Translation> bt = Collections.singleton(new Translation("de", "Willkommen"));
    b.setTranslations(bt);
    assertTrue(a.compareTo(b, Locale.GERMAN) < 0);

    b.setTranslations(at);
    assertFalse(a.equals(null));
    assertFalse(a.equals(new Object()));
    assertTrue(a.equals(a));
    assertTrue(a.equals(b));
    assertEquals(a.hashCode(), b.hashCode());
    assertEquals(a.toString(), b.toString());

    a.setId("1");
    b.setId("2");
    assertNotEquals(a, b);
  }

  @Test
  void getId() {
    CategoryEntity expected = new CategoryEntity();
    expected.setId("1");
    assertEquals("1", expected.getId());
  }

  @Test
  void getAcl() {
    CategoryEntity expected = new CategoryEntity();
    AceEntity aceEntity = new AceEntity();
    aceEntity.setGuest(true);
    AclEntity aclEntity = new AclEntity();
    aclEntity.setRead(aceEntity);
    expected.setAcl(aclEntity);
    assertEquals(aclEntity, expected.getAcl());
  }

  @Test
  void getOrder() {
    CategoryEntity expected = new CategoryEntity();
    expected.setOrder(1);
    assertEquals(1, expected.getOrder());
  }

}