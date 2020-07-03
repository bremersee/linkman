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
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.linkman.model.LinkSpec;
import org.bremersee.linkman.service.LinkService;
import org.bremersee.web.UploadedItem;
import org.bremersee.web.reactive.UploadedItemBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.Part;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The link controller.
 *
 * @author Christian Bremer
 */
@Tag(name = "link-controller", description = "The link API.")
@RestController
@Validated
@Slf4j
public class LinkController {

  private final LinkService linkService;

  private final UploadedItemBuilder uploadedItemBuilder;

  /**
   * Instantiates a new link controller.
   *
   * @param linkService the link service
   * @param uploadedItemBuilder the uploaded item builder
   */
  public LinkController(
      LinkService linkService,
      UploadedItemBuilder uploadedItemBuilder) {
    this.linkService = linkService;
    this.uploadedItemBuilder = uploadedItemBuilder;
  }

  /**
   * Gets links.
   *
   * @param categoryId the category id
   * @return the links
   */
  @Operation(
      summary = "Get all links.",
      operationId = "getLinks",
      tags = {"link-controller"})
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "The links.",
          content = @Content(
              array = @ArraySchema(
                  schema = @Schema(implementation = LinkSpec.class)))),
      @ApiResponse(
          responseCode = "403",
          description = "Forbidden")
  })
  @GetMapping(path = "/api/links", produces = MediaType.APPLICATION_JSON_VALUE)
  public Flux<LinkSpec> getLinks(
      @Parameter(name = "categoryId", description = "The category ID.")
      @RequestParam(name = "categoryId", required = false) String categoryId) {
    return linkService.getLinks(categoryId);
  }

  /**
   * Add link.
   *
   * @param link the link
   * @return the added link
   */
  @Operation(
      summary = "Add a link.",
      operationId = "addLink",
      tags = {"link-controller"})
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "The added link.",
          content = @Content(
              schema = @Schema(
                  implementation = LinkSpec.class))),
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
  @PostMapping(path = "/api/links",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public Mono<LinkSpec> addLink(
      @Parameter(description = "The new link.", required = true) @Valid @RequestBody
          LinkSpec link) {

    return linkService.addLink(link);
  }

  /**
   * Gets link.
   *
   * @param id the link id
   * @return the link
   */
  @Operation(
      summary = "Get a link.",
      operationId = "getLink",
      tags = {"link-controller"})
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "The link.",
          content = @Content(
              schema = @Schema(
                  implementation = LinkSpec.class))),
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
  @GetMapping(path = "/api/links/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<LinkSpec> getLink(
      @Parameter(description = "The link ID.", required = true) @PathVariable("id") String id) {
    return linkService.getLink(id);
  }

  /**
   * Update link.
   *
   * @param id the link id
   * @param link the link
   * @return the updated link
   */
  @Operation(
      summary = "Update a link.",
      operationId = "updateLink",
      tags = {"link-controller"})
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "The updated link.",
          content = @Content(
              schema = @Schema(
                  implementation = LinkSpec.class))),
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
  @PutMapping(path = "/api/links/{id}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public Mono<LinkSpec> updateLink(
      @Parameter(description = "The link ID.", required = true) @PathVariable("id") String id,
      @Parameter(description = "The new link specification.", required = true) @Valid @RequestBody
          LinkSpec link) {

    return linkService.updateLink(id, link);
  }

  /**
   * Update link images.
   *
   * @param id the link id
   * @param cardImage the card image
   * @param menuImage the menu image
   * @return the link
   */
  @Operation(
      summary = "Update link images. The multipart may contain a part with name 'cardImage' "
          + "or/and a part with name 'menuImage'. The part can be a file or a value as data uri.",
      operationId = "updateLinkImages",
      tags = {"link-controller"},
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          required = true,
          content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
      ))
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "The updated link with the image URLs.",
          content = @Content(
              schema = @Schema(
                  implementation = LinkSpec.class))),
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
  @PostMapping(
      path = "/api/links/{id}/images",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<LinkSpec> updateLinkImages(
      @Parameter(description = "The link ID.", required = true) @PathVariable("id") String id,
      @RequestPart(name = "cardImage", required = false) Flux<Part> cardImage,
      @RequestPart(name = "menuImage", required = false)  Flux<Part> menuImage) {

    log.info("Updating link images (link id = {}", id);
    return lastPart(cardImage)
        .flatMap(uploadedItemBuilder::build)
        .flatMap(item -> linkService.updateLinkImages(id, item, UploadedItem.EMPTY))
        .then(lastPart(menuImage))
        .flatMap(uploadedItemBuilder::build)
        .flatMap(item -> linkService.updateLinkImages(id, UploadedItem.EMPTY, item))
        .switchIfEmpty(linkService.getLink(id));

    /*
    return parts
        .flatMap(multiPartData -> uploadedItemBuilder.buildFromFirstParameterValue(
            multiPartData,
            new ReqParam(LinkSpec.CARD_IMAGE_NAME, false),
            new ReqParam(LinkSpec.MENU_IMAGE_NAME, false)))
        .flatMap(putObjects -> linkService.updateLinkImages(
            id,
            getUploadedItem(putObjects, 0),
            getUploadedItem(putObjects, 1)));
    */
  }

  private Mono<Part> lastPart(Flux<Part> parts) {
    return parts == null ? Mono.empty() : parts.last();
  }

  /*
  public Mono<LinkSpec> updateLinkImages(
      @Parameter(description = "The link ID.", required = true) @PathVariable("id") String id,
      ServerWebExchange webExchange) {

    log.info("Updating link images (link id = {}", id);
    return webExchange.getMultipartData()
        .flatMap(multiPartData -> uploadedItemBuilder.buildFromFirstParameterValue(
            multiPartData,
            new ReqParam(LinkSpec.CARD_IMAGE_NAME, false),
            new ReqParam(LinkSpec.MENU_IMAGE_NAME, false)))
        .flatMap(putObjects -> linkService.updateLinkImages(
            id,
            getUploadedItem(putObjects, 0),
            getUploadedItem(putObjects, 1)));
  }
  */

  /**
   * Delete link images.
   *
   * @param id the link id
   * @param names the image names ({@code cardImage} and/or {@code menuImage})
   * @return the link
   */
  @Operation(
      summary = "Delete link images.",
      operationId = "deleteLinkImages",
      tags = {"link-controller"})
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "The link.",
          content = @Content(
              schema = @Schema(
                  implementation = LinkSpec.class))),
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
  @DeleteMapping(
      path = "/api/links/{id}/images",
      consumes = MediaType.ALL_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<LinkSpec> deleteLinkImages(
      @Parameter(description = "The link ID.", required = true) @PathVariable("id") String id,
      @Parameter(
          description = "The names of the images ('cardImage' and/or 'menuImage').",
          required = true)
      @RequestParam(name = "name") List<String> names) {

    return linkService.deleteLinkImages(id, names);
  }

  /**
   * Delete link.
   *
   * @param id the link id
   * @return void mono
   */
  @Operation(
      summary = "Delete a link.",
      operationId = "deleteLink",
      tags = {"link-controller"})
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "OK"),
      @ApiResponse(
          responseCode = "403",
          description = "Forbidden")
  })
  @DeleteMapping(path = "/api/links/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Void> deleteLink(
      @Parameter(description = "The link ID.", required = true) @PathVariable("id") String id) {

    return linkService.deleteLink(id);
  }

}
