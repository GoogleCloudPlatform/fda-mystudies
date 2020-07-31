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

import lombok.Getter;
import lombok.Setter;

@Configuration
@Setter
@Getter
public class AppPropertyConfig implements Serializable {

  private static final long serialVersionUID = 5755215378945331532L;

  @Value("${securityCodeExpireDate}")
  private String securityCodeExpireDate;

  @Value("${enrollmentTokenExpiryinHours}")
  private Integer enrollmentTokenExpiryinHours;

  @Value("${participant.invite.subject}")
  private String participantInviteSubject;

  @Value("${participant.invite.body}")
  private String participantInviteBody;

  @Value("${fromEmailAddress}")
  private String fromEmailAddress;

  @Value("${fromEmailPasswod}")
  private String fromEmailPasswod;
}
