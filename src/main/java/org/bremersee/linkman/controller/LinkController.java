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
import org.bremersee.linkman.model.LinkSpecification;
import org.bremersee.linkman.service.LinkService;
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
 * The link controller.
 *
 * @author Christian Bremer
 */
@RestController
@Validated
public class LinkController {

  private final LinkService linkService;

  /**
   * Instantiates a new link controller.
   *
   * @param linkService the link service
   */
  public LinkController(LinkService linkService) {
    this.linkService = linkService;
  }

  /**
   * Gets links.
   *
   * @return the links
   */
  @ApiOperation(
      value = "Get all links.",
      nickname = "getLinks",
      response = LinkSpecification.class,
      responseContainer = "List",
      tags = {"link-controller"})
  @ApiResponses(value = {
      @ApiResponse(
          code = 200,
          message = "OK",
          response = LinkSpecification.class,
          responseContainer = "List"),
      @ApiResponse(code = 403, message = "Forbidden")
  })
  @GetMapping(path = "/api/admin/links", produces = MediaType.APPLICATION_JSON_VALUE)
  public Flux<LinkSpecification> getLinks() {
    return linkService.getLinks();
  }

  /**
   * Add link.
   *
   * @param link the link
   * @return the added link
   */
  @ApiOperation(
      value = "Add a link.",
      nickname = "addLink",
      response = LinkSpecification.class,
      tags = {"link-controller"})
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "OK", response = LinkSpecification.class),
      @ApiResponse(code = 400, message = "Bad Request",
          response = org.bremersee.exception.model.RestApiException.class),
      @ApiResponse(code = 403, message = "Forbidden")
  })
  @PostMapping(path = "/api/admin/links",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public Mono<LinkSpecification> addLink(
      @ApiParam(value = "The new link.", required = true) @Valid @RequestBody
          LinkSpecification link) {

    return linkService.addLink(link);
  }

  /**
   * Gets link.
   *
   * @param id the id
   * @return the link
   */
  @ApiOperation(
      value = "Get a link.",
      nickname = "getLink",
      response = LinkSpecification.class,
      tags = {"link-controller"})
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "OK", response = LinkSpecification.class),
      @ApiResponse(code = 404, message = "Not Found",
          response = org.bremersee.exception.model.RestApiException.class),
      @ApiResponse(code = 403, message = "Forbidden")
  })
  @GetMapping(path = "/api/admin/links/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<LinkSpecification> getLink(
      @ApiParam(value = "The link ID.", required = true) @PathVariable("id") String id) {
    return linkService.getLink(id);
  }

  /**
   * Update link.
   *
   * @param id the id
   * @param link the link
   * @return the updated link
   */
  @ApiOperation(
      value = "Update a link.",
      nickname = "updateLink",
      response = LinkSpecification.class,
      tags = {"link-controller"})
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "OK", response = LinkSpecification.class),
      @ApiResponse(code = 400, message = "Bad Request",
          response = org.bremersee.exception.model.RestApiException.class),
      @ApiResponse(code = 404, message = "Not Found",
          response = org.bremersee.exception.model.RestApiException.class),
      @ApiResponse(code = 403, message = "Forbidden")
  })
  @PutMapping(path = "/api/admin/links/{id}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public Mono<LinkSpecification> updateLink(
      @ApiParam(value = "The link ID.", required = true) @PathVariable("id") String id,
      @ApiParam(value = "The new link specification.", required = true) @Valid @RequestBody
          LinkSpecification link) {

    return linkService.updateLink(id, link);
  }

  /**
   * Delete link.
   *
   * @param id the id
   * @return void
   */
  @ApiOperation(
      value = "Delete a link.",
      nickname = "deleteLink",
      tags = {"link-controller"})
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "OK", response = LinkSpecification.class),
      @ApiResponse(code = 403, message = "Forbidden")
  })
  @DeleteMapping(path = "/api/admin/links/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Void> deleteLink(
      @ApiParam(value = "The link ID.", required = true) @PathVariable("id") String id) {

    return linkService.deleteLink(id);
  }

}
