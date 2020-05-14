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
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.bremersee.linkman.model.SelectOption;
import org.bremersee.linkman.service.RoleService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * The role controller.
 *
 * @author Christian Bremer
 */
@Tag(name = "role-controller", description = "The available roles API.")
@RestController
public class RoleController {

  private final RoleService roleService;

  /**
   * Instantiates a new role controller.
   *
   * @param roleService the role service
   */
  public RoleController(RoleService roleService) {
    this.roleService = roleService;
  }

  /**
   * Gets available roles.
   *
   * @return the available roles
   */
  @Operation(
      summary = "Get available roles.",
      operationId = "getAvailableRoles",
      tags = {"role-controller"})
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "The available roles.",
          content = @Content(
              array = @ArraySchema(
                  schema = @Schema(implementation = SelectOption.class))))
  })
  @GetMapping(path = "/api/roles", produces = MediaType.APPLICATION_JSON_VALUE)
  public Flux<SelectOption> getAvailableRoles() {
    return roleService.getAllRoles();
  }

}
