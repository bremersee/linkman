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
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.bremersee.groupman.api.GroupWebfluxControllerApi;
import org.bremersee.linkman.model.LinkContainer;
import org.bremersee.linkman.service.LinkService;
import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The link container controller.
 *
 * @author Christian Bremer
 */
@Tag(name = "link-container-controller", description = "The categorized links API.")
@RestController
@Validated
public class LinkContainerController {

  private final LinkService linkService;

  private final GroupWebfluxControllerApi groupService;

  /**
   * Instantiates a new link container controller.
   *
   * @param linkService the link service
   * @param groupService the group service
   */
  public LinkContainerController(
      LinkService linkService,
      GroupWebfluxControllerApi groupService) {
    this.linkService = linkService;
    this.groupService = groupService;
  }

  private Set<String> toRoles(Authentication authentication) {
    return authentication.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toSet());
  }

  private <R> Flux<R> manyWithUserContext(Function<UserContext, ? extends Publisher<R>> function) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .filter(Authentication::isAuthenticated)
        .zipWith(groupService.getMembershipIds())
        .map(tuple -> new UserContext(
            tuple.getT1().getName(),
            toRoles(tuple.getT1()),
            tuple.getT2()))
        .switchIfEmpty(Mono.just(new UserContext()))
        .flatMapMany(function);
  }

  /**
   * Get link containers.
   *
   * @param language the language
   * @return the link containers
   */
  @Operation(
      summary = "Get categorized links.",
      description = "Get categorized links for displaying in a menu.",
      tags = {"link-container-controller"})
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "The categorized links.",
          content = @Content(
              array = @ArraySchema(schema = @Schema(implementation = LinkContainer.class))))
  })
  @GetMapping(path = "/api/public/links", produces = MediaType.APPLICATION_JSON_VALUE)
  public Flux<LinkContainer> getLinkContainers(
      @Parameter(hidden = true) final Locale language) {

    return manyWithUserContext(userContext -> linkService.getLinks(
        language,
        userContext.getUserId(),
        userContext.getRoles(),
        userContext.getGroups()));
  }

}
