/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.ACTIVITY_ID;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.ACTIVITY_TYPE;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.ACTIVITY_VERSION;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.RUN_ID;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.ACTIVITY_DATA_DELETION_FAILED;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.ACTIVITY_RESPONSE_DATA_PROCESSING_FAILED;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.ACTIVITY_RESPONSE_NOT_SAVED;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.ACTIVITY_RESPONSE_RECEIPT_FAILED;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.ACTIVITY_RESPONSE_RECEIVED;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.ACTIVITY_RESPONSE_SAVED;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.ACTIVITY_STATE_SAVED_OR_UPDATED_AFTER_RESPONSE_SUBMISSION;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.ACTIVTY_METADATA_RETRIEVAL_FAILED;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.ACTIVTY_METADATA_RETRIEVED;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.DATA_SHARING_CONSENT_VALUE_CONJOINED_WITH_ACTIVITY_RESPONSE_DATA;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.DATA_SHARING_CONSENT_VALUE_RETRIEVAL_FAILED;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.DATA_SHARING_CONSENT_VALUE_RETRIEVED;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.PARTICIPANT_ACTIVITY_DATA_DELETED;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.PARTICIPANT_ID_INVALID;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.PARTICIPANT_WITHDRAWAL_INTIMATION_FROM_PARTICIPANT_DATASTORE;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.READ_OPERATION_FOR_RESPONSE_DATA_FAILED;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.READ_OPERATION_FOR_RESPONSE_DATA_SUCCEEDED;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.WITHDRAWAL_INFORMATION_RETREIVAL_FAILED;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.WITHDRAWAL_INFORMATION_RETRIEVED;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.WITHDRAWAL_INFORMATION_UPDATED;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.WITHDRAWAL_INFORMATION_UPDATE_FAILED;

import com.google.cloud.healthcare.fdamystudies.bean.ActivityResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.ActivityStateRequestBean;
import com.google.cloud.healthcare.fdamystudies.bean.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.bean.ParticipantActivityBean;
import com.google.cloud.healthcare.fdamystudies.bean.ParticipantStudyInformation;
import com.google.cloud.healthcare.fdamystudies.bean.QuestionnaireActivityStructureBean;
import com.google.cloud.healthcare.fdamystudies.bean.StoredResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.StudyActivityMetadataRequestBean;
import com.google.cloud.healthcare.fdamystudies.bean.SuccessResponseBean;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.common.ResponseServerAuditLogHelper;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.response.model.ParticipantInfoEntity;
import com.google.cloud.healthcare.fdamystudies.service.ActivityResponseProcessorService;
import com.google.cloud.healthcare.fdamystudies.service.ParticipantActivityStateResponseService;
import com.google.cloud.healthcare.fdamystudies.service.ParticipantService;
import com.google.cloud.healthcare.fdamystudies.service.ParticipantStudyInfoService;
import com.google.cloud.healthcare.fdamystudies.service.StudyMetadataService;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.AppUtil;
import com.google.cloud.healthcare.fdamystudies.utils.ErrorCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Process activity response", description = "Response activity operation performed")
@RestController
public class ProcessActivityResponseController {
  @Autowired private ParticipantService participantService;
  @Autowired private StudyMetadataService studyMetadataService;
  @Autowired private ParticipantStudyInfoService partStudyInfoService;

  @Autowired private ActivityResponseProcessorService activityResponseProcessorService;

  @Autowired
  private ParticipantActivityStateResponseService participantActivityStateResponseService;

  @Autowired private ResponseServerAuditLogHelper responseServerAuditLogHelper;

  private static final String BEGIN_REQUEST_LOG = "%s request";

  private XLogger logger =
      XLoggerFactory.getXLogger(ProcessActivityResponseController.class.getName());

  @ApiOperation(value = "Process activity response for participant and store in cloud fire store")
  @PostMapping("/participant/process-response")
  public ResponseEntity<?> processActivityResponseForParticipant(
      @RequestBody ActivityResponseBean questionnaireActivityResponseBean,
      @RequestHeader String userId,
      HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    auditRequest.setUserId(userId);
    String applicationId = null;
    String studyId = null;
    String studyVersion = null;
    String activityId = null;
    String activityVersion = null;
    String participantId = null;
    String secureEnrollmentToken = null;
    boolean savedResponseData = false;
    try {
      applicationId = questionnaireActivityResponseBean.getApplicationId();
      studyId = questionnaireActivityResponseBean.getMetadata().getStudyId();
      studyVersion = questionnaireActivityResponseBean.getMetadata().getStudyVersion();
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
      if (StringUtils.isBlank(applicationId)
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

      auditRequest.setStudyId(studyId);
      auditRequest.setParticipantId(participantId);
      auditRequest.setUserId(userId);
      auditRequest.setStudyVersion(studyVersion);
      Map<String, String> activityMap = new HashedMap<>();
      activityMap.put(ACTIVITY_TYPE, questionnaireActivityResponseBean.getType());
      activityMap.put(ACTIVITY_ID, activityId);
      activityMap.put(ACTIVITY_VERSION, activityVersion);
      activityMap.put(RUN_ID, questionnaireActivityResponseBean.getMetadata().getActivityRunId());
      responseServerAuditLogHelper.logEvent(ACTIVITY_RESPONSE_RECEIVED, auditRequest, activityMap);

      // Check if participant is valid
      ParticipantInfoEntity participantBo = new ParticipantInfoEntity();
      participantBo.setTokenId(secureEnrollmentToken);
      participantBo.setParticipantId(participantId);

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
                applicationId, studyActivityMetadataRequestBean, auditRequest);
        if (activityMetadatFromWcp == null) {
          logger.error(
              "Input values are :\n Study Id: "
                  + studyId
                  + "\n Activity Id: "
                  + activityId
                  + "\n Activity Version: "
                  + activityVersion);

          Map<String, String> receiptMap = new HashedMap<>();
          receiptMap.put(
              "questionnaire_or_active_task", questionnaireActivityResponseBean.getType());
          receiptMap.put(ACTIVITY_ID, activityId);
          receiptMap.put(ACTIVITY_VERSION, activityVersion);
          receiptMap.put(
              RUN_ID, questionnaireActivityResponseBean.getMetadata().getActivityRunId());
          responseServerAuditLogHelper.logEvent(
              ACTIVITY_RESPONSE_RECEIPT_FAILED, auditRequest, receiptMap);

          Map<String, String> map = new HashedMap<>();
          map.put(ACTIVITY_TYPE, questionnaireActivityResponseBean.getType());
          map.put(ACTIVITY_ID, activityId);
          map.put(ACTIVITY_VERSION, activityVersion);
          responseServerAuditLogHelper.logEvent(
              ACTIVTY_METADATA_RETRIEVAL_FAILED, auditRequest, map);
          map.put(RUN_ID, questionnaireActivityResponseBean.getMetadata().getActivityRunId());
          responseServerAuditLogHelper.logEvent(
              ACTIVITY_RESPONSE_DATA_PROCESSING_FAILED, auditRequest, map);

          ErrorBean errorBean =
              AppUtil.dynamicResponse(
                  ErrorCode.EC_705.code(),
                  ErrorCode.EC_705.errorMessage(),
                  AppConstants.ERROR_STR,
                  ErrorCode.EC_705.errorMessage());
          return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
        }
        Map<String, String> map = new HashedMap<>();
        map.put(ACTIVITY_TYPE, questionnaireActivityResponseBean.getType());
        map.put(ACTIVITY_ID, activityId);
        map.put(ACTIVITY_VERSION, activityVersion);
        responseServerAuditLogHelper.logEvent(ACTIVTY_METADATA_RETRIEVED, auditRequest, map);

        // Get ParticipantStudyInfo from Registration Server
        ParticipantStudyInformation partStudyInfo =
            partStudyInfoService.getParticipantStudyInfo(studyId, participantId, auditRequest);
        if (partStudyInfo == null) {
          logger.error("GetParticipantStudyInfo() - ParticipantInfo is null. Study Id: " + studyId);
          responseServerAuditLogHelper.logEvent(
              DATA_SHARING_CONSENT_VALUE_RETRIEVAL_FAILED, auditRequest);

          responseServerAuditLogHelper.logEvent(
              WITHDRAWAL_INFORMATION_RETREIVAL_FAILED, auditRequest);
          ErrorBean errorBean =
              AppUtil.dynamicResponse(
                  ErrorCode.EC_715.code(),
                  ErrorCode.EC_715.errorMessage(),
                  AppConstants.ERROR_STR,
                  ErrorCode.EC_715.errorMessage());
          return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
        }
        String sharingConsent = partStudyInfo.getSharing();
        Map<String, String> consentMap = new HashedMap<>();
        consentMap.put("datasharing_consent_value", sharingConsent);
        responseServerAuditLogHelper.logEvent(
            DATA_SHARING_CONSENT_VALUE_RETRIEVED, auditRequest, consentMap);

        questionnaireActivityResponseBean.setSharingConsent(sharingConsent);
        Map<String, String> sharingMap = new HashedMap<>();
        sharingMap.put(ACTIVITY_TYPE, questionnaireActivityResponseBean.getType());
        sharingMap.put(ACTIVITY_ID, activityId);
        sharingMap.put(ACTIVITY_VERSION, activityVersion);
        sharingMap.put(RUN_ID, questionnaireActivityResponseBean.getMetadata().getActivityRunId());
        responseServerAuditLogHelper.logEvent(
            DATA_SHARING_CONSENT_VALUE_CONJOINED_WITH_ACTIVITY_RESPONSE_DATA,
            auditRequest,
            sharingMap);
        boolean withdrawalStatus = !StringUtils.isBlank(partStudyInfo.getWithdrawal());

        Map<String, String> withdrawMap = new HashedMap<>();
        withdrawMap.put("withdrawn_status", String.valueOf(withdrawalStatus));
        responseServerAuditLogHelper.logEvent(
            WITHDRAWAL_INFORMATION_RETRIEVED, auditRequest, withdrawMap);
        if (!withdrawalStatus) {
          activityResponseProcessorService.saveActivityResponseDataForParticipant(
              activityMetadatFromWcp, questionnaireActivityResponseBean, auditRequest);
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
          Map<String, String> activityStateMap = new HashedMap<>();
          activityStateMap.put("activity_state", participantActivityBean.getActivityState());
          activityStateMap.put(ACTIVITY_ID, activityId);
          activityStateMap.put(ACTIVITY_VERSION, activityVersion);
          activityStateMap.put(
              RUN_ID, questionnaireActivityResponseBean.getMetadata().getActivityRunId());
          responseServerAuditLogHelper.logEvent(
              ACTIVITY_STATE_SAVED_OR_UPDATED_AFTER_RESPONSE_SUBMISSION,
              auditRequest,
              activityStateMap);
          SuccessResponseBean srBean = new SuccessResponseBean();
          srBean.setMessage(AppConstants.SUCCESS_MSG);

          Map<String, String> activityResponseMap = new HashedMap<>();
          activityResponseMap.put(ACTIVITY_TYPE, questionnaireActivityResponseBean.getType());
          activityResponseMap.put(ACTIVITY_ID, activityId);
          activityResponseMap.put(ACTIVITY_VERSION, activityVersion);
          activityResponseMap.put(
              RUN_ID, questionnaireActivityResponseBean.getMetadata().getActivityRunId());
          responseServerAuditLogHelper.logEvent(
              ACTIVITY_RESPONSE_SAVED, auditRequest, activityResponseMap);
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
          Map<String, String> notSaveMap = new HashedMap<>();
          notSaveMap.put(ACTIVITY_TYPE, questionnaireActivityResponseBean.getType());
          notSaveMap.put(ACTIVITY_ID, activityId);
          notSaveMap.put(ACTIVITY_VERSION, activityVersion);
          notSaveMap.put(
              "submission_timestamp", questionnaireActivityResponseBean.getCreatedTimestamp());
          responseServerAuditLogHelper.logEvent(
              ACTIVITY_RESPONSE_NOT_SAVED, auditRequest, notSaveMap);
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
                "Could not save response for invalid participant.\n Study Id: "
                    + studyId
                    + "\n Activity Id: "
                    + activityId
                    + "\n Activity Version: "
                    + activityVersion
                    + "\n Particpant Id: "
                    + participantId);

        logger.error(
            "Could not save response for invalid participant.\n Study Id: "
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
            "An error occured while saving response for participant.\n Study Id: "
                + studyId
                + "\n Activity Id: "
                + activityId
                + "\n Activity Version: "
                + activityVersion);
        responseServerAuditLogHelper.logEvent(PARTICIPANT_ID_INVALID, auditRequest);
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

  @ApiOperation(value = "Get activity response data for participant from cloud fire store")
  @GetMapping("/participant/getresponse")
  public ResponseEntity<?> getActivityResponseDataForParticipant(
      @RequestParam("appId") String applicationId,
      @RequestParam("studyId") String studyId,
      @RequestParam("siteId") String siteId,
      @RequestParam("participantId") String participantId,
      @RequestParam(AppConstants.PARTICIPANT_TOKEN_IDENTIFIER_KEY) String tokenIdentifier,
      @RequestParam("activityId") String activityId,
      @RequestParam("questionKey") String questionKey,
      @RequestHeader String userId,
      HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    try {

      logger.debug(
          "Input values are :\n Study Id: "
              + studyId
              + "\n Site Id: "
              + siteId
              + "\n Activity Id: "
              + activityId);
      if (StringUtils.isBlank(applicationId)
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
      auditRequest.setAppId(applicationId);
      auditRequest.setSiteId(siteId);
      auditRequest.setStudyId(studyId);
      auditRequest.setParticipantId(participantId);

      // Check if participant is valid
      ParticipantInfoEntity participantBo = new ParticipantInfoEntity();
      participantBo.setTokenId(tokenIdentifier);
      participantBo.setParticipantId(participantId);

      if (participantService.isValidParticipant(participantBo)) {

        StoredResponseBean storedResponseBean =
            activityResponseProcessorService.getActivityResponseDataForParticipant(
                studyId, siteId, participantId, activityId, questionKey);
        responseServerAuditLogHelper.logEvent(
            READ_OPERATION_FOR_RESPONSE_DATA_SUCCEEDED, auditRequest);
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
        responseServerAuditLogHelper.logEvent(
            READ_OPERATION_FOR_RESPONSE_DATA_FAILED, auditRequest);
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

  @ApiOperation(value = "Withdraw participant from study from response datastore")
  @PostMapping("/participant/withdraw")
  public ResponseEntity<?> withdrawParticipantFromStudy(
      @RequestHeader String appId,
      @RequestParam(name = "studyId") String studyId,
      @RequestParam(name = "studyVersion") String studyVersion,
      @RequestParam(name = "participantId") String participantId,
      HttpServletRequest request) {
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    if (StringUtils.isBlank(studyId) || StringUtils.isBlank(participantId)) {
      logger.debug(
          "ParticipantIdController withdrawParticipantFromStudy() - studyId or participantId is blank ");
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
        auditRequest.setStudyId(studyId);
        auditRequest.setStudyVersion(studyVersion);
        auditRequest.setAppId(appId);
        auditRequest.setParticipantId(participantId);
        Map<String, String> map = new HashMap<>();
        map.put("withdrawal_timetamp", Timestamp.from(Instant.now()).toString());
        responseServerAuditLogHelper.logEvent(
            PARTICIPANT_WITHDRAWAL_INTIMATION_FROM_PARTICIPANT_DATASTORE, auditRequest, map);

        activityResponseProcessorService.updateWithdrawalStatusForParticipant(
            studyId, participantId);
        responseDataUpdate = true;

        responseServerAuditLogHelper.logEvent(WITHDRAWAL_INFORMATION_UPDATED, auditRequest);

        // Delete all participant activity state from the table
        participantActivityStateResponseService.deleteParticipantActivites(studyId, participantId);
        SuccessResponseBean srBean = new SuccessResponseBean();
        responseServerAuditLogHelper.logEvent(PARTICIPANT_ACTIVITY_DATA_DELETED, auditRequest);
        srBean.setMessage(AppConstants.SUCCESS_MSG);
        return new ResponseEntity<>(srBean, HttpStatus.OK);
      } catch (Exception e) {
        if (responseDataUpdate) {
          logger.debug(
              "ParticipantIdController withdrawParticipantFromStudy() - Catch responseDataUpdate 1: "
                  + responseDataUpdate);
          ErrorBean errorBean =
              AppUtil.dynamicResponse(
                  ErrorCode.EC_717.code(),
                  ErrorCode.EC_717.errorMessage(),
                  AppConstants.ERROR_STR,
                  e.getMessage());
          responseServerAuditLogHelper.logEvent(ACTIVITY_DATA_DELETION_FAILED, auditRequest);
          logger.error(
              "Could not successfully withdraw for participant.\n Study Id: "
                  + studyId
                  + "\n Particpant Id");
          return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
        } else {
          logger.debug(
              "ParticipantIdController withdrawParticipantFromStudy() - Catch responseDataUpdate 2: "
                  + responseDataUpdate);
          responseServerAuditLogHelper.logEvent(WITHDRAWAL_INFORMATION_UPDATE_FAILED, auditRequest);
          ErrorBean errorBean =
              AppUtil.dynamicResponse(
                  ErrorCode.EC_712.code(),
                  ErrorCode.EC_712.errorMessage(),
                  AppConstants.ERROR_STR,
                  e.getMessage());
          logger.error(
              "Could not successfully withdraw for participant.\n Study Id: "
                  + studyId
                  + "\n Particpant Id");
          return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
        }
      }
    }
  }
}
