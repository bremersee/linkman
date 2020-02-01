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
import org.bremersee.linkman.model.CategorySpecification;
import org.bremersee.linkman.repository.CategoryEntity;
import org.bremersee.linkman.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
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
public class CategoryServiceImpl implements CategoryService {

  private CategoryRepository categoryRepository;

  private ModelMapper modelMapper;

  /**
   * Instantiates a new category service.
   *
   * @param categoryRepository the category repository
   * @param modelMapper the model mapper
   */
  public CategoryServiceImpl(
      CategoryRepository categoryRepository,
      ModelMapper modelMapper) {
    this.categoryRepository = categoryRepository;
    this.modelMapper = modelMapper;
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
    return categoryRepository.save(modelMapper.map(model, CategoryEntity.class))
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
    return categoryRepository.findById(id)
        .switchIfEmpty(Mono.error(() -> ServiceException.notFound("Category", id)))
        .flatMap(entity -> categoryRepository
            .save(modelMapper.map(
                category.toBuilder().id(entity.getId()).build(),
                CategoryEntity.class)))
        .map(entity -> modelMapper.map(entity, CategorySpecification.class));
  }

  @Override
  public Mono<Void> deleteCategory(String id) {
    return categoryRepository.deleteById(id);
  }

}
