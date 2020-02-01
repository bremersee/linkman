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

import org.bremersee.linkman.model.LinkSpecification;
import org.bremersee.linkman.service.LinkService;
import org.springframework.http.MediaType;
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
  @PostMapping(path = "/api/admin/links",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public Mono<LinkSpecification> addLink(@RequestBody LinkSpecification link) {
    return linkService.addLink(link);
  }

  /**
   * Gets link.
   *
   * @param id the id
   * @return the link
   */
  @GetMapping(path = "/api/admin/links/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<LinkSpecification> getLink(@PathVariable("id") String id) {
    return linkService.getLink(id);
  }

  /**
   * Update link.
   *
   * @param id the id
   * @param link the link
   * @return the updated link
   */
  @PutMapping(path = "/api/admin/links/{id}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public Mono<LinkSpecification> updateLink(
      @PathVariable("id") String id,
      @RequestBody LinkSpecification link) {
    return linkService.updateLink(id, link);
  }

  /**
   * Delete link.
   *
   * @param id the id
   * @return void
   */
  @DeleteMapping(path = "/api/admin/links/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Void> deleteLink(@PathVariable("id") String id) {
    return linkService.deleteLink(id);
  }

}
