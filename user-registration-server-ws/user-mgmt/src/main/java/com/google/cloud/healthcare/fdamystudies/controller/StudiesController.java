/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

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
import com.google.cloud.healthcare.fdamystudies.bean.StudyMetadataBean;
import com.google.cloud.healthcare.fdamystudies.beans.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.beans.NotificationForm;
import com.google.cloud.healthcare.fdamystudies.service.StudiesServices;
import com.google.cloud.healthcare.fdamystudies.util.ErrorCode;

@RestController
@Validated
@RequestMapping("/studies")
public class StudiesController {
  private static Logger logger = LoggerFactory.getLogger(StudiesController.class);

  @Autowired private StudiesServices studiesServices;

  @PostMapping("/studymetadata")
  public ResponseEntity<?> addUpdateStudyMetadata(
      @Valid @RequestBody StudyMetadataBean studyMetadataBean) {
    logger.info("StudiesController - addUpdateStudyMetadata() : starts");
    ErrorBean errorBean;
    try {
      errorBean = studiesServices.saveStudyMetadata(studyMetadataBean);
      if (errorBean.getCode() != ErrorCode.EC_200.code()) {
        return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
      }

    } catch (Exception e) {
      logger.error("StudiesController - addUpdateStudyMetadata() : error ", e);
      errorBean = new ErrorBean(ErrorCode.EC_500.code(), ErrorCode.EC_500.errorMessage());
      return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
    }
    logger.info("StudiesController - getStudyParticipants() : ends");
    return new ResponseEntity<>(errorBean, HttpStatus.OK);
  }

  @PostMapping("/sendNotification")
  public ResponseEntity<?> SendNotification(@Valid @RequestBody NotificationForm notificationForm) {
    logger.info("StudiesController - SendNotification() : starts");
    ErrorBean errorBean = null;
    try {
      errorBean = studiesServices.SendNotificationAction(notificationForm);
      if (errorBean.getCode() != ErrorCode.EC_200.code()) {
        return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      logger.error("StudiesController - SendNotification() : error", e);
      errorBean = new ErrorBean(ErrorCode.EC_500.code(), ErrorCode.EC_500.errorMessage());
      return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
    }
    logger.info("StudiesController - SendNotification() : ends");
    errorBean = new ErrorBean(ErrorCode.EC_200.code(), ErrorCode.EC_200.errorMessage());
    return new ResponseEntity<>(errorBean, HttpStatus.OK);
  }
}
