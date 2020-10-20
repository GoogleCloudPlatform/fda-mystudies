package com.hphc.mystudies.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.MonitoredResource;
import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Logging;
import com.google.cloud.logging.LoggingOptions;
import com.google.cloud.logging.Payload;
import com.google.cloud.logging.Severity;
import com.hphc.mystudies.bean.AuditLogEventRequest;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.Map;

public class AuditEventService {

  private static final Logger LOGGER = Logger.getLogger(AuditEventService.class);

  private static final String AUDIT_LOG_NAME = "application-audit-log";
  public void postAuditLogEvent(AuditLogEventRequest auditRequest) {
    LOGGER.debug(
            String.format("begin postAuditLogEvent() for %s event", auditRequest.getEventCode()));

    JsonNode requestBody = getObjectMapper().convertValue(auditRequest, JsonNode.class);
    Logging logging = LoggingOptions.getDefaultInstance().getService();

    // The data to write to the log
    Map<String, Object> jsonPayloadMap = getObjectMapper().convertValue(auditRequest, Map.class);

    LogEntry entry = LogEntry.newBuilder(Payload.JsonPayload.of(jsonPayloadMap))
            .setTimestamp(auditRequest.getOccurred().getTime())
            .setSeverity(Severity.INFO)
            .setLogName(AUDIT_LOG_NAME)
            .setResource(MonitoredResource.newBuilder("global").build())
            .build();
    // Writes the log entry asynchronously
    logging.write(Collections.singleton(entry));

    LOGGER.debug(String.format("postAuditLogEvent() for %s event finished", auditRequest.getEventCode()));
  }

  private static ObjectMapper getObjectMapper() {
    return new ObjectMapper();
  }
}
