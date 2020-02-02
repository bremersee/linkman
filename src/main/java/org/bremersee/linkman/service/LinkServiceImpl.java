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

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.bremersee.common.model.Link;
import org.bremersee.exception.ServiceException;
import org.bremersee.linkman.config.LinkmanProperties;
import org.bremersee.linkman.model.LinkContainer;
import org.bremersee.linkman.model.LinkSpecification;
import org.bremersee.linkman.repository.CategoryEntity;
import org.bremersee.linkman.repository.CategoryRepository;
import org.bremersee.linkman.repository.LinkEntity;
import org.bremersee.linkman.repository.LinkRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

/**
 * The link service implementation.
 *
 * @author Christian Bremer
 */
@Component
public class LinkServiceImpl implements LinkService {

  private LinkmanProperties properties;

  private CategoryRepository categoryRepository;

  private LinkRepository linkRepository;

  private ModelMapper modelMapper;

  /**
   * Instantiates a new link service.
   *
   * @param properties the properties
   * @param categoryRepository the category repository
   * @param linkRepository the link repository
   * @param modelMapper the model mapper
   */
  public LinkServiceImpl(LinkmanProperties properties,
      CategoryRepository categoryRepository,
      LinkRepository linkRepository, ModelMapper modelMapper) {
    this.properties = properties;
    this.categoryRepository = categoryRepository;
    this.linkRepository = linkRepository;
    this.modelMapper = modelMapper;
  }

  @Override
  public Flux<LinkContainer> getLinks(
      Locale language,
      String userId,
      Set<String> roles,
      Set<String> groups) {

    return linkRepository
        .findReadableLinks(userId, roles, groups)
        .flatMap(link -> categoryRepository.findCategories(link.getAcl())
            .switchIfEmpty(Flux.just(getDefaultCategory()))
            .map(categoryEntity -> Tuples.of(categoryEntity, link)))
        .sort((o1, o2) -> o1.getT1().compareTo(o2.getT1(), language))
        .collectMultimap(
            Tuple2::getT1,
            Tuple2::getT2,
            LinkedHashMap::new)
        .flatMapMany(map -> Flux.fromStream(map.entrySet().stream()
            .map(mapEntry -> LinkContainer.builder()
                .category(mapEntry.getKey().getName(language))
                .pub(Boolean.TRUE.equals(mapEntry.getKey().getMatchesGuest()))
                .links(mapEntry.getValue().stream()
                    .sorted((o1, o2) -> o1.compareTo(o2, language))
                    .map(linkEntity -> Link.builder()
                        .id(linkEntity.getId())
                        .href(linkEntity.getHref())
                        .text(linkEntity.getText(language))
                        .description(linkEntity.getDescription(language))
                        .build())
                    .collect(Collectors.toList()))
                .build())));
  }

  @Override
  public Flux<LinkSpecification> getLinks() {
    return linkRepository.findAll(Sort.by(Order.asc("order"), Order.asc("text")))
        .map(entity -> modelMapper.map(entity, LinkSpecification.class));
  }

  @Override
  public Mono<LinkSpecification> addLink(LinkSpecification link) {
    final LinkSpecification model = link.toBuilder()
        .id(null)
        .build();
    return linkRepository.save(modelMapper.map(model, LinkEntity.class))
        .map(entity -> modelMapper.map(entity, LinkSpecification.class));
  }

  @Override
  public Mono<LinkSpecification> getLink(String id) {
    return linkRepository.findById(id)
        .switchIfEmpty(Mono.error(() -> ServiceException.notFound("Link", id)))
        .map(entity -> modelMapper.map(entity, LinkSpecification.class));
  }

  @Override
  public Mono<LinkSpecification> updateLink(String id, LinkSpecification link) {
    return linkRepository.findById(id)
        .switchIfEmpty(Mono.error(() -> ServiceException.notFound("Link", id)))
        .flatMap(entity -> linkRepository
            .save(modelMapper.map(
                link.toBuilder().id(entity.getId()).build(),
                LinkEntity.class)))
        .map(entity -> modelMapper.map(entity, LinkSpecification.class));
  }

  @Override
  public Mono<Void> deleteLink(String id) {
    return linkRepository.deleteById(id);
  }

  private CategoryEntity getDefaultCategory() {
    CategoryEntity entity = new CategoryEntity();
    entity.setName(properties.getDefaultCategory().getName());
    entity.setTranslations(properties.getDefaultCategory().getTranslations());
    entity.setOrder(Integer.MAX_VALUE);
    return entity;
  }

}
