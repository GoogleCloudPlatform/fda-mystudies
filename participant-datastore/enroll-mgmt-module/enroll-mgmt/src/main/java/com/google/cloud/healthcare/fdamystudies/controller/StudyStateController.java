/*
 * Copyright 2020 Google LLC
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
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.core.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StudyStateController {

  private static final Logger logger = LoggerFactory.getLogger(StudyStateController.class);

  @Autowired private StudyStateService studyStateService;

  @Autowired private CommonService commonService;

  @Autowired private EnrollAuditEventHelper enrollAuditEventHelper;

  @Autowired private StudyRepository studyRepository;

  @PostMapping(
      value = "/updateStudyState",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> updateStudyState(
      @RequestHeader(USER_ID) String userId,
      @Valid @RequestBody StudyStateReqBean studyStateReqBean,
      @Context HttpServletResponse response,
      HttpServletRequest request) {
    logger.info("StudyStateController updateStudyState() - Starts ");
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
      }
    } else {
      MyStudiesUserRegUtil.getFailureResponse(
          MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.INVALID_USER_ID.getValue(),
          response);
      return null;
    }

    logger.info("StudyStateController updateStudyState() - Ends ");
    return new ResponseEntity<>(studyStateRespBean, HttpStatus.OK);
  }

  @GetMapping(value = "/studyState", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getStudyState(
      @RequestHeader(USER_ID) String userId,
      @Context HttpServletResponse response,
      HttpServletRequest request)
      throws Exception {
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);

    try {
      logger.info("(C)...StudyStateController.getStudyState()...Started");
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

  @PostMapping(
      value = "/withdrawfromstudy",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> withdrawFromStudy(
      @Valid @RequestBody WithdrawFromStudyBean withdrawFromStudyBean,
      @Context HttpServletResponse response,
      HttpServletRequest request) {
    logger.info("StudyStateController withdrawFromStudy() - Starts ");
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
            withdrawFromStudyBean.isDelete(),
            auditRequest);
    if (respBean != null) {
      logger.info("StudyStateController withdrawFromStudy() - Ends ");
      respBean.setCode(ErrorCode.EC_200.code());
      respBean.setMessage(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue());

      enrollAuditEventHelper.logEvent(WITHDRAWAL_FROM_STUDY_SUCCEEDED, auditRequest);

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
