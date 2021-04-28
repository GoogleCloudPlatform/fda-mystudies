/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.STUDY_METADATA_RECEIVED;

import com.google.cloud.healthcare.fdamystudies.bean.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.bean.StudyMetadataBean;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.common.ResponseServerAuditLogHelper;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.service.StudyMetadataService;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.AppUtil;
import com.google.cloud.healthcare.fdamystudies.utils.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.utils.ProcessResponseException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Api(
    tags = "Study metadata",
    value = "Study metadata",
    description =
        "Operations pertaining to study metadata, once study is published from study builder")
@RestController
public class StudyMetadataController {
  @Autowired private StudyMetadataService studyMetadataService;

  @Autowired private ResponseServerAuditLogHelper responseServerAuditLogHelper;

  private static final String BEGIN_REQUEST_LOG = "%s request";

  private static final String STATUS_LOG = "status=%d";

  private XLogger logger = XLoggerFactory.getXLogger(StudyMetadataController.class.getName());

  @ApiOperation(
      value =
          "Add or update study metadata in response datastore when a study is published from study builder")
  @PostMapping("/studymetadata")
  public ResponseEntity<?> addUpdateStudyMetadata(
      @RequestBody StudyMetadataBean studyMetadataBean, HttpServletRequest request)
      throws ProcessResponseException, IllegalAccessException, IllegalArgumentException,
          InvocationTargetException, IntrospectionException {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    String studyIdToUpdate = null;
    studyIdToUpdate = studyMetadataBean.getStudyId();
    if (StringUtils.isBlank(studyIdToUpdate)
        || StringUtils.isBlank(studyMetadataBean.getStudyVersion())
        || StringUtils.isBlank(studyMetadataBean.getAppId())) {
      ErrorBean errorBean =
          AppUtil.dynamicResponse(
              ErrorCode.EC_701.code(),
              ErrorCode.EC_701.errorMessage(),
              AppConstants.ERROR_STR,
              ErrorCode.EC_701.errorMessage());
      return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
    }
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    auditRequest.setAppId(studyMetadataBean.getAppId());
    auditRequest.setStudyId(studyMetadataBean.getStudyId());
    auditRequest.setStudyVersion(studyMetadataBean.getStudyVersion());

    studyMetadataService.saveStudyMetadata(studyMetadataBean);
    responseServerAuditLogHelper.logEvent(STUDY_METADATA_RECEIVED, auditRequest);
    logger.exit(String.format(STATUS_LOG, HttpStatus.OK.value()));
    return new ResponseEntity<String>(HttpStatus.OK);
  }
}
