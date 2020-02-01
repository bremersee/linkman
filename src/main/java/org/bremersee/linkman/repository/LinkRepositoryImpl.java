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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;

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
  public Flux<LinkEntity> findReadableLinks(
      final String userId,
      final Set<String> roles,
      final Set<String> groups) {

    final List<Criteria> criteriaList = new ArrayList<>();
    criteriaList.add(Criteria.where("acl.read.guest").is(true));
    Optional.ofNullable(userId)
        .ifPresent(user -> {
          criteriaList.add(Criteria.where("acl.owner").is(user));
          criteriaList.add(Criteria.where("acl.read.users").all(user));
        });
    Optional.ofNullable(groups)
        .ifPresent(roleSet -> criteriaList
            .add(Criteria.where("acl.read.roles").elemMatch(new Criteria().in(roleSet))));
    Optional.ofNullable(groups)
        .ifPresent(groupSet -> criteriaList
            .add(Criteria.where("acl.read.groups").elemMatch(new Criteria().in(groupSet))));

    /* this works:
    Optional.ofNullable(roles).ifPresent(roleSet -> criteriaList.addAll(roleSet
        .stream()
        .filter(StringUtils::hasText)
        .map(role -> Criteria.where("acl.read.roles").all(role))
        .collect(Collectors.toList())));
    Optional.ofNullable(groups).ifPresent(groupSet -> criteriaList.addAll(groupSet
        .stream()
        .filter(StringUtils::hasText)
        .map(group -> Criteria.where("acl.read.groups").all(group))
        .collect(Collectors.toList())));
    */
    final Criteria criteria = new Criteria()
        .orOperator(criteriaList.toArray(new Criteria[0]));
    return mongoTemplate.find(Query.query(criteria), LinkEntity.class);
  }

}
