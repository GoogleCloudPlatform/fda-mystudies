/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.EMAIL_LENGTH;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@JsonInclude(Include.NON_NULL)
@Component
@Scope(value = "prototype")
@ToString
@Getter
@Setter
public class ParticipantDetailRequest {

  @ToString.Exclude
  @Size(max = EMAIL_LENGTH)
  @Email
  @NotBlank
  private String email;

  private String invitedDate;

  private String onboardingStatus;

  private String siteId;

  private String participantId;
}
