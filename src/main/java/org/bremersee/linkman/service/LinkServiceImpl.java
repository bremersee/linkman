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

import lombok.extern.slf4j.Slf4j;
import org.bremersee.data.minio.MinioOperations;
import org.bremersee.data.minio.PutObject;
import org.bremersee.exception.ServiceException;
import org.bremersee.linkman.config.LinkmanProperties;
import org.bremersee.linkman.model.LinkSpec;
import org.bremersee.linkman.repository.CategoryRepository;
import org.bremersee.linkman.repository.LinkEntity;
import org.bremersee.linkman.repository.LinkRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.ObjectProvider;
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
@Slf4j
public class LinkServiceImpl implements LinkService {

  private final LinkmanProperties properties;

  private final LinkRepository linkRepository;

  private final CategoryRepository categoryRepository;

  private final ModelMapper modelMapper;

  private final MinioOperations minioOperations;

  /**
   * Instantiates a new link service.
   *
   * @param linkRepository the link repository
   * @param categoryRepository the category repository
   * @param modelMapper the model mapper
   */
  public LinkServiceImpl(
      LinkmanProperties properties,
      LinkRepository linkRepository,
      CategoryRepository categoryRepository,
      ObjectProvider<MinioOperations> minioOperationsProvider,
      ModelMapper modelMapper) {
    this.properties = properties;
    this.linkRepository = linkRepository;
    this.categoryRepository = categoryRepository;
    this.minioOperations = minioOperationsProvider.getIfAvailable();
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
    return categoryRepository.validateCategoryIds(link.getCategoryIds())
        .map(categoryIds -> link.toBuilder()
            .id(null)
            .categoryIds(categoryIds)
            .build())
        .flatMap(model -> linkRepository
            .save(modelMapper.map(model, LinkEntity.class))
            .map(entity -> modelMapper.map(entity, LinkSpec.class)));
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
        .flatMap(entity -> categoryRepository
            .validateCategoryIds(link.getCategoryIds())
            .map(categoryIds -> link.toBuilder()
                .id(entity.getId())
                .categoryIds(categoryIds)
                .build())
            .map(model -> {
              modelMapper.map(model, entity);
              return entity;
            })
        )
        .flatMap(linkRepository::save)
        .map(entity -> modelMapper.map(entity, LinkSpec.class));
  }

  @Override
  public Mono<LinkSpec> updateLinkImages(
      String id,
      PutObject<?> cardImage,
      PutObject<?> menuImage) {

    return linkRepository.findById(id)
        .switchIfEmpty(Mono.error(() -> ServiceException.notFound("Link", id)))
        .flatMap(entity -> {
          String cardImageName = entity.getId() + "_card_image";
          if (cardImage.upload(minioOperations, properties.getBucketName(), cardImageName, true)) {
            entity.setCardImage(cardImageName);
          }
          String menuImageName = entity.getId() + "_menu_image";
          if (menuImage.upload(minioOperations, properties.getBucketName(), menuImageName, true)) {
            entity.setMenuImage(menuImageName);
          }
          return linkRepository.save(entity);
        })
        .map(entity -> modelMapper.map(entity, LinkSpec.class));
  }

  @Override
  public Mono<Void> deleteLink(String id) {

    return linkRepository.findById(id)
        .flatMap(entity -> {
          if (minioOperations != null) {
            if (StringUtils.hasText(entity.getCardImage())) {
              try {
                minioOperations.removeObject(properties.getBucketName(), entity.getCardImage());
              } catch (RuntimeException e) {
                log.error("Removing card image " + entity.getCardImage() + " failed.");
              }
            }
            if (StringUtils.hasText(entity.getMenuImage())) {
              try {
                minioOperations.removeObject(properties.getBucketName(), entity.getMenuImage());
              } catch (RuntimeException e) {
                log.error("Removing menu image " + entity.getMenuImage() + " failed.");
              }
            }
          }
          return linkRepository.delete(entity);
        });
  }

}
