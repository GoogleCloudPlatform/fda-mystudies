/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Configuration
public class ApplicationConfiguratation {
  // application-appConfig.properties properties

  @Value("${sessionTimeOutInMinutes}")
  private String sessionTimeOutInMinutes;

  @Value("${interceptor}")
  private String interceptorUrls;

  @Value("${authServerAccessTokenValidationUrl}")
  private String authServerAccessTokenValidationUrl;

  @Value("${clientId}")
  private String clientId;

  @Value("${secretKey}")
  private String secretKey;

  @Value("${auth.server.url}")
  private String authServerUrl;

  @Value("${register.url}")
  private String authServerRegisterStatusUrl;

  @Value("${auth.server.updateStatusUrl}")
  private String authServerUpdateStatusUrl;

  @Value("${auth.server.deleteStatusUrl}")
  private String authServerDeleteStatusUrl;

  @Value("${participant.invite.subject}")
  private String participantInviteSubject;

  @Value("${participant.invite.body}")
  private String participantInviteBody;

  @Value("${newlyCreatedTimeframeMinutes}")
  private Integer newlyCreatedTimeframeMinutes;

  @Value("${appEnv}")
  private String appEnv;

  @Value("${enrollmentTokenExpiryinHours}")
  private Integer enrollmentTokenExpiryinHours;

  @Value("${securityCodeExpireDate}")
  private String securityCodeExpireDate;

  @Value("${register.user.subject}")
  private String registerUserSubject;

  @Value("${register.user.body}")
  private String registerUserBody;

  @Value("${user.details.link}")
  private String userDetailsLink;
}
