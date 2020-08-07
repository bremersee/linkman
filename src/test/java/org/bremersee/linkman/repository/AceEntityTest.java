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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * The ace entity test.
 *
 * @author Christian Bremer
 */
class AceEntityTest {

  /**
   * Unmodifiable.
   */
  @SuppressWarnings({"SimplifiableJUnitAssertion", "ConstantConditions", "EqualsWithItself"})
  @Test
  void unmodifiable() {
    AceEntity expected = new AceEntity();
    String value = UUID.randomUUID().toString();
    expected.setUsers(Collections.singleton(value));
    assertEquals(expected, new AceEntity(expected));
    assertEquals(expected, expected.unmodifiable());
    assertEquals(expected.hashCode(), expected.unmodifiable().hashCode());
    assertEquals(expected.toString(), expected.unmodifiable().toString());
    assertFalse(expected.equals(null));
    assertFalse(expected.equals(new Object()));
    assertTrue(expected.equals(expected));
  }

  /**
   * Is guest.
   */
  @Test
  void isGuest() {
    AceEntity expected = new AceEntity();
    expected.setGuest(true);
    assertTrue(expected.isGuest());
    assertEquals(expected, expected.unmodifiable());
  }

  /**
   * Gets users.
   */
  @Test
  void getUsers() {
    AceEntity expected = new AceEntity();
    String value = UUID.randomUUID().toString();
    expected.setUsers(Collections.singleton(value));
    assertTrue(expected.getUsers().contains(value));
  }

  /**
   * Gets roles.
   */
  @Test
  void getRoles() {
    AceEntity expected = new AceEntity();
    String value = UUID.randomUUID().toString();
    expected.setRoles(Collections.singleton(value));
    assertTrue(expected.getRoles().contains(value));
  }

  /**
   * Gets groups.
   */
  @Test
  void getGroups() {
    AceEntity expected = new AceEntity();
    String value = UUID.randomUUID().toString();
    expected.setGroups(Collections.singleton(value));
    assertTrue(expected.getGroups().contains(value));
  }
}