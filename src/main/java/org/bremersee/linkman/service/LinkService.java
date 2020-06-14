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

package org.bremersee.linkman.service;

import org.bremersee.data.minio.PutObject;
import org.bremersee.linkman.model.LinkSpec;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The link service.
 *
 * @author Christian Bremer
 */
public interface LinkService {

  /**
   * Gets links.
   *
   * @param categoryId the category id
   * @return the links
   */
  Flux<LinkSpec> getLinks(@Nullable String categoryId);

  /**
   * Add link.
   *
   * @param link the link
   * @return the added link
   */
  Mono<LinkSpec> addLink(LinkSpec link);

  /**
   * Gets link.
   *
   * @param id the id
   * @return the link
   */
  Mono<LinkSpec> getLink(String id);

  /**
   * Update link.
   *
   * @param id the id
   * @param link the link
   * @return the updated link
   */
  Mono<LinkSpec> updateLink(String id, LinkSpec link);

  /**
   * Update link images mono.
   *
   * @param id the id
   * @param cardImage the card image
   * @param menuImage the menu image
   * @return the mono
   */
  Mono<LinkSpec> updateLinkImages(
      String id,
      PutObject<?> cardImage,
      PutObject<?> menuImage);

  /**
   * Delete link.
   *
   * @param id the id
   * @return void mono
   */
  Mono<Void> deleteLink(String id);

}
