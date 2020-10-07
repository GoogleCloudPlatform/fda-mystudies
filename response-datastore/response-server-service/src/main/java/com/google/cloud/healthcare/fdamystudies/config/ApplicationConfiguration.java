/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Setter
@Getter
@Configuration
@PropertySources({
  @PropertySource("classpath:application-appConfig-${spring.profiles.active}.properties"),
  @PropertySource("classpath:application-${spring.profiles.active}.properties")
})
public class ApplicationConfiguration {

  @Value("${firestoreProjectId}")
  private String firestoreProjectId;

  @Value("${interceptor}")
  private String interceptorUrls;

  @Value("${serverApiUrls}")
  private String serverApiUrls;

  @Value("${wcpStudyActivityMetadataUrl}")
  private String wcpStudyActivityMetadataUrl;

  @Value("${responseDataFilePath}")
  private String responseDataFilePath;

  @Value("${lastResponseOnly}")
  private String lastResponseOnly;

  @Value("${supportStringResponse}")
  private String supportStringResponse;

  @Value("${responseSupportedQTypeDouble}")
  private String responseSupportedQTypeDouble;

  @Value("${responseSupportedQTypeDate}")
  private String responseSupportedQTypeDate;

  @Value("${responseSupportedQTypeString}")
  private String responseSupportedQTypeString;

  @Value("${saveRawResponseData}")
  private String saveRawResponseData;

  @Value("${wcpBundleId}")
  private String wcpBundleId;

  @Value("${wcpAppToken}")
  private String wcpAppToken;

  @Value("${regServerClientId}")
  private String regServerClientId;

  @Value("${regServerClientSecret}")
  private String regServerClientSecret;

  @Value("${regServerPartStudyInfoUrl}")
  private String regServerPartStudyInfoUrl;
}
