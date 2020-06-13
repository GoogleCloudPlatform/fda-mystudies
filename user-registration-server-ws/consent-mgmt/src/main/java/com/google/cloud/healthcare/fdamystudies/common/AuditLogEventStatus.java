package com.google.cloud.healthcare.fdamystudies.common;

public enum AuditLogEventStatus {
  RECORDED_AT_AUDIT_LOG_SERVICE(1),
  NOT_RECORDED_AT_AUDIT_LOG_SERVICE(0);

  private int status = 0;

  private AuditLogEventStatus(int status) {
    this.status = status;
  }

  public int getStatus() {
    return status;
  }
}
