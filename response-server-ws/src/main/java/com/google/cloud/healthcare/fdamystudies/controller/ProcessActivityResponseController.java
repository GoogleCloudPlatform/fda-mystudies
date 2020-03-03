/*******************************************************************************
 * Copyright 2020 Google LLC
 * 
 * Use of this source code is governed by an MIT-style license that can be found in the LICENSE file
 * or at https://opensource.org/licenses/MIT.
 ******************************************************************************/
package com.google.cloud.healthcare.fdamystudies.controller;

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
import com.google.cloud.healthcare.fdamystudies.bean.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.bean.QuestionnaireActivityStructureBean;
import com.google.cloud.healthcare.fdamystudies.bean.StoredResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.StudyActivityMetadataRequestBean;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantBo;
import com.google.cloud.healthcare.fdamystudies.service.ActivityResponseProcessorService;
import com.google.cloud.healthcare.fdamystudies.service.ParticipantService;
import com.google.cloud.healthcare.fdamystudies.service.StudyMetadataService;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.AppUtil;
import com.google.cloud.healthcare.fdamystudies.utils.ErrorCode;

@RestController
public class ProcessActivityResponseController {
  @Autowired
  private ParticipantService participantService;
  @Autowired
  private StudyMetadataService studyMetadataService;

  @Autowired
  private ActivityResponseProcessorService activityResponseProcessorService;

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
    try {
      orgId = questionnaireActivityResponseBean.getOrgId();
      applicationId = questionnaireActivityResponseBean.getApplicationId();
      studyId = questionnaireActivityResponseBean.getMetadata().getStudyId();
      activityId = questionnaireActivityResponseBean.getMetadata().getActivityId();
      activityVersion = questionnaireActivityResponseBean.getMetadata().getVersion();
      participantId = questionnaireActivityResponseBean.getParticipantId();
      secureEnrollmentToken = questionnaireActivityResponseBean.getTokenIdentifier();
      logger.info("Input values are :\n Study Id: " + studyId + "\n Activity Id: " + activityId
          + "\n Activity Version: " + activityVersion + "\n Particpant Id: " + participantId);
      if (StringUtils.isBlank(orgId) || StringUtils.isBlank(applicationId)
          || StringUtils.isBlank(secureEnrollmentToken) || StringUtils.isBlank(studyId)
          || StringUtils.isBlank(activityId) || StringUtils.isBlank(activityVersion)) {
        logger.error("Input values are :\n Study Id: " + studyId + "\n Activity Id: " + activityId
            + "\n Activity Version: " + activityVersion);
        ErrorBean errorBean =
            AppUtil.dynamicResponse(ErrorCode.EC_701.code(), ErrorCode.EC_701.errorMessage(),
                AppConstants.ERROR_STR, ErrorCode.EC_701.errorMessage());
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
        QuestionnaireActivityStructureBean activityMetadatFromWCP = studyMetadataService
            .getStudyActivityMetadata(orgId, applicationId, studyActivityMetadataRequestBean);
        if (activityMetadatFromWCP == null) {
          logger.error("Input values are :\n Study Id: " + studyId + "\n Activity Id: " + activityId
              + "\n Activity Version: " + activityVersion);
          ErrorBean errorBean =
              AppUtil.dynamicResponse(ErrorCode.EC_705.code(), ErrorCode.EC_705.errorMessage(),
                  AppConstants.ERROR_STR, ErrorCode.EC_705.errorMessage());
          return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
        }
        activityResponseProcessorService.saveActivityResponseDataForParticipant(
            activityMetadatFromWCP, questionnaireActivityResponseBean);
        return new ResponseEntity<>(HttpStatus.OK);
      } else {
        ErrorBean errorBean = AppUtil.dynamicResponse(ErrorCode.EC_706.code(),
            ErrorCode.EC_706.errorMessage(), AppConstants.ERROR_STR,
            "Could not save response for participant.\n Study Id: " + studyId + "\n Activity Id: "
                + activityId + "\n Activity Version: " + activityVersion + "\n Particpant Id: "
                + participantId);

        logger.error("Could not save response for participant.\n Study Id: " + studyId
            + "\n Activity Id: " + activityId + "\n Activity Version: " + activityVersion
            + "\n Particpant Id: " + participantId);
        return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      ErrorBean errorBean = AppUtil.dynamicResponse(ErrorCode.EC_707.code(),
          ErrorCode.EC_707.errorMessage(), AppConstants.ERROR_STR, e.getMessage());
      logger.error("Could not save response for participant.\n Study Id: " + studyId
          + "\n Activity Id: " + activityId + "\n Activity Version: " + activityVersion
          + "\n Particpant Id: " + participantId);
      return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);

    }
  }


  @GetMapping("/participant/getresponse")
  public ResponseEntity<?> getActivityResponseDataForParticipant(
      @RequestParam("orgId") String orgId, @RequestParam("applicationId") String applicationId,
      @RequestParam("studyId") String studyId, @RequestParam("siteId") String siteId,
      @RequestParam("participantId") String participantId,
      @RequestParam(AppConstants.PARTICIPANT_TOKEN_IDENTIFIER_KEY) String tokenIdentifier,
      @RequestParam("activityId") String activityId) {
    try {

      logger.info("Input values are :\n Study Id: " + studyId + "\n Site Id: " + siteId
          + "\n Activity Id: " + activityId + "\n Particpant Id: " + participantId);
      if (StringUtils.isBlank(orgId) || StringUtils.isBlank(applicationId)
          || StringUtils.isBlank(studyId) || StringUtils.isBlank(siteId)
          || StringUtils.isBlank(participantId) || StringUtils.isBlank(activityId)
          || StringUtils.isBlank(tokenIdentifier)) {
        ErrorBean errorBean =
            AppUtil.dynamicResponse(ErrorCode.EC_701.code(), ErrorCode.EC_701.errorMessage(),
                AppConstants.ERROR_STR, ErrorCode.EC_701.errorMessage());
        return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
      }
      // Check if participant is valid
      ParticipantBo participantBo = new ParticipantBo();
      participantBo.setTokenIdentifier(tokenIdentifier);
      participantBo.setParticipantIdentifier(participantId);

      if (participantService.isValidParticipant(participantBo)) {

        StoredResponseBean storedResponseBean = activityResponseProcessorService
            .getActivityResponseDataForParticipant(studyId, siteId, participantId, activityId);
        if (storedResponseBean == null) {
          logger.error("Input values are :\n Study Id: " + studyId + "\n Site Id: " + siteId
              + "\n Activity Id: " + activityId + "\n Participant id: " + participantId);
          ErrorBean errorBean =
              AppUtil.dynamicResponse(ErrorCode.EC_705.code(), ErrorCode.EC_705.errorMessage(),
                  AppConstants.ERROR_STR, ErrorCode.EC_705.errorMessage());
          return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(storedResponseBean, HttpStatus.OK);
      } else {
        ErrorBean errorBean = AppUtil.dynamicResponse(ErrorCode.EC_706.code(),
            ErrorCode.EC_706.errorMessage(), AppConstants.ERROR_STR,
            "Could not get response data for participant.\n Study Id: " + studyId + "\n Site Id: "
                + siteId + "\n Activity Id: " + activityId + "\n Particpant Id: " + participantId);

        logger.error(
            "Could not get response data for participant.\n Study Id: " + studyId + "\n Site Id: "
                + siteId + "\n Activity Id: " + activityId + "\n Particpant Id: " + participantId);
        return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      ErrorBean errorBean = AppUtil.dynamicResponse(ErrorCode.EC_708.code(),
          ErrorCode.EC_708.errorMessage(), AppConstants.ERROR_STR, e.getMessage());
      logger.error(
          "Could not get response data for participant.\n Study Id: " + studyId + "\n Site Id: "
              + siteId + "\n Activity Id: " + activityId + "\n Particpant Id: " + participantId);
      return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);

    }
  }
}
