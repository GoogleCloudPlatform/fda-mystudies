/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.config;

import static org.mockito.Mockito.mock;

import com.google.cloud.healthcare.fdamystudies.util.EmailNotification;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("mockit")
@Configuration
public class AppConfig {
  @Bean
  @Primary
  public EmailNotification emailNotification() throws Exception {
    EmailNotification emailNotification = mock(EmailNotification.class);
    Mockito.when(
            emailNotification.sendEmailNotification(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(),
                Mockito.any()))
        .thenReturn(true);
    return emailNotification;
  }
}
