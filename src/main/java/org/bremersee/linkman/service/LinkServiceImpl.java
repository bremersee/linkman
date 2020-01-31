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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.bremersee.common.model.Link;
import org.bremersee.common.model.TwoLetterLanguageCode;
import org.bremersee.linkman.model.LinkContainer;
import org.bremersee.linkman.repository.CategoryEntity;
import org.bremersee.linkman.repository.CategoryRepository;
import org.bremersee.linkman.repository.LinkEntity;
import org.bremersee.linkman.repository.LinkRepository;
import org.reactivestreams.Publisher;
import org.springframework.util.LinkedMultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

/**
 * @author Christian Bremer
 */
public class LinkServiceImpl {

  private CategoryRepository categoryRepository;

  private LinkRepository linkRepository;

  public Mono<Void> saveLink() {
    return null;
  }

  public Flux<LinkContainer> getLinks(
      TwoLetterLanguageCode language,
      String userId,
      Set<String> roles,
      Set<String> groups) {


    // Flux<LinkEntity> links =
    linkRepository.findReadableLinks(userId, roles, groups)
        .flatMap(link -> categoryRepository.findCategories(link.getAcl()).collectList().zipWith(Mono.just(link)))
        .collectList().map(list -> {
          Map<CategoryEntity, List<LinkEntity>> map = new LinkedHashMap<>();
          for (Tuple2<List<CategoryEntity>, LinkEntity> tuple : list) {

          }
          return map;
        });

    Mono<Map<CategoryEntity, Collection<LinkEntity>>> mapMono = linkRepository
        .findReadableLinks(userId, roles, groups)
        .flatMap(link -> categoryRepository.findCategories(link.getAcl())
            .map(categoryEntity -> Tuples.of(categoryEntity, link)))
        .collectMultimap(Tuple2::getT1, Tuple2::getT2, LinkedHashMap::new);



    /*
    Mono<Map<String, Collection<LinkEntity>>> map = links.collectMultimap(
        linkEntity -> linkEntity.getId(),
        linkEntity -> linkEntity,
        () -> new LinkedHashMap<>());

    links.collectList().map(list -> {
      Map<String, Collection<LinkEntity>> m = new LinkedHashMap<>();
      for (LinkEntity e : list) {
        categoryRepository.findCategories(e.getAcl()).collectList()
      }
      return m;
    });

     */


    return null;
  }

}
