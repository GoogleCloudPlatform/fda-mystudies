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
      if (enrollmentBean == null) {
        ErrorResponseUtil.getFailureResponse(
          ErrorResponseUtil.ErrorCodes.STATUS_102.getValue(),
          ErrorResponseUtil.ErrorCodes.INVALID_INPUT.getValue(),
          ErrorResponseUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
          response);
        return null;
      }

      if (StringUtils.isEmpty(enrollmentBean.getStudyId())) {
        ErrorResponseUtil.getFailureResponse(
            ErrorResponseUtil.ErrorCodes.STATUS_102.getValue(),
            ErrorResponseUtil.ErrorCodes.INVALID_INPUT.getValue(),
            ErrorResponseUtil.ErrorCodes.ERROR_REQUIRED.getValue(),
            response);
        return null;
      } 

      if (!enrollmentTokenfService.studyExists(enrollmentBean.getStudyId())) {
        logger.warn("ValidateEnrollmentTokenController validateEnrollmentToken() - studyID not exist:" + enrollmentBean.getStudyId());
        ErrorResponseUtil.getFailureResponse(
            ErrorResponseUtil.ErrorCodes.STATUS_103.getValue(),
            ErrorResponseUtil.ErrorCodes.INVALID_INPUT.getValue(),
            ErrorResponseUtil.ErrorCodes.STUDYID_NOT_EXIST.getValue(),
            response);
        return null;
      } 

      if (!StringUtils.isEmpty(enrollmentBean.getToken())) {
        if (enrollmentTokenfService.hasParticipant(
            enrollmentBean.getStudyId(), enrollmentBean.getToken())) {
          ErrorResponseUtil.getFailureResponse(
              ErrorResponseUtil.ErrorCodes.STATUS_103.getValue(),
              ErrorResponseUtil.ErrorCodes.INVALID_INPUT.getValue(),
              ErrorResponseUtil.ErrorCodes.TOKEN_ALREADY_USE.getValue(),
              response);
          return null;
        }

        if (!enrollManagementUtil.isChecksumValid(enrollmentBean.getToken())) {
          ErrorResponseUtil.getFailureResponse(
              ErrorResponseUtil.ErrorCodes.STATUS_102.getValue(),
              ErrorResponseUtil.ErrorCodes.INVALID_INPUT.getValue(),
              ErrorResponseUtil.ErrorCodes.INVALID_TOKEN.getValue(),
              response);
          return null;
        }

        if (!enrollmentTokenfService.isValidStudyToken(
            enrollmentBean.getToken(), enrollmentBean.getStudyId())) {
          ErrorResponseUtil.getFailureResponse(
              ErrorResponseUtil.ErrorCodes.STATUS_102.getValue(),
              ErrorResponseUtil.ErrorCodes.INVALID_INPUT.getValue(),
              ErrorResponseUtil.ErrorCodes.UNKNOWN_TOKEN.getValue(),
              response);
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
        return null;
      }

      errorBean = new ErrorBean();
      errorBean.setCode(ErrorCode.EC_200.code());
      errorBean.setMessage(ErrorResponseUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());

    } catch (Exception e) {
      logger.error("EnrollmentTokenController validateEnrollmentToken() - error ", e);
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
      if (enrollmentBean == null) {
        logger.error("EnrollmentTokenController enrollParticipant() - INVALID_INPUT_ERROR_MSG.");
        errorBean = new ErrorBean();
        errorBean.setCode(ErrorCode.EC_102.code());
        errorBean.setMessage(ErrorResponseUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue());

        ErrorResponseUtil.getFailureResponse(
            ErrorResponseUtil.ErrorCodes.STATUS_102.getValue(),
            ErrorResponseUtil.ErrorCodes.INVALID_INPUT.getValue(),
            ErrorResponseUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
            response);
        return null;
      }

      if (StringUtils.isEmpty(enrollmentBean.getStudyId())) {
        logger.error("EnrollmentTokenController enrollParticipant() - studyId missing.");
        errorBean = new ErrorBean();
        errorBean.setCode(ErrorCode.EC_102.code());
        errorBean.setMessage(ErrorResponseUtil.ErrorCodes.ERROR_REQUIRED.getValue());
        ErrorResponseUtil.getFailureResponse(
            ErrorResponseUtil.ErrorCodes.STATUS_102.getValue(),
            ErrorResponseUtil.ErrorCodes.INVALID_INPUT.getValue(),
            ErrorResponseUtil.ErrorCodes.ERROR_REQUIRED.getValue(),
            response);
        return null;
      }

      if (!enrollmentTokenfService.studyExists(enrollmentBean.getStudyId())) {
        logger.error("EnrollmentTokenController enrollParticipant() - STUDYID_NOT_EXIST ");
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

      if (enrollmentTokenfService.enrollmentTokenRequired(enrollmentBean.getStudyId())) {
        if (StringUtils.isEmpty(enrollmentBean.getToken())) {
          logger.error("EnrollmentTokenController enrollParticipant() - TOKEN_REQUIRED ");
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

        if (enrollmentTokenfService.hasParticipant(
            enrollmentBean.getStudyId(), enrollmentBean.getToken())) {
          logger.error("EnrollmentTokenController enrollParticipant() - TOKEN_ALREADY_USE ");
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

        if (!enrollManagementUtil.isChecksumValid(enrollmentBean.getToken())) {
          logger.error("EnrollmentTokenController enrollParticipant() - INVALID_TOKEN ");
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
        if (!enrollmentTokenfService.isValidStudyToken(
            enrollmentBean.getToken(), enrollmentBean.getStudyId())) {
              logger.error("EnrollmentTokenController enrollParticipant() - UNKNOWN_TOKEN ");
          ErrorResponseUtil.getFailureResponse(
              ErrorResponseUtil.ErrorCodes.STATUS_102.getValue(),
              ErrorResponseUtil.ErrorCodes.INVALID_INPUT.getValue(),
              ErrorResponseUtil.ErrorCodes.UNKNOWN_TOKEN.getValue(),
              response);
          errorBean = new ErrorBean();
          errorBean.setCode(HttpStatus.BAD_REQUEST.value());
          errorBean.setMessage(ErrorResponseUtil.ErrorCodes.UNKNOWN_TOKEN.getValue());
          return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
        }

        respBean =
            enrollmentTokenfService.enrollParticipant(
                enrollmentBean.getStudyId(), enrollmentBean.getToken(), userId);
        if (respBean != null) {
          respBean.setCode(ErrorCode.EC_200.code());
          respBean.setMessage(
              MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());
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
        }
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
      return null;
    }
    logger.info("EnrollmentTokenController enrollParticipant() - Ends ");
    return new ResponseEntity<>(respBean, HttpStatus.OK);
  }
}
