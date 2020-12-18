/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.PASSWORD_REGEX;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.PASSWORD_REGEX_MESSAGE;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class ChangePasswordRequest {

  @ToString.Exclude
  @Size(min = 8, max = 64)
  private String currentPassword;

  @ToString.Exclude
  @Pattern(regexp = PASSWORD_REGEX, message = PASSWORD_REGEX_MESSAGE)
  private String newPassword;

  @ToString.Exclude private String userId;
}
