/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationResponse extends BaseResponse {

  private String userId;

  private String tempRegId;

  @JsonIgnore private ErrorCode errorCode;

  /**
   * @param userId
   * @param tempRegId
   */
  public UserRegistrationResponse(String userId, String tempRegId) {
    super();
    this.userId = userId;
    this.tempRegId = tempRegId;
  }
}
