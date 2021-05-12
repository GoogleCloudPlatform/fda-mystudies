/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.google.cloud.healthcare.fdamystudies.common.EnrollAuditEvent.ENROLLMENT_TOKEN_FOUND_INVALID;
import static com.google.cloud.healthcare.fdamystudies.common.EnrollAuditEvent.PARTICIPANT_ID_NOT_RECEIVED;
import static com.google.cloud.healthcare.fdamystudies.common.EnrollAuditEvent.STUDY_ENROLLMENT_FAILED;
import static com.google.cloud.healthcare.fdamystudies.common.EnrollAuditEvent.USER_ENROLLED_INTO_STUDY;
import static com.google.cloud.healthcare.fdamystudies.common.EnrollAuditEvent.USER_FOUND_ELIGIBLE_FOR_STUDY;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.EnrollmentBean;
import com.google.cloud.healthcare.fdamystudies.beans.EnrollmentResponseBean;
import com.google.cloud.healthcare.fdamystudies.beans.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.common.EnrollAuditEventHelper;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.service.EnrollmentTokenService;
import com.google.cloud.healthcare.fdamystudies.service.StudyStateService;
import com.google.cloud.healthcare.fdamystudies.util.EnrollmentManagementUtil;
import com.google.cloud.healthcare.fdamystudies.util.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.util.ErrorResponseUtil;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;
import com.google.cloud.healthcare.fdamystudies.util.TokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.core.Context;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Api(
    tags = "Enrollment",
    value = "enroll management",
    description = "Operations pertaining to enroll flow in enrollment module")
@RestController
public class EnrollmentTokenController {

  private static final XLogger logger =
      XLoggerFactory.getXLogger(EnrollmentTokenController.class.getName());

  private static final String STATUS_LOG = "status=%d";

  private static final String BEGIN_REQUEST_LOG = "%s request";

  @Autowired EnrollmentTokenService enrollmentTokenfService;

  @Autowired private EnrollmentManagementUtil enrollManagementUtil;

  @Autowired EnrollAuditEventHelper enrollAuditEventHelper;

  @Autowired StudyStateService studyStateService;

  @ApiOperation(value = "validates enrollment token of the participant ")
  @PostMapping(value = "/validateEnrollmentToken", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> validateEnrollmentToken(
      @RequestHeader("userId") String userId,
      @Valid @RequestBody EnrollmentBean enrollmentBean,
      @Context HttpServletResponse response,
      @Context HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    ErrorBean errorBean = null;
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    StudyEntity studyDetails = enrollmentTokenfService.getStudyDetails(enrollmentBean.getStudyId());
    auditRequest.setUserId(userId);
    if (studyDetails == null) {
      ErrorResponseUtil.getFailureResponse(
          ErrorResponseUtil.ErrorCodes.STATUS_103.getValue(),
          ErrorResponseUtil.ErrorCodes.INVALID_INPUT.getValue(),
          ErrorResponseUtil.ErrorCodes.STUDYID_NOT_EXIST.getValue(),
          response);
      return null;
    } else if (!StringUtils.isEmpty(enrollmentBean.getToken())) {
      auditRequest.setStudyId(studyDetails.getCustomId());
      auditRequest.setStudyVersion(String.valueOf(studyDetails.getVersion()));
      if (enrollmentTokenfService.hasParticipant(
          enrollmentBean.getStudyId(), enrollmentBean.getToken())) {
        ErrorResponseUtil.getFailureResponse(
            ErrorResponseUtil.ErrorCodes.STATUS_102.getValue(),
            ErrorResponseUtil.ErrorCodes.INVALID_INPUT.getValue(),
            ErrorResponseUtil.ErrorCodes.TOKEN_ALREADY_USE.getValue(),
            response);
        return null;
      } else if (!enrollManagementUtil.isChecksumValid(enrollmentBean.getToken())
          || !enrollmentTokenfService.isValidStudyToken(
              enrollmentBean.getToken(), enrollmentBean.getStudyId(), userId)) {
        ErrorResponseUtil.getFailureResponse(
            ErrorResponseUtil.ErrorCodes.STATUS_102.getValue(),
            ErrorResponseUtil.ErrorCodes.INVALID_INPUT.getValue(),
            ErrorResponseUtil.ErrorCodes.INVALID_TOKEN.getValue(),
            response);
        enrollAuditEventHelper.logEvent(ENROLLMENT_TOKEN_FOUND_INVALID, auditRequest);
        return null;
      }
    } else if (enrollmentTokenfService.enrollmentTokenRequired(enrollmentBean.getStudyId())) {
      ErrorResponseUtil.getFailureResponse(
          ErrorResponseUtil.ErrorCodes.STATUS_102.getValue(),
          ErrorResponseUtil.ErrorCodes.INVALID_INPUT.getValue(),
          ErrorResponseUtil.ErrorCodes.TOKEN_REQUIRED.getValue(),
          response);
      return null;
    }

    String siteId = studyStateService.getSiteId(userId, enrollmentBean.getToken());

    errorBean = new ErrorBean();
    errorBean.setCode(ErrorCode.EC_200.code());
    errorBean.setSiteId(siteId);
    errorBean.setMessage(ErrorResponseUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());

    logger.exit(String.format(STATUS_LOG, errorBean.getCode()));
    return new ResponseEntity<>(errorBean, HttpStatus.OK);
  }

  @ApiOperation(value = "enrolling into a study")
  @PostMapping(
      value = "/enroll",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> enrollParticipant(
      @RequestHeader("userId") String userId,
      @Valid @RequestBody EnrollmentBean enrollmentBean,
      @Context HttpServletResponse response,
      @Context HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    EnrollmentResponseBean respBean = null;
    ErrorBean errorBean = null;
    String tokenValue = "";

    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    auditRequest.setUserId(userId);

    try {
      StudyEntity studyDetails =
          enrollmentTokenfService.getStudyDetails(enrollmentBean.getStudyId());
      if (studyDetails != null) {
        auditRequest.setStudyId(studyDetails.getCustomId());
        auditRequest.setStudyVersion(String.valueOf(studyDetails.getVersion()));
        if (enrollmentTokenfService.enrollmentTokenRequired(enrollmentBean.getStudyId())) {
          if (!StringUtils.isEmpty(enrollmentBean.getToken())) {
            if (!enrollmentTokenfService.hasParticipant(
                enrollmentBean.getStudyId(), enrollmentBean.getToken())) {
              if (enrollManagementUtil.isChecksumValid(enrollmentBean.getToken())) {
                if (enrollmentTokenfService.isValidStudyToken(
                    enrollmentBean.getToken(), enrollmentBean.getStudyId(), userId)) {
                  enrollAuditEventHelper.logEvent(USER_FOUND_ELIGIBLE_FOR_STUDY, auditRequest);
                  respBean =
                      enrollmentTokenfService.enrollParticipant(
                          enrollmentBean.getStudyId(),
                          enrollmentBean.getToken(),
                          userId,
                          studyDetails.getVersion(),
                          auditRequest);
                  if (respBean != null) {
                    respBean.setCode(ErrorCode.EC_200.code());
                    respBean.setMessage(
                        MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());
                    auditRequest.setParticipantId(respBean.getParticipantId());
                    enrollAuditEventHelper.logEvent(USER_ENROLLED_INTO_STUDY, auditRequest);
                  }
                } else {
                  ErrorResponseUtil.getFailureResponse(
                      ErrorResponseUtil.ErrorCodes.STATUS_102.getValue(),
                      ErrorResponseUtil.ErrorCodes.INVALID_INPUT.getValue(),
                      ErrorResponseUtil.ErrorCodes.INVALID_TOKEN.getValue(),
                      response);
                  errorBean = new ErrorBean();
                  errorBean.setCode(HttpStatus.BAD_REQUEST.value());
                  errorBean.setMessage(ErrorResponseUtil.ErrorCodes.INVALID_TOKEN.getValue());

                  return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
                }
              } else {
                ErrorResponseUtil.getFailureResponse(
                    ErrorResponseUtil.ErrorCodes.STATUS_102.getValue(),
                    ErrorResponseUtil.ErrorCodes.INVALID_INPUT.getValue(),
                    ErrorResponseUtil.ErrorCodes.INVALID_TOKEN.getValue(),
                    response);

                errorBean = new ErrorBean();
                errorBean.setCode(HttpStatus.BAD_REQUEST.value());
                errorBean.setMessage(ErrorResponseUtil.ErrorCodes.INVALID_TOKEN.getValue());

                enrollAuditEventHelper.logEvent(PARTICIPANT_ID_NOT_RECEIVED, auditRequest);

                return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
              }
            } else {
              ErrorResponseUtil.getFailureResponse(
                  ErrorResponseUtil.ErrorCodes.STATUS_103.getValue(),
                  ErrorResponseUtil.ErrorCodes.INVALID_INPUT.getValue(),
                  ErrorResponseUtil.ErrorCodes.TOKEN_ALREADY_USE.getValue(),
                  response);
              errorBean = new ErrorBean();
              errorBean.setCode(HttpStatus.FORBIDDEN.value());
              errorBean.setMessage(ErrorResponseUtil.ErrorCodes.TOKEN_ALREADY_USE.getValue());
              return new ResponseEntity<>(errorBean, HttpStatus.FORBIDDEN);
            }
          } else {
            errorBean = new ErrorBean();
            errorBean.setCode(ErrorCode.EC_103.code());
            errorBean.setMessage(ErrorResponseUtil.ErrorCodes.TOKEN_REQUIRED.getValue());
            ErrorResponseUtil.getFailureResponse(
                ErrorResponseUtil.ErrorCodes.STATUS_102.getValue(),
                ErrorResponseUtil.ErrorCodes.INVALID_INPUT.getValue(),
                ErrorResponseUtil.ErrorCodes.TOKEN_REQUIRED.getValue(),
                response);
            return null;
          }
        } else {
          if (enrollmentBean.getToken().isEmpty()) {
            tokenValue = TokenUtil.randomString(8);
          }
          respBean =
              enrollmentTokenfService.enrollParticipant(
                  enrollmentBean.getStudyId(),
                  tokenValue,
                  userId,
                  studyDetails.getVersion(),
                  auditRequest);
          if (respBean != null) {
            respBean.setCode(ErrorCode.EC_200.code());
            respBean.setMessage(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());

            enrollAuditEventHelper.logEvent(USER_FOUND_ELIGIBLE_FOR_STUDY, auditRequest);
            enrollAuditEventHelper.logEvent(USER_ENROLLED_INTO_STUDY, auditRequest);
          }
        }
      } else {
        errorBean = new ErrorBean();
        errorBean.setCode(ErrorCode.EC_103.code());
        errorBean.setMessage(ErrorResponseUtil.ErrorCodes.STUDYID_NOT_EXIST.getValue());
        ErrorResponseUtil.getFailureResponse(
            ErrorResponseUtil.ErrorCodes.STATUS_103.getValue(),
            ErrorResponseUtil.ErrorCodes.INVALID_INPUT.getValue(),
            ErrorResponseUtil.ErrorCodes.STUDYID_NOT_EXIST.getValue(),
            response);

        return null;
      }

    } catch (Exception e) {
      logger.error("EnrollmentTokenController enrollParticipant() - error ", e);
      enrollAuditEventHelper.logEvent(STUDY_ENROLLMENT_FAILED, auditRequest);
      throw e;
    }
    logger.exit(String.format(STATUS_LOG, respBean.getCode()));
    return new ResponseEntity<>(respBean, HttpStatus.OK);
  }
}
