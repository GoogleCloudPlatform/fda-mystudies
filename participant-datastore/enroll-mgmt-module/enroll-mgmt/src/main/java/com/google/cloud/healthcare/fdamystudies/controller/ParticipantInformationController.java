/*
 * Copyright 2020 Google LLC
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
import com.google.cloud.healthcare.fdamystudies.service.ParticipantInformationService;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ParticipantInformationController {

  private static final Logger logger =
      LoggerFactory.getLogger(ParticipantInformationController.class);

  @Autowired ParticipantInformationService participantInfoService;

  @Autowired EnrollAuditEventHelper enrollAuditEventHelper;

  @GetMapping(value = "/participantInfo", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getParticipantDetails(
      @RequestParam(name = "studyId") String studyId,
      @RequestParam(name = "participantId") String participantId,
      @Context HttpServletResponse response,
      @Context HttpServletRequest request) {
    logger.info("ParticipantInformationController getParticipantDetails() - starts ");
    ParticipantInfoRespBean participantInfoResp = null;

    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    Map<String, String> placeHolders = new HashMap<>();

    if (StringUtils.hasText(participantId) && StringUtils.hasText(studyId)) {

      auditRequest.setStudyId(studyId);
      auditRequest.setParticipantId(participantId);
      participantInfoResp =
          participantInfoService.getParticipantInfoDetails(participantId, studyId);
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
    } else {
      MyStudiesUserRegUtil.getFailureResponse(
          MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
          response);
      return null;
    }

    logger.info("ParticipantInformationController getParticipantDetails() - Ends ");
    return new ResponseEntity<>(participantInfoResp, HttpStatus.OK);
  }
}
