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

import org.bremersee.linkman.model.CategorySpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The category service.
 *
 * @author Christian Bremer
 */
public interface CategoryService {

  /**
   * Gets categories.
   *
   * @return the categories
   */
  Flux<CategorySpec> getCategories();

  /**
   * Add category.
   *
   * @param category the category
   * @return the added category
   */
  Mono<CategorySpec> addCategory(CategorySpec category);

  /**
   * Gets category.
   *
   * @param id the id
   * @return the category
   */
  Mono<CategorySpec> getCategory(String id);

  /**
   * Update category.
   *
   * @param id the id
   * @param category the category
   * @return the updated category
   */
  Mono<CategorySpec> updateCategory(String id, CategorySpec category);

  /**
   * Delete category.
   *
   * @param id the id
   * @return void
   */
  Mono<Void> deleteCategory(String id);

  /**
   * Checks whether a public category exists.
   *
   * @return {@code true} if a public category exists, otherwise {@code false}
   */
  Mono<Boolean> publicCategoryExists();

}
