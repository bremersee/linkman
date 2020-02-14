package org.bremersee.linkman.config;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.server.WebFilter;

/**
 * The web configuration.
 */
@Configuration
public class WebConfiguration {

  private ServerProperties serverProperties;

  /**
   * Instantiates a new web configuration.
   *
   * @param serverProperties the server properties
   */
  public WebConfiguration(ServerProperties serverProperties) {
    this.serverProperties = serverProperties;
  }

  /**
   * Context path web filter.
   *
   * @return the web filter
   */
  @Bean
  public WebFilter contextPathWebFilter() {
    String contextPath = serverProperties.getServlet().getContextPath();
    return (exchange, chain) -> {
      ServerHttpRequest request = exchange.getRequest();
      if (StringUtils.hasText(contextPath)
          && !contextPath.equals("/")
          && request.getURI().getPath().startsWith(contextPath)) {
        return chain.filter(
            exchange.mutate()
                .request(request.mutate().contextPath(contextPath).build())
                .build());
      }
      return chain.filter(exchange);
    };
  }

}
