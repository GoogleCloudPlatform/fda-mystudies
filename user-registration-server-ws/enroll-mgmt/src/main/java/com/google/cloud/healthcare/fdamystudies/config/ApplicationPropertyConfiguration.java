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
import org.springframework.context.annotation.PropertySource;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Configuration
@PropertySource("classpath:applicationConfiguration.properties")
@Setter
@Getter
@ToString
public class ApplicationPropertyConfiguration {

  @Value("${max.login.attempts}")
  private String maxLoginAttempts;

  @Value("${expiration.login.attempts.minute}")
  private String expirationLoginAttemptsMinute;

  @Value("${from.email.address}")
  private String fromEmailAddress;

  @Value("${from.email.password}")
  private String fromEmailPassword;

  @Value("${factory.value}")
  private String sslFactoryValue;

  @Value("${port}")
  private String smtpPortValue;

  @Value("${host.name}")
  private String smtpHostName;

  @Value("${passwd.reset.link.subject}")
  private String passwdResetLinkSubject;

  @Value("${passwd.reset.link.content}")
  private String passwdResetLinkContent;

  @Value("${password.history.count}")
  private String passwordHistoryCount;

  @Value("${resend.confirmation.mail.subject}")
  private String resendConfirmationMailSubject;

  @Value("${resend.confirmation.mail.content}")
  private String resendConfirmationMail;

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
}
