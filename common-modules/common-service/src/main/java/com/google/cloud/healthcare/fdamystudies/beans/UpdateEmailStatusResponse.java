/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class UpdateEmailStatusResponse extends BaseResponse {

  @ToString.Exclude private String tempRegId;

  public UpdateEmailStatusResponse() {}

  public UpdateEmailStatusResponse(ErrorCode errorCode) {
    super(errorCode);
  }

  public UpdateEmailStatusResponse(HttpStatus httpStatus, String message) {
    super(httpStatus, message);
  }
}
