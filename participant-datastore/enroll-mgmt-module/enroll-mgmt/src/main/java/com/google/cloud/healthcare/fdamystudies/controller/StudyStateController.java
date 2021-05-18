/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.google.cloud.healthcare.fdamystudies.common.EnrollAuditEvent.READ_OPERATION_FAILED_FOR_STUDY_INFO;
import static com.google.cloud.healthcare.fdamystudies.common.EnrollAuditEvent.READ_OPERATION_SUCCEEDED_FOR_STUDY_INFO;
import static com.google.cloud.healthcare.fdamystudies.common.EnrollAuditEvent.USER_FOUND_INELIGIBLE_FOR_STUDY;
import static com.google.cloud.healthcare.fdamystudies.common.EnrollAuditEvent.WITHDRAWAL_FROM_STUDY_FAILED;
import static com.google.cloud.healthcare.fdamystudies.common.EnrollAuditEvent.WITHDRAWAL_FROM_STUDY_SUCCEEDED;
import static com.google.cloud.healthcare.fdamystudies.util.AppConstants.USER_ID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.StudiesBean;
import com.google.cloud.healthcare.fdamystudies.beans.StudyStateBean;
import com.google.cloud.healthcare.fdamystudies.beans.StudyStateReqBean;
import com.google.cloud.healthcare.fdamystudies.beans.StudyStateRespBean;
import com.google.cloud.healthcare.fdamystudies.beans.StudyStateResponse;
import com.google.cloud.healthcare.fdamystudies.beans.WithDrawFromStudyRespBean;
import com.google.cloud.healthcare.fdamystudies.beans.WithdrawFromStudyBean;
import com.google.cloud.healthcare.fdamystudies.common.EnrollAuditEventHelper;
import com.google.cloud.healthcare.fdamystudies.common.EnrollmentStatus;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.repository.StudyRepository;
import com.google.cloud.healthcare.fdamystudies.service.CommonService;
import com.google.cloud.healthcare.fdamystudies.service.StudyStateService;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import com.google.cloud.healthcare.fdamystudies.util.BeanUtil;
import com.google.cloud.healthcare.fdamystudies.util.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.core.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
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
import org.springframework.web.bind.annotation.RestController;

@Api(
    tags = "Study Information",
    value = "Study info related API's",
    description =
        "Operations related to /updateStudyState, /studyState and /withdrawfromstudy endpoints ")
@RestController
public class StudyStateController {

  private static final XLogger logger =
      XLoggerFactory.getXLogger(StudyStateController.class.getName());

  private static final String STATUS_LOG = "status=%d";

  private static final String BEGIN_REQUEST_LOG = "%s request";

  @Autowired private StudyStateService studyStateService;

  @Autowired private CommonService commonService;

  @Autowired private EnrollAuditEventHelper enrollAuditEventHelper;

  @Autowired private StudyRepository studyRepository;

  @ApiOperation(value = "update enrollment status of a participant associated to particular study")
  @PostMapping(
      value = "/updateStudyState",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> updateStudyState(
      @RequestHeader(USER_ID) String userId,
      @Valid @RequestBody StudyStateReqBean studyStateReqBean,
      @Context HttpServletResponse response,
      HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    StudyStateRespBean studyStateRespBean = null;
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    List<StudiesBean> studiesBeenList = studyStateReqBean.getStudies();
    UserDetailsEntity user = commonService.getUserInfoDetails(userId);

    logger.info(
        "userId="
            + userId
            + "study Request="
            + ReflectionToStringBuilder.toString(studyStateReqBean));

    try {
      ObjectMapper mapper = new ObjectMapper();
      // Converting the Object to JSONString
      String jsonString = mapper.writeValueAsString(studyStateReqBean);
      logger.info("userId=" + userId + "study Request=" + jsonString);
    } catch (Exception e) {

    }

    if (user != null) {
      List<ParticipantStudyEntity> existParticipantStudies =
          studyStateService.getParticipantStudiesList(user, studiesBeenList);

      studyStateRespBean =
          studyStateService.saveParticipantStudies(
              studiesBeenList, existParticipantStudies, auditRequest, user);
      StudiesBean studyInfo = studiesBeenList.get(0);
      if (studyStateRespBean != null
          && studyStateRespBean
              .getMessage()
              .equalsIgnoreCase(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue())) {
        studyStateRespBean.setCode(HttpStatus.OK.value());
        if (StringUtils.equals(studyInfo.getStatus(), EnrollmentStatus.NOT_ELIGIBLE.getStatus())) {
          enrollAuditEventHelper.logEvent(USER_FOUND_INELIGIBLE_FOR_STUDY, auditRequest);
        }
        logger.exit(String.format(STATUS_LOG, studyStateRespBean.getCode()));
      }
    } else {
      MyStudiesUserRegUtil.getFailureResponse(
          MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.INVALID_USER_ID.getValue(),
          response);
      return null;
    }

    return new ResponseEntity<>(studyStateRespBean, HttpStatus.OK);
  }

  @ApiOperation(value = "fetch participant's study information")
  @GetMapping(value = "/studyState", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getStudyState(
      @RequestHeader(USER_ID) String userId,
      @Context HttpServletResponse response,
      HttpServletRequest request)
      throws Exception {
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);

    try {
      logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
      StudyStateResponse studyStateResponse = BeanUtil.getBean(StudyStateResponse.class);

      List<StudyStateBean> studies = studyStateService.getStudiesState(userId);
      studyStateResponse.setStudies(studies);
      studyStateResponse.setMessage(AppConstants.SUCCESS);

      auditRequest.setUserId(userId);
      enrollAuditEventHelper.logEvent(READ_OPERATION_SUCCEEDED_FOR_STUDY_INFO, auditRequest);

      return new ResponseEntity<>(studyStateResponse, HttpStatus.OK);

    } catch (Exception e) {
      auditRequest.setUserId(userId);
      enrollAuditEventHelper.logEvent(READ_OPERATION_FAILED_FOR_STUDY_INFO, auditRequest);
      logger.info("(C)...StudyStateController.getStudyState()...Ended with Internal Server Error");
      throw e;
    }
  }

  @ApiOperation(value = "withdraw participant from study")
  @PostMapping(
      value = "/withdrawfromstudy",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> withdrawFromStudy(
      @Valid @RequestBody WithdrawFromStudyBean withdrawFromStudyBean,
      @Context HttpServletResponse response,
      HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    WithDrawFromStudyRespBean respBean = null;
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);

    auditRequest.setParticipantId(withdrawFromStudyBean.getParticipantId());
    Optional<StudyEntity> optStudyEntity =
        studyRepository.findByCustomStudyId(withdrawFromStudyBean.getStudyId());
    if (optStudyEntity.isPresent()) {
      auditRequest.setStudyId(optStudyEntity.get().getCustomId());
      auditRequest.setStudyVersion(String.valueOf(optStudyEntity.get().getVersion()));
    }

    respBean =
        studyStateService.withdrawFromStudy(
            withdrawFromStudyBean.getParticipantId(),
            withdrawFromStudyBean.getStudyId(),
            auditRequest);
    if (respBean != null) {
      respBean.setCode(ErrorCode.EC_200.code());
      respBean.setMessage(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue());

      enrollAuditEventHelper.logEvent(WITHDRAWAL_FROM_STUDY_SUCCEEDED, auditRequest);
      logger.exit(String.format(STATUS_LOG, respBean.getCode()));
      return new ResponseEntity<>(respBean, HttpStatus.OK);
    } else {
      MyStudiesUserRegUtil.getFailureResponse(
          MyStudiesUserRegUtil.ErrorCodes.STATUS_104.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue(),
          response);

      enrollAuditEventHelper.logEvent(WITHDRAWAL_FROM_STUDY_FAILED, auditRequest);

      return null;
    }
  }
}
