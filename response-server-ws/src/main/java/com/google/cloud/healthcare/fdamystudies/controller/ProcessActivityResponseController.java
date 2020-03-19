/**
 * ***************************************************************************** Copyright 2020
 * Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * ****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.google.cloud.healthcare.fdamystudies.bean.ActivityResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.ActivityStateRequestBean;
import com.google.cloud.healthcare.fdamystudies.bean.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.bean.ParticipantActivityBean;
import com.google.cloud.healthcare.fdamystudies.bean.ParticipantStudyInformation;
import com.google.cloud.healthcare.fdamystudies.bean.QuestionnaireActivityStructureBean;
import com.google.cloud.healthcare.fdamystudies.bean.StoredResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.StudyActivityMetadataRequestBean;
import com.google.cloud.healthcare.fdamystudies.bean.SuccessResponseBean;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantBo;
import com.google.cloud.healthcare.fdamystudies.service.ActivityResponseProcessorService;
import com.google.cloud.healthcare.fdamystudies.service.ParticipantActivityStateResponseService;
import com.google.cloud.healthcare.fdamystudies.service.ParticipantService;
import com.google.cloud.healthcare.fdamystudies.service.ParticipantStudyInfoService;
import com.google.cloud.healthcare.fdamystudies.service.StudyMetadataService;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.AppUtil;
import com.google.cloud.healthcare.fdamystudies.utils.ErrorCode;

@RestController
public class ProcessActivityResponseController {
  @Autowired private ParticipantService participantService;
  @Autowired private StudyMetadataService studyMetadataService;
  @Autowired private ParticipantStudyInfoService partStudyInfoService;

  @Autowired private ActivityResponseProcessorService activityResponseProcessorService;

  @Autowired
  private ParticipantActivityStateResponseService participantActivityStateResponseService;

  private static final Logger logger =
      LoggerFactory.getLogger(ProcessActivityResponseController.class);

  @PostMapping("/participant/process-response")
  public ResponseEntity<?> processActivityResponseForParticipant(
      @RequestBody ActivityResponseBean questionnaireActivityResponseBean) {
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
      logger.info(
          "Input values are :\n Study Id: "
              + studyId
              + "\n Activity Id: "
              + activityId
              + "\n Activity Version: "
              + activityVersion
              + "\n Particpant Id: "
              + participantId);
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
        QuestionnaireActivityStructureBean activityMetadatFromWCP =
            studyMetadataService.getStudyActivityMetadata(
                orgId, applicationId, studyActivityMetadataRequestBean);
        if (activityMetadatFromWCP == null) {
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
            partStudyInfoService.getParticipantStudyInfo(
                orgId, applicationId, studyId, participantId);
        if (partStudyInfo == null) {
          logger.error(
              "Input values are :\n Study Id: "
                  + studyId
                  + "\n participantId Id: "
                  + participantId);
          ErrorBean errorBean =
              AppUtil.dynamicResponse(
                  ErrorCode.EC_715.code(),
                  ErrorCode.EC_715.errorMessage(),
                  AppConstants.ERROR_STR,
                  ErrorCode.EC_715.errorMessage());
          return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
        }
        Boolean sharingConsent =
            (StringUtils.isBlank(partStudyInfo.getSharing())
                && partStudyInfo.getSharing().equalsIgnoreCase(AppConstants.TRUE_STR));
        questionnaireActivityResponseBean.setSharingConsent(sharingConsent);
        boolean withdrawalStatus = !StringUtils.isBlank(partStudyInfo.getWithdrawal());
        if (withdrawalStatus) {
          activityResponseProcessorService.saveActivityResponseDataForParticipant(
              activityMetadatFromWCP, questionnaireActivityResponseBean);
          savedResponseData = true;

          // Update Participant ACtivity State
          ActivityStateRequestBean activityStateRequestBean = new ActivityStateRequestBean();
          activityStateRequestBean.setParticipantId(participantId);
          activityStateRequestBean.setStudyId(studyId);

          List<ParticipantActivityBean> activity = new ArrayList<>();
          ParticipantActivityBean participantActivityBean = new ParticipantActivityBean();
          participantActivityBean.setActivityId(activityId);
          participantActivityBean.setActivityVersion(activityVersion);
          participantActivityBean.setActivityState(AppConstants.COMPLETED);
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
                  + activityVersion
                  + "\n Particpant Id: "
                  + participantId);
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
                + activityVersion
                + "\n Particpant Id: "
                + participantId);
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
                + activityVersion
                + "\n Particpant Id: "
                + participantId);
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
                + activityVersion
                + "\n Particpant Id: "
                + participantId);
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
      @RequestParam("activityId") String activityId) {
    try {

      logger.info(
          "Input values are :\n Study Id: "
              + studyId
              + "\n Site Id: "
              + siteId
              + "\n Activity Id: "
              + activityId
              + "\n Particpant Id: "
              + participantId);
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
                studyId, siteId, participantId, activityId);
        if (storedResponseBean == null) {
          logger.error(
              "Input values are :\n Study Id: "
                  + studyId
                  + "\n Site Id: "
                  + siteId
                  + "\n Activity Id: "
                  + activityId
                  + "\n Participant id: "
                  + participantId);
          ErrorBean errorBean =
              AppUtil.dynamicResponse(
                  ErrorCode.EC_706.code(),
                  ErrorCode.EC_706.errorMessage(),
                  AppConstants.ERROR_STR,
                  ErrorCode.EC_706.errorMessage());
          return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
        }

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
                + activityId
                + "\n Particpant Id: "
                + participantId);
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
              + activityId
              + "\n Particpant Id: "
              + participantId);
      return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping("/participant/withdraw")
  public ResponseEntity<?> withdrawParticipantFromStudy(
      @RequestParam(name = "studyId") String studyId,
      @RequestParam(name = "participantId") String participantId,
      @RequestParam(name = "deleteResponses") String deleteResponses) {
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
        if (!StringUtils.isBlank(deleteResponses)
            && deleteResponses.equalsIgnoreCase(AppConstants.TRUE_STR)) {
          activityResponseProcessorService.deleteActivityResponseDataForParticipant(
              studyId, participantId);
          SuccessResponseBean srBean = new SuccessResponseBean();
          srBean.setMessage(AppConstants.SUCCESS_MSG);
          return new ResponseEntity<>(srBean, HttpStatus.OK);
        } else {
          activityResponseProcessorService.updateWithdrawalStatusForParticipant(
              studyId, participantId);
          SuccessResponseBean srBean = new SuccessResponseBean();
          srBean.setMessage(AppConstants.SUCCESS_MSG);
          return new ResponseEntity<>(srBean, HttpStatus.OK);
        }
      } catch (Exception e) {
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
                + participantId
                + " Withdrawal Action "
                + deleteResponses);
        return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
      }
    }
  }
}
