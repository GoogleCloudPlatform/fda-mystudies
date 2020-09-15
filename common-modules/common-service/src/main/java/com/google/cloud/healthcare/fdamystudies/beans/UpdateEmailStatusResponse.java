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
import lombok.ToString;

@Getter
@Setter
public class UpdateEmailStatusResponse extends BaseResponse {

  @ToString.Exclude private String tempRegId;

  public UpdateEmailStatusResponse() {}

  public UpdateEmailStatusResponse(MessageCode messageCode, String tempRegId) {
    super(messageCode);
    this.tempRegId = tempRegId;
  }
}
