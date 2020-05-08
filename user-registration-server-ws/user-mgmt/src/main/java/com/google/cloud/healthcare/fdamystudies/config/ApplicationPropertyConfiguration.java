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
@PropertySource("classpath:appConfigurations.properties")
@Setter
@Getter
@ToString
public class ApplicationPropertyConfiguration {

  @Value("${from.email.address}")
  private String fromEmailAddress;

  // Password for email address we send communication with. Not needed if we
  // are not authenticating. See `useIpWhitelist`.
  @Value("${from.email.password}")
  private String fromEmailPassword;

  @Value("${factory.value}")
  private String sslFactoryValue;

  @Value("${smtp.port}")
  private String smtpPortValue;

  @Value("${smtp.hostname}")
  private String smtpHostName;

  // If true, we do not authenticate with the SMTP server but rather rely on
  // an IP whitelist for the domain `fromDomain`.
  @Value("${from.email.use_ip_whitelist}")
  private Boolean useIpWhitelist;

  // Domain to use with the IP whitelist relay.
  // Must be in the form rocketturtle rather than rocketturtle.net.

  @Value("${from.email.domain}")
  private String fromDomain;

  @Value("${resend.confirmation.mail.subject}")
  private String resendConfirmationMailSubject;

  @Value("${resend.confirmation.mail.content}")
  private String resendConfirmationMail;

  @Value("${authServerAccessTokenValidationUrl}")
  private String authServerAccessTokenValidationUrl;

  @Value("${auth.server.url}")
  private String authServerUrl;

  @Value("${auth.server.updateStatusUrl}")
  private String authServerUpdateStatusUrl;

  @Value("${auth.server.deleteStatusUrl}")
  private String authServerDeleteStatusUrl;

  @Value("${register.url}")
  private String authServerRegisterStatusUrl;

  @Value("${interceptor}")
  private String interceptorUrls;

  @Value("${AUTH_KEY_FCM}")
  private String authKeyFcm;

  @Value("${API_URL_FCM}")
  private String apiUrlFcm;

  @Value("${serverApiUrls}")
  private String serverApiUrls;

  @Value("${authServerClientValidationUrl}")
  private String authServerClientValidationUrl;

  @Value("${clientId}")
  private String clientId;

  @Value("${secretKey}")
  private String secretKey;

  @Value("${response.server.url.participant.withdraw}")
  private String withdrawStudyUrl;

  @Value("${ios.push.notification.type}")
  private String iosPushNotificationType;

  // Comma separated list of whitelisted domains.
  @Value("${email.whitelisted_domains}")
  private String whitelistedUserDomains;

  // Feedback & Contactus mail content starts
  @Value("${feedback.mail.content}")
  private String feedbackMailBody;

  @Value("${feedback.mail.subject}")
  private String feedbackMailSubject;

  @Value("${feedback.email}")
  private String feedbackToEmail;

  @Value("${contactus.mail.content}")
  private String contactusMailBody;

  @Value("${contactus.mail.subject}")
  private String contactusMailSubject;

  @Value("${contactus.email}")
  private String contactusToEmail;
  // Feedback & Contactus mail content ends

  @Value("${cloud.institution.bucket}")
  private String institutionBucketName;
}
