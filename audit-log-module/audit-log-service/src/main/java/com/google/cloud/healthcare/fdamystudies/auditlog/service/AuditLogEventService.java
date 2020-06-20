package com.google.cloud.healthcare.fdamystudies.auditlog.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface AuditLogEventService {

  public long saveAuditLogEvent(JsonNode eventParams);
}
