/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.controller;

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
import com.google.cloud.healthcare.fdamystudies.bean.EnrollmentTokenIdentifierBean;
import com.google.cloud.healthcare.fdamystudies.bean.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantBo;
import com.google.cloud.healthcare.fdamystudies.service.CommonService;
import com.google.cloud.healthcare.fdamystudies.service.ParticipantService;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.AppUtil;
import com.google.cloud.healthcare.fdamystudies.utils.ErrorCode;

@RestController
public class ParticipantIdController {

  @Autowired private ParticipantService participantService;

  @Autowired private CommonService commonService;

  private static final Logger logger = LoggerFactory.getLogger(ParticipantIdController.class);

  @PostMapping("/participant/add")
  public ResponseEntity<?> addParticipantIdentifier(
      @RequestHeader("applicationId") String applicationId,
      @RequestBody EnrollmentTokenIdentifierBean enrollmentTokenIdentifierBean,
      @RequestHeader String clientId) {
    logger.info("ParticipantIdController addParticipantIdentifier() - starts ");
    String tokenIdentifier = null;
    String customStudyId = null;
    if (enrollmentTokenIdentifierBean != null) {
      tokenIdentifier = enrollmentTokenIdentifierBean.getTokenIdentifier();
      customStudyId = enrollmentTokenIdentifierBean.getCustomStudyId();
    }
    if (StringUtils.isBlank(tokenIdentifier) || StringUtils.isBlank(customStudyId)) {
      logger.error("ParticipantIdController addParticipantIdentifier() - Error");
      commonService.createAuditLog(
          null,
          "Participant ID generation failure",
          "Participant ID could not be generated",
          AppConstants.CLIENT_ID_PART_DATA_STORE,
          null,
          customStudyId,
          null);
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
      commonService.createAuditLog(
          null,
          "Participant ID generated",
          "Participant ID " + particpantUniqueIdentifier + " generated",
          AppConstants.CLIENT_ID_PART_DATA_STORE,
          particpantUniqueIdentifier,
          enrollmentTokenIdentifierBean.getCustomStudyId(),
          null);
      logger.info("ParticipantIdController addParticipantIdentifier() - Ends ");
      return new ResponseEntity<>(particpantUniqueIdentifier, HttpStatus.OK);
    } catch (Exception e) {
      commonService.createAuditLog(
          null,
          "Participant ID generation failure",
          "Participant ID could not be generated",
          AppConstants.CLIENT_ID_PART_DATA_STORE,
          null,
          enrollmentTokenIdentifierBean.getCustomStudyId(),
          null);
      ErrorBean errorBean =
          AppUtil.dynamicResponse(
              ErrorCode.EC_703.code(),
              ErrorCode.EC_703.errorMessage(),
              AppConstants.ERROR_STR,
              e.getMessage());
      logger.error("Could not create participant identifier: ");
      return new ResponseEntity<>(errorBean, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
