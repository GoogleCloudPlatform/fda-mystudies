/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Configuration
@PropertySource("classpath:common-application.properties")
public class CommonApplicationPropertyConfig {

  @Value("${application.version.mobile.app}")
  private String mobileApplicationVersion;

  @Value("${application.version.response.datastore}")
  private String responseDatastoreApplicationVersion;

  @Value("${application.version.participant.datastore}")
  private String participantDatastoreApplicationVersion;

  @Value("${application.version.study.builder}")
  private String studyBuilderApplicationVersion;

  @Value("${application.version.study.builder.app}")
  private String studyBuilderAppApplicationVersion;

  @Value("${application.version.cloud.storage}")
  private String cloudStorageApplicationVersion;

  @Value("${application.version.scim.auth.server}")
  private String scimAuthServerApplicationVersion;

  @Value("${application.version.auth.server}")
  private String authServerApplicationVersion;

  @Value("${application.version.participant.manager}")
  private String participantManagerApplicationVersion;

  @Value("${application.version.participant.manager.app}")
  private String participantManagerAppApplicationVersion;

  @Value("${application.version.platform}")
  private String platformVersion;
}
