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
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantRegistryResponse;
import com.google.cloud.healthcare.fdamystudies.beans.StudyResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateTargetEnrollmentRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateTargetEnrollmentResponse;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.service.SiteService;
import com.google.cloud.healthcare.fdamystudies.service.StudyService;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/studies")
public class StudyController {
  private XLogger logger = XLoggerFactory.getXLogger(StudyController.class.getName());

  private static final String STATUS_LOG = "status=%d";

  private static final String BEGIN_REQUEST_LOG = "%s request";

  @Autowired private StudyService studyService;

  @Autowired private SiteService siteService;

  @GetMapping
  public ResponseEntity<StudyResponse> getStudies(
      @RequestHeader(name = USER_ID_HEADER) String userId, HttpServletRequest request) {
    logger.entry(BEGIN_REQUEST_LOG, request.getRequestURI());
    StudyResponse studyResponse = studyService.getStudies(userId);
    logger.exit(String.format(STATUS_LOG, studyResponse.getHttpStatusCode()));
    return ResponseEntity.status(studyResponse.getHttpStatusCode()).body(studyResponse);
  }

  @GetMapping(
      value = "{studyId}/participants",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ParticipantRegistryResponse> getStudyParticipants(
      @RequestHeader(name = USER_ID_HEADER) String userId,
      @PathVariable String studyId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int limit,
      HttpServletRequest request) {
    logger.entry(BEGIN_REQUEST_LOG, request.getRequestURI());
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);

    ParticipantRegistryResponse participantRegistryResponse =
        studyService.getStudyParticipants(userId, studyId, auditRequest, page, limit);
    logger.exit(String.format(STATUS_LOG, participantRegistryResponse.getHttpStatusCode()));
    return ResponseEntity.status(participantRegistryResponse.getHttpStatusCode())
        .body(participantRegistryResponse);
  }

  @PatchMapping(
      value = "/{studyId}/targetEnrollment",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UpdateTargetEnrollmentResponse> updateTargetEnrollment(
      @RequestHeader(name = USER_ID_HEADER) String userId,
      @PathVariable String studyId,
      @Valid @RequestBody UpdateTargetEnrollmentRequest targetEnrollmentRequest,
      HttpServletRequest request) {
    logger.entry(BEGIN_REQUEST_LOG, request.getRequestURI());
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);

    targetEnrollmentRequest.setUserId(userId);
    targetEnrollmentRequest.setStudyId(studyId);
    UpdateTargetEnrollmentResponse updateTargetEnrollmentResponse =
        siteService.updateTargetEnrollment(targetEnrollmentRequest, auditRequest);

    logger.exit(String.format(STATUS_LOG, updateTargetEnrollmentResponse.getHttpStatusCode()));
    return ResponseEntity.status(updateTargetEnrollmentResponse.getHttpStatusCode())
        .body(updateTargetEnrollmentResponse);
  }
}
