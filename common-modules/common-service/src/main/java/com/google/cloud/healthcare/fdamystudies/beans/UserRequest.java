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

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class UserRequest {

  @NotBlank
  @Size(max = 100)
  private String appId;

  @ToString.Exclude
  @NotBlank
  @Size(max = EMAIL_LENGTH)
  @Email
  private String email;

  @ToString.Exclude
  @NotBlank
  @Pattern(regexp = PASSWORD_REGEX, message = PASSWORD_REGEX_MESSAGE)
  private String password;

  /** Refer UserAccountStatus enum for values. */
  @NotNull private Integer status;

  private String appName;
}
