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

@Setter
@Getter
@ToString
public class AuditLogEventResponse extends BaseResponse {

  private String eventId;

  public AuditLogEventResponse() {
    super();
  }

  public AuditLogEventResponse(ErrorCode errorCode) {
    super(errorCode);
  }

  public AuditLogEventResponse(HttpStatus httpStatus, String message) {
    super(httpStatus, message);
  }

  public AuditLogEventResponse(String eventId, HttpStatus httpStatus, String message) {
    super(httpStatus, message);
    this.eventId = eventId;
  }
}
