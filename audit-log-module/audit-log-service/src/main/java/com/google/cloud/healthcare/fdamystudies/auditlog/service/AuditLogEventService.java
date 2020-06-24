package com.google.cloud.healthcare.fdamystudies.auditlog.service;

import com.google.cloud.healthcare.fdamystudies.auditlog.beans.AuditLogEventResponse;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;

public interface AuditLogEventService {

  public AuditLogEventResponse saveAuditLogEvent(AuditLogEventRequest aleRequest);
}
