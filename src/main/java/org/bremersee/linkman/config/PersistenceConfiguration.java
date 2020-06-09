package org.bremersee.linkman.config;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.data.convert.BaseCommonConversions;
import org.bremersee.data.minio.MinioOperations;
import org.bremersee.linkman.repository.CategoryRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.util.StringUtils;

/**
 * The persistence configuration.
 */
@Configuration
@EnableReactiveMongoRepositories(basePackageClasses = {
    CategoryRepository.class
})
@EnableConfigurationProperties(LinkmanProperties.class)
@Slf4j
public class PersistenceConfiguration {

  private final LinkmanProperties properties;

  private final MinioOperations minioOperations;

  public PersistenceConfiguration(
      LinkmanProperties properties,
      ObjectProvider<MinioOperations> minioOperationsProvider) {
    this.properties = properties;
    this.minioOperations = minioOperationsProvider.getIfAvailable();
  }

  @EventListener(ApplicationReadyEvent.class)
  public void init() {
    if (StringUtils.hasText(properties.getBucketName())
        && minioOperations != null
        && !minioOperations.bucketExists(properties.getBucketName())) {
      log.info("Creating bucket {} ...", properties.getBucketName());
      minioOperations.makeBucket(properties.getBucketName());
    }
  }

  /**
   * Custom conversions.
   *
   * @return the custom conversions
   */
  @Primary
  @Bean
  public MongoCustomConversions customConversions() {
    return new MongoCustomConversions(Arrays.asList(BaseCommonConversions.CONVERTERS));
  }

}
