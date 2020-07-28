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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * The acl entity test.
 *
 * @author Christian Bremer
 */
class AclEntityTest {

  /**
   * Entry map.
   */
  @Test
  void entryMap() {
    AclEntity expected = new AclEntity();
    AceEntity value = new AceEntity();
    value.getUsers().add("anna");
    expected.setRead(value);
    Map<String, ? extends AceEntity> map = expected.entryMap();
    assertNotNull(map);
    assertTrue(map.containsKey("read"));
    assertEquals(value, map.get("read"));
  }

  /**
   * Gets owner.
   */
  @SuppressWarnings({"SimplifiableJUnitAssertion", "ConstantConditions", "EqualsWithItself"})
  @Test
  void getOwner() {
    AclEntity expected = new AclEntity();
    assertNull(expected.getOwner());

    expected.setOwner("anna");
    assertEquals("anna", expected.getOwner());

    expected = new AclEntity("anna", null);
    assertEquals("anna", expected.getOwner());

    assertFalse(expected.equals(null));
    assertFalse(expected.equals(new Object()));
    assertTrue(expected.equals(expected));
    assertTrue(expected.equals(new AclEntity("anna", null)));
  }

  /**
   * Gets read.
   */
  @Test
  void getRead() {
    AceEntity value = new AceEntity();
    value.getUsers().add("anna");
    AclEntity expected = new AclEntity("otto", Collections.singletonMap("read", value));
    assertEquals(value, expected.getRead());
  }
}