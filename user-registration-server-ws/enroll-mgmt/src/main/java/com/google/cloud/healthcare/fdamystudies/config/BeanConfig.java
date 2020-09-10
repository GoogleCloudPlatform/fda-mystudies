/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.config;

import com.google.cloud.healthcare.fdamystudies.util.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableScheduling
@Configuration
@EnableWebMvc
public class BeanConfig implements WebMvcConfigurer {

  @Autowired ApplicationPropertyConfiguration appConfig;

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowedHeaders("*").allowedMethods("*");
      }
    };
  }

  @Bean
  public FilterRegistrationBean<AuthenticationFilter> loggingFilter() {
    FilterRegistrationBean<AuthenticationFilter> authenticationBean =
        new FilterRegistrationBean<>();
    authenticationBean.setFilter(new AuthenticationFilter());
    authenticationBean.addUrlPatterns("/*");
    return authenticationBean;
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
