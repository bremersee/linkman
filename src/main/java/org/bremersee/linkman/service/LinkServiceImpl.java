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

import static org.bremersee.linkman.model.LinkSpec.CARD_IMAGE_NAME;
import static org.bremersee.linkman.model.LinkSpec.MENU_IMAGE_NAME;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.data.minio.DeleteMode;
import org.bremersee.data.minio.MinioObjectId;
import org.bremersee.data.minio.MinioRepository;
import org.bremersee.exception.ServiceException;
import org.bremersee.linkman.model.LinkSpec;
import org.bremersee.linkman.repository.CategoryRepository;
import org.bremersee.linkman.repository.LinkEntity;
import org.bremersee.linkman.repository.LinkRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
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

  private final LinkRepository linkRepository;

  private final CategoryRepository categoryRepository;

  private final ModelMapper modelMapper;

  private final MinioRepository imageRepository;

  /**
   * Instantiates a new link service.
   *
   * @param linkRepository the link repository
   * @param categoryRepository the category repository
   * @param imageRepository the minio image repository
   * @param modelMapper the model mapper
   */
  public LinkServiceImpl(
      LinkRepository linkRepository,
      CategoryRepository categoryRepository,
      MinioRepository imageRepository,
      ModelMapper modelMapper) {
    this.linkRepository = linkRepository;
    this.categoryRepository = categoryRepository;
    this.imageRepository = imageRepository;
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
      MultipartFile cardImage,
      MultipartFile menuImage) {

    System.out.println("images: card = " + cardImage.isEmpty() + ", menu = " + menuImage.isEmpty());
    return linkRepository.findById(id)
        .switchIfEmpty(Mono.error(() -> ServiceException.notFound("Link", id)))
        .flatMap(entity -> {
          String cardImageName = entity.getId() + "_card_image";
          imageRepository
              .save(MinioObjectId.from(cardImageName), cardImage, DeleteMode.ALWAYS)
              .ifPresent(response -> entity.setCardImage(response.object()));
          String menuImageName = entity.getId() + "_menu_image";
          imageRepository
              .save(MinioObjectId.from(menuImageName), menuImage, DeleteMode.ALWAYS)
              .ifPresent(response -> entity.setMenuImage(response.object()));
          return linkRepository.save(entity);
        })
        .map(entity -> modelMapper.map(entity, LinkSpec.class));
  }

  @Override
  public Mono<LinkSpec> deleteLinkImages(String id, List<String> names) {
    return linkRepository.findById(id)
        .switchIfEmpty(Mono.error(() -> ServiceException.notFound("Link", id)))
        .flatMap(entity -> {
          if (StringUtils.hasText(entity.getCardImage()) && names.contains(CARD_IMAGE_NAME)) {
            imageRepository.delete(MinioObjectId.from(entity.getCardImage()));
            entity.setCardImage(null);
          }
          if (StringUtils.hasText(entity.getMenuImage()) && names.contains(MENU_IMAGE_NAME)) {
            imageRepository.delete(MinioObjectId.from(entity.getMenuImage()));
            entity.setMenuImage(null);
          }
          return linkRepository.save(entity);
        })
        .map(linkEntity -> modelMapper.map(linkEntity, LinkSpec.class));
  }

  @Override
  public Mono<Void> deleteLink(String id) {

    return linkRepository.findById(id)
        .flatMap(entity -> {
          List<MinioObjectId> imageIds = new ArrayList<>(2);
          if (StringUtils.hasText(entity.getCardImage())) {
            imageIds.add(MinioObjectId.from(entity.getCardImage()));
          }
          if (StringUtils.hasText(entity.getMenuImage())) {
            imageIds.add(MinioObjectId.from(entity.getMenuImage()));
          }
          if (!imageIds.isEmpty()) {
            imageRepository.deleteAll(imageIds);
          }
          return linkRepository.delete(entity);
        });
  }

}
