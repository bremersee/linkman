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

package org.bremersee.linkman.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.Valid;
import org.bremersee.linkman.model.CategorySpecification;
import org.bremersee.linkman.service.CategoryService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The category controller.
 *
 * @author Christian Bremer
 */
@RestController
@Validated
public class CategoryController {

  private final CategoryService categoryService;

  /**
   * Instantiates a new category controller.
   *
   * @param categoryService the category service
   */
  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  /**
   * Gets categories.
   *
   * @return the categories
   */
  @ApiOperation(
      value = "Get all categories.",
      nickname = "getCategories",
      response = CategorySpecification.class,
      responseContainer = "List",
      tags = {"category-controller"})
  @ApiResponses(value = {
      @ApiResponse(
          code = 200,
          message = "OK",
          response = CategorySpecification.class,
          responseContainer = "List"),
      @ApiResponse(code = 403, message = "Forbidden")
  })
  @GetMapping(path = "/api/admin/categories", produces = MediaType.APPLICATION_JSON_VALUE)
  public Flux<CategorySpecification> getCategories() {
    return categoryService.getCategories();
  }

  /**
   * Add category.
   *
   * @param category the category
   * @return the added category
   */
  @ApiOperation(
      value = "Add a category.",
      nickname = "addCategory",
      response = CategorySpecification.class,
      tags = {"category-controller"})
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "OK", response = CategorySpecification.class),
      @ApiResponse(code = 400, message = "Bad Request",
          response = org.bremersee.exception.model.RestApiException.class),
      @ApiResponse(code = 403, message = "Forbidden")
  })
  @PostMapping(path = "/api/admin/categories",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public Mono<CategorySpecification> addCategory(
      @ApiParam(value = "The new category.", required = true) @Valid @RequestBody
          CategorySpecification category) {

    return categoryService.addCategory(category);
  }

  /**
   * Gets category.
   *
   * @param id the id
   * @return the category
   */
  @ApiOperation(
      value = "Get a category.",
      nickname = "getCategory",
      response = CategorySpecification.class,
      tags = {"category-controller"})
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "OK", response = CategorySpecification.class),
      @ApiResponse(code = 404, message = "Not Found",
          response = org.bremersee.exception.model.RestApiException.class),
      @ApiResponse(code = 403, message = "Forbidden")
  })
  @GetMapping(path = "/api/admin/categories/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<CategorySpecification> getCategory(
      @ApiParam(value = "The category ID.", required = true) @PathVariable("id") String id) {
    return categoryService.getCategory(id);
  }

  /**
   * Update category.
   *
   * @param id the id
   * @param category the category
   * @return the the updated category
   */
  @ApiOperation(
      value = "Update a category.",
      nickname = "updateCategory",
      response = CategorySpecification.class,
      tags = {"category-controller"})
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "OK", response = CategorySpecification.class),
      @ApiResponse(code = 400, message = "Bad Request",
          response = org.bremersee.exception.model.RestApiException.class),
      @ApiResponse(code = 404, message = "Not Found",
          response = org.bremersee.exception.model.RestApiException.class),
      @ApiResponse(code = 403, message = "Forbidden")
  })
  @PutMapping(path = "/api/admin/categories/{id}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public Mono<CategorySpecification> updateCategory(
      @ApiParam(value = "The category ID.", required = true) @PathVariable("id") String id,
      @ApiParam(value = "The new category specification.", required = true) @Valid @RequestBody
          CategorySpecification category) {

    return categoryService.updateCategory(id, category);
  }

  /**
   * Delete category.
   *
   * @param id the id
   * @return void
   */
  @ApiOperation(
      value = "Delete a category.",
      nickname = "deleteCategory",
      tags = {"category-controller"})
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "OK", response = CategorySpecification.class),
      @ApiResponse(code = 403, message = "Forbidden")
  })
  @DeleteMapping(path = "/api/admin/categories/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Void> deleteCategory(
      @ApiParam(value = "The category ID.", required = true) @PathVariable("id") String id) {

    return categoryService.deleteCategory(id);
  }

}
