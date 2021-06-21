/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020-2021 Google LLC
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
 * HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.fdahpstudydesigner.service;

import static com.fdahpstudydesigner.common.JsonUtils.getObjectMapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fdahpstudydesigner.bean.AuditLogEventRequest;
import com.google.cloud.MonitoredResource;
import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Logging;
import com.google.cloud.logging.LoggingOptions;
import com.google.cloud.logging.Payload;
import com.google.cloud.logging.Severity;
import java.util.Collections;
import java.util.Map;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuditEventServiceImpl implements AuditEventService {

  private static XLogger logger = XLoggerFactory.getXLogger(AuditEventServiceImpl.class.getName());

  private static final String AUDIT_LOG_NAME = "application-audit-log";

  @Override
  public void postAuditLogEvent(AuditLogEventRequest auditRequest) {
    logger.entry(
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

    logger.exit(
        String.format("postAuditLogEvent() for %s event finished", auditRequest.getEventCode()));
  }
}
