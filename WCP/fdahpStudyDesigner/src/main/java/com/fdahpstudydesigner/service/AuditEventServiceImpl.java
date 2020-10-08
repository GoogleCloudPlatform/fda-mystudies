/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.service;

import static com.fdahpstudydesigner.common.JsonUtils.getObjectMapper;

import com.google.cloud.MonitoredResource;
import com.google.cloud.logging.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fdahpstudydesigner.bean.AuditLogEventRequest;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Map;


@Service
public class AuditEventServiceImpl implements AuditEventService {

  private static Logger logger = Logger.getLogger(AuditEventServiceImpl.class);

  private static final String AUDIT_LOG_NAME = "application-audit-log";

  @Override
  public void postAuditLogEvent(AuditLogEventRequest auditRequest) {
    logger.debug(
        String.format("begin postAuditLogEvent() for %s event", auditRequest.getEventCode()));

    JsonNode requestBody = getObjectMapper().convertValue(auditRequest, JsonNode.class);
    Logging logging = LoggingOptions.getDefaultInstance().getService();

    // The data to write to the log
    Map<String, Object> jsonPayloadMap = getObjectMapper().convertValue(auditRequest, Map.class);

    LogEntry entry = LogEntry.newBuilder(Payload.JsonPayload.of(jsonPayloadMap))
            .setTimestamp(auditRequest.getOccured().getTime())
            .setSeverity(Severity.INFO)
            .setLogName(AUDIT_LOG_NAME)
            .setResource(MonitoredResource.newBuilder("global").build())
            .build();
    // Writes the log entry asynchronously
    logging.write(Collections.singleton(entry));

    logger.debug(String.format("postAuditLogEvent() for %s event finished", auditRequest.getEventCode()));
  }
}
