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

import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The custom link repository implementation.
 *
 * @author Christian Bremer
 */
@SuppressWarnings("unused")
public class LinkRepositoryImpl implements LinkRepositoryCustom {

  private ReactiveMongoTemplate mongoTemplate;

  /**
   * Instantiates a new custom link repository.
   *
   * @param mongoTemplate the mongo template
   */
  public LinkRepositoryImpl(ReactiveMongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public Flux<LinkEntity> findByCategoryId(String categoryId) {
    return findByCategoryId(categoryId, null);
  }

  @Override
  public Flux<LinkEntity> findByCategoryId(String categoryId, Sort sort) {
    final Query query = Optional.ofNullable(sort)
        .map(s -> Query.query(Criteria.where("categoryIds").all(categoryId)).with(s))
        .orElseGet(() -> Query.query(Criteria.where("categoryIds").all(categoryId)));
    return Optional.ofNullable(categoryId)
        .map(id -> mongoTemplate.find(query, LinkEntity.class))
        .orElseGet(Flux::empty);
  }

  @Override
  public Mono<Void> removeCategoryReferences(String categoryId) {
    return findByCategoryId(categoryId)
        .flatMap(link -> {
          link.getCategoryIds().remove(categoryId);
          if (link.getCategoryIds().isEmpty()) {
            return mongoTemplate.remove(link);
          } else {
            return mongoTemplate.save(link);
          }
        })
        .count()
        .flatMap(size -> Mono.empty());
  }

}
