/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import com.google.cloud.healthcare.fdamystudies.bean.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.bean.StudyMetadataBean;
import com.google.cloud.healthcare.fdamystudies.service.CommonService;
import com.google.cloud.healthcare.fdamystudies.service.StudyMetadataService;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.AppUtil;
import com.google.cloud.healthcare.fdamystudies.utils.ErrorCode;
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

  @Autowired private CommonService commonService;

  private static final Logger logger = LoggerFactory.getLogger(StudyMetadataController.class);

  @PostMapping("/studymetadata")
  public ResponseEntity<?> addUpdateStudyMetadata(
      @RequestBody StudyMetadataBean studyMetadataBean) {
    String studyIdToUpdate = null;
    try {
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

      studyMetadataService.saveStudyMetadata(studyMetadataBean);
      commonService.createActivityLog(
          null,
          "Study metadata updated successfully",
          "Study metadata successful for study with id: " + studyIdToUpdate + " .");
      return new ResponseEntity<String>(HttpStatus.OK);
    } catch (Exception e) {
      commonService.createActivityLog(
          null,
          "Study metadata update failed",
          "Study metadata update failed for study with id: " + studyIdToUpdate + " .");
      ErrorBean errorBean =
          AppUtil.dynamicResponse(
              ErrorCode.EC_702.code(),
              ErrorCode.EC_702.errorMessage(),
              AppConstants.ERROR_STR,
              e.getMessage());

      logger.error("Could not create/update Study Metadata for study: " + studyIdToUpdate);
      return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
    }
  }
}
