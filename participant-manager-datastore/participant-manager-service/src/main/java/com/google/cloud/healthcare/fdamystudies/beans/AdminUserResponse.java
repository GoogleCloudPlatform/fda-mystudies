/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AdminUserResponse extends BaseResponse {

  private String userId;

  public AdminUserResponse(MessageCode messageCode, String userId) {
    super(messageCode);
    this.userId = userId;
  }
}
