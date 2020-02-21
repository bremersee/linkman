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

import static org.bremersee.linkman.model.Translation.toTranslations;

import lombok.extern.slf4j.Slf4j;
import org.bremersee.exception.ServiceException;
import org.bremersee.linkman.config.LinkmanProperties;
import org.bremersee.linkman.model.CategorySpecification;
import org.bremersee.linkman.repository.CategoryEntity;
import org.bremersee.linkman.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The category service implementation.
 *
 * @author Christian Bremer
 */
@Component
@Slf4j
public class CategoryServiceImpl implements CategoryService {

  private LinkmanProperties properties;

  private CategoryRepository categoryRepository;

  private ModelMapper modelMapper;

  /**
   * Instantiates a new category service.
   *
   * @param properties the properties
   * @param categoryRepository the category repository
   * @param modelMapper the model mapper
   */
  public CategoryServiceImpl(
      LinkmanProperties properties,
      CategoryRepository categoryRepository,
      ModelMapper modelMapper) {
    this.properties = properties;
    this.categoryRepository = categoryRepository;
    this.modelMapper = modelMapper;
  }

  /**
   * Init.
   */
  @EventListener(ApplicationReadyEvent.class)
  public void init() {
    final CategorySpecification publicCategory = CategorySpecification.builder()
        .matchesGuest(true)
        .order(Integer.MIN_VALUE)
        .name(properties.getPublicCategory().getName())
        .translations(toTranslations(properties.getPublicCategory().getTranslations()))
        .build();
    final CategorySpecification savedPublicCategory = categoryRepository.countPublicCategories()
        .filter(size -> size == 0)
        .flatMap(size -> addCategory(publicCategory))
        .block();
    if (savedPublicCategory != null) {
      log.info("Public category created: {}", savedPublicCategory);
    }
  }

  @Override
  public Flux<CategorySpecification> getCategories() {
    return categoryRepository.findAll(Sort.by(Order.asc("order"), Order.asc("name")))
        .map(entity -> modelMapper.map(entity, CategorySpecification.class));
  }

  @Override
  public Mono<CategorySpecification> addCategory(CategorySpecification category) {
    final CategorySpecification model = category.toBuilder()
        .id(null)
        .build();
    return categoryRepository.countPublicCategories()
        .flatMap(size -> size > 0 && Boolean.TRUE.equals(model.getMatchesGuest())
            ? Mono.error(ServiceException.badRequest(
            "There is already a public category.",
            "ONLY_ONE_PUBLIC_CATEGORY_IS_ALLOWED"))
            : categoryRepository.save(modelMapper.map(model, CategoryEntity.class)))
        .map(entity -> modelMapper.map(entity, CategorySpecification.class));
  }

  @Override
  public Mono<CategorySpecification> getCategory(String id) {
    return categoryRepository.findById(id)
        .switchIfEmpty(Mono.error(() -> ServiceException.notFound("Category", id)))
        .map(entity -> modelMapper.map(entity, CategorySpecification.class));
  }

  @Override
  public Mono<CategorySpecification> updateCategory(String id, CategorySpecification category) {
    final CategorySpecification model = category.toBuilder().id(id).build();
    return categoryRepository.findById(id)
        .switchIfEmpty(Mono.error(() -> ServiceException.notFound("Category", id)))
        .flatMap(entity -> {
          if (Boolean.TRUE.equals(model.getMatchesGuest())
              && !Boolean.TRUE.equals(entity.getMatchesGuest())) {
            return categoryRepository.countPublicCategories()
                .flatMap(size -> size > 0
                    ? Mono.error(ServiceException.badRequest(
                    "There is already a public category.",
                    "ONLY_ONE_PUBLIC_CATEGORY_IS_ALLOWED"))
                    : categoryRepository.save(modelMapper.map(model, CategoryEntity.class)));
          } else {
            return categoryRepository.save(modelMapper.map(model, CategoryEntity.class));
          }
        })
        .map(entity -> modelMapper.map(entity, CategorySpecification.class));
  }

  @Override
  public Mono<Void> deleteCategory(String id) {
    return categoryRepository.deleteById(id);
  }

  @Override
  public Mono<Boolean> publicCategoryExists() {
    return categoryRepository.countPublicCategories()
        .map(size -> size > 0L);
  }

}
