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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import com.google.cloud.healthcare.fdamystudies.bean.VerifyEmailIdResponse;
import com.google.cloud.healthcare.fdamystudies.beans.EmailIdVerificationForm;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateAccountInfo;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateAccountInfoResponseBean;
import com.google.cloud.healthcare.fdamystudies.beans.VerifyCodeResponse;
import com.google.cloud.healthcare.fdamystudies.exceptions.InvalidEmailCodeException;
import com.google.cloud.healthcare.fdamystudies.exceptions.InvalidUserIdException;
import com.google.cloud.healthcare.fdamystudies.service.CommonService;
import com.google.cloud.healthcare.fdamystudies.service.FdaEaUserDetailsService;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import com.google.cloud.healthcare.fdamystudies.util.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;
import com.google.cloud.healthcare.fdamystudies.util.UserManagementUtil;

@RestController
public class VerifyEmailIdController {

  private static final Logger logger = LoggerFactory.getLogger(VerifyEmailIdController.class);

  @Autowired private FdaEaUserDetailsService userDetailsService;

  @Autowired private UserManagementUtil userManagementUtil;

  @Autowired private CommonService commonService;

  @PostMapping("/verifyEmailId")
  public ResponseEntity<?> verifyEmailId(
      @RequestBody EmailIdVerificationForm verificationForm,
      @RequestHeader("userId") String userId,
      @RequestHeader("clientToken") String clientToken,
      @RequestHeader("accessToken") String accessToken,
      @Context HttpServletResponse response) {
    logger.info("VerifyEmailIdController verifyEmailId() - starts");
    VerifyEmailIdResponse verifyEmailIdResponse = null;
    String verificationCode = "";

     if (StringUtils.isEmpty(clientToken)
        || StringUtils.isEmpty(accessToken)
        || StringUtils.isEmpty(userId)
        || StringUtils.isEmpty(verificationForm.getCode())) {

      MyStudiesUserRegUtil.getFailureResponse(
          400 + "",
          MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
          response);
      verifyEmailIdResponse = new VerifyEmailIdResponse();
      verifyEmailIdResponse.setCode(HttpStatus.BAD_REQUEST.value());
      verifyEmailIdResponse.setMessage(
          MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue());
      logger.info(AppConstants.VERIFY_EMAILID_CONTROLLER_ENDS_MESSAGE);
      return new ResponseEntity<>(verifyEmailIdResponse, HttpStatus.BAD_REQUEST);
    }
    try {
      verificationCode = verificationForm.getCode().trim(); // trim the surrounding whitespace.
      VerifyCodeResponse serviceResult = userDetailsService.verifyCode(verificationCode, userId);

      if (serviceResult != null && Boolean.TRUE.equals(serviceResult.getIsCodeVerified())) {
        UpdateAccountInfo accountStatus = new UpdateAccountInfo();
        accountStatus.setEmailVerified(true);
        UpdateAccountInfoResponseBean authResponse =
            userManagementUtil.updateUserInfoInAuthServer(
                accountStatus, userId, accessToken, clientToken);

        if (authResponse != null && "200".equals(authResponse.getCode())) {
          MyStudiesUserRegUtil.getFailureResponse(
              MyStudiesUserRegUtil.ErrorCodes.STATUS_200.getValue(),
              MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue(),
              MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue(),
              response);
          verifyEmailIdResponse = new VerifyEmailIdResponse();
          verifyEmailIdResponse.setCode(ErrorCode.EC_200.code());
          verifyEmailIdResponse.setMessage(ErrorCode.EC_200.errorMessage());
          verifyEmailIdResponse.setVerified(serviceResult.getIsCodeVerified());
          logger.info(AppConstants.VERIFY_EMAILID_CONTROLLER_ENDS_MESSAGE);
          commonService.createActivityLog(
              userId,
              "User Verification",
              "User verified for email " + serviceResult.getEmailId() + ".");
          return new ResponseEntity<>(verifyEmailIdResponse, HttpStatus.OK);

        } else if (authResponse != null && "400".equals(authResponse.getHttpStatusCode())) {
          if (AppConstants.INVALID_CLIENTID_SECRETKEY.equals(authResponse.getMessage())) {
            MyStudiesUserRegUtil.getFailureResponse(
                authResponse.getCode(),
                MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
                authResponse.getMessage(),
                response);
            verifyEmailIdResponse = new VerifyEmailIdResponse();
            verifyEmailIdResponse.setCode(HttpStatus.UNAUTHORIZED.value());
            verifyEmailIdResponse.setMessage(
                MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue());
            logger.info(AppConstants.VERIFY_EMAILID_CONTROLLER_ENDS_MESSAGE);
            return new ResponseEntity<>(authResponse, HttpStatus.UNAUTHORIZED);
          } else {
            MyStudiesUserRegUtil.getFailureResponse(
                authResponse.getCode(),
                MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
                authResponse.getMessage(),
                response);
            verifyEmailIdResponse = new VerifyEmailIdResponse();
            verifyEmailIdResponse.setCode(HttpStatus.BAD_REQUEST.value());
            verifyEmailIdResponse.setMessage(
                MyStudiesUserRegUtil.ErrorCodes.CONNECTION_ERROR_MSG.getValue());
            logger.info(AppConstants.VERIFY_EMAILID_CONTROLLER_ENDS_MESSAGE);
            return new ResponseEntity<>(authResponse, HttpStatus.BAD_REQUEST);
          }
        } else {
          MyStudiesUserRegUtil.getFailureResponse(
              500 + "",
              MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue(),
              MyStudiesUserRegUtil.ErrorCodes.CONNECTION_ERROR_MSG.getValue(),
              response);

          verifyEmailIdResponse = new VerifyEmailIdResponse();
          verifyEmailIdResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
          verifyEmailIdResponse.setMessage(
              MyStudiesUserRegUtil.ErrorCodes.CONNECTION_ERROR_MSG.getValue());
          logger.info(AppConstants.VERIFY_EMAILID_CONTROLLER_ENDS_MESSAGE);
          return new ResponseEntity<>(authResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
      } else throw new InvalidUserIdException(); // InvalidEmailCodeException
    } catch (InvalidUserIdException e) {
      MyStudiesUserRegUtil.getFailureResponse(
          400 + "",
          MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.INVALID_USER_ID.getValue(),
          response);

      verifyEmailIdResponse = new VerifyEmailIdResponse();
      verifyEmailIdResponse.setCode(HttpStatus.BAD_REQUEST.value());
      verifyEmailIdResponse.setMessage(MyStudiesUserRegUtil.ErrorCodes.INVALID_USER_ID.getValue());
      logger.error(AppConstants.VERIFY_EMAILID_CONTROLLER_ENDS_MESSAGE + ": ", e);
      return new ResponseEntity<>(verifyEmailIdResponse, HttpStatus.BAD_REQUEST);
    } catch (InvalidEmailCodeException e) {
      MyStudiesUserRegUtil.getFailureResponse(
          400 + "",
          MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.INVALID_EMAIL_CODE.getValue(),
          response);

      verifyEmailIdResponse = new VerifyEmailIdResponse();
      verifyEmailIdResponse.setCode(HttpStatus.BAD_REQUEST.value());
      verifyEmailIdResponse.setMessage(
          MyStudiesUserRegUtil.ErrorCodes.INVALID_EMAIL_CODE.getValue());
      logger.error(AppConstants.VERIFY_EMAILID_CONTROLLER_ENDS_MESSAGE + ": ", e);
      return new ResponseEntity<>(verifyEmailIdResponse, HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      // prepare system failure Response
      MyStudiesUserRegUtil.getFailureResponse(
          500 + "",
          MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.CONNECTION_ERROR_MSG.getValue(),
          response);

      verifyEmailIdResponse = new VerifyEmailIdResponse();
      verifyEmailIdResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
      verifyEmailIdResponse.setMessage(
          MyStudiesUserRegUtil.ErrorCodes.CONNECTION_ERROR_MSG.getValue());

      logger.error(AppConstants.VERIFY_EMAILID_CONTROLLER_ENDS_MESSAGE + ": ", e);
      return new ResponseEntity<>(verifyEmailIdResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
