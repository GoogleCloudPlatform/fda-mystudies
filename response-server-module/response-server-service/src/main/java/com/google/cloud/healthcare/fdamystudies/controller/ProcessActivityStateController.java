/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import com.google.cloud.healthcare.fdamystudies.bean.ActivitiesBean;
import com.google.cloud.healthcare.fdamystudies.bean.ActivityStateRequestBean;
import com.google.cloud.healthcare.fdamystudies.bean.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.bean.SuccessResponseBean;
import com.google.cloud.healthcare.fdamystudies.service.CommonService;
import com.google.cloud.healthcare.fdamystudies.service.ParticipantActivityStateResponseService;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.AppUtil;
import com.google.cloud.healthcare.fdamystudies.utils.ErrorCode;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProcessActivityStateController {
  @Autowired
  private ParticipantActivityStateResponseService participantActivityStateResponseService;

  @Autowired private CommonService commonService;

  private static final Logger logger =
      LoggerFactory.getLogger(ProcessActivityStateController.class);

  @GetMapping(
      value = "/participant/get-activity-state",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getActivityState(
      @RequestParam(name = "studyId") String studyId,
      @RequestParam("participantId") String participantId) {

    if (StringUtils.isBlank(studyId) || StringUtils.isBlank(participantId)) {
      ErrorBean errorBean =
          AppUtil.dynamicResponse(
              ErrorCode.EC_701.code(),
              ErrorCode.EC_701.errorMessage(),
              AppConstants.ERROR_STR,
              ErrorCode.EC_701.errorMessage());
      logger.warn(
          "ProcessActivityStateController getActivityState() failed. studyId or participantId missing.");
      return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
    } else {
      try {
        ActivitiesBean activitiesBean =
            participantActivityStateResponseService.getParticipantActivities(
                studyId, participantId);
        return new ResponseEntity<>(activitiesBean, HttpStatus.OK);
      } catch (Exception e) {
        logger.warn("ProcessActivityStateController getActivityState() failed ", e);
        ErrorBean errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_713.code(),
                ErrorCode.EC_713.errorMessage(),
                AppConstants.ERROR_STR,
                e.getMessage());
        return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
      }
    }
  }

  @PostMapping("/participant/update-activity-state")
  public ResponseEntity<?> updateActivityState(
      @RequestBody ActivityStateRequestBean activityStateRequestBean,
      @Context HttpServletResponse response,
      @RequestHeader String userId) {

    if (activityStateRequestBean == null
        || Strings.isBlank(activityStateRequestBean.getParticipantId())
        || Strings.isBlank(activityStateRequestBean.getStudyId())) {
      ErrorBean errorBean =
          AppUtil.dynamicResponse(
              ErrorCode.EC_701.code(),
              ErrorCode.EC_701.errorMessage(),
              AppConstants.ERROR_STR,
              ErrorCode.EC_701.errorMessage());
      return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
    } else {
      try {
        participantActivityStateResponseService.saveParticipantActivities(activityStateRequestBean);
        SuccessResponseBean srBean = new SuccessResponseBean();
        srBean.setMessage(AppConstants.SUCCESS_MSG);
        String activityIds =
            activityStateRequestBean
                .getActivity()
                .stream()
                .map(s -> s.getActivityId())
                .collect(Collectors.joining(", "));
        commonService.createActivityLog(
            userId,
            "Activity State Update -success",
            "Activity state update successful for partcipant "
                + activityStateRequestBean.getParticipantId()
                + " and activityIds "
                + activityIds
                + " .");

        return new ResponseEntity<>(srBean, HttpStatus.OK);
      } catch (Exception e) {
        logger.warn("ProcessActivityStateController updateActivityState() failed ", e);
        String activityIds =
            activityStateRequestBean
                .getActivity()
                .stream()
                .map(s -> s.getActivityId())
                .collect(Collectors.joining(", "));
        commonService.createActivityLog(
            userId,
            "Activity State Update -failure",
            "Activity state update unsuccessful for partcipant "
                + activityStateRequestBean.getParticipantId()
                + " and activityIds "
                + activityIds
                + " .");
        ErrorBean errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_714.code(),
                ErrorCode.EC_714.errorMessage(),
                AppConstants.ERROR_STR,
                e.getMessage());
        return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
      }
    }
  }
}
