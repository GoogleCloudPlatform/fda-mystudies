/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.auditlog.controller;

import com.google.cloud.healthcare.fdamystudies.auditlog.config.AppPropConfig;
import com.google.cloud.healthcare.fdamystudies.auditlog.service.AuditLogEventService;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventResponse;
import com.google.cloud.healthcare.fdamystudies.service.AuditEventService;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuditLogEventController {

  private XLogger logger = XLoggerFactory.getXLogger(AuditLogEventController.class.getName());

  @Autowired private AuditLogEventService auditService;

  @Autowired private AuditEventService commonAuditService;

  @Autowired private AppPropConfig appPropConfig;

  @PostMapping(
      value = "/events",
      produces = {MediaType.APPLICATION_JSON_VALUE},
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<?> logEvent(
      @Valid @RequestBody AuditLogEventRequest auditRequest, HttpServletRequest request) {
    logger.entry(String.format("begin %s request", request.getRequestURI()));

    if (StringUtils.equalsIgnoreCase(appPropConfig.getAuditStorage(), "stackdriver")) {
      commonAuditService.postAuditLogEvent(auditRequest);
      logger.exit(
          String.format(
              "%s event posted to %s ",
              auditRequest.getEventCode(), appPropConfig.getAuditStorage()));
      return ResponseEntity.status(HttpStatus.OK).build();
    } else {
      AuditLogEventResponse auditResponse = auditService.saveAuditLogEvent(auditRequest);
      logger.exit(
          String.format(
              "%s event saved, eventId=%s",
              auditRequest.getEventCode(), auditResponse.getEventId()));
      return ResponseEntity.status(HttpStatus.CREATED).body(auditResponse);
    }
  }
}
