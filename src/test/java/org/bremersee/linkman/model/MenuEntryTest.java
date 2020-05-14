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
import java.util.List;
import java.util.UUID;
import org.bremersee.common.model.Link;
import org.junit.jupiter.api.Test;

/**
 * The menu entry test.
 *
 * @author Christian Bremer
 */
class MenuEntryTest {

  /**
   * Gets category.
   */
  @Test
  void getCategory() {
    MenuEntry model = new MenuEntry();
    String value = UUID.randomUUID().toString();
    model.setCategory(value);
    assertEquals(value, model.getCategory());

    assertEquals(model, model);
    assertEquals(model, MenuEntry.builder().category(value).build());
    assertNotEquals(model, null);
    assertNotEquals(model, new Object());

    assertTrue(model.toBuilder().build().toString().contains(value));
  }

  /**
   * Is pub.
   */
  @Test
  void isPub() {
    MenuEntry model = new MenuEntry();
    model.setPub(true);
    assertTrue(model.isPub());
    assertEquals(model, MenuEntry.builder().pub(true).build());
    assertTrue(model.toBuilder().build().toString().contains(String.valueOf(true)));
  }

  /**
   * Gets links.
   */
  @Test
  void getLinks() {
    String href = "http://example.org";
    List<Link> value = Collections.singletonList(Link.builder().href(href).build());
    MenuEntry model = new MenuEntry();
    model.setLinks(value);
    assertEquals(value, model.getLinks());
    assertEquals(model, MenuEntry.builder().links(value).build());
    assertTrue(model.toBuilder().build().toString().contains(href));
  }
}