/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.controller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.cloud.healthcare.fdamystudies.bean.DashboardBean;
import com.google.cloud.healthcare.fdamystudies.bean.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.bean.ParticipantRegistryResponseBean;
import com.google.cloud.healthcare.fdamystudies.exception.InvalidUserIdException;
import com.google.cloud.healthcare.fdamystudies.service.StudyService;
import com.google.cloud.healthcare.fdamystudies.utils.AppUtil;
import com.google.cloud.healthcare.fdamystudies.utils.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.utils.URWebAppWSConstants;

@RestController
@RequestMapping("/studies")
public class StudiesController {
  private static Logger logger = LoggerFactory.getLogger(StudiesController.class);

  @Autowired private StudyService studyService;

  @GetMapping(value = "")
  public ResponseEntity<?> getStudies(@RequestHeader("userId") Integer userId) {

    logger.info("SiteController - getStudies(): starts");
    DashboardBean dashboardBean;
    ErrorBean errorBean = null;
    if (null != userId && userId != 0) {
      try {
        dashboardBean = studyService.getStudies(userId);
        if (dashboardBean.getError().getApp_error_code() == ErrorCode.EC_200.code()) {
          return new ResponseEntity<>(dashboardBean.getStudies(), HttpStatus.OK);
        } else if (dashboardBean.getError().getApp_error_code() == ErrorCode.EC_816.code()) {
          return new ResponseEntity<>(dashboardBean.getError(), HttpStatus.BAD_REQUEST);
        } else if (dashboardBean.getError().getApp_error_code() == ErrorCode.EC_500.code()) {
          return new ResponseEntity<>(dashboardBean.getError(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
      } catch (Exception e) {
        logger.info("SiteController - getStudies() : error", e);
        errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_500.code(),
                ErrorCode.EC_500.errorMessage(),
                URWebAppWSConstants.ERROR,
                ErrorCode.EC_500.errorMessage());
        return new ResponseEntity<>(errorBean, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } else {
      errorBean =
          AppUtil.dynamicResponse(
              ErrorCode.EC_777.code(),
              ErrorCode.EC_777.errorMessage(),
              URWebAppWSConstants.ERROR,
              ErrorCode.EC_777.errorMessage());
      return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
    }
    logger.info("SiteController - getStudies() : ends");

    return new ResponseEntity<>(dashboardBean.getStudies(), HttpStatus.OK);
  }

  @GetMapping(
      value = "{studyId}/participants",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getStudyParticipants(
      @RequestHeader("userId") String userId, @PathVariable("studyId") String studyId) {

    logger.info("StudiesController - getStudyParticipants() : starts");
    ParticipantRegistryResponseBean respBean = null;
    ErrorBean errorBean = null;

    if (((studyId.length() != 0) || (!StringUtils.isEmpty(studyId)))
        && ((userId.length() != 0) || (!StringUtils.isEmpty(userId)))) {
      try {
        respBean =
            studyService.getStudyParticipants(Integer.parseInt(userId), Integer.parseInt(studyId));

        if (respBean != null) {
          logger.info("StudiesController - getStudyParticipants() : ends successfully");
          return new ResponseEntity<>(respBean, HttpStatus.OK);
        } else {
          errorBean =
              AppUtil.dynamicResponse(
                  ErrorCode.EC_816.code(),
                  ErrorCode.EC_816.errorMessage(),
                  URWebAppWSConstants.ERROR,
                  ErrorCode.EC_816.errorMessage());
          logger.info(
              "StudiesController - getStudyParticipants() : ends with UNAUTHORIZED request");
          return new ResponseEntity<>(errorBean, HttpStatus.UNAUTHORIZED);
        }
      } catch (InvalidUserIdException e) {
        errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_816.code(),
                ErrorCode.EC_816.errorMessage(),
                URWebAppWSConstants.ERROR,
                ErrorCode.EC_816.errorMessage());
        logger.error("StudiesController - getStudyParticipants() : error ", e);
        return new ResponseEntity<>(errorBean, HttpStatus.UNAUTHORIZED);
      } catch (Exception e) {
        errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_500.code(),
                ErrorCode.EC_500.errorMessage(),
                URWebAppWSConstants.ERROR,
                ErrorCode.EC_500.errorMessage());
        logger.error("StudiesController - getStudyParticipants() : error ", e);
        return new ResponseEntity<>(errorBean, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } else {
      errorBean =
          AppUtil.dynamicResponse(
              ErrorCode.EC_777.code(),
              ErrorCode.EC_777.errorMessage(),
              URWebAppWSConstants.ERROR,
              ErrorCode.EC_777.errorMessage());
      logger.error("StudiesController - getStudyParticipants() : ends with BAD_REQUEST");
      return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
    }
  }
}
