/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.config;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Setter
@Getter
public class AppPropertyConfig implements Serializable {

  private static final long serialVersionUID = 2189883675260389666L;

  @Value("${oauth.scim.service.password.expiry.days:90}")
  private int passwordExpiryDays;

  @Value("${oauth.scim.service.password.history.max.size:10}")
  private int passwordHistoryMaxSize;

  @Value("${mail.contact-email}")
  private String contactEmail;

  @Value("${mail.from-email}")
  private String fromEmail;

  @Value("${mail.subject.reset-password}")
  private String mailResetPasswordSubject;

  @Value("${mail.body.reset-password}")
  private String mailResetPasswordBody;

  @Value("${mail.subject.reset-password-mobile}")
  private String mailResetPasswordSubjectForMobileApp;

  @Value("${mail.body.reset-password-mobile}")
  private String mailResetPasswordBodyForMobileApp;

  @Value("${oauth.scim.service.max.invalid.login.attempts:5}")
  private int maxInvalidLoginAttempts;

  @Value("${oauth.scim.service.account.lockout.period.minutes:15}")
  private int accountLockPeriodInMinutes;

  @Value("${oauth.scim.service.reset.password.expiry.hours:48}")
  private int resetPasswordExpiryInHours;

  @Value("${mail.account.locked.subject}")
  private String mailAccountLockedSubject;

  @Value("${mail.account.locked.content}")
  private String mailAccountLockedBody;

  @Value("${mail.account.locked.subject-mobile}")
  private String mailAccountLockedSubjectForMobileApp;

  @Value("${mail.account.locked.content-mobile}")
  private String mailAccountLockedBodyForMobileApp;

  @Value("${cookie.secure:true}")
  private boolean secureCookie;

  @Value("${tempregid.expiry.minutes:30}")
  private int tempRegIdExpiryMinutes;
}
