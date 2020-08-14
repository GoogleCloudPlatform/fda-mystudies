/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class AuthenticationResponse extends BaseResponse {

  @ToString.Exclude private String userId;

  private int accountStatus;

  public AuthenticationResponse() {}

  public AuthenticationResponse(ErrorCode errorCode) {
    super(errorCode);
  }

  public AuthenticationResponse(ErrorCode errorCode, String userId, int accountStatus) {
    super(errorCode);
    this.userId = userId;
    this.accountStatus = accountStatus;
  }
}
