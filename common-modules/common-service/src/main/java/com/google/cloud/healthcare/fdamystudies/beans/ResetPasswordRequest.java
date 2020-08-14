/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class ResetPasswordRequest {

  @ToString.Exclude
  @Size(max = 320)
  @Email
  @NotBlank
  private String email;

  @Size(max = 100)
  @NotBlank
  private String appId;

  @Size(max = 100)
  @NotBlank
  private String orgId;

  @ToString.Exclude private String userId;
}
