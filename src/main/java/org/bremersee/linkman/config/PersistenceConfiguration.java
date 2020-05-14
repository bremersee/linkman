package org.bremersee.linkman.config;

import java.util.Arrays;
import org.bremersee.data.convert.BaseCommonConversions;
import org.bremersee.linkman.repository.CategoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

/**
 * The persistence configuration.
 */
@Configuration
@EnableReactiveMongoRepositories(basePackageClasses = {
    CategoryRepository.class
})
public class PersistenceConfiguration {

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
