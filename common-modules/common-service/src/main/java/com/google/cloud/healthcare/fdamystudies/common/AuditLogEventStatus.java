/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

public enum AuditLogEventStatus {
  RECORDED_AT_CENTRAL_AUDIT_LOG(1),
  NOT_RECORDED_AT_CENTRAL_AUDIT_LOG(0);

  private int status;

  private AuditLogEventStatus(int status) {
    this.status = status;
  }

  public int getStatus() {
    return status;
  }
}
