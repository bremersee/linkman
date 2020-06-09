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

import io.minio.http.Method;
import java.time.Duration;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.bremersee.data.minio.MinioOperations;
import org.bremersee.data.minio.PresignedUrlProvider;
import org.bremersee.linkman.config.LinkmanProperties;
import org.bremersee.linkman.model.Link;
import org.bremersee.linkman.model.MenuEntry;
import org.bremersee.linkman.repository.CategoryRepository;
import org.bremersee.linkman.repository.LinkRepository;
import org.bremersee.security.core.UserContext;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

/**
 * The menu service implementation.
 *
 * @author Christian Bremer
 */
@Component
public class MenuServiceImpl implements MenuService {

  private final CategoryRepository categoryRepository;

  private final LinkRepository linkRepository;

  private final Function<String, String> presignedUrlProvider;

  /**
   * Instantiates a new menu service.
   *
   * @param properties the properties
   * @param categoryRepository the category repository
   * @param linkRepository the link repository
   * @param minioOperationsProvider the minio operations provider
   */
  public MenuServiceImpl(
      LinkmanProperties properties,
      CategoryRepository categoryRepository,
      LinkRepository linkRepository,
      ObjectProvider<MinioOperations> minioOperationsProvider) {

    this.categoryRepository = categoryRepository;
    this.linkRepository = linkRepository;
    this.presignedUrlProvider = PresignedUrlProvider.newInstance(
        minioOperationsProvider.getIfAvailable(),
        Method.GET,
        properties.getBucketName(),
        Duration.ofDays(1L),
        null);
  }

  @Override
  public Flux<MenuEntry> getMenuEntries(
      UserContext userContext,
      Locale language) {

    return categoryRepository
        .findReadableCategories(
            userContext.getUserId(),
            userContext.getRoles(),
            userContext.getGroups())
        .flatMap(category -> linkRepository.findByCategoryId(category.getId())
            .collectSortedList((o1, o2) -> o1.compareTo(o2, language))
            .map(links -> Tuples.of(category, links)))
        .sort((o1, o2) -> o1.getT1().compareTo(o2.getT1(), language))
        .map(tuple -> MenuEntry.builder()
            .category(tuple.getT1().getName(language))
            .pub(tuple.getT1().isPublic())
            .links(tuple.getT2().stream()
                .map(linkEntity -> Link.builder()
                    .id(linkEntity.getId())
                    .href(linkEntity.getHref())
                    .blank(linkEntity.getBlank())
                    .text(linkEntity.getText(language))
                    .description(linkEntity.getDescription(language))
                    .cardImageUrl(presignedUrlProvider.apply(linkEntity.getCardImage()))
                    .menuImageUrl(presignedUrlProvider.apply(linkEntity.getMenuImage()))
                    .build())
                .collect(Collectors.toList()))
            .build())
        .filter(menuEntry -> menuEntry.getLinks() != null && !menuEntry.getLinks().isEmpty());
  }


}
