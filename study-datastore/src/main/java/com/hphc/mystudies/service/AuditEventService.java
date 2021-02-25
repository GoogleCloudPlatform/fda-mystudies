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
import java.util.Collections;
import java.util.Map;
import org.apache.log4j.Logger;

public class AuditEventService {

  private static final Logger LOGGER = Logger.getLogger(AuditEventService.class);

  private static final String AUDIT_LOG_NAME = "application-audit-log";

  public void postAuditLogEvent(AuditLogEventRequest auditRequest) {
    LOGGER.debug(
        String.format("begin postAuditLogEvent() for %s event", auditRequest.getEventCode()));
    try (Logging logging = LoggingOptions.getDefaultInstance().getService()) {

      JsonNode requestBody = getObjectMapper().convertValue(auditRequest, JsonNode.class);

      // The data to write to the log
      Map<String, Object> jsonPayloadMap = getObjectMapper().convertValue(auditRequest, Map.class);

      LogEntry entry =
          LogEntry.newBuilder(Payload.JsonPayload.of(jsonPayloadMap))
              .setTimestamp(auditRequest.getOccurred().getTime())
              .setSeverity(Severity.INFO)
              .setLogName(AUDIT_LOG_NAME)
              .setResource(MonitoredResource.newBuilder("global").build())
              .build();

      // Writes the log entry asynchronously
      logging.write(Collections.singleton(entry));

    } catch (Exception e) {
      LOGGER.error(String.format("%s failed with an exception", auditRequest.getEventCode()), e);
    }
    LOGGER.debug(
        String.format("postAuditLogEvent() for %s event finished", auditRequest.getEventCode()));
  }

  private static ObjectMapper getObjectMapper() {
    return new ObjectMapper();
  }
}
