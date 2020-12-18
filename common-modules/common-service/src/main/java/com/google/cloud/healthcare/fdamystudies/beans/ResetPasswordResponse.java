/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import com.google.cloud.healthcare.fdamystudies.common.MessageCode;

public class ResetPasswordResponse extends BaseResponse {

  public ResetPasswordResponse() {}

  public ResetPasswordResponse(MessageCode messageCode) {
    super(messageCode);
  }
}
