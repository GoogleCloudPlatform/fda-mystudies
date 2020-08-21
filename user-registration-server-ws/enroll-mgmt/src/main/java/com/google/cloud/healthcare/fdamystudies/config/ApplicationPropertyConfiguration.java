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
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Setter
@Getter
@ToString
public class ApplicationPropertyConfiguration {

  @Value("${authServerAccessTokenValidationUrl}")
  private String authServerAccessTokenValidationUrl;

  @Value("${clientId}")
  private String clientId;

  @Value("${secretKey}")
  private String secretKey;

  @Value("${auth.server.url}")
  private String authServerUrl;

  @Value("${authServerClientValidationUrl}")
  private String authServerClientValidationUrl;

  @Value("${auth.server.updateStatusUrl}")
  private String authServerUpdateStatusUrl;

  @Value("${auth.server.deleteStatusUrl}")
  private String authServerDeleteStatusUrl;

  @Value("${register.url}")
  private String authServerRegisterStatusUrl;

  @Value("${interceptor}")
  private String interceptorUrls;

  @Value("${serverApiUrls}")
  private String serverApiUrls;

  @Value("${response.server.url}")
  private String responseServerUrl;

  @Value("${response.server.url.addParticipantId}")
  private String addParticipantId;

  @Value("${response.server.url.participant.withdraw}")
  private String withdrawStudyUrl;
}
