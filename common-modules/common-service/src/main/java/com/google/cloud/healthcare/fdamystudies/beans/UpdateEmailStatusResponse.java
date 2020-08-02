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

public class UpdateEmailStatusResponse extends BaseResponse {

  public UpdateEmailStatusResponse() {}

  public UpdateEmailStatusResponse(ErrorCode errorCode) {
    super(errorCode);
  }

  public UpdateEmailStatusResponse(MessageCode messageCode) {
    super(messageCode);
  }
}
