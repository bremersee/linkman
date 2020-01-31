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

import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.validation.annotation.Validated;

/**
 * @author Christian Bremer
 */
@Document(collection = "categories")
@TypeAlias("category")
@Getter
@Setter
@ToString
@NoArgsConstructor
@Validated
public class CategoryEntity {

  @Id
  private String id;

  private AclEntity acl;

  private String name;

  private Map<String, String> translations;

  @Indexed
  private Boolean matchesGuest;

  @Indexed
  private Set<String> matchesUsers;

  @Indexed
  private Set<String> matchesRoles;

  @Indexed
  private Set<String> matchesGroups;

}
