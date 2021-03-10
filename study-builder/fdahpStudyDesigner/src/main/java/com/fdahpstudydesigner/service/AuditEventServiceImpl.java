/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.service;

import com.fdahpstudydesigner.bean.AuditLogEventRequest;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class AuditEventServiceImpl implements AuditEventService {

  private static Logger logger = Logger.getLogger(AuditEventServiceImpl.class);

  private static final String AUDIT_LOG_NAME = "application-audit-log";

  @Override
  public void postAuditLogEvent(AuditLogEventRequest auditRequest) {
    /*logger.debug(
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
      logger.error(String.format("%s failed with an exception", auditRequest.getEventCode()), e);
    }

    logger.debug(
        String.format("postAuditLogEvent() for %s event finished", auditRequest.getEventCode()));*/
  }
}
