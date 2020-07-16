package org.bremersee.linkman.config;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.data.convert.BaseCommonConversions;
import org.bremersee.data.minio.MinioOperations;
import org.bremersee.data.minio.MinioRepository;
import org.bremersee.data.minio.MinioRepositoryImpl;
import org.bremersee.linkman.repository.CategoryRepository;
import org.bremersee.linkman.repository.LinkRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.util.Assert;

/**
 * The persistence configuration.
 *
 * @author Christian Bremer
 */
@Configuration
@EnableReactiveMongoRepositories(basePackageClasses = {
    CategoryRepository.class,
    LinkRepository.class
})
@EnableConfigurationProperties(LinkmanProperties.class)
@Slf4j
public class PersistenceConfiguration {

  private final LinkmanProperties properties;

  private final MinioOperations minioOperations;

  /**
   * Instantiates a new persistence configuration.
   *
   * @param properties the properties
   * @param minioOperationsProvider the minio operations provider
   */
  public PersistenceConfiguration(
      LinkmanProperties properties,
      ObjectProvider<MinioOperations> minioOperationsProvider) {
    this.properties = properties;
    this.minioOperations = minioOperationsProvider.getIfAvailable();
    Assert.notNull(this.minioOperations, "Minio operations must not be null.");
  }

  /**
   * Init.
   */
  @EventListener(ApplicationReadyEvent.class)
  public void init() {
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

  /**
   * Minio repository.
   *
   * @return the minio repository
   */
  @Bean
  public MinioRepository minioRepository() {
    return new MinioRepositoryImpl(
        minioOperations,
        null,
        properties.getBucketName(),
        false,
        true,
        properties.getPresignedObjectUrlDuration());
  }

}
