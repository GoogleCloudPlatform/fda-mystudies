/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style license that can be found in the LICENSE file
 * or at https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.config;

import static org.mockito.Mockito.mock;

import com.fdahpstudydesigner.service.AuditEventService;
import com.fdahpstudydesigner.service.AuditEventServiceImpl;
import com.fdahpstudydesigner.service.LoginService;
import com.fdahpstudydesigner.service.LoginServiceImpl;
import com.fdahpstudydesigner.util.Mail;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@EnableWebSecurity
@ComponentScan("com.fdahpstudydesigner")
@ImportResource(value = {"classpath:spring-security.xml"})
public class WebAppTestConfig extends WebSecurityConfigurerAdapter {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  @Primary
  public AuditEventService auditService() {
    return mock(AuditEventServiceImpl.class);
  }

  @Bean
  public LoginService loginService() {
    return new LoginServiceImpl();
  }

  @Bean
  @Primary
  public Mail email() {
    Mail mail = mock(Mail.class);
    Mockito.when(mail.sendemail()).thenReturn(true);
    return mail;
  }
}
