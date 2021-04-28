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
import com.google.cloud.healthcare.fdamystudies.response.model.ParticipantInfoEntity;
import com.google.cloud.healthcare.fdamystudies.service.ParticipantService;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.AppUtil;
import com.google.cloud.healthcare.fdamystudies.utils.ErrorCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Api(
    tags = "Generate participant id",
    value = "Generate participant id based on study",
    description = "Generate participant id based on study")
@RestController
public class ParticipantIdController {

  @Autowired private ParticipantService participantService;

  @Autowired private ResponseServerAuditLogHelper responseServerAuditLogHelper;

  private XLogger logger = XLoggerFactory.getXLogger(ParticipantIdController.class.getName());

  private static final String STATUS_LOG = "status=%d";

  private static final String BEGIN_REQUEST_LOG = "%s request";

  @ApiOperation(value = "Generate participant id from response datastore")
  @PostMapping("/participant/add")
  public ResponseEntity<?> addParticipantIdentifier(
      @RequestHeader("appId") String applicationId,
      @RequestBody EnrollmentTokenIdentifierBean enrollmentTokenIdentifierBean,
      HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);

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
      ParticipantInfoEntity participantBo = new ParticipantInfoEntity();
      participantBo.setTokenId(enrollmentTokenIdentifierBean.getTokenIdentifier());
      participantBo.setStudyId(enrollmentTokenIdentifierBean.getCustomStudyId());
      participantBo.setCreatedBy(applicationId);
      String particpantUniqueIdentifier = participantService.saveParticipant(participantBo);

      auditRequest.setStudyId(enrollmentTokenIdentifierBean.getCustomStudyId());
      auditRequest.setStudyVersion(enrollmentTokenIdentifierBean.getStudyVersion());
      auditRequest.setAppId(applicationId);
      auditRequest.setParticipantId(particpantUniqueIdentifier);
      responseServerAuditLogHelper.logEvent(PARTICIPANT_ID_GENERATED, auditRequest);
      logger.exit(String.format(STATUS_LOG, HttpStatus.OK.value()));
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
