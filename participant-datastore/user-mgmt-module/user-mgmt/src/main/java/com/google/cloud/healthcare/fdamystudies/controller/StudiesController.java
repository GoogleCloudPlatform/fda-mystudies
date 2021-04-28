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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(
    tags = "Studies",
    value = "Studies",
    description = "Operations pertaining to Studies in user management service")
@RestController
@Validated
@RequestMapping("/studies")
public class StudiesController {
  private XLogger logger = XLoggerFactory.getXLogger(StudiesController.class.getName());

  private static final String STATUS_LOG = "status=%d";

  private static final String BEGIN_REQUEST_LOG = "%s request";

  @Autowired private StudiesServices studiesServices;

  @Autowired private UserMgmntAuditHelper userMgmntAuditHelper;

  @ApiOperation(value = "Add or update studymetadata")
  @PostMapping("/studymetadata")
  public ResponseEntity<?> addUpdateStudyMetadata(
      @Valid @RequestBody StudyMetadataBean studyMetadataBean, HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    auditRequest.setStudyId(studyMetadataBean.getStudyId());
    auditRequest.setStudyVersion(studyMetadataBean.getStudyVersion());
    auditRequest.setAppId(studyMetadataBean.getAppId());

    ErrorBean errorBean = studiesServices.saveStudyMetadata(studyMetadataBean);
    if (errorBean.getCode() != ErrorCode.EC_200.code()) {
      return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
    }

    userMgmntAuditHelper.logEvent(STUDY_METADATA_RECEIVED, auditRequest);

    logger.exit(String.format(STATUS_LOG, errorBean.getCode()));
    return new ResponseEntity<>(errorBean, HttpStatus.OK);
  }

  @ApiOperation(value = "Send Notification")
  @PostMapping("/sendNotification")
  public ResponseEntity<?> SendNotification(
      @Valid @RequestBody NotificationForm notificationForm, HttpServletRequest request)
      throws IOException {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
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
    logger.exit(String.format(STATUS_LOG, errorBean.getCode()));
    return new ResponseEntity<>(errorBean, HttpStatus.OK);
  }
}
