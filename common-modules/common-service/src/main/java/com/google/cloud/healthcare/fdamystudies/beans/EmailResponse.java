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

public class EmailResponse extends BaseResponse {

  private ErrorCode errorCode;

  public EmailResponse() {}

  public EmailResponse(MessageCode messageCode) {
    super(messageCode);
  }

  public EmailResponse(ErrorCode errorCode) {
    this.errorCode = errorCode;
  }
}
