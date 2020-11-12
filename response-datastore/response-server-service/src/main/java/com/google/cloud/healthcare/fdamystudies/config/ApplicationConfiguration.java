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

@Setter
@Getter
@Configuration
public class ApplicationConfiguration {

  @Value("${firestore.project.id}")
  private String firestoreProjectId;

  @Value("${studydatastore.study.activity.metadata.url}")
  private String wcpStudyActivityMetadataUrl;

  @Value("${response.data.file.path}")
  private String responseDataFilePath;

  @Value("${last.response.only}")
  private String lastResponseOnly;

  @Value("${support.string.response}")
  private String supportStringResponse;

  @Value("${response.supported.QType.double}")
  private String responseSupportedQTypeDouble;

  @Value("${response.supported.QType.date}")
  private String responseSupportedQTypeDate;

  @Value("${response.supported.QType.string}")
  private String responseSupportedQTypeString;

  @Value("${save.raw.response.data}")
  private String saveRawResponseData;

  @Value("${studydatastore.bundle.id}")
  private String wcpBundleId;

  @Value("${studydatastore.app.token}")
  private String wcpAppToken;

  @Value("${security.oauth2.client.client-id}")
  private String regServerClientId;

  @Value("${security.oauth2.client.client-secret}")
  private String regServerClientSecret;

  @Value("${enroll.mgmt.service.url}")
  private String regServerPartStudyInfoUrl;
}
