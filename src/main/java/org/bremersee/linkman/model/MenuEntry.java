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

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.Link;

/**
 * The menu entry is a category with it's links.
 *
 * @author Christian Bremer
 */
@Schema(description = "The category and it's links.")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class MenuEntry {

  @Schema(description = "The name of the category.", required = true)
  @JsonProperty(value = "category", required = true)
  private String category;

  @Schema(description = "Specifies whether the links of this category can be seen without "
      + "authentication. Default is false.", required = true)
  @JsonProperty(value = "pub", required = true)
  private boolean pub;

  @Schema(description = "The links of the category.")
  @JsonProperty("links")
  private List<Link> links;

  /**
   * Instantiates a new link container.
   *
   * @param category the category
   * @param pub specifies whether the category is public or not
   * @param links the links
   */
  @Builder(toBuilder = true)
  @SuppressWarnings("unused")
  public MenuEntry(
      String category,
      boolean pub,
      List<Link> links) {
    this.category = category;
    this.pub = pub;
    this.links = links;
  }

}
