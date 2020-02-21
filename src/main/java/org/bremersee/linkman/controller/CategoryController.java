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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "category-controller", description = "The category API.")
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
  @Operation(
      summary = "Get all categories.",
      operationId = "getCategories",
      tags = {"category-controller"})
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "The categories.",
          content = @Content(
              array = @ArraySchema(
                  schema = @Schema(implementation = CategorySpecification.class)))),
      @ApiResponse(
          responseCode = "403",
          description = "Forbidden")
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
  @Operation(
      summary = "Add a category.",
      operationId = "addCategory",
      tags = {"category-controller"})
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "The added category.",
          content = @Content(
              schema = @Schema(
                  implementation = CategorySpecification.class))),
      @ApiResponse(
          responseCode = "400",
          description = "Bad Request",
          content = @Content(
              schema = @Schema(
                  implementation = org.bremersee.exception.model.RestApiException.class))),
      @ApiResponse(
          responseCode = "403",
          description = "Forbidden")
  })
  @PostMapping(path = "/api/admin/categories",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public Mono<CategorySpecification> addCategory(
      @Parameter(description = "The new category.", required = true) @Valid @RequestBody
          CategorySpecification category) {

    return categoryService.addCategory(category);
  }

  /**
   * Gets category.
   *
   * @param id the id
   * @return the category
   */
  @Operation(
      summary = "Get a category.",
      operationId = "getCategory",
      tags = {"category-controller"})
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "The category.",
          content = @Content(
              schema = @Schema(
                  implementation = CategorySpecification.class))),
      @ApiResponse(
          responseCode = "404",
          description = "Not Found",
          content = @Content(
              schema = @Schema(
                  implementation = org.bremersee.exception.model.RestApiException.class))),
      @ApiResponse(
          responseCode = "403",
          description = "Forbidden")
  })
  @GetMapping(path = "/api/admin/categories/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<CategorySpecification> getCategory(
      @Parameter(description = "The category ID.", required = true) @PathVariable("id") String id) {
    return categoryService.getCategory(id);
  }

  /**
   * Update category.
   *
   * @param id the id
   * @param category the category
   * @return the the updated category
   */
  @Operation(
      summary = "Update a category.",
      operationId = "updateCategory",
      tags = {"category-controller"})
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "The updated category.",
          content = @Content(
              schema = @Schema(
                  implementation = CategorySpecification.class))),
      @ApiResponse(
          responseCode = "400",
          description = "Bad Request",
          content = @Content(
              schema = @Schema(
                  implementation = org.bremersee.exception.model.RestApiException.class))),
      @ApiResponse(
          responseCode = "404",
          description = "Not Found",
          content = @Content(
              schema = @Schema(
                  implementation = org.bremersee.exception.model.RestApiException.class))),
      @ApiResponse(
          responseCode = "403",
          description = "Forbidden")
  })
  @PutMapping(path = "/api/admin/categories/{id}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public Mono<CategorySpecification> updateCategory(
      @Parameter(description = "The category ID.", required = true) @PathVariable("id") String id,
      @Parameter(description = "The new category specification.", required = true)
      @Valid @RequestBody CategorySpecification category) {

    return categoryService.updateCategory(id, category);
  }

  /**
   * Delete category.
   *
   * @param id the id
   * @return void
   */
  @Operation(
      summary = "Delete a category.",
      operationId = "deleteCategory",
      tags = {"category-controller"})
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "OK"),
      @ApiResponse(
          responseCode = "403",
          description = "Forbidden")
  })
  @DeleteMapping(path = "/api/admin/categories/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Void> deleteCategory(
      @Parameter(description = "The category ID.", required = true) @PathVariable("id") String id) {

    return categoryService.deleteCategory(id);
  }

  @Operation(
      summary = "Checks whether a public category exists.",
      operationId = "publicCategoryExists",
      tags = {"category-controller"})
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "'true' if a public category exists, otherwise 'false'."),
      @ApiResponse(
          responseCode = "403",
          description = "Forbidden")
  })
  @GetMapping(
      path = "/api/admin/categories/f/public-exists",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Boolean> publicCategoryExists() {
    return categoryService.publicCategoryExists();
  }

}
