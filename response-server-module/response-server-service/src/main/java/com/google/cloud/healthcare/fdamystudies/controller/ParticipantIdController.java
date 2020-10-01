/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.PARTICIPANT_ID_GENERATED;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.PARTICIPANT_ID_GENERATION_FAILED;

import com.google.cloud.healthcare.fdamystudies.bean.EnrollmentTokenIdentifierBean;
import com.google.cloud.healthcare.fdamystudies.bean.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.common.ResponseServerAuditLogHelper;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.response.model.ParticipantBo;
import com.google.cloud.healthcare.fdamystudies.service.ParticipantService;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.AppUtil;
import com.google.cloud.healthcare.fdamystudies.utils.ErrorCode;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ParticipantIdController {

  @Autowired private ParticipantService participantService;

  @Autowired private ResponseServerAuditLogHelper responseServerAuditLogHelper;

  private static final Logger logger = LoggerFactory.getLogger(ParticipantIdController.class);

  @PostMapping("/participant/add")
  public ResponseEntity<?> addParticipantIdentifier(
      @RequestHeader("applicationId") String applicationId,
      @RequestBody EnrollmentTokenIdentifierBean enrollmentTokenIdentifierBean,
      HttpServletRequest request) {
    logger.info("ParticipantIdController addParticipantIdentifier() - starts ");
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    auditRequest.setAppId(applicationId);

    if (enrollmentTokenIdentifierBean == null
        || StringUtils.isBlank(enrollmentTokenIdentifierBean.getTokenIdentifier())
        || StringUtils.isBlank(enrollmentTokenIdentifierBean.getCustomStudyId())) {
      logger.info("ParticipantIdController addParticipantIdentifier() Inside Error");
      ErrorBean errorBean =
          AppUtil.dynamicResponse(
              ErrorCode.EC_701.code(),
              ErrorCode.EC_701.errorMessage(),
              AppConstants.ERROR_STR,
              ErrorCode.EC_701.errorMessage());
      return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
    }
    try {
      ParticipantBo participantBo = new ParticipantBo();
      participantBo.setTokenIdentifier(enrollmentTokenIdentifierBean.getTokenIdentifier());
      participantBo.setStudyId(enrollmentTokenIdentifierBean.getCustomStudyId());
      participantBo.setCreatedBy(applicationId);
      String particpantUniqueIdentifier = participantService.saveParticipant(participantBo);

      responseServerAuditLogHelper.logEvent(PARTICIPANT_ID_GENERATED, auditRequest);
      logger.info("ParticipantIdController addParticipantIdentifier() - Ends ");
      return new ResponseEntity<>(particpantUniqueIdentifier, HttpStatus.OK);
    } catch (Exception e) {
      ErrorBean errorBean =
          AppUtil.dynamicResponse(
              ErrorCode.EC_703.code(),
              ErrorCode.EC_703.errorMessage(),
              AppConstants.ERROR_STR,
              e.getMessage());

      responseServerAuditLogHelper.logEvent(PARTICIPANT_ID_GENERATION_FAILED, auditRequest);
      logger.error("Could not create participant identifier: ");
      return new ResponseEntity<>(errorBean, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
