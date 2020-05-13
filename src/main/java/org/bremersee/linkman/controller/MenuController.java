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

import static org.bremersee.security.core.ReactiveUserContextCaller.EMPTY_USER_CONTEXT_SUPPLIER;
import static org.bremersee.security.core.ReactiveUserContextCaller.manyWithUserContext;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Locale;
import org.bremersee.groupman.api.GroupWebfluxControllerApi;
import org.bremersee.linkman.model.MenuEntry;
import org.bremersee.linkman.service.MenuService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * The menu controller.
 *
 * @author Christian Bremer
 */
@Tag(name = "menu-controller", description = "The menu API.")
@RestController
@Validated
public class MenuController {

  private final MenuService menuService;

  private final GroupWebfluxControllerApi groupService;

  /**
   * Instantiates a new menu controller.
   *
   * @param menuService the menu service
   * @param groupServiceProvider the group service
   */
  public MenuController(
      MenuService menuService,
      ObjectProvider<GroupWebfluxControllerApi> groupServiceProvider) {
    this.menuService = menuService;
    this.groupService = groupServiceProvider.getIfAvailable();
    Assert.notNull(this.groupService, "Group service must be present.");
  }

  /**
   * Get menu entries.
   *
   * @param language the language
   * @return the menu entries
   */
  @Operation(
      summary = "Get menu entries.",
      operationId = "getMenuEntries",
      tags = {"menu-controller"})
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "The menu entries.",
          content = @Content(
              array = @ArraySchema(schema = @Schema(implementation = MenuEntry.class))))
  })
  @GetMapping(path = "/api/menu", produces = MediaType.APPLICATION_JSON_VALUE)
  public Flux<MenuEntry> getMenuEntries(
      @Parameter(hidden = true) final Locale language) {

    return manyWithUserContext(
        userContext -> menuService.getMenuEntries(userContext, language),
        groupService::getMembershipIds,
        EMPTY_USER_CONTEXT_SUPPLIER);
  }

}
