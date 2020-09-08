/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import com.google.cloud.healthcare.fdamystudies.bean.ActivityResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.ActivityStateRequestBean;
import com.google.cloud.healthcare.fdamystudies.bean.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.bean.ParticipantActivityBean;
import com.google.cloud.healthcare.fdamystudies.bean.ParticipantStudyInformation;
import com.google.cloud.healthcare.fdamystudies.bean.QuestionnaireActivityStructureBean;
import com.google.cloud.healthcare.fdamystudies.bean.StoredResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.StudyActivityMetadataRequestBean;
import com.google.cloud.healthcare.fdamystudies.bean.SuccessResponseBean;
import com.google.cloud.healthcare.fdamystudies.responsedatastore.model.ParticipantBo;
import com.google.cloud.healthcare.fdamystudies.service.ActivityResponseProcessorService;
import com.google.cloud.healthcare.fdamystudies.service.CommonService;
import com.google.cloud.healthcare.fdamystudies.service.ParticipantActivityStateResponseService;
import com.google.cloud.healthcare.fdamystudies.service.ParticipantService;
import com.google.cloud.healthcare.fdamystudies.service.ParticipantStudyInfoService;
import com.google.cloud.healthcare.fdamystudies.service.StudyMetadataService;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.AppUtil;
import com.google.cloud.healthcare.fdamystudies.utils.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProcessActivityResponseController {
  @Autowired private ParticipantService participantService;
  @Autowired private StudyMetadataService studyMetadataService;
  @Autowired private ParticipantStudyInfoService partStudyInfoService;

  @Autowired private ActivityResponseProcessorService activityResponseProcessorService;

  @Autowired
  private ParticipantActivityStateResponseService participantActivityStateResponseService;

  @Autowired private CommonService commonService;
  private static final Logger logger =
      LoggerFactory.getLogger(ProcessActivityResponseController.class);

  @PostMapping("/participant/process-response")
  public ResponseEntity<?> processActivityResponseForParticipant(
      @RequestBody ActivityResponseBean questionnaireActivityResponseBean,
      @RequestHeader String userId) {
    String orgId = null;
    String applicationId = null;
    String studyId = null;
    String activityId = null;
    String activityVersion = null;
    String participantId = null;
    String secureEnrollmentToken = null;
    boolean savedResponseData = false;
    try {
      orgId = questionnaireActivityResponseBean.getOrgId();
      applicationId = questionnaireActivityResponseBean.getApplicationId();
      studyId = questionnaireActivityResponseBean.getMetadata().getStudyId();
      activityId = questionnaireActivityResponseBean.getMetadata().getActivityId();
      activityVersion = questionnaireActivityResponseBean.getMetadata().getVersion();
      participantId = questionnaireActivityResponseBean.getParticipantId();
      secureEnrollmentToken = questionnaireActivityResponseBean.getTokenIdentifier();
      logger.debug(
          "Input values are :\n Study Id: "
              + studyId
              + "\n Activity Id: "
              + activityId
              + "\n Activity Version: "
              + activityVersion);
      if (StringUtils.isBlank(orgId)
          || StringUtils.isBlank(applicationId)
          || StringUtils.isBlank(secureEnrollmentToken)
          || StringUtils.isBlank(studyId)
          || StringUtils.isBlank(activityId)
          || StringUtils.isBlank(activityVersion)) {
        logger.error(
            "Input values are :\n Study Id: "
                + studyId
                + "\n Activity Id: "
                + activityId
                + "\n Activity Version: "
                + activityVersion);
        ErrorBean errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_701.code(),
                ErrorCode.EC_701.errorMessage(),
                AppConstants.ERROR_STR,
                ErrorCode.EC_701.errorMessage());
        return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
      }
      // Check if participant is valid
      ParticipantBo participantBo = new ParticipantBo();
      participantBo.setTokenIdentifier(secureEnrollmentToken);
      participantBo.setParticipantIdentifier(participantId);

      if (participantService.isValidParticipant(participantBo)) {

        // Get ActivityMetadata from the WCP - we map the metadata information to the activity
        // response information to come up with a consolidated response object
        StudyActivityMetadataRequestBean studyActivityMetadataRequestBean =
            new StudyActivityMetadataRequestBean();
        studyActivityMetadataRequestBean.setStudyId(studyId);
        studyActivityMetadataRequestBean.setActivityId(activityId);
        studyActivityMetadataRequestBean.setActivityVersion(activityVersion);
        QuestionnaireActivityStructureBean activityMetadatFromWcp =
            studyMetadataService.getStudyActivityMetadata(
                orgId, applicationId, studyActivityMetadataRequestBean);
        if (activityMetadatFromWcp == null) {
          logger.error(
              "Input values are :\n Study Id: "
                  + studyId
                  + "\n Activity Id: "
                  + activityId
                  + "\n Activity Version: "
                  + activityVersion);
          ErrorBean errorBean =
              AppUtil.dynamicResponse(
                  ErrorCode.EC_705.code(),
                  ErrorCode.EC_705.errorMessage(),
                  AppConstants.ERROR_STR,
                  ErrorCode.EC_705.errorMessage());
          return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
        }

        // Get ParticipantStudyInfo from Registration Server
        ParticipantStudyInformation partStudyInfo =
            partStudyInfoService.getParticipantStudyInfo(studyId, participantId);
        if (partStudyInfo == null) {
          logger.error("GetParticipantStudyInfo() - ParticipantInfo is null. Study Id: " + studyId);
          ErrorBean errorBean =
              AppUtil.dynamicResponse(
                  ErrorCode.EC_715.code(),
                  ErrorCode.EC_715.errorMessage(),
                  AppConstants.ERROR_STR,
                  ErrorCode.EC_715.errorMessage());
          return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
        }
        String sharingConsent = partStudyInfo.getSharing();

        questionnaireActivityResponseBean.setSharingConsent(sharingConsent);
        boolean withdrawalStatus = !StringUtils.isBlank(partStudyInfo.getWithdrawal());

        if (!withdrawalStatus) {
          activityResponseProcessorService.saveActivityResponseDataForParticipant(
              activityMetadatFromWcp, questionnaireActivityResponseBean);
          savedResponseData = true;

          // Update Participant Activity State
          ActivityStateRequestBean activityStateRequestBean = new ActivityStateRequestBean();
          activityStateRequestBean.setParticipantId(participantId);
          activityStateRequestBean.setStudyId(studyId);

          ParticipantActivityBean participantActivityBean = new ParticipantActivityBean();
          participantActivityBean.setActivityId(activityId);
          participantActivityBean.setActivityVersion(activityVersion);
          participantActivityBean.setActivityState(AppConstants.COMPLETED);
          List<ParticipantActivityBean> activity = new ArrayList<>();
          activity.add(participantActivityBean);
          activityStateRequestBean.setActivity(activity);
          participantActivityStateResponseService.saveParticipantActivities(
              activityStateRequestBean);
          SuccessResponseBean srBean = new SuccessResponseBean();
          srBean.setMessage(AppConstants.SUCCESS_MSG);
          return new ResponseEntity<>(srBean, HttpStatus.OK);
        } else {
          ErrorBean errorBean =
              AppUtil.dynamicResponse(
                  ErrorCode.EC_716.code(),
                  ErrorCode.EC_716.errorMessage(),
                  AppConstants.ERROR_STR,
                  "Could not save response for withdrawn participant.\n Study Id: "
                      + studyId
                      + "\n Activity Id: "
                      + activityId
                      + "\n Activity Version: "
                      + activityVersion
                      + "\n Particpant Id: "
                      + participantId);

          logger.error(
              "Could not save response for participant.\n Study Id: "
                  + studyId
                  + "\n Activity Id: "
                  + activityId
                  + "\n Activity Version: "
                  + activityVersion);
          return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
        }
      } else {
        ErrorBean errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_706.code(),
                ErrorCode.EC_706.errorMessage(),
                AppConstants.ERROR_STR,
                "Could not save response for participant.\n Study Id: "
                    + studyId
                    + "\n Activity Id: "
                    + activityId
                    + "\n Activity Version: "
                    + activityVersion
                    + "\n Particpant Id: "
                    + participantId);

        logger.error(
            "Could not save response for participant.\n Study Id: "
                + studyId
                + "\n Activity Id: "
                + activityId
                + "\n Activity Version: "
                + activityVersion);

        return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {

      if (!savedResponseData) {
        ErrorBean errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_707.code(),
                ErrorCode.EC_707.errorMessage(),
                AppConstants.ERROR_STR,
                e.getMessage());
        logger.error(
            "Could not save response for participant.\n Study Id: "
                + studyId
                + "\n Activity Id: "
                + activityId
                + "\n Activity Version: "
                + activityVersion);
        return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
      } else {
        ErrorBean errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_714.code(),
                ErrorCode.EC_714.errorMessage(),
                AppConstants.ERROR_STR,
                e.getMessage());
        logger.error(
            "Could not update participant activity data for participant.\n Study Id: "
                + studyId
                + "\n Activity Id: "
                + activityId
                + "\n Activity Version: "
                + activityVersion);
        return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
      }
    }
  }

  @GetMapping("/participant/getresponse")
  public ResponseEntity<?> getActivityResponseDataForParticipant(
      @RequestParam("orgId") String orgId,
      @RequestParam("appId") String applicationId,
      @RequestParam("studyId") String studyId,
      @RequestParam("siteId") String siteId,
      @RequestParam("participantId") String participantId,
      @RequestParam(AppConstants.PARTICIPANT_TOKEN_IDENTIFIER_KEY) String tokenIdentifier,
      @RequestParam("activityId") String activityId,
      @RequestParam("questionKey") String questionKey,
      @RequestHeader String userId) {
    try {

      logger.debug(
          "Input values are :\n Study Id: "
              + studyId
              + "\n Site Id: "
              + siteId
              + "\n Activity Id: "
              + activityId);
      if (StringUtils.isBlank(orgId)
          || StringUtils.isBlank(applicationId)
          || StringUtils.isBlank(studyId)
          || StringUtils.isBlank(siteId)
          || StringUtils.isBlank(participantId)
          || StringUtils.isBlank(activityId)
          || StringUtils.isBlank(tokenIdentifier)) {
        ErrorBean errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_701.code(),
                ErrorCode.EC_701.errorMessage(),
                AppConstants.ERROR_STR,
                ErrorCode.EC_701.errorMessage());
        return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
      }
      // Check if participant is valid
      ParticipantBo participantBo = new ParticipantBo();
      participantBo.setTokenIdentifier(tokenIdentifier);
      participantBo.setParticipantIdentifier(participantId);

      if (participantService.isValidParticipant(participantBo)) {

        StoredResponseBean storedResponseBean =
            activityResponseProcessorService.getActivityResponseDataForParticipant(
                studyId, siteId, participantId, activityId, questionKey);
        return new ResponseEntity<>(storedResponseBean, HttpStatus.OK);
      } else {
        ErrorBean errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_706.code(),
                ErrorCode.EC_706.errorMessage(),
                AppConstants.ERROR_STR,
                "Could not get response data for participant.\n Study Id: "
                    + studyId
                    + "\n Site Id: "
                    + siteId
                    + "\n Activity Id: "
                    + activityId
                    + "\n Particpant Id: "
                    + participantId);

        logger.error(
            "Could not get response data for participant.\n Study Id: "
                + studyId
                + "\n Site Id: "
                + siteId
                + "\n Activity Id: "
                + activityId);
        return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      ErrorBean errorBean =
          AppUtil.dynamicResponse(
              ErrorCode.EC_708.code(),
              ErrorCode.EC_708.errorMessage(),
              AppConstants.ERROR_STR,
              e.getMessage());
      logger.error(
          "Could not get response data for participant.\n Study Id: "
              + studyId
              + "\n Site Id: "
              + siteId
              + "\n Activity Id: "
              + activityId);
      return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping("/participant/withdraw")
  public ResponseEntity<?> withdrawParticipantFromStudy(
      @RequestParam(name = "studyId") String studyId,
      @RequestParam(name = "participantId") String participantId,
      @RequestParam(name = "deleteResponses") String deleteResponses) {

    if (StringUtils.isBlank(studyId) || StringUtils.isBlank(participantId)) {
      ErrorBean errorBean =
          AppUtil.dynamicResponse(
              ErrorCode.EC_701.code(),
              ErrorCode.EC_701.errorMessage(),
              AppConstants.ERROR_STR,
              ErrorCode.EC_701.errorMessage());
      return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
    } else {
      boolean responseDataUpdate = false;
      try {
        if (!StringUtils.isBlank(deleteResponses)
            && deleteResponses.equalsIgnoreCase(AppConstants.TRUE_STR)) {
          activityResponseProcessorService.deleteActivityResponseDataForParticipant(
              studyId, participantId);
          responseDataUpdate = true;

        } else {
          activityResponseProcessorService.updateWithdrawalStatusForParticipant(
              studyId, participantId);
          responseDataUpdate = true;
        }
        // Delete all participant activity state from the table
        participantActivityStateResponseService.deleteParticipantActivites(studyId, participantId);
        SuccessResponseBean srBean = new SuccessResponseBean();
        srBean.setMessage(AppConstants.SUCCESS_MSG);
        return new ResponseEntity<>(srBean, HttpStatus.OK);
      } catch (Exception e) {
        if (responseDataUpdate) {
          ErrorBean errorBean =
              AppUtil.dynamicResponse(
                  ErrorCode.EC_717.code(),
                  ErrorCode.EC_717.errorMessage(),
                  AppConstants.ERROR_STR,
                  e.getMessage());
          logger.error(
              "Could not successfully withdraw for participant.\n Study Id: "
                  + studyId
                  + "\n Particpant Id: "
                  + " Withdrawal Action "
                  + deleteResponses);
          return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
        } else {
          ErrorBean errorBean =
              AppUtil.dynamicResponse(
                  ErrorCode.EC_712.code(),
                  ErrorCode.EC_712.errorMessage(),
                  AppConstants.ERROR_STR,
                  e.getMessage());
          logger.error(
              "Could not successfully withdraw for participant.\n Study Id: "
                  + studyId
                  + "\n Particpant Id: "
                  + " Withdrawal Action "
                  + deleteResponses);
          return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
        }
      }
    }
  }
}
