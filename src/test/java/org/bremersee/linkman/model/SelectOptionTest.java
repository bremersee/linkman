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

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * The select option test.
 *
 * @author Christian Bremer
 */
class SelectOptionTest {

  /**
   * Gets value.
   */
  @SuppressWarnings({"SimplifiableJUnitAssertion", "ConstantConditions", "EqualsWithItself"})
  @Test
  void getValue() {
    SelectOption expected = new SelectOption();
    String value = UUID.randomUUID().toString();
    expected.setValue(value);
    assertNotNull(expected.getValue());
    assertEquals(value, expected.getValue());
    assertFalse(expected.equals(null));
    assertFalse(expected.equals(new Object()));
    assertTrue(expected.equals(expected));

    SelectOption actual = SelectOption.builder()
        .value(value)
        .build();
    assertTrue(actual.equals(expected));
    assertEquals(expected.hashCode(), actual.hashCode());
    assertEquals(expected.toString(), actual.toString());
    assertEquals(expected, actual.toBuilder().build());
  }

  /**
   * Gets display value.
   */
  @Test
  void getDisplayValue() {
    SelectOption expected = new SelectOption();
    String value = UUID.randomUUID().toString();
    expected.setDisplayValue(value);
    assertEquals(value, expected.getDisplayValue());
    assertEquals(expected, SelectOption.builder().displayValue(value).build());

    expected = new SelectOption("a", "b");
    assertEquals(expected, SelectOption.builder()
        .value("a")
        .displayValue("b")
        .build());
  }
}