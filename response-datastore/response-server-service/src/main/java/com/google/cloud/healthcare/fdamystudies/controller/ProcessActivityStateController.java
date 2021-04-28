/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.ACTIVITY_ID;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.ACTIVITY_STATE;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.ACTIVITY_VERSION;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.RUN_ID;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.ACTIVITY_STATE_SAVED_OR_UPDATED;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.ACTIVITY_STATE_SAVE_OR_UPDATE_FAILED;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.READ_OPERATION_FOR_ACTIVITY_STATE_INFO_FAILED;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.READ_OPERATION_FOR_ACTIVITY_STATE_INFO_SUCCEEDED;

import com.google.cloud.healthcare.fdamystudies.bean.ActivitiesBean;
import com.google.cloud.healthcare.fdamystudies.bean.ActivityStateRequestBean;
import com.google.cloud.healthcare.fdamystudies.bean.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.bean.ParticipantActivityBean;
import com.google.cloud.healthcare.fdamystudies.bean.SuccessResponseBean;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.common.ResponseServerAuditLogHelper;
import com.google.cloud.healthcare.fdamystudies.exception.ProcessActivityStateException;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.service.ParticipantActivityStateResponseService;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.AppUtil;
import com.google.cloud.healthcare.fdamystudies.utils.ErrorCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
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

@Api(tags = "Process activity state", description = "Activity state operation performed")
@RestController
public class ProcessActivityStateController {
  @Autowired
  private ParticipantActivityStateResponseService participantActivityStateResponseService;

  @Autowired private ResponseServerAuditLogHelper responseServerAuditLogHelper;

  private static final String BEGIN_REQUEST_LOG = "%s request";

  private XLogger logger =
      XLoggerFactory.getXLogger(ProcessActivityStateController.class.getName());

  @ApiOperation(value = "Get activity state")
  @GetMapping(
      value = "/participant/get-activity-state",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getActivityState(
      @RequestParam(name = "studyId") String studyId,
      @RequestParam("participantId") String participantId,
      HttpServletRequest request)
      throws ProcessActivityStateException {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);

    if (StringUtils.isBlank(studyId) || StringUtils.isBlank(participantId)) {
      ErrorBean errorBean =
          AppUtil.dynamicResponse(
              ErrorCode.EC_701.code(),
              ErrorCode.EC_701.errorMessage(),
              AppConstants.ERROR_STR,
              ErrorCode.EC_701.errorMessage());
      logger.warn(
          "ProcessActivityStateController getActivityState() failed. studyId or participantId missing.");
      responseServerAuditLogHelper.logEvent(
          READ_OPERATION_FOR_ACTIVITY_STATE_INFO_FAILED, auditRequest);
      return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
    } else {
      ActivitiesBean activitiesBean =
          participantActivityStateResponseService.getParticipantActivities(studyId, participantId);

      auditRequest.setStudyId(studyId);
      auditRequest.setStudyVersion("NA");
      auditRequest.setParticipantId(participantId);
      responseServerAuditLogHelper.logEvent(
          READ_OPERATION_FOR_ACTIVITY_STATE_INFO_SUCCEEDED, auditRequest);
      return new ResponseEntity<>(activitiesBean, HttpStatus.OK);
    }
  }

  @ApiOperation(value = "Update activity state")
  @PostMapping("/participant/update-activity-state")
  public ResponseEntity<?> updateActivityState(
      @RequestBody ActivityStateRequestBean activityStateRequestBean,
      @Context HttpServletResponse response,
      @RequestHeader String userId,
      HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    auditRequest.setUserId(userId);
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

        auditRequest.setStudyId(activityStateRequestBean.getStudyId());
        auditRequest.setStudyVersion("NA");
        auditRequest.setParticipantId(activityStateRequestBean.getParticipantId());
        for (ParticipantActivityBean activity : activityStateRequestBean.getActivity()) {
          Map<String, String> map = new HashedMap<>();
          map.put(ACTIVITY_STATE, activity.getActivityState());
          map.put(ACTIVITY_ID, activity.getActivityId());
          map.put(ACTIVITY_VERSION, activity.getActivityVersion());
          map.put(RUN_ID, activity.getActivityRunId());
          responseServerAuditLogHelper.logEvent(ACTIVITY_STATE_SAVED_OR_UPDATED, auditRequest, map);
        }

        return new ResponseEntity<>(srBean, HttpStatus.OK);
      } catch (Exception e) {
        logger.warn("ProcessActivityStateController updateActivityState() failed ", e);
        for (ParticipantActivityBean activity : activityStateRequestBean.getActivity()) {
          Map<String, String> map = new HashedMap<>();
          map.put(ACTIVITY_STATE, activity.getActivityState());
          map.put(ACTIVITY_ID, activity.getActivityId());
          map.put(ACTIVITY_VERSION, activity.getActivityVersion());
          map.put(RUN_ID, activity.getActivityRunId());
          responseServerAuditLogHelper.logEvent(ACTIVITY_STATE_SAVE_OR_UPDATE_FAILED, auditRequest);
        }
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
