/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.config;

import static org.mockito.Mockito.mock;

import com.google.cloud.healthcare.fdamystudies.service.AuditEventService;
import com.google.cloud.healthcare.fdamystudies.service.AuditEventServiceImpl;
import com.google.cloud.storage.Storage;
import org.springframework.cloud.gcp.storage.GoogleStorageProtocolResolver;
import org.springframework.cloud.gcp.storage.GoogleStorageProtocolResolverSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("mockit")
@Configuration
@Import(GoogleStorageProtocolResolver.class)
public class AppTestConfig {
  @Bean
  @Primary
  public AuditEventService auditService() {
    return mock(AuditEventServiceImpl.class);
  }

  @Bean
  @Primary
  public static Storage mockStorage() throws Exception {
    return mock(Storage.class);
  }

  @Bean
  public static GoogleStorageProtocolResolverSettings googleStorageProtocolResolverSettings() {
    return new GoogleStorageProtocolResolverSettings();
  }
}
