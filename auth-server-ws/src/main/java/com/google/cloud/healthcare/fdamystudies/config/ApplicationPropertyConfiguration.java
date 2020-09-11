/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.config;

import java.io.Serializable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import lombok.Getter;
import lombok.Setter;

@Configuration
// @PropertySource("classpath:application-${spring.profiles.active}.properties")
@PropertySource("classpath:appConfigurations.properties")
@Setter
@Getter
public class ApplicationPropertyConfiguration implements Serializable {

  private static final long serialVersionUID = 2189883675260389666L;

  @Value("${sessionTimeOutInMinutes}")
  private String sessionTimeOutInMinutes;

  @Value("${max.login.attempts}")
  private String maxLoginAttempts;

  @Value("${expiration.login.attempts.minute}")
  private String expirationLoginAttemptsMinute;

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
  // Must be in the form domain rather than domain.com.
  @Value("${from.email.domain}")
  private String fromDomain;

  @Value("${password.expiration.in.day}")
  private String passwdExpiryInDay;

  @Value("${password.expiration.in.min}")
  private String passwdExpiryInMin;

  @Value("${passwd.reset.link.subject}")
  private String passwdResetLinkSubject;

  @Value("${passwd.reset.link.content}")
  private String passwdResetLinkContent;

  @Value("${password.history.count}")
  private String passwordHistoryCount;

  @Value("${interceptor}")
  private String interceptorUrls;

  @Value("${verification.expiration.in.hour}")
  private String verificationExpInHr;

  @Value("${locked.account.mail.subject}")
  private String lockAccountMailSubject;

  @Value("${locked.account.mail.content}")
  private String lockAccountMailContent;
}
