/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.controller;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.USER_ID_HEADER;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ConsentDocumentResponse;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.service.ConsentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Api(
    tags = "Get Consent Document",
    value = "Consent related api",
    description = "Operations pertaining to download consent document in participant manager")
@RestController
public class ConsentController {

  private XLogger logger = XLoggerFactory.getXLogger(ConsentController.class.getName());

  @Autowired private ConsentService consentService;

  @ApiOperation(value = "fetch consent document")
  @GetMapping("/consents/{consentId}/consentDocument")
  public ResponseEntity<ConsentDocumentResponse> getConsentDocument(
      @PathVariable String consentId,
      @RequestHeader(name = USER_ID_HEADER) String userId,
      HttpServletRequest request) {
    logger.entry("%s request", request.getRequestURI());
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    ConsentDocumentResponse consentDocument =
        consentService.getConsentDocument(consentId, userId, auditRequest);

    logger.exit(String.format("status=%d", consentDocument.getHttpStatusCode()));
    return ResponseEntity.status(consentDocument.getHttpStatusCode()).body(consentDocument);
  }
}
