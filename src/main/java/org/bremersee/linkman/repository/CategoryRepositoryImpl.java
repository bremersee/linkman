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
import org.bremersee.security.access.Ace;
import org.bremersee.security.access.Acl;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;

/**
 * @author Christian Bremer
 */
public class CategoryRepositoryImpl implements CategoryRepositoryCustom {

  private ReactiveMongoTemplate mongoTemplate;

  public CategoryRepositoryImpl(
      ReactiveMongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public Flux<CategoryEntity> findCategories(final Acl<? extends Ace> linkAcl) {
    return Optional.ofNullable(linkAcl == null ? null : linkAcl.entryMap().get("read"))
        .map(ace -> query(createCriteriaList(ace)))
        .orElseGet(Flux::empty);
  }

  private List<Criteria> createCriteriaList(final Ace ace) {
    final List<Criteria> criteriaList = new ArrayList<>();
    if (ace.isGuest()) {
      criteriaList.add(Criteria.where("matchesGuest").is(true));
    } else {
      if (!ace.getUsers().isEmpty()) {
        criteriaList.add(Criteria
            .where("matchesUsers").elemMatch(new Criteria().in(ace.getUsers())));
      }
      if (!ace.getRoles().isEmpty()) {
        criteriaList.add(Criteria
            .where("matchesRoles").elemMatch(new Criteria().in(ace.getRoles())));
      }
      if (!ace.getGroups().isEmpty()) {
        criteriaList.add(Criteria
            .where("matchesGroups").elemMatch(new Criteria().in(ace.getGroups())));
      }
    }
    return criteriaList;
  }

  private Flux<CategoryEntity> query(final List<Criteria> criteriaList) {
    if (criteriaList.isEmpty()) {
      return Flux.empty();
    } else if (criteriaList.size() == 1) {
      return mongoTemplate.find(Query.query(criteriaList.get(0)), CategoryEntity.class);
    } else {
      final Criteria criteria = new Criteria()
          .orOperator(criteriaList.toArray(new Criteria[0]));
      return mongoTemplate.find(Query.query(criteria), CategoryEntity.class);
    }
  }

}
