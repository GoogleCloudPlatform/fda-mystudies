/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class AuthenticationResponse extends BaseResponse {

  @ToString.Exclude private String userId;

  private int accountStatus;

  public AuthenticationResponse() {}

  public AuthenticationResponse(String userId, int accountStatus) {
    this.userId = userId;
    this.accountStatus = accountStatus;
  }
}
