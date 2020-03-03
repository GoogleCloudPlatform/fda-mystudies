/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
/** */
package com.google.cloud.healthcare.fdamystudies.controller;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.SystemException;
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
import com.google.cloud.healthcare.fdamystudies.exceptions.InvalidUserIdOrEmailCodeException;
import com.google.cloud.healthcare.fdamystudies.service.FdaEaUserDetailsService;
import com.google.cloud.healthcare.fdamystudies.util.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;
import com.google.cloud.healthcare.fdamystudies.util.UserManagementUtil;

/**
 * Project Name: UserManagementServiceBundle
 *
 * @author Chiranjibi Dash
 */
@RestController
public class VerifyEmailIdController {

  private static final Logger logger = LoggerFactory.getLogger(VerifyEmailIdController.class);

  @Autowired private FdaEaUserDetailsService userDetailsService;

  @Autowired private UserManagementUtil userManagementUtil;

  @PostMapping("/verifyEmailId")
  public ResponseEntity<?> verifyEmailId(
      @RequestBody EmailIdVerificationForm verificationForm,
      @RequestHeader("userId") String userId,
      @RequestHeader("clientToken") String clientToken,
      @RequestHeader("accessToken") String accessToken,
      @Context HttpServletResponse response) {

    logger.info("(C)....VerifyEmailIdController.verifyUser()...STARTED");
    VerifyEmailIdResponse verifyEmailIdResponse = null;

    if ((clientToken.length() == 0 || clientToken == null && StringUtils.isEmpty(clientToken))
        || (accessToken.length() == 0 || accessToken == null && StringUtils.isEmpty(accessToken))
        || (userId.length() == 0 || userId == null && StringUtils.isEmpty(userId))
        || (verificationForm.getCode() == null
            && StringUtils.isEmpty(verificationForm.getCode()))) {
      MyStudiesUserRegUtil.getFailureResponse(
          400 + "",
          MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
          response);
      verifyEmailIdResponse = new VerifyEmailIdResponse();
      verifyEmailIdResponse.setCode(HttpStatus.BAD_REQUEST.value());
      verifyEmailIdResponse.setMessage(
          MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue());
      logger.info("(C)....VerifyEmailIdController.verifyUser()...ENDED");
      return new ResponseEntity<>(verifyEmailIdResponse, HttpStatus.BAD_REQUEST);
    }
    try {
      Integer verificationResponse =
          userManagementUtil.validateAccessToken(userId, accessToken, clientToken);
      if (verificationResponse != null && verificationResponse == 1) {
        // Success case
        try {
          boolean serviceResult = userDetailsService.verifyCode(verificationForm.getCode(), userId);
          if (serviceResult) {
            // Update Auth Server UserAccoutStatus
            UpdateAccountInfo accountStatus = new UpdateAccountInfo();
            accountStatus.setEmailVerified(true);
            UpdateAccountInfoResponseBean authResponse =
                userManagementUtil.updateUserInfoInAuthServer(
                    accountStatus, userId, accessToken, clientToken);
            if (authResponse != null && "200".equals(authResponse.getCode())) {
              // prepare Success response to mobile
              MyStudiesUserRegUtil.getFailureResponse(
                  MyStudiesUserRegUtil.ErrorCodes.STATUS_200.getValue(),
                  MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue(),
                  MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue(),
                  response);
              verifyEmailIdResponse = new VerifyEmailIdResponse();
              verifyEmailIdResponse.setCode(ErrorCode.EC_200.code());
              verifyEmailIdResponse.setMessage(ErrorCode.EC_200.errorMessage());
              verifyEmailIdResponse.setVerified(serviceResult);
              logger.info("(C)....VerifyEmailIdController.verifyUser()...ENDED");
              return new ResponseEntity<>(verifyEmailIdResponse, HttpStatus.OK);
            } else if ("400".equals(authResponse.getHttpStatusCode())) {
              if ("Invalid clientId or secretKey".equals(authResponse.getMessage())) {
                MyStudiesUserRegUtil.getFailureResponse(
                    authResponse.getCode(),
                    MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
                    authResponse.getMessage(),
                    response);
                verifyEmailIdResponse = new VerifyEmailIdResponse();
                verifyEmailIdResponse.setCode(HttpStatus.UNAUTHORIZED.value());
                verifyEmailIdResponse.setMessage(
                    MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue());
                logger.info("(C)....VerifyEmailIdController.verifyUser()...ENDED");
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
                logger.info("(C)....VerifyEmailIdController.verifyUser()...ENDED");
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
              logger.info("(C)....VerifyEmailIdController.verifyUser()...ENDED");
              return new ResponseEntity<>(authResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }
          } else {
            MyStudiesUserRegUtil.getFailureResponse(
                400 + "",
                MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
                MyStudiesUserRegUtil.ErrorCodes.INVALID_USER_ID_Or_EMAIL_CODE.getValue(),
                response);

            verifyEmailIdResponse = new VerifyEmailIdResponse();
            verifyEmailIdResponse.setCode(HttpStatus.BAD_REQUEST.value());
            verifyEmailIdResponse.setMessage(
                MyStudiesUserRegUtil.ErrorCodes.INVALID_USER_ID_Or_EMAIL_CODE.getValue());
            logger.info("(C)....VerifyEmailIdController.verifyUser()...ENDED");
            return new ResponseEntity<>(verifyEmailIdResponse, HttpStatus.BAD_REQUEST);
          }
        } catch (InvalidUserIdOrEmailCodeException e) {
          MyStudiesUserRegUtil.getFailureResponse(
              400 + "",
              MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
              MyStudiesUserRegUtil.ErrorCodes.INVALID_USER_ID_Or_EMAIL_CODE.getValue(),
              response);

          verifyEmailIdResponse = new VerifyEmailIdResponse();
          verifyEmailIdResponse.setCode(HttpStatus.BAD_REQUEST.value());
          verifyEmailIdResponse.setMessage(
              MyStudiesUserRegUtil.ErrorCodes.INVALID_USER_ID_Or_EMAIL_CODE.getValue());
          logger.error("(C)....VerifyEmailIdController.verifyUser()...ENDED");
          return new ResponseEntity<>(verifyEmailIdResponse, HttpStatus.BAD_REQUEST);
        } catch (SystemException e) {
          MyStudiesUserRegUtil.getFailureResponse(
              500 + "",
              MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue(),
              MyStudiesUserRegUtil.ErrorCodes.CONNECTION_ERROR_MSG.getValue(),
              response);

          verifyEmailIdResponse = new VerifyEmailIdResponse();
          verifyEmailIdResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
          verifyEmailIdResponse.setMessage(
              MyStudiesUserRegUtil.ErrorCodes.CONNECTION_ERROR_MSG.getValue());
          logger.error("(C)....VerifyEmailIdController.verifyUser()...ENDED");
          return new ResponseEntity<>(verifyEmailIdResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
      } else if (verificationResponse != null && verificationResponse == 0) {
        // prepare session expired response
        MyStudiesUserRegUtil.getFailureResponse(
            401 + "",
            MyStudiesUserRegUtil.ErrorCodes.UNAUTHORIZED.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.SESSION_EXPIRED_MSG.getValue(),
            response);

        verifyEmailIdResponse = new VerifyEmailIdResponse();
        verifyEmailIdResponse.setCode(HttpStatus.UNAUTHORIZED.value());
        verifyEmailIdResponse.setMessage(
            MyStudiesUserRegUtil.ErrorCodes.SESSION_EXPIRED_MSG.getValue());
        logger.info("(C)....VerifyEmailIdController.verifyUser()...ENDED");
        return new ResponseEntity<>(verifyEmailIdResponse, HttpStatus.UNAUTHORIZED);
      } else if (verificationResponse != null && verificationResponse == 2) {
        // prepare token invalid response
        MyStudiesUserRegUtil.getFailureResponse(
            401 + "",
            MyStudiesUserRegUtil.ErrorCodes.UNAUTHORIZED.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_ACCESS_TOKEN.getValue(),
            response);

        verifyEmailIdResponse = new VerifyEmailIdResponse();
        verifyEmailIdResponse.setCode(HttpStatus.UNAUTHORIZED.value());
        verifyEmailIdResponse.setMessage(
            MyStudiesUserRegUtil.ErrorCodes.INVALID_ACCESS_TOKEN.getValue());
        logger.info("(C)....VerifyEmailIdController.verifyUser()...ENDED");
        return new ResponseEntity<>(verifyEmailIdResponse, HttpStatus.UNAUTHORIZED);
      } else {
        // prepare invalid clientId or secretKey
        MyStudiesUserRegUtil.getFailureResponse(
            401 + "",
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_CLIENT_TOKEN.getValue(),
            response);

        verifyEmailIdResponse = new VerifyEmailIdResponse();
        verifyEmailIdResponse.setCode(HttpStatus.UNAUTHORIZED.value());
        verifyEmailIdResponse.setMessage(
            MyStudiesUserRegUtil.ErrorCodes.INVALID_CLIENT_TOKEN.getValue());
        logger.info("(C)....VerifyEmailIdController.verifyUser()...ENDED");
        return new ResponseEntity<>(verifyEmailIdResponse, HttpStatus.UNAUTHORIZED);
      }
    } catch (Exception e) {
      // prepare system failure Response
      logger.error("(C)....VerifyEmailIdController.verifyUser()...ENDED: ", e);
      MyStudiesUserRegUtil.getFailureResponse(
          500 + "",
          MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.CONNECTION_ERROR_MSG.getValue(),
          response);

      verifyEmailIdResponse = new VerifyEmailIdResponse();
      verifyEmailIdResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
      verifyEmailIdResponse.setMessage(
          MyStudiesUserRegUtil.ErrorCodes.CONNECTION_ERROR_MSG.getValue());

      logger.error("(C)....VerifyEmailIdController.verifyUser()...ENDED");
      return new ResponseEntity<>(verifyEmailIdResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
