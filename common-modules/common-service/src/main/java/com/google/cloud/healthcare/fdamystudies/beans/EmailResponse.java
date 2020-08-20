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
import org.springframework.http.HttpStatus;

public class EmailResponse extends BaseResponse {

  public EmailResponse() {}

  public EmailResponse(ErrorCode errorCode) {
    super(errorCode);
  }

  public EmailResponse(MessageCode messageCode) {
    super(messageCode);
  }
}
