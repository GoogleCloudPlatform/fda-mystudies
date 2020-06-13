package com.google.cloud.healthcare.fdamystudies.service;

import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.cloud.healthcare.fdamystudies.common.AuditLogEvents;

public interface AuditLogService {

  public ResponseEntity<JsonNode> logAuditEvent(AuditLogEvents eventEnum, JsonNode params);

  public void resendLogAuditEvents();
}
