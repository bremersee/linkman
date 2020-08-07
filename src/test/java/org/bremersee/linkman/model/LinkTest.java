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
 * The link test.
 *
 * @author Christian Bremer
 */
class LinkTest {

  /**
   * Gets id.
   */
  @SuppressWarnings({"SimplifiableJUnitAssertion", "ConstantConditions", "EqualsWithItself"})
  @Test
  void getId() {
    Link expected = new Link();
    String value = UUID.randomUUID().toString();
    expected.setId(value);
    assertNotNull(expected.getId());
    assertEquals(value, expected.getId());
    assertFalse(expected.equals(null));
    assertFalse(expected.equals(new Object()));
    assertTrue(expected.equals(expected));

    Link actual = Link.builder()
        .id(value)
        .build();
    assertTrue(actual.equals(expected));
    assertEquals(expected.hashCode(), actual.hashCode());
    assertEquals(expected.toString(), actual.toString());
    assertEquals(expected, actual.toBuilder().build());
  }

  /**
   * Gets href.
   */
  @Test
  void getHref() {
    Link expected = new Link();
    String value = UUID.randomUUID().toString();
    expected.setHref(value);
    assertEquals(value, expected.getHref());
    assertEquals(expected, Link.builder().href(value).build());
    assertEquals(expected, expected.toBuilder().build());
  }

  /**
   * Gets blank.
   */
  @Test
  void getBlank() {
    Link expected = new Link();
    assertFalse(expected.getBlank());
    expected.setBlank(Boolean.TRUE);
    assertTrue(expected.getBlank());
    assertEquals(expected, Link.builder().blank(true).build());
    assertEquals(expected, expected.toBuilder().build());
  }

  /**
   * Gets text.
   */
  @Test
  void getText() {
    Link expected = new Link();
    String value = UUID.randomUUID().toString();
    expected.setText(value);
    assertEquals(value, expected.getText());
    assertEquals(expected, Link.builder().text(value).build());
    assertEquals(expected, expected.toBuilder().build());
  }

  /**
   * Gets display text.
   */
  @Test
  void getDisplayText() {
    Link expected = new Link();
    assertTrue(expected.getDisplayText());

    expected.setDisplayText(null);
    assertTrue(expected.getDisplayText());

    expected.setDisplayText(false);
    assertFalse(expected.getDisplayText());
    assertEquals(expected, Link.builder().displayText(false).build());
    assertEquals(expected, expected.toBuilder().build());
  }

  /**
   * Gets description.
   */
  @Test
  void getDescription() {
    Link expected = new Link();
    String value = UUID.randomUUID().toString();
    expected.setDescription(value);
    assertEquals(value, expected.getDescription());
    assertEquals(expected, Link.builder().description(value).build());
    assertEquals(expected, expected.toBuilder().build());
  }

  /**
   * Gets card image url.
   */
  @Test
  void getCardImageUrl() {
    Link expected = new Link();
    String value = UUID.randomUUID().toString();
    expected.setCardImageUrl(value);
    assertEquals(value, expected.getCardImageUrl());
    assertEquals(expected, Link.builder().cardImageUrl(value).build());
    assertEquals(expected, expected.toBuilder().build());
  }

  /**
   * Gets menu image url.
   */
  @Test
  void getMenuImageUrl() {
    Link expected = new Link();
    String value = UUID.randomUUID().toString();
    expected.setMenuImageUrl(value);
    assertEquals(value, expected.getMenuImageUrl());
    assertEquals(expected, Link.builder().menuImageUrl(value).build());
    assertEquals(expected, expected.toBuilder().build());
  }

  /**
   * Constructor.
   */
  @Test
  void constructor() {
    Link expected = new Link("a", "b", true, "c", false, "d", "e", "f");
    assertEquals(expected, Link.builder()
        .id("a")
        .href("b")
        .blank(true)
        .text("c")
        .displayText(false)
        .description("d")
        .cardImageUrl("e")
        .menuImageUrl("f")
        .build());
  }
}