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

import java.io.IOException;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.data.minio.MinioOperations;
import org.bremersee.exception.ServiceException;
import org.bremersee.linkman.config.LinkmanProperties;
import org.bremersee.linkman.model.LinkSpec;
import org.bremersee.linkman.repository.CategoryRepository;
import org.bremersee.linkman.repository.LinkEntity;
import org.bremersee.linkman.repository.LinkRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.codec.multipart.FilePart;
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
                .build()))
        .flatMap(model -> linkRepository
            .save(modelMapper.map(model, LinkEntity.class)))
        .map(entity -> modelMapper.map(entity, LinkSpec.class));
  }

  public Mono<LinkSpec> saveCardImage(String id, FilePart cardImage, FilePart menuImage) {
    // TODO
    log.info("Saving card image {} and menu image {}", cardImage.filename(), menuImage.filename());
    return linkRepository.findById(id)
        .switchIfEmpty(Mono.error(() -> ServiceException.notFound("Link", id)))
        .flatMap(entity -> {
          if (minioOperations == null) {
            return Mono.just(entity);
          }
          return DataBufferUtils.join(cardImage.content())
              .map(dataBuffer -> dataBuffer.asInputStream())
              .map(inputStream -> {
                String name = entity.getId() + "_card_" + cardImage.filename();
                if (StringUtils.hasText(entity.getCardImage())
                    && !name.equals(entity.getCardImage())) {
                  minioOperations.removeObject(properties.getBucketName(), entity.getCardImage());
                }
                minioOperations
                    .putObject(properties.getBucketName(), name, inputStream, null);
                entity.setCardImage(name);
                return entity;
              })
              .then(DataBufferUtils.join(menuImage.content()))
              .map(dataBuffer -> dataBuffer.asInputStream())
              .map(inputStream -> {
                String name = entity.getId() + "_menu_" + menuImage.filename();
                if (StringUtils.hasText(entity.getMenuImage())
                    && !name.equals(entity.getMenuImage())) {
                  minioOperations.removeObject(properties.getBucketName(), entity.getMenuImage());
                }
                minioOperations
                    .putObject(properties.getBucketName(), name, inputStream, null);
                entity.setMenuImage(name);
                return entity;
              });
        })
        .flatMap(entity -> linkRepository.save(entity))
        /*
        .zipWith(DataBufferUtils.join(cardImage.content()))
        .flatMap(tuple -> {
          LinkEntity entity = tuple.getT1();
          try (InputStream in = tuple.getT2().asInputStream()) {
            if (minioOperations != null) {
              String name = entity.getId() + "_card_" + cardImage.filename();
              if (StringUtils.hasText(entity.getCardImage())
                  && !name.equals(entity.getCardImage())) {
                minioOperations.removeObject(properties.getBucketName(), entity.getCardImage());
              }
              minioOperations
                  .putObject(properties.getBucketName(), entity.getCardImage(), in, null);
              entity.setCardImage(name);
            }

          } catch (IOException e) {
            ServiceException se = ServiceException
                .internalServerError("Reading image " + cardImage.filename() + " failed.", e);
            log.error("Saving card image failed.", se);
            return Mono.error(se);
          }
          return linkRepository.save(entity);
        })
        */
        .map(entity -> modelMapper.map(entity, LinkSpec.class));
  }

  @Override
  public Mono<Void> deleteLink(String id) {
    return linkRepository.deleteById(id);
  }

}
