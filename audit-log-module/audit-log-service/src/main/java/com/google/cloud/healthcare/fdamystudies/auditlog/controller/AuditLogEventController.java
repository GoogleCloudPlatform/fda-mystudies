/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.auditlog.controller;

import com.google.cloud.healthcare.fdamystudies.auditlog.service.AuditLogEventService;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventResponse;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class AuditLogEventController {

  private XLogger logger = XLoggerFactory.getXLogger(AuditLogEventController.class.getName());

  @Autowired private AuditLogEventService aleService;

  @PostMapping(
      value = "/events",
      produces = {MediaType.APPLICATION_JSON_VALUE},
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<AuditLogEventResponse> logEvent(
      @Valid @RequestBody AuditLogEventRequest aleRequest, HttpServletRequest request) {
    logger.entry(String.format("begin %s request", request.getRequestURI()));

    AuditLogEventResponse response = aleService.saveAuditLogEvent(aleRequest);

    logger.exit(String.format("eventId=%s", response.getEventId()));
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}
