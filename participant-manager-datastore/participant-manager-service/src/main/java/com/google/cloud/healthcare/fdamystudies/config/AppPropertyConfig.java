/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.config;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Setter
@Getter
public class AppPropertyConfig implements Serializable {

  private static final long serialVersionUID = 5755215378945331532L;

  @Value("${security.xsrf.enabled}")
  private boolean isXsrfEnabled;

  @Value("${securityCodeExpireInHours}")
  private String securityCodeExpireInHours;

  @Value("${enrollmentTokenExpiryInHours}")
  private Integer enrollmentTokenExpiryInHours;

  @Value("${participant.invite.subject}")
  private String participantInviteSubject;

  @Value("${participant.invite.body}")
  private String participantInviteBody;

  @Value("${bucket.name}")
  private String bucketName;

  @Value("${register.user.subject}")
  private String registerUserSubject;

  @Value("${register.user.body}")
  private String registerUserBody;

  @Value("${user.details.link}")
  private String userDetailsLink;

  @Value("${org.name}")
  private String orgName;

  @Value("${update.user.subject}")
  private String updateUserSubject;

  @Value("${update.user.body}")
  private String updateUserBody;

  @Value("${auth.server.register.url}")
  private String authRegisterUrl;

  @Value("${auth.server.updateStatusUrl}")
  private String authServerUpdateStatusUrl;

  @Value("${auth.server.logout.user.url}")
  private String authLogoutUserUrl;

  @Value("${mail.contact-email}")
  private String contactEmail;

  @Value("${mail.from-email}")
  private String fromEmail;

  @Value("${study.builder.cloud.bucket.name}")
  private String studyBuilderCloudBucketName;
}
