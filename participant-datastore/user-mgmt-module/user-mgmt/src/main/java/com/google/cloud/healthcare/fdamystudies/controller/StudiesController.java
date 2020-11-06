/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.NOTIFICATION_METADATA_RECEIVED;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.STUDY_METADATA_RECEIVED;

import com.google.cloud.healthcare.fdamystudies.bean.StudyMetadataBean;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.beans.NotificationForm;
import com.google.cloud.healthcare.fdamystudies.common.UserMgmntAuditHelper;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.service.StudiesServices;
import com.google.cloud.healthcare.fdamystudies.util.ErrorCode;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/studies")
public class StudiesController {
  private static Logger logger = LoggerFactory.getLogger(StudiesController.class);

  @Autowired private StudiesServices studiesServices;

  @Autowired private UserMgmntAuditHelper userMgmntAuditHelper;

  @PostMapping("/studymetadata")
  public ResponseEntity<?> addUpdateStudyMetadata(
      @Valid @RequestBody StudyMetadataBean studyMetadataBean, HttpServletRequest request) {
    logger.info("StudiesController - addUpdateStudyMetadata() : starts");
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    auditRequest.setStudyId(studyMetadataBean.getStudyId());
    auditRequest.setStudyVersion(studyMetadataBean.getStudyVersion());
    auditRequest.setAppId(studyMetadataBean.getAppId());

    ErrorBean errorBean = studiesServices.saveStudyMetadata(studyMetadataBean);
    if (errorBean.getCode() != ErrorCode.EC_200.code()) {
      return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
    }

    userMgmntAuditHelper.logEvent(STUDY_METADATA_RECEIVED, auditRequest);

    logger.info("StudiesController - getStudyParticipants() : ends");
    return new ResponseEntity<>(errorBean, HttpStatus.OK);
  }

  @PostMapping("/sendNotification")
  public ResponseEntity<?> SendNotification(
      @Valid @RequestBody NotificationForm notificationForm, HttpServletRequest request)
      throws IOException {
    logger.info("StudiesController - SendNotification() : starts");
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);

    userMgmntAuditHelper.logEvent(NOTIFICATION_METADATA_RECEIVED, auditRequest);

    ErrorBean errorBean = null;

    errorBean = studiesServices.SendNotificationAction(notificationForm, auditRequest);

    if (errorBean.getCode() != ErrorCode.EC_200.code()) {
      return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
    }

    errorBean =
        new ErrorBean(
            ErrorCode.EC_200.code(), ErrorCode.EC_200.errorMessage(), errorBean.getResponse());
    logger.info("StudiesController - SendNotification() : ends");
    return new ResponseEntity<>(errorBean, HttpStatus.OK);
  }
}
