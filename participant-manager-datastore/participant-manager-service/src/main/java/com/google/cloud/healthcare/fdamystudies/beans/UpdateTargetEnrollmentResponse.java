/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdateTargetEnrollmentResponse extends BaseResponse {

  private String siteId;

  public UpdateTargetEnrollmentResponse(String siteId, MessageCode messageCode) {
    super(messageCode);
    this.siteId = siteId;
  }
}
