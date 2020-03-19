/**
 * ***************************************************************************** Copyright 2020
 * Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * ****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.google.cloud.healthcare.fdamystudies.bean.ActivitiesBean;
import com.google.cloud.healthcare.fdamystudies.bean.ActivityStateRequestBean;
import com.google.cloud.healthcare.fdamystudies.bean.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.bean.SuccessResponseBean;
import com.google.cloud.healthcare.fdamystudies.service.ParticipantActivityStateResponseService;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.AppUtil;
import com.google.cloud.healthcare.fdamystudies.utils.ErrorCode;

@RestController
public class ProcessActivityStateController {
  @Autowired
  private ParticipantActivityStateResponseService participantActivityStateResponseService;

  private static final Logger logger =
      LoggerFactory.getLogger(ProcessActivityStateController.class);

  @GetMapping(
      value = "/participant/get-activity-state",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getActivityState(
      @RequestParam(name = "studyId") String studyId,
      @RequestParam("participantId") String participantId) {
    logger.info("Input values are :\n Study Id: " + studyId + "\n Particpant Id: " + participantId);
    if (StringUtils.isBlank(studyId) || StringUtils.isBlank(participantId)) {
      ErrorBean errorBean =
          AppUtil.dynamicResponse(
              ErrorCode.EC_701.code(),
              ErrorCode.EC_701.errorMessage(),
              AppConstants.ERROR_STR,
              ErrorCode.EC_701.errorMessage());
      return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
    } else {
      try {
        ActivitiesBean activitiesBean =
            participantActivityStateResponseService.getParticipantActivities(
                studyId, participantId);
        return new ResponseEntity<>(activitiesBean, HttpStatus.OK);
      } catch (Exception e) {
        ErrorBean errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_713.code(),
                ErrorCode.EC_713.errorMessage(),
                AppConstants.ERROR_STR,
                e.getMessage());
        logger.error(
            "(C)...ProcessActivityResponseController.getActivityState()...Exception "
                + e.getMessage());
        return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
      }
    }
  }

  @PostMapping("/participant/update-activity-state")
  public ResponseEntity<?> updateActivityState(
      @RequestBody ActivityStateRequestBean activityStateRequestBean,
      @Context HttpServletResponse response) {

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
        return new ResponseEntity<>(srBean, HttpStatus.OK);

      } catch (Exception e) {
        ErrorBean errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_714.code(),
                ErrorCode.EC_714.errorMessage(),
                AppConstants.ERROR_STR,
                e.getMessage());
        logger.error(
            "(C)...ProcessActivityResponseController.updateActivityState()...Exception "
                + e.getMessage());
        return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
      }
    }
    // try {
    // if (activityStateBean != null && participantId != null
    // && !StringUtils.isEmpty(participantId)) {
    // if (activityStateBean.getActivity() != null && !activityStateBean.getActivity().isEmpty()) {
    // List<ActivitiesBean> activitiesBeanList = activityStateBean.getActivity();
    // List<ParticipantActivityBean> participantActivitiesList = activityResponseProcessorService
    // .getParticipantActivitiesList(activityStateBean.getStudyId(), participantId);
    // for (int i = 0; i < activitiesBeanList.size(); i++) {
    // ActivitiesBean activitiesBean = activitiesBeanList.get(i);
    // boolean isExists = false;
    // if (participantActivitiesList != null && !participantActivitiesList.isEmpty()) {
    // for (ParticipantActivityBean participantActivities : participantActivitiesList) {
    // if (participantActivities.getActivityId()
    // .equalsIgnoreCase(activitiesBean.getActivityId())) {
    // isExists = true;
    // if (activitiesBean.getActivityVersion() != null
    // && StringUtils.isNotEmpty(activitiesBean.getActivityVersion()))
    // participantActivities.setActivityVersion(activitiesBean.getActivityVersion());
    // if (activitiesBean.getActivityState() != null
    // && StringUtils.isNotEmpty(activitiesBean.getActivityState()))
    // participantActivities.setActivityState(activitiesBean.getActivityState());
    // if (activitiesBean.getActivityRunId() != null
    // && StringUtils.isNotEmpty(activitiesBean.getActivityRunId()))
    // participantActivities.setActivityRunId(activitiesBean.getActivityRunId());
    // if (activitiesBean.getBookmarked() != null)
    // participantActivities.setBookmark(activitiesBean.getBookmarked());
    // if (activitiesBean.getActivityRun() != null) {
    // if (activitiesBean.getActivityRun().getTotal() != null)
    // participantActivities.setTotal(activitiesBean.getActivityRun().getTotal());
    // if (activitiesBean.getActivityRun().getCompleted() != null)
    // participantActivities
    // .setCompleted(activitiesBean.getActivityRun().getCompleted());
    // if (activitiesBean.getActivityRun().getMissed() != null)
    // participantActivities.setMissed(activitiesBean.getActivityRun().getMissed());
    // }
    // addParticipantActivitiesList.add(participantActivities);
    // }
    // }
    // }
    // if (!isExists) {
    // ParticipantActivityBean addParticipantActivities = new ParticipantActivityBean();
    // if (activitiesBean != null
    // && StringUtils.isNotEmpty(activitiesBean.getActivityState()))
    // addParticipantActivities.setActivityState(activitiesBean.getActivityState());
    // if (activitiesBean.getActivityVersion() != null
    // && StringUtils.isNotEmpty(activitiesBean.getActivityVersion()))
    // addParticipantActivities.setActivityVersion(activitiesBean.getActivityVersion());
    // if (activitiesBean.getActivityId() != null
    // && StringUtils.isNotEmpty(activitiesBean.getActivityId()))
    // addParticipantActivities.setActivityId(activitiesBean.getActivityId());
    // if (activitiesBean.getActivityRunId() != null
    // && StringUtils.isNotEmpty(activitiesBean.getActivityRunId()))
    // addParticipantActivities.setActivityRunId(activitiesBean.getActivityRunId());
    // if (activityStateBean.getStudyId() != null
    // && StringUtils.isNotEmpty(activityStateBean.getStudyId()))
    // addParticipantActivities.setCustomStudyId(activityStateBean.getStudyId());
    // if (userId != null && StringUtils.isNotEmpty(userId))
    // addParticipantActivities.setParticipantId(userId);
    // if (activitiesBean.getBookmarked() != null)
    // addParticipantActivities.setBookmark(activitiesBean.getBookmarked());
    // if (activitiesBean.getActivityRun() != null) {
    // if (activitiesBean.getActivityRun().getTotal() != null)
    // addParticipantActivities.setTotal(activitiesBean.getActivityRun().getTotal());
    // if (activitiesBean.getActivityRun().getCompleted() != null)
    // addParticipantActivities
    // .setCompleted(activitiesBean.getActivityRun().getCompleted());
    // if (activitiesBean.getActivityRun().getMissed() != null)
    // addParticipantActivities.setMissed(activitiesBean.getActivityRun().getMissed());
    // }
    // addParticipantActivitiesList.add(addParticipantActivities);
    // }
    // }
    // responseBean = activityResponseProcessorService
    // .saveParticipantActivities(addParticipantActivitiesList);
    // if (responseBean.getCode() == ErrorCode.EC_200.code()) {
    // responseBean.setMessage(ResponseServerUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());
    // } else if (responseBean.getCode() == ErrorCode.EC_500.code()) {
    // errorBean = new ErrorBean(HttpStatus.INTERNAL_SERVER_ERROR.value(),
    // ErrorCode.EC_500.errorMessage());
    // return new ResponseEntity<>(errorBean, HttpStatus.INTERNAL_SERVER_ERROR);
    // } else {
    // errorBean =
    // new ErrorBean(HttpStatus.BAD_REQUEST.value(), ErrorCode.EC_400.errorMessage());
    // return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
    // }
    // } else {
    // ResponseServerUtil.getFailureResponse(ResponseServerUtil.ErrorCodes.STATUS_102.getValue(),
    // ResponseServerUtil.ErrorCodes.INVALID_INPUT.getValue(),
    // ResponseServerUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(), response);
    // return null;
    // }
    // } else {
    // ResponseServerUtil.getFailureResponse(ResponseServerUtil.ErrorCodes.STATUS_102.getValue(),
    // ResponseServerUtil.ErrorCodes.INVALID_INPUT.getValue(),
    // ResponseServerUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(), response);
    // return null;
    // }
    // } catch (Exception e) {
    // logger.error("ProcessActivityResponseController updateActivityState() - error ", e);
    // }
    // logger.info("ProcessActivityResponseController updateActivityState() - Ends ");

  }
}
