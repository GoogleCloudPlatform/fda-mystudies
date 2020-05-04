/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
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
import org.springframework.web.bind.annotation.RequestHeader;
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
import com.google.cloud.healthcare.fdamystudies.service.CommonService;
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
    String activityType = null;
    String activityRunId = null;
    String participantId = null;
    String secureEnrollmentToken = null;
    boolean isResponseDataSaved = false;
    long timestampResponseSave = System.currentTimeMillis();
    try {
      orgId = questionnaireActivityResponseBean.getOrgId();
      applicationId = questionnaireActivityResponseBean.getApplicationId();
      studyId = questionnaireActivityResponseBean.getMetadata().getStudyId();
      activityId = questionnaireActivityResponseBean.getMetadata().getActivityId();
      activityVersion = questionnaireActivityResponseBean.getMetadata().getVersion();
      activityRunId = questionnaireActivityResponseBean.getMetadata().getActivityRunId();
      activityType = questionnaireActivityResponseBean.getType();
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
        commonService.createAuditLog(
            userId,
            "Activity response receipt failure",
            "Activity response receipt from participant, failed for Activity Type "
                + activityType
                + ", Activity ID "
                + activityId
                + ", Activity Version "
                + activityVersion
                + "and Run ID "
                + activityRunId,
            AppConstants.CLIENT_ID_MOBILEAPP,
            participantId,
            studyId,
            AppConstants.PARTICIPANT_LEVEL_ACCESS);
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
      commonService.createAuditLog(
          userId,
          "Activity response received",
          "Activity response Activity response successfully received from participant for Activity Type "
              + activityType
              + ", Activity ID "
              + activityId
              + ", Activity Version "
              + activityVersion
              + "and Run ID "
              + activityRunId,
          AppConstants.CLIENT_ID_MOBILEAPP,
          participantId,
          studyId,
          AppConstants.PARTICIPANT_LEVEL_ACCESS);
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
          commonService.createAuditLog(
              null,
              "Activity metadata retrieval failure",
              "Activity metadata could not be retrieved from Study Builder for Activity Type "
                  + activityType
                  + ", Activity ID "
                  + activityId
                  + ", Activity Version "
                  + activityVersion
                  + "and Run ID "
                  + activityRunId,
              AppConstants.CLIENT_ID_RESP_DATA_STORE,
              participantId,
              studyId,
              null);
          ErrorBean errorBean =
              AppUtil.dynamicResponse(
                  ErrorCode.EC_705.code(),
                  ErrorCode.EC_705.errorMessage(),
                  AppConstants.ERROR_STR,
                  ErrorCode.EC_705.errorMessage());
          return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
        }
        commonService.createAuditLog(
            null,
            "Activity metadata retrieved",
            "Activity metadata retrieved from Study Builder for Activity Type "
                + activityType
                + ", Activity ID "
                + activityId
                + ", Activity Version "
                + activityVersion
                + "and Run ID "
                + activityRunId,
            AppConstants.CLIENT_ID_RESP_DATA_STORE,
            participantId,
            studyId,
            null);
        // Get ParticipantStudyInfo from Registration Server
        ParticipantStudyInformation partStudyInfo =
            partStudyInfoService.getParticipantStudyInfo(studyId, participantId);
        if (partStudyInfo == null) {
          logger.error("GetParticipantStudyInfo() - ParticipantInfo is null. Study Id: " + studyId);
          commonService.createAuditLog(
              null,
              "Data-sharing consent value retrieval failure",
              "Latest data-sharing consent value could not be retrieved from Participant Datastore for Participant",
              AppConstants.CLIENT_ID_RESP_DATA_STORE,
              participantId,
              studyId,
              null);

          commonService.createAuditLog(
              null,
              "Withdrawal information retrieval failure",
              "Withdrawal information value could not be retrieved from Participant Datastore for Participant",
              AppConstants.CLIENT_ID_RESP_DATA_STORE,
              participantId,
              studyId,
              null);
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
        commonService.createAuditLog(
            null,
            "Data-sharing consent value retrieved",
            "Latest data-sharing consent value "
                + sharingConsent
                + " successfully retrieved from Participant Datastore for Participant",
            AppConstants.CLIENT_ID_RESP_DATA_STORE,
            participantId,
            studyId,
            null);
        questionnaireActivityResponseBean.setSharingConsent(sharingConsent);
        commonService.createAuditLog(
            null,
            "Data-sharing consent value successfully conjoined with activity response data",
            "Latest data-sharing consent value of participant successfully conjoined with response data for Activity Type "
                + activityType
                + ", Activity ID "
                + activityId
                + ", Activity Version "
                + activityVersion
                + "and Run ID "
                + activityRunId,
            AppConstants.CLIENT_ID_RESP_DATA_STORE,
            participantId,
            studyId,
            null);
        boolean withdrawalStatus = !StringUtils.isBlank(partStudyInfo.getWithdrawal());

        commonService.createAuditLog(
            null,
            "Withdrawal information retrieved",
            "Participant's withdrawal information (Withdrawn Status:"
                + withdrawalStatus
                + " successfully retrieved from Participant Datastore for Participant",
            AppConstants.CLIENT_ID_RESP_DATA_STORE,
            participantId,
            studyId,
            null);
        if (!withdrawalStatus) {
          activityResponseProcessorService.saveActivityResponseDataForParticipant(
              activityMetadatFromWCP, questionnaireActivityResponseBean);
          isResponseDataSaved = true;

          // Update Participant Activity State
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

          commonService.createAuditLog(
              null,
              "Activity State saved/updated for participant, after response submission.",
              "Activity State for Activity ID "
                  + activityId
                  + " was saved or updated for participant in Response Datastore, after response submission."
                  + "Key details: Activity Type:"
                  + activityType
                  + ", Activity ID "
                  + activityId
                  + ", Activity Version "
                  + activityVersion
                  + "and Run ID "
                  + activityRunId,
              AppConstants.CLIENT_ID_RESP_DATA_STORE,
              participantId,
              studyId,
              null);

          commonService.createAuditLog(
              null,
              "Activity response saved successfully",
              "Activity response saved successfully for Activity Type "
                  + activityType
                  + ", Activity ID "
                  + activityId
                  + ", Activity Version "
                  + activityVersion
                  + "and Run ID "
                  + activityRunId,
              AppConstants.CLIENT_ID_RESP_DATA_STORE,
              participantId,
              studyId,
              null);
          return new ResponseEntity<>(srBean, HttpStatus.OK);
        } else {
          commonService.createAuditLog(
              null,
              "Activity response save operation failure ",
              "Activity response save operation failed as participant is withdrawn. Activity Type "
                  + activityType
                  + ", Activity ID "
                  + activityId
                  + ", Activity Version "
                  + activityVersion
                  + "and Run ID "
                  + activityRunId,
              AppConstants.CLIENT_ID_RESP_DATA_STORE,
              participantId,
              studyId,
              null);
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
              "Could not save response for withdrawn participant.\n Study Id: "
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
                "Could not save response as participant is invalid.\n Study Id: "
                    + studyId
                    + "\n Activity Id: "
                    + activityId
                    + "\n Activity Version: "
                    + activityVersion
                    + "\n Particpant Id: "
                    + participantId);
        commonService.createAuditLog(
            userId,
            "Participant ID found invalid",
            "Participant ID found invalid in the response submitted by the participant. Activity Type "
                + activityType
                + ", Activity ID "
                + activityId
                + ", Activity Version "
                + activityVersion
                + "and Run ID "
                + activityRunId,
            AppConstants.CLIENT_ID_MOBILEAPP,
            participantId,
            studyId,
            AppConstants.PARTICIPANT_LEVEL_ACCESS);

        logger.error(
            "Could not save response as participant is invalid.\n Study Id: "
                + studyId
                + "\n Activity Id: "
                + activityId
                + "\n Activity Version: "
                + activityVersion);

        return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {

      if (!isResponseDataSaved) {
        commonService.createAuditLog(
            null,
            "Activity response not saved ",
            "Activity response save operation failed for Activity Type "
                + activityType
                + ", Activity ID "
                + activityId
                + ", Activity Version "
                + activityVersion
                + ", Run ID "
                + activityRunId
                + ", Timestamp of response submission from mobile app: "
                + timestampResponseSave,
            AppConstants.CLIENT_ID_RESP_DATA_STORE,
            participantId,
            studyId,
            null);
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
        commonService.createAuditLog(
            null,
            "Activity State save/update operation failure, after response submission.",
            "Activity State for Activity ID "
                + activityId
                + " could not be saved or updated for participant in Response Datastore, after response submission.",
            AppConstants.CLIENT_ID_RESP_DATA_STORE,
            participantId,
            studyId,
            null);

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
        commonService.createAuditLog(
            userId,
            "Read operation successful for response data",
            "Participant's response data for 1 or more runs of 1 or more activities, read by Mobile App. "
                + activityId,
            AppConstants.CLIENT_ID_MOBILEAPP,
            participantId,
            studyId,
            AppConstants.PARTICIPANT_LEVEL_ACCESS);
        return new ResponseEntity<>(storedResponseBean, HttpStatus.OK);
      } else {
        ErrorBean errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_706.code(),
                ErrorCode.EC_706.errorMessage(),
                AppConstants.ERROR_STR,
                "Could not get response data as participant is invalid.\n Study Id: "
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
        commonService.createAuditLog(
            userId,
            "Read operation failed for response data",
            "Participant's response data for 1 or more runs of 1 or more activities, could not be read by Mobile App as participant is invalid. "
                + activityId,
            AppConstants.CLIENT_ID_MOBILEAPP,
            participantId,
            studyId,
            AppConstants.PARTICIPANT_LEVEL_ACCESS);
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
      commonService.createAuditLog(
          userId,
          "Read operation failed for response data",
          "Participant's response data for 1 or more runs of 1 or more activities, could not be read by Mobile App"
              + activityId,
          AppConstants.CLIENT_ID_MOBILEAPP,
          participantId,
          studyId,
          AppConstants.PARTICIPANT_LEVEL_ACCESS);
      return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping("/participant/withdraw")
  public ResponseEntity<?> withdrawParticipantFromStudy(
      @RequestParam(name = "studyId") String studyId,
      @RequestParam(name = "participantId") String participantId,
      @RequestParam(name = "deleteResponses") String deleteResponses,
      @RequestHeader String clientId) {

    if (StringUtils.isBlank(studyId) || StringUtils.isBlank(participantId)) {
      ErrorBean errorBean =
          AppUtil.dynamicResponse(
              ErrorCode.EC_701.code(),
              ErrorCode.EC_701.errorMessage(),
              AppConstants.ERROR_STR,
              ErrorCode.EC_701.errorMessage());
      return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
    } else {
      boolean isResponseDataDeleted = false;
      boolean isDataDeleteSettings = false;
      if (!StringUtils.isBlank(deleteResponses)
          && deleteResponses.equalsIgnoreCase(AppConstants.TRUE_STR)) {
        isDataDeleteSettings = true;
      }
      commonService.createAuditLog(
          null,
          "Participant withdrawal intimation from Participant Datastore",
          "Information about participant's withdrawal from study received from Participant Datastore as withdrawal timestamp "
              + System.currentTimeMillis()
              + " and Data Retention Setting: "
              + isDataDeleteSettings,
          AppConstants.CLIENT_ID_RESP_DATA_STORE,
          participantId,
          studyId,
          null);
      try {
        if (isDataDeleteSettings) {
          activityResponseProcessorService.deleteActivityResponseDataForParticipant(
              studyId, participantId);
          isResponseDataDeleted = true;
          commonService.createAuditLog(
              null,
              "Response data deleted for participant",
              "All response data belonging to participant was deleted from the Response Datastore",
              AppConstants.CLIENT_ID_RESP_DATA_STORE,
              participantId,
              studyId,
              null);
        } else {
          activityResponseProcessorService.updateWithdrawalStatusForParticipant(
              studyId, participantId);
          commonService.createAuditLog(
              null,
              "Withdrawal information successfully updated",
              "Withdrawal information of participant successfully updated into all activity responses belonging to participant",
              AppConstants.CLIENT_ID_RESP_DATA_STORE,
              participantId,
              studyId,
              null);
        }
        // Delete all participant activity state from the table
        participantActivityStateResponseService.deleteParticipantActivites(studyId, participantId);
        SuccessResponseBean srBean = new SuccessResponseBean();

        srBean.setMessage(AppConstants.SUCCESS_MSG);
        commonService.createAuditLog(
            null,
            "Activity data deleted for Participant.",
            "All activity-state data for the participant was deleted from the Response Datastore.",
            AppConstants.CLIENT_ID_RESP_DATA_STORE,
            participantId,
            studyId,
            null);
        return new ResponseEntity<>(srBean, HttpStatus.OK);
      } catch (Exception e) {
        if (!isResponseDataDeleted) {
          commonService.createAuditLog(
              null,
              "Participant activity data: delete operation failure.",
              "Activity-state data of the participant, failed to get deleted from Response Datastore.",
              AppConstants.CLIENT_ID_RESP_DATA_STORE,
              participantId,
              studyId,
              null);
          ErrorBean errorBean =
              AppUtil.dynamicResponse(
                  ErrorCode.EC_717.code(),
                  ErrorCode.EC_717.errorMessage(),
                  AppConstants.ERROR_STR,
                  e.getMessage());
          logger.error(
              "Could not successfully delete data for participant on withdrawal.\n Study Id: "
                  + studyId
                  + "\n Particpant Id: "
                  + " Withdrawal Action "
                  + deleteResponses);
          return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
        } else {

          commonService.createAuditLog(
              null,
              "Withdrawal information value failed to get updated",
              "Withdrawal information of participant failed to get updated  into 1 or more activity responses belonging to participant",
              AppConstants.CLIENT_ID_RESP_DATA_STORE,
              participantId,
              studyId,
              null);
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
