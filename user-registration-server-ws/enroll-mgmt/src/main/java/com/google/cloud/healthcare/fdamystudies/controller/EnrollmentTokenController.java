/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.controller;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.cloud.healthcare.fdamystudies.beans.EnrollmentBean;
import com.google.cloud.healthcare.fdamystudies.beans.EnrollmentResponseBean;
import com.google.cloud.healthcare.fdamystudies.beans.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.exception.InvalidRequestException;
import com.google.cloud.healthcare.fdamystudies.exception.UnAuthorizedRequestException;
import com.google.cloud.healthcare.fdamystudies.service.CommonService;
import com.google.cloud.healthcare.fdamystudies.service.EnrollmentTokenService;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import com.google.cloud.healthcare.fdamystudies.util.AppUtil;
import com.google.cloud.healthcare.fdamystudies.util.EnrollmentManagementUtil;
import com.google.cloud.healthcare.fdamystudies.util.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.util.ErrorResponseUtil;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;
import com.google.cloud.healthcare.fdamystudies.util.TokenUtil;

@RestController
public class EnrollmentTokenController {

  private static final Logger logger = LoggerFactory.getLogger(EnrollmentTokenController.class);

  @Autowired EnrollmentTokenService enrollmentTokenfService;

  @Autowired CommonService commonService;

  @Autowired ApplicationPropertyConfiguration appConfig;

  @Autowired private EnrollmentManagementUtil enrollManagementUtil;

  @RequestMapping(value = "/ping")
  public String ping() {
    logger.info(" EnrollmentTokenController - ping()  ");
    return "Mystudies UserRegistration Webservice Enrollment Management Service Bundle Started !!!";
  }

  @PostMapping(value = "/validateEnrollmentToken", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> validateEnrollmentToken(
      @RequestHeader("userId") String userId,
      @RequestBody EnrollmentBean enrollmentBean,
      @Context HttpServletResponse response) {
    logger.info("ValidateEnrollmentTokenController validateEnrollmentToken() - Starts ");
    ErrorBean errorBean = null;
    try {
      if (enrollmentBean != null) {
        if (StringUtils.isEmpty(enrollmentBean.getStudyId())) {
          ErrorResponseUtil.getFailureResponse(
              ErrorResponseUtil.ErrorCodes.STATUS_102.getValue(),
              ErrorResponseUtil.ErrorCodes.INVALID_INPUT.getValue(),
              ErrorResponseUtil.ErrorCodes.ERROR_REQUIRED.getValue(),
              response);
          commonService.createAuditLog(
              userId,
              AppConstants.AUDIT_EVENT_ENROLL_FAIL_NAME,
              AppConstants.AUDIT_EVENT_ENROLL_FAIL_DESC,
              AppConstants.AUDIT_LOG_MOBILE_APP_CLIENT_ID,
              "",
              "",
              AppConstants.APP_LEVEL_ACCESS);
          return null;
        } else if (!enrollmentTokenfService.studyExists(enrollmentBean.getStudyId())) {
          ErrorResponseUtil.getFailureResponse(
              ErrorResponseUtil.ErrorCodes.STATUS_103.getValue(),
              ErrorResponseUtil.ErrorCodes.INVALID_INPUT.getValue(),
              ErrorResponseUtil.ErrorCodes.STUDYID_NOT_EXIST.getValue(),
              response);
          commonService.createAuditLog(
              userId,
              AppConstants.AUDIT_EVENT_ENROLL_FAIL_NAME,
              AppConstants.AUDIT_EVENT_ENROLL_FAIL_DESC,
              AppConstants.AUDIT_LOG_MOBILE_APP_CLIENT_ID,
              "",
              "",
              AppConstants.APP_LEVEL_ACCESS);
          return null;
        } else if (!StringUtils.isEmpty(enrollmentBean.getToken())) {
          if (enrollmentTokenfService.hasParticipant(
              enrollmentBean.getStudyId(), enrollmentBean.getToken())) {
            ErrorResponseUtil.getFailureResponse(
                ErrorResponseUtil.ErrorCodes.STATUS_103.getValue(),
                ErrorResponseUtil.ErrorCodes.INVALID_INPUT.getValue(),
                ErrorResponseUtil.ErrorCodes.TOKEN_ALREADY_USE.getValue(),
                response);
            commonService.createAuditLog(
                userId,
                AppConstants.AUDIT_EVENT_ENROLL_FAIL_NAME,
                AppConstants.AUDIT_EVENT_ENROLL_FAIL_DESC,
                AppConstants.AUDIT_LOG_MOBILE_APP_CLIENT_ID,
                "",
                enrollmentBean.getStudyId(),
                AppConstants.APP_LEVEL_ACCESS);
            return null;
          } else if (!enrollManagementUtil.isChecksumValid(enrollmentBean.getToken())) {
            ErrorResponseUtil.getFailureResponse(
                ErrorResponseUtil.ErrorCodes.STATUS_102.getValue(),
                ErrorResponseUtil.ErrorCodes.INVALID_INPUT.getValue(),
                ErrorResponseUtil.ErrorCodes.INVALID_TOKEN.getValue(),
                response);
            commonService.createAuditLog(
                userId,
                AppConstants.AUDIT_EVENT_INVALID_ENROLLMENT_TOKEN_NAME,
                String.format(
                    AppConstants.AUDIT_EVENT_INVALID_ENROLLMENT_TOKEN_DESC,
                    enrollmentBean.getToken()),
                AppConstants.AUDIT_LOG_MOBILE_APP_CLIENT_ID,
                "",
                enrollmentBean.getStudyId(),
                AppConstants.APP_LEVEL_ACCESS);
            return null;
          } else if (!enrollmentTokenfService.isValidStudyToken(
              enrollmentBean.getToken(), enrollmentBean.getStudyId())) {
            ErrorResponseUtil.getFailureResponse(
                ErrorResponseUtil.ErrorCodes.STATUS_102.getValue(),
                ErrorResponseUtil.ErrorCodes.INVALID_INPUT.getValue(),
                ErrorResponseUtil.ErrorCodes.UNKNOWN_TOKEN.getValue(),
                response);
            commonService.createAuditLog(
                userId,
                AppConstants.AUDIT_EVENT_INVALID_ENROLLMENT_TOKEN_NAME,
                String.format(
                    AppConstants.AUDIT_EVENT_INVALID_ENROLLMENT_TOKEN_DESC,
                    enrollmentBean.getToken()),
                AppConstants.AUDIT_LOG_MOBILE_APP_CLIENT_ID,
                "",
                enrollmentBean.getStudyId(),
                AppConstants.APP_LEVEL_ACCESS);
            return null;
          }
        }
        // Allow for the possibility that someone can enroll without using an enrollment
        // token
        else if (enrollmentTokenfService.enrollmentTokenRequired(enrollmentBean.getStudyId())) {
          ErrorResponseUtil.getFailureResponse(
              ErrorResponseUtil.ErrorCodes.STATUS_102.getValue(),
              ErrorResponseUtil.ErrorCodes.INVALID_INPUT.getValue(),
              ErrorResponseUtil.ErrorCodes.TOKEN_REQUIRED.getValue(),
              response);
          commonService.createAuditLog(
              userId,
              AppConstants.AUDIT_EVENT_ENROLL_FAIL_NAME,
              AppConstants.AUDIT_EVENT_ENROLL_FAIL_DESC,
              AppConstants.AUDIT_LOG_MOBILE_APP_CLIENT_ID,
              "",
              enrollmentBean.getStudyId(),
              AppConstants.APP_LEVEL_ACCESS);
          return null;
        }
      } else {
        ErrorResponseUtil.getFailureResponse(
            ErrorResponseUtil.ErrorCodes.STATUS_102.getValue(),
            ErrorResponseUtil.ErrorCodes.INVALID_INPUT.getValue(),
            ErrorResponseUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
            response);
        commonService.createAuditLog(
            userId,
            AppConstants.AUDIT_EVENT_ENROLL_FAIL_NAME,
            AppConstants.AUDIT_EVENT_ENROLL_FAIL_DESC,
            AppConstants.AUDIT_LOG_MOBILE_APP_CLIENT_ID,
            "",
            "",
            AppConstants.APP_LEVEL_ACCESS);
        return null;
      }
      errorBean = new ErrorBean();
      errorBean.setCode(ErrorCode.EC_200.code());
      errorBean.setMessage(ErrorResponseUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());

    } catch (Exception e) {
      logger.error("EnrollmentTokenController validateEnrollmentToken() - error ", e);
      commonService.createAuditLog(
          userId,
          AppConstants.AUDIT_EVENT_ENROLL_FAIL_NAME,
          AppConstants.AUDIT_EVENT_ENROLL_FAIL_DESC,
          AppConstants.AUDIT_LOG_MOBILE_APP_CLIENT_ID,
          "",
          "",
          AppConstants.APP_LEVEL_ACCESS);
      return AppUtil.httpResponseForInternalServerError();
    }

    logger.info("EnrollmentTokenController validateEnrollmentToken() - Ends ");
    return new ResponseEntity<>(errorBean, HttpStatus.OK);
  }

  @PostMapping(
      value = "/enroll",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> enrollParticipant(
      @RequestHeader("userId") String userId,
      @RequestBody EnrollmentBean enrollmentBean,
      @Context HttpServletResponse response) {
    logger.info("EnrollmentTokenController enrollParticipant() - Starts ");
    EnrollmentResponseBean respBean = null;
    ErrorBean errorBean = null;
    boolean isTokenRequired = false;
    String tokenValue = "";
    try {
      if (enrollmentBean != null) {
        if (!StringUtils.isEmpty(enrollmentBean.getStudyId())) {
          if (enrollmentTokenfService.studyExists(enrollmentBean.getStudyId())) {
            if (enrollmentTokenfService.enrollmentTokenRequired(enrollmentBean.getStudyId())) {
              if (!StringUtils.isEmpty(enrollmentBean.getToken())) {
                if (!enrollmentTokenfService.hasParticipant(
                    enrollmentBean.getStudyId(), enrollmentBean.getToken())) {
                  if (enrollManagementUtil.isChecksumValid(enrollmentBean.getToken())) {
                    if (enrollmentTokenfService.isValidStudyToken(
                        enrollmentBean.getToken(), enrollmentBean.getStudyId())) {
                      respBean =
                          enrollmentTokenfService.enrollParticipant(
                              enrollmentBean.getStudyId(), enrollmentBean.getToken(), userId);
                      if (respBean != null) {
                        respBean.setCode(ErrorCode.EC_200.code());
                        respBean.setMessage(
                            MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());
                        commonService.createAuditLog(
                            userId,
                            AppConstants.AUDIT_EVENT_APP_USER_ELIGIBLE_NAME,
                            String.format(
                                AppConstants.AUDIT_EVENT_APP_USER_ELIGIBLE_DESC,
                                AppConstants.CLOSE_STUDY,
                                enrollmentBean.getToken()),
                            AppConstants.AUDIT_LOG_MOBILE_APP_CLIENT_ID,
                            "",
                            enrollmentBean.getStudyId(),
                            AppConstants.APP_LEVEL_ACCESS);
                      } else {
                        commonService.createAuditLog(
                            userId,
                            AppConstants.AUDIT_EVENT_ENROLL_FAIL_NAME,
                            AppConstants.AUDIT_EVENT_ENROLL_FAIL_DESC,
                            AppConstants.AUDIT_LOG_MOBILE_APP_CLIENT_ID,
                            "",
                            enrollmentBean.getStudyId(),
                            AppConstants.APP_LEVEL_ACCESS);
                        errorBean = new ErrorBean();
                        errorBean.setCode(ErrorCode.EC_500.code());
                        errorBean.setMessage(ErrorCode.EC_500.errorMessage());
                        return new ResponseEntity<>(errorBean, HttpStatus.INTERNAL_SERVER_ERROR);
                      }
                    } else {
                      ErrorResponseUtil.getFailureResponse(
                          ErrorResponseUtil.ErrorCodes.STATUS_102.getValue(),
                          ErrorResponseUtil.ErrorCodes.INVALID_INPUT.getValue(),
                          ErrorResponseUtil.ErrorCodes.UNKNOWN_TOKEN.getValue(),
                          response);
                      errorBean = new ErrorBean();
                      errorBean.setCode(HttpStatus.BAD_REQUEST.value());
                      errorBean.setMessage(ErrorResponseUtil.ErrorCodes.UNKNOWN_TOKEN.getValue());
                      commonService.createAuditLog(
                          userId,
                          AppConstants.AUDIT_EVENT_ENROLL_FAIL_NAME,
                          AppConstants.AUDIT_EVENT_ENROLL_FAIL_DESC,
                          AppConstants.AUDIT_LOG_MOBILE_APP_CLIENT_ID,
                          "",
                          enrollmentBean.getStudyId(),
                          AppConstants.APP_LEVEL_ACCESS);
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
                    commonService.createAuditLog(
                        userId,
                        AppConstants.AUDIT_EVENT_ENROLL_FAIL_NAME,
                        AppConstants.AUDIT_EVENT_ENROLL_FAIL_DESC,
                        AppConstants.AUDIT_LOG_MOBILE_APP_CLIENT_ID,
                        "",
                        enrollmentBean.getStudyId(),
                        AppConstants.APP_LEVEL_ACCESS);
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
                  commonService.createAuditLog(
                      userId,
                      AppConstants.AUDIT_EVENT_ENROLL_FAIL_NAME,
                      AppConstants.AUDIT_EVENT_ENROLL_FAIL_DESC,
                      AppConstants.AUDIT_LOG_MOBILE_APP_CLIENT_ID,
                      "",
                      enrollmentBean.getStudyId(),
                      AppConstants.APP_LEVEL_ACCESS);
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
                commonService.createAuditLog(
                    userId,
                    AppConstants.AUDIT_EVENT_ENROLL_FAIL_NAME,
                    AppConstants.AUDIT_EVENT_ENROLL_FAIL_DESC,
                    AppConstants.AUDIT_LOG_MOBILE_APP_CLIENT_ID,
                    "",
                    enrollmentBean.getStudyId(),
                    AppConstants.APP_LEVEL_ACCESS);
                return null;
              }
            } else {
              if (enrollmentBean.getToken().isEmpty()) {
                tokenValue = TokenUtil.randomString(8);
              }
              respBean =
                  enrollmentTokenfService.enrollParticipant(
                      enrollmentBean.getStudyId(), tokenValue, userId);
              if (respBean != null) {
                respBean.setCode(ErrorCode.EC_200.code());
                respBean.setMessage(
                    MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());
                commonService.createAuditLog(
                    userId,
                    AppConstants.AUDIT_EVENT_APP_USER_ELIGIBLE_NAME,
                    String.format(
                        AppConstants.AUDIT_EVENT_APP_USER_ELIGIBLE_DESC,
                        AppConstants.OPEN_STUDY,
                        tokenValue),
                    AppConstants.AUDIT_LOG_MOBILE_APP_CLIENT_ID,
                    "",
                    enrollmentBean.getStudyId(),
                    AppConstants.APP_LEVEL_ACCESS);
              } else {
                commonService.createAuditLog(
                    userId,
                    AppConstants.AUDIT_EVENT_ENROLL_FAIL_NAME,
                    AppConstants.AUDIT_EVENT_ENROLL_FAIL_DESC,
                    AppConstants.AUDIT_LOG_MOBILE_APP_CLIENT_ID,
                    "",
                    enrollmentBean.getStudyId(),
                    AppConstants.APP_LEVEL_ACCESS);

                errorBean = new ErrorBean();
                errorBean.setCode(ErrorCode.EC_500.code());
                errorBean.setMessage(ErrorCode.EC_500.errorMessage());
                return new ResponseEntity<>(errorBean, HttpStatus.INTERNAL_SERVER_ERROR);
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
            commonService.createAuditLog(
                userId,
                AppConstants.AUDIT_EVENT_ENROLL_FAIL_NAME,
                AppConstants.AUDIT_EVENT_ENROLL_FAIL_DESC,
                AppConstants.AUDIT_LOG_MOBILE_APP_CLIENT_ID,
                "",
                "",
                AppConstants.APP_LEVEL_ACCESS);
            return null;
          }
        } else {
          errorBean = new ErrorBean();
          errorBean.setCode(ErrorCode.EC_102.code());
          errorBean.setMessage(ErrorResponseUtil.ErrorCodes.ERROR_REQUIRED.getValue());
          ErrorResponseUtil.getFailureResponse(
              ErrorResponseUtil.ErrorCodes.STATUS_102.getValue(),
              ErrorResponseUtil.ErrorCodes.INVALID_INPUT.getValue(),
              ErrorResponseUtil.ErrorCodes.ERROR_REQUIRED.getValue(),
              response);
          commonService.createAuditLog(
              userId,
              AppConstants.AUDIT_EVENT_ENROLL_FAIL_NAME,
              AppConstants.AUDIT_EVENT_ENROLL_FAIL_DESC,
              AppConstants.AUDIT_LOG_MOBILE_APP_CLIENT_ID,
              "",
              "",
              AppConstants.APP_LEVEL_ACCESS);
          return null;
        }
      } else {
        errorBean = new ErrorBean();
        errorBean.setCode(ErrorCode.EC_102.code());
        errorBean.setMessage(ErrorResponseUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue());

        ErrorResponseUtil.getFailureResponse(
            ErrorResponseUtil.ErrorCodes.STATUS_102.getValue(),
            ErrorResponseUtil.ErrorCodes.INVALID_INPUT.getValue(),
            ErrorResponseUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
            response);
        commonService.createAuditLog(
            userId,
            AppConstants.AUDIT_EVENT_ENROLL_FAIL_NAME,
            AppConstants.AUDIT_EVENT_ENROLL_FAIL_DESC,
            AppConstants.AUDIT_LOG_MOBILE_APP_CLIENT_ID,
            "",
            "",
            AppConstants.APP_LEVEL_ACCESS);
        return null;
      }
    } catch (InvalidRequestException e) {
      logger.error("EnrollmentTokenController enrollParticipant() - error ", e);
      errorBean = new ErrorBean();
      errorBean.setCode(ErrorCode.EC_102.code());
      errorBean.setMessage(ErrorResponseUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue());

      ErrorResponseUtil.getFailureResponse(
          ErrorResponseUtil.ErrorCodes.STATUS_102.getValue(),
          ErrorResponseUtil.ErrorCodes.INVALID_INPUT.getValue(),
          ErrorResponseUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
          response);
      commonService.createAuditLog(
          userId,
          AppConstants.AUDIT_EVENT_ENROLL_FAIL_NAME,
          AppConstants.AUDIT_EVENT_ENROLL_FAIL_DESC,
          AppConstants.AUDIT_LOG_MOBILE_APP_CLIENT_ID,
          "",
          "",
          AppConstants.APP_LEVEL_ACCESS);
      return null;
    } catch (UnAuthorizedRequestException e) {
      logger.error("EnrollmentTokenController enrollParticipant() - error ", e);
      errorBean = new ErrorBean();
      errorBean.setCode(ErrorCode.EC_401.code());
      errorBean.setMessage(ErrorCode.EC_401.errorMessage());

      ErrorResponseUtil.getFailureResponse(
          ErrorResponseUtil.ErrorCodes.STATUS_401.getValue(),
          ErrorResponseUtil.ErrorCodes.UNAUTHORIZED.getValue(),
          ErrorResponseUtil.ErrorCodes.UNAUTHORIZED_CLIENT.getValue(),
          response);
      commonService.createAuditLog(
          userId,
          AppConstants.AUDIT_EVENT_ENROLL_FAIL_NAME,
          AppConstants.AUDIT_EVENT_ENROLL_FAIL_DESC,
          AppConstants.AUDIT_LOG_MOBILE_APP_CLIENT_ID,
          "",
          "",
          AppConstants.APP_LEVEL_ACCESS);
      return null;
    } catch (Exception e) {
      logger.error("EnrollmentTokenController enrollParticipant() - error ", e);
      errorBean = new ErrorBean();
      errorBean.setCode(ErrorCode.EC_500.code());
      errorBean.setMessage(ErrorCode.EC_500.errorMessage());

      ErrorResponseUtil.getFailureResponse(
          ErrorResponseUtil.ErrorCodes.EC_500.getValue(),
          ErrorResponseUtil.ErrorCodes.UNKNOWN.getValue(),
          ErrorResponseUtil.ErrorCodes.INTERNAL_SERER_ERROR.getValue(),
          response);
      commonService.createAuditLog(
          userId,
          AppConstants.AUDIT_EVENT_ENROLL_FAIL_NAME,
          AppConstants.AUDIT_EVENT_ENROLL_FAIL_DESC,
          AppConstants.AUDIT_LOG_MOBILE_APP_CLIENT_ID,
          "",
          "",
          AppConstants.APP_LEVEL_ACCESS);
      return null;
    }
    logger.info("EnrollmentTokenController enrollParticipant() - Ends ");
    return new ResponseEntity<>(respBean, HttpStatus.OK);
  }
}
