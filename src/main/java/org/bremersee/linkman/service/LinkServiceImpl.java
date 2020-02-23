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

import org.bremersee.exception.ServiceException;
import org.bremersee.linkman.model.LinkSpec;
import org.bremersee.linkman.repository.LinkEntity;
import org.bremersee.linkman.repository.LinkRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The link service implementation.
 *
 * @author Christian Bremer
 */
@Component
public class LinkServiceImpl implements LinkService {

  private LinkRepository linkRepository;

  private ModelMapper modelMapper;

  /**
   * Instantiates a new link service.
   *
   * @param linkRepository the link repository
   * @param modelMapper the model mapper
   */
  public LinkServiceImpl(
      LinkRepository linkRepository,
      ModelMapper modelMapper) {
    this.linkRepository = linkRepository;
    this.modelMapper = modelMapper;
  }

  @Override
  public Flux<LinkSpec> getLinks(String categoryId) {
    final Sort sort = Sort.by(Order.asc("order"), Order.asc("text"));
    return (StringUtils.hasText(categoryId)
        ? linkRepository.findByCategoryId(categoryId, sort)
        : linkRepository.findAll(sort))
        .map(entity -> modelMapper.map(entity, LinkSpec.class));
  }

  @Override
  public Mono<LinkSpec> addLink(LinkSpec link) {
    final LinkSpec model = link.toBuilder().id(null).build();
    return linkRepository.save(modelMapper.map(model, LinkEntity.class))
        .map(entity -> modelMapper.map(entity, LinkSpec.class));
  }

  @Override
  public Mono<LinkSpec> getLink(String id) {
    return linkRepository.findById(id)
        .switchIfEmpty(Mono.error(() -> ServiceException.notFound("Link", id)))
        .map(entity -> modelMapper.map(entity, LinkSpec.class));
  }

  @Override
  public Mono<LinkSpec> updateLink(String id, LinkSpec link) {
    return linkRepository.findById(id)
        .switchIfEmpty(Mono.error(() -> ServiceException.notFound("Link", id)))
        .flatMap(entity -> linkRepository
            .save(modelMapper.map(
                link.toBuilder().id(entity.getId()).build(),
                LinkEntity.class)))
        .map(entity -> modelMapper.map(entity, LinkSpec.class));
  }

  @Override
  public Mono<Void> deleteLink(String id) {
    return linkRepository.deleteById(id);
  }

}
