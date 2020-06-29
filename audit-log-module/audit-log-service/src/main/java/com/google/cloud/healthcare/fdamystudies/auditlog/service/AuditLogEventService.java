package com.google.cloud.healthcare.fdamystudies.auditlog.service;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventResponse;

public interface AuditLogEventService {

  public AuditLogEventResponse saveAuditLogEvent(AuditLogEventRequest aleRequest);
}
