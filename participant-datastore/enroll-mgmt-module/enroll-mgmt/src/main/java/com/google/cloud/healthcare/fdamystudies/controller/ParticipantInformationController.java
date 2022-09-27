/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.google.cloud.healthcare.fdamystudies.common.EnrollAuditEvent.READ_OPERATION_FAILED_FOR_ENROLLMENT_STATUS;
import static com.google.cloud.healthcare.fdamystudies.common.EnrollAuditEvent.READ_OPERATION_SUCCEEDED_FOR_ENROLLMENT_STATUS;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantInfoRespBean;
import com.google.cloud.healthcare.fdamystudies.common.EnrollAuditEventHelper;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.repository.StudyRepository;
import com.google.cloud.healthcare.fdamystudies.service.ParticipantInformationService;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(
    tags = "Participant Information",
    value = "participant information related api",
    description = "Operations pertaining to get participant details")
@RestController
public class ParticipantInformationController {

  private static final XLogger logger =
      XLoggerFactory.getXLogger(ParticipantInformationController.class.getName());

  private static final String STATUS_LOG = "status=%d";

  private static final String BEGIN_REQUEST_LOG = "%s request";

  @Autowired private ParticipantInformationService participantInfoService;

  @Autowired private EnrollAuditEventHelper enrollAuditEventHelper;

  @Autowired private StudyRepository studyRepository;

  @ApiOperation(value = "fetch participant's enrollment details")
  @GetMapping(value = "/participantInfo", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getParticipantDetails(
      @RequestParam(name = "studyId") String studyId,
      @RequestParam(name = "participantId") String participantId,
      @Context HttpServletResponse response,
      @Context HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    ParticipantInfoRespBean participantInfoResp = null;

    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    Map<String, String> placeHolders = new HashMap<>();

    Optional<StudyEntity> optStudyEntity = studyRepository.findByCustomStudyId(studyId);
    if (optStudyEntity.isPresent()) {
      auditRequest.setStudyId(optStudyEntity.get().getCustomId());
      auditRequest.setStudyVersion(String.valueOf(optStudyEntity.get().getVersion()));
    }

    auditRequest.setParticipantId(participantId);
    participantInfoResp = participantInfoService.getParticipantInfoDetails(participantId, studyId);
    if (participantInfoResp != null) {
      participantInfoResp.setMessage(
          MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());
      participantInfoResp.setCode(HttpStatus.OK.value());

      placeHolders.put("enrollment_status", participantInfoResp.getEnrollment());
      enrollAuditEventHelper.logEvent(
          READ_OPERATION_SUCCEEDED_FOR_ENROLLMENT_STATUS, auditRequest, placeHolders);
    } else {
      MyStudiesUserRegUtil.getFailureResponse(
          MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.NO_DATA_AVAILABLE.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.NO_DATA_AVAILABLE.getValue(),
          response);

      enrollAuditEventHelper.logEvent(READ_OPERATION_FAILED_FOR_ENROLLMENT_STATUS, auditRequest);

      return null;
    }

    logger.exit(String.format(STATUS_LOG, participantInfoResp.getCode()));
    return new ResponseEntity<>(participantInfoResp, HttpStatus.OK);
  }
}
