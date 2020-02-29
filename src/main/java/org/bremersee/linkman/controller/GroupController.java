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
import org.bremersee.linkman.service.GroupService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * The group controller.
 *
 * @author Christian Bremer
 */
@Tag(name = "group-controller", description = "The available groups API.")
@RestController
public class GroupController {

  private GroupService groupService;

  /**
   * Instantiates a new group controller.
   *
   * @param groupService the group service
   */
  public GroupController(GroupService groupService) {
    this.groupService = groupService;
  }

  /**
   * Gets available groups.
   *
   * @return the available groups
   */
  @Operation(
      summary = "Get available groups.",
      operationId = "getAvailableGroups",
      tags = {"group-controller"})
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "The available groups.",
          content = @Content(
              array = @ArraySchema(
                  schema = @Schema(implementation = SelectOption.class))))
  })
  @GetMapping(path = "/api/groups", produces = MediaType.APPLICATION_JSON_VALUE)
  public Flux<SelectOption> getAvailableGroups() {
    return groupService.getAllGroups();
  }

}
