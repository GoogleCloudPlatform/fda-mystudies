/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class IDPAdminDetailsResponse extends BaseResponse {

  private List<String> email;

  private Boolean mfaEnabledForPM;

  public IDPAdminDetailsResponse(
      MessageCode messageCode, List<String> email, Boolean mfaEnabledForPM) {
    super(messageCode);
    this.email = email;
    this.mfaEnabledForPM = mfaEnabledForPM;
  }
}
