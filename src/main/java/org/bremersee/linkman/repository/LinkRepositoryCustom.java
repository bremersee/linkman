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

import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The custom link repository.
 *
 * @author Christian Bremer
 */
public interface LinkRepositoryCustom {

  /**
   * Find by category id.
   *
   * @param categoryId the category id
   * @return the link entities
   */
  Flux<LinkEntity> findByCategoryId(String categoryId);

  Flux<LinkEntity> findByCategoryId(String categoryId, Sort sort);

  /**
   * Remove category references. Links that have no references anymore, will be deleted.
   *
   * @param categoryId the category id
   * @return void
   */
  Mono<Void> removeCategoryReferences(String categoryId);

}
