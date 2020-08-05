/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;

public class UpdateUserResponse extends BaseResponse {

  public UpdateUserResponse() {}

  public UpdateUserResponse(ErrorCode errorCode) {
    super(errorCode);
  }

  public UpdateUserResponse(MessageCode messageCode) {
    super(messageCode);
  }
}
