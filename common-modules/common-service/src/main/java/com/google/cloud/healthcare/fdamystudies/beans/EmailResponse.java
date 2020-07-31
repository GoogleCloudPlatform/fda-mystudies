/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import org.springframework.http.HttpStatus;

import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;

public class EmailResponse extends BaseResponse {

  public EmailResponse() {}

  public EmailResponse(ErrorCode errorCode) {
    super(errorCode);
  }

  public EmailResponse(MessageCode messageCode) {
    super(messageCode);
  }

  public EmailResponse(HttpStatus httpStatus, String message) {
    super(httpStatus, message);
  }
}
