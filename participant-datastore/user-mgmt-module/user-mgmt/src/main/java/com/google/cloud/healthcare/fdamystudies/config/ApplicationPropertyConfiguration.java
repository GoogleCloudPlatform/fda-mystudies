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

  // If true, we do not authenticate with the SMTP server but rather rely on
  // an IP whitelist for the domain `fromDomain`.
  @Value("${from.email.use_ip_whitelist}")
  private Boolean useIpWhitelist;

  // Domain to use with the IP whitelist relay.
  // Must be in the form domain rather than domain.com.

  @Value("${from.email.domain}")
  private String fromDomain;

  @Value("${confirmation.mail.subject}")
  private String confirmationMailSubject;

  @Value("${confirmation.mail.content}")
  private String confirmationMail;

  @Value("${auth.server.updateStatus.url}")
  private String authServerUpdateStatusUrl;

  @Value("${register.url}")
  private String authServerRegisterStatusUrl;

  @Value("${messaging.fcm.url}")
  private String apiUrlFcm;

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

  @Value("${org.name}")
  private String orgName;

  @Value("${mail.contact-email}")
  private String contactEmail;

  @Value("${mail.from-email}")
  private String fromEmail;

  @Value("${auth.server.deleteStatusUrl}")
  private String authServerDeleteStatusUrl;
}
