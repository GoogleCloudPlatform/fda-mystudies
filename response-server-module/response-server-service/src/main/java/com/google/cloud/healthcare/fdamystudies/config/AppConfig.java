/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.config;

import com.google.cloud.healthcare.fdamystudies.utils.AuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig extends CommonModuleConfiguration {

  @Bean
  public FilterRegistrationBean<AuthenticationFilter> loggingFilter() {
    FilterRegistrationBean<AuthenticationFilter> authenticationBean =
        new FilterRegistrationBean<>();
    authenticationBean.setFilter(new AuthenticationFilter());
    authenticationBean.addUrlPatterns("/*");
    return authenticationBean;
  }
}
