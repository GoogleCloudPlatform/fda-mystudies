/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.beans;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class LoginRequest {

  private static final String PASSWORD_REGEX =
      "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!\\\\\\\"#$%&'()*+,-.:;<=>?@\\\\\\\\[\\\\\\\\]^_`{|}~]).{8,64}$";

  @ToString.Exclude
  @Size(max = 320)
  @Email
  private String email;

  @ToString.Exclude
  @Size(
      min = 8,
      max = 64,
      message =
          "Password must contain at least 8 characters, including uppercase, lowercase letters, numbers and allowed special characters.")
  @Pattern(regexp = PASSWORD_REGEX, message = "Your password does not meet the required criteria.")
  private String password;
}
