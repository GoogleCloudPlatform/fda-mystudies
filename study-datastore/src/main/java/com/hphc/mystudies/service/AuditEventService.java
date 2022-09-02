/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

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
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class AuditEventService {

  private static final XLogger LOGGER =
      XLoggerFactory.getXLogger(AuditEventService.class.getName());

  private static final String AUDIT_LOG_NAME = "application-audit-log";

  public void postAuditLogEvent(AuditLogEventRequest auditRequest) {
    LOGGER.entry(
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
    LOGGER.exit(
        String.format("postAuditLogEvent() for %s event finished", auditRequest.getEventCode()));
  }

  private static ObjectMapper getObjectMapper() {
    return new ObjectMapper();
  }
}
