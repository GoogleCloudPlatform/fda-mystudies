/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.EMAIL_LENGTH;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.PASSWORD_REGEX;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.PASSWORD_REGEX_MESSAGE;

import java.time.LocalDateTime;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationForm {

  private String userId;
  private String firstName;
  private String lastName;

  @ToString.Exclude
  @NotBlank
  @Size(max = EMAIL_LENGTH)
  @Email
  private String emailId;

  @ToString.Exclude
  @NotBlank
  @Pattern(regexp = PASSWORD_REGEX, message = PASSWORD_REGEX_MESSAGE)
  private String password;

  private boolean usePassCode;
  private boolean touchId;
  private boolean localNotification;
  private boolean remoteNotification;
  private boolean reminderFlag;
  private String auth;
  private int status;
  private boolean tempPassword;
  private LocalDateTime tempPasswordDate;
  private String appId;
  private String appName;

  public UserRegistrationForm(String userId, String emailId, String password) {
    super();
    this.userId = userId;
    this.emailId = emailId;
    this.password = password;
  }

  public UserRegistrationForm(@NotNull String emailId, String password) {
    super();
    this.emailId = emailId;
    this.password = password;
  }
}
