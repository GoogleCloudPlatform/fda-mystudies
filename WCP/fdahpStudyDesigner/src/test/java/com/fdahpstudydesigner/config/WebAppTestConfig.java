/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.config;

import static org.mockito.Mockito.mock;

import com.fdahpstudydesigner.service.AuditEventService;
import com.fdahpstudydesigner.service.AuditEventServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
@ComponentScan("com.fdahpstudydesigner")
public class WebAppTestConfig extends WebMvcConfigurerAdapter {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  @Primary
  public AuditEventService auditService() {
    return mock(AuditEventServiceImpl.class);
  }
}
