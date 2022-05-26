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

  @Value("${confirmation.mail.subject}")
  private String confirmationMailSubject;

  @Value("${confirmation.mail.content}")
  private String confirmationMail;

  @Value("${auth.server.updateStatus.url}")
  private String authServerUpdateStatusUrl;

  @Value("${messaging.fcm.url}")
  private String apiUrlFcm;

  @Value("${response.server.url.participant.withdraw}")
  private String withdrawStudyUrl;

  @Value("${ios.push.notification.type}")
  private String iosPushNotificationType;

  // Feedback & Contactus mail content starts
  @Value("${feedback.mail.content}")
  private String feedbackMailBody;

  @Value("${feedback.mail.subject}")
  private String feedbackMailSubject;

  @Value("${contactus.mail.content}")
  private String contactusMailBody;

  @Value("${contactus.mail.subject}")
  private String contactusMailSubject;

  @Value("${mail.from-email}")
  private String fromEmail;

  // Feedback & Contactus mail content ends

  @Value("${auth.server.deleteStatusUrl}")
  private String authServerDeleteStatusUrl;
}
