/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.config;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.cloud.healthcare.fdamystudies.service.AuditEventService;
import com.google.cloud.healthcare.fdamystudies.service.AuditEventServiceImpl;
import com.google.cloud.healthcare.fdamystudies.util.EmailNotification;
import javax.mail.internet.MimeMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;

@Profile("mockit")
@Configuration
public class AppTestConfig {

  @Bean
  @Primary
  public EmailNotification emailNotification() throws Exception {
    return mock(EmailNotification.class);
  }

  @Bean
  @Primary
  public JavaMailSender javaMailSender() {
    JavaMailSender javaMailSender = mock(JavaMailSender.class);
    MimeMessage mimeMessage = mock(MimeMessage.class);
    when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
    doNothing().when(javaMailSender).send(mimeMessage);
    return javaMailSender;
  }

  @Bean
  @Primary
  public AuditEventService auditService() {
    return mock(AuditEventServiceImpl.class);
  }
}
