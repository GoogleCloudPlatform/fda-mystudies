/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.healthcare.fdamystudies.exceptions.RestResponseErrorHandler;
import com.google.cloud.healthcare.fdamystudies.interceptor.RestTemplateAuthTokenModifierInterceptor;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan(basePackages = "com.google.cloud.healthcare.fdamystudies")
public class CommonModuleConfiguration implements WebMvcConfigurer {

  @Value("${cors.allowed.origins:}")
  private String corsAllowedOrigins;
  
  @Autowired
  private RestTemplateAuthTokenModifierInterceptor restTemplateAuthTokenModifierInterceptor;

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

  @Bean
  public RestTemplate restTemplate() {
    RestTemplate restTemplate = new RestTemplate();

    restTemplate.setErrorHandler(new RestResponseErrorHandler());
    addInterceptors(restTemplate);

    restTemplate.setErrorHandler(new RestResponseErrorHandler());
    return restTemplate;
  }

  protected void addInterceptors(RestTemplate restTemplate) {
    List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
    if (CollectionUtils.isEmpty(interceptors)) {
      interceptors = new ArrayList<>();
    }
    interceptors.add(restTemplateAuthTokenModifierInterceptor);
    restTemplate.setInterceptors(interceptors);
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    if (StringUtils.isNotEmpty(corsAllowedOrigins)) {
      registry
          .addMapping("/**")
          .allowedOrigins(corsAllowedOrigins.split(","))
          .allowedMethods("*")
          .allowedHeaders("*");
    }
  }
}
