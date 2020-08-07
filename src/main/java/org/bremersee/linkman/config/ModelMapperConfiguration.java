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

package org.bremersee.linkman.config;

import io.minio.http.Method;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import org.bremersee.converter.ModelMapperConfigurerAdapter;
import org.bremersee.data.minio.MinioObjectId;
import org.bremersee.data.minio.MinioRepository;
import org.bremersee.linkman.model.LinkSpec;
import org.bremersee.linkman.repository.LinkEntity;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.Provider;
import org.springframework.context.annotation.Configuration;

/**
 * The model mapper configuration.
 *
 * @author Christian Bremer
 */
@Configuration
public class ModelMapperConfiguration {

  /**
   * The link spec mapper configuration.
   */
  @Configuration
  static class LinkSpecMapperConfiguration implements ModelMapperConfigurerAdapter {

    private final Converter<String, String> urlSignConverter;

    public LinkSpecMapperConfiguration(MinioRepository minioRepository) {
      this.urlSignConverter = mappingContext -> Optional
          .ofNullable(mappingContext.getSource())
          .map(MinioObjectId::from)
          .map(id -> minioRepository.getPresignedObjectUrl(id, Method.GET))
          .orElse(null);
    }

    @Override
    public void configure(ModelMapper modelMapper) {

      final Provider<Set<?>> linkedHashSetProvider = provisionRequest -> new LinkedHashSet<>();

      modelMapper.
          createTypeMap(LinkEntity.class, LinkSpec.class)
          .addMappings(mapper -> mapper.with(linkedHashSetProvider)
              .map(LinkEntity::getCategoryIds, LinkSpec::setCategoryIds))
          .addMappings(mapper -> mapper.with(linkedHashSetProvider)
              .map(LinkEntity::getDescriptionTranslations, LinkSpec::setDescriptionTranslations))
          .addMappings(mapper -> mapper.with(linkedHashSetProvider)
              .map(LinkEntity::getTextTranslations, LinkSpec::setTextTranslations))
          .addMappings(mapper -> mapper.using(urlSignConverter)
              .map(LinkEntity::getCardImage, LinkSpec::setCardImageUrl))
          .addMappings(mapper -> mapper.using(urlSignConverter)
              .map(LinkEntity::getMenuImage, LinkSpec::setMenuImageUrl));

      modelMapper
          .createTypeMap(LinkSpec.class, LinkEntity.class)
          .addMappings(mapper -> mapper.with(linkedHashSetProvider)
              .map(LinkSpec::getCategoryIds, LinkEntity::setCategoryIds))
          .addMappings(mapper -> mapper.with(linkedHashSetProvider)
              .map(LinkSpec::getDescriptionTranslations, LinkEntity::setDescriptionTranslations))
          .addMappings(mapper -> mapper.with(linkedHashSetProvider)
              .map(LinkSpec::getTextTranslations, LinkEntity::setTextTranslations))
          .addMappings(mapper -> mapper.skip(LinkEntity::setCardImage))
          .addMappings(mapper -> mapper.skip(LinkEntity::setMenuImage));
    }
  }

}
