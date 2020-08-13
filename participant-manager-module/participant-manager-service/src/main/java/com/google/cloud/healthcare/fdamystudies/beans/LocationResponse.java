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
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LocationResponse extends BaseResponse {

  private String locationId;
  private String customId;
  private String description;
  private String name;
  private Integer status;

  public LocationResponse(ErrorCode errorCode) {
    super(errorCode);
  }

  public LocationResponse(MessageCode messageCode) {
    super(messageCode);
  }
}
