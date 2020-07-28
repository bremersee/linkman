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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * The group representation test.
 *
 * @author Christian Bremer
 */
class GroupRepresentationTest {

  /**
   * Gets id.
   */
  @SuppressWarnings({"SimplifiableJUnitAssertion", "ConstantConditions", "EqualsWithItself"})
  @Test
  void getId() {
    GroupRepresentation expected = new GroupRepresentation();
    String value = UUID.randomUUID().toString();
    expected.setId(value);
    assertNotNull(expected.getId());
    assertEquals(value, expected.getId());
    assertFalse(expected.equals(null));
    assertFalse(expected.equals(new Object()));
    assertTrue(expected.equals(expected));

    GroupRepresentation actual = GroupRepresentation.builder()
        .id(value)
        .build();
    assertTrue(actual.equals(expected));
    assertEquals(expected.hashCode(), actual.hashCode());
    assertEquals(expected.toString(), actual.toString());
    assertEquals(expected, actual.toBuilder().build());
  }

  /**
   * Gets name.
   */
  @Test
  void getName() {
    String value = UUID.randomUUID().toString();
    GroupRepresentation expected = new GroupRepresentation(null, value);
    assertEquals(value, expected.getName());

    value = UUID.randomUUID().toString();
    expected.setName(value);
    assertEquals(value, expected.getName());

    assertEquals(value, expected.getName());
    assertEquals(expected, GroupRepresentation.builder().name(value).build());
    assertTrue(expected.toString().contains(value));
  }
}