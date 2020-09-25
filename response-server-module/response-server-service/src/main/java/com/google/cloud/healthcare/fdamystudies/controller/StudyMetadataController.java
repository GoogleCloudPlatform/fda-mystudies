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
import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StudyMetadataController {
  @Autowired private StudyMetadataService studyMetadataService;

  @Autowired private ResponseServerAuditLogHelper responseServerAuditLogHelper;

  private static final Logger logger = LoggerFactory.getLogger(StudyMetadataController.class);

  @PostMapping("/studymetadata")
  public ResponseEntity<?> addUpdateStudyMetadata(
      @RequestBody StudyMetadataBean studyMetadataBean, HttpServletRequest request)
      throws ProcessResponseException, IllegalAccessException, IllegalArgumentException,
          InvocationTargetException, IntrospectionException {
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

    studyMetadataService.saveStudyMetadata(studyMetadataBean);
    responseServerAuditLogHelper.logEvent(STUDY_METADATA_RECEIVED, auditRequest);
    return new ResponseEntity<String>(HttpStatus.OK);
  }
}
