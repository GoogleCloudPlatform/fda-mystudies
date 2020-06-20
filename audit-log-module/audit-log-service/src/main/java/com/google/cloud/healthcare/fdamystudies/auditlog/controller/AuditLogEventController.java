/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.auditlog.controller;

import javax.servlet.http.HttpServletRequest;
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
import com.fasterxml.jackson.databind.JsonNode;
import com.google.cloud.healthcare.fdamystudies.auditlog.service.AuditLogEventService;
import com.google.cloud.healthcare.fdamystudies.auditlog.validator.AuditLogEventValidator;
import com.google.cloud.healthcare.fdamystudies.common.JsonUtils;

@RestController
@RequestMapping("/v1")
public class AuditLogEventController {

  private XLogger logger = XLoggerFactory.getXLogger(AuditLogEventController.class.getName());

  private static final String ERROR_DESCRIPTION = "error_description";

  @Autowired private AuditLogEventService aleService;

  @Autowired private AuditLogEventValidator validator;

  @PostMapping(
      value = "/events",
      produces = {MediaType.APPLICATION_JSON_VALUE},
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<JsonNode> logEvent(
      @RequestBody JsonNode requestBody, HttpServletRequest request) {
    logger.entry(
        String.format(
            "begin %s request with requestBody= %s", request.getRequestURI(), requestBody));

    JsonNode validationResult = validator.validateJson(requestBody);
    if (validationResult != null && validationResult.has(ERROR_DESCRIPTION)) {
      logger.exit(String.format("validation errors=%s", validationResult));
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationResult);
    }

    long id = aleService.saveAuditLogEvent(requestBody);

    JsonNode response = JsonUtils.getObjectNode().put("event_id", id);
    logger.exit(String.format("response=%s", response));
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}
