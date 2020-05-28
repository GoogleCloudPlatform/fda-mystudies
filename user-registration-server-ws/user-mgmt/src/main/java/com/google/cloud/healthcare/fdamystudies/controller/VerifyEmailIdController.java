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
import com.google.cloud.healthcare.fdamystudies.beans.AppOrgInfoBean;
import com.google.cloud.healthcare.fdamystudies.beans.EmailIdVerificationForm;
import com.google.cloud.healthcare.fdamystudies.exceptions.InvalidEmailCodeException;
import com.google.cloud.healthcare.fdamystudies.exceptions.InvalidRequestException;
import com.google.cloud.healthcare.fdamystudies.exceptions.InvalidUserIdException;
import com.google.cloud.healthcare.fdamystudies.exceptions.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsBO;
import com.google.cloud.healthcare.fdamystudies.service.CommonService;
import com.google.cloud.healthcare.fdamystudies.service.FdaEaUserDetailsService;
import com.google.cloud.healthcare.fdamystudies.service.UserManagementProfileService;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import com.google.cloud.healthcare.fdamystudies.util.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;

@RestController
public class VerifyEmailIdController {

  private static final Logger logger = LoggerFactory.getLogger(VerifyEmailIdController.class);

  @Autowired private FdaEaUserDetailsService userDetailsService;

  @Autowired private CommonService commonService;

  @Autowired UserManagementProfileService userManagementProfService;

  @PostMapping("/verifyEmailId")
  public ResponseEntity<?> verifyEmailId(
      @RequestBody EmailIdVerificationForm verificationForm,
      @RequestHeader("appId") String appId,
      @RequestHeader("orgId") String orgId,
      @Context HttpServletResponse response) {

    logger.info("VerifyEmailIdController verifyEmailId() - starts");
    VerifyEmailIdResponse verifyEmailIdResponse = null;
    String isValidAppMsg = "";
    UserDetailsBO participantDetails = null;

    if (verificationForm == null
        || StringUtils.isBlank(verificationForm.getEmailId())
        || StringUtils.isBlank(appId)
        || StringUtils.isBlank(orgId)
        || StringUtils.isBlank(verificationForm.getCode())) {

      verifyEmailIdResponse = prepareResponse(response, AppConstants.MISSING_REQUIRED_PARAMETER);
      logger.info(AppConstants.VERIFY_EMAILID_CONTROLLER_ENDS_MESSAGE);
      return new ResponseEntity<>(verifyEmailIdResponse, HttpStatus.BAD_REQUEST);
    }
    try {
      isValidAppMsg =
          commonService.validatedUserAppDetailsByAllApi(
              "", verificationForm.getEmailId(), appId, orgId);

      if (!StringUtils.isNotEmpty(isValidAppMsg)) {
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
            response);
        return null;
      }
      AppOrgInfoBean appOrgInfoBean =
          commonService.getUserAppDetailsByAllApi("", verificationForm.getEmailId(), appId, orgId);
      if (appOrgInfoBean != null) {
        participantDetails =
            userManagementProfService.getParticipantDetailsByEmail(
                verificationForm.getEmailId(),
                appOrgInfoBean.getAppInfoId(),
                appOrgInfoBean.getOrgInfoId());
      }
      if (participantDetails == null) {
        return prepareEmailNotExistsErrorResponse(response);
      }
      boolean serviceResult =
          verifyEmailCode(verificationForm.getCode().trim(), participantDetails.getUserId());

      if (Boolean.TRUE.equals(serviceResult)) {
        boolean serviceResponse = changeStatusInBothServers(participantDetails);
        if (serviceResponse) {
          verifyEmailIdResponse = prepareResponse(response, AppConstants.SUCCESS_RESPONSE);
          return new ResponseEntity<>(verifyEmailIdResponse, HttpStatus.OK);
        } else {
          throw new SystemException();
        }
      } else {
        throw new InvalidUserIdException();
      }
    } catch (InvalidUserIdException e) {
      verifyEmailIdResponse = prepareResponse(response, AppConstants.INVALID_USERID_EXCEPTION);
      logger.error(AppConstants.VERIFY_EMAILID_CONTROLLER_ENDS_MESSAGE + ": ", e);
      return new ResponseEntity<>(verifyEmailIdResponse, HttpStatus.BAD_REQUEST);

    } catch (InvalidEmailCodeException e) {
      verifyEmailIdResponse = prepareResponse(response, AppConstants.INVALID_EMAIL_CODE_EXCEPTION);
      logger.error(AppConstants.VERIFY_EMAILID_CONTROLLER_ENDS_MESSAGE + ": ", e);
      return new ResponseEntity<>(verifyEmailIdResponse, HttpStatus.BAD_REQUEST);

    } catch (InvalidRequestException e) {
      verifyEmailIdResponse = prepareResponse(response, AppConstants.INVALID_REQUEST_EXCEPTION);
      logger.error(AppConstants.VERIFY_EMAILID_CONTROLLER_ENDS_MESSAGE + ": ", e);
      return new ResponseEntity<>(verifyEmailIdResponse, HttpStatus.BAD_REQUEST);

    } catch (Exception e) {
      verifyEmailIdResponse = prepareResponse(response, AppConstants.SYSTEM_EXCEPTION);
      logger.error(AppConstants.VERIFY_EMAILID_CONTROLLER_ENDS_MESSAGE + ": ", e);
      return new ResponseEntity<>(verifyEmailIdResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private VerifyEmailIdResponse prepareResponse(HttpServletResponse response, String flag) {
    VerifyEmailIdResponse verifyEmailIdResponse = new VerifyEmailIdResponse();
    switch (flag) {
      case AppConstants.MISSING_REQUIRED_PARAMETER:
      case AppConstants.INVALID_REQUEST_EXCEPTION:
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.STATUS_400.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
            response);
        verifyEmailIdResponse.setCode(HttpStatus.BAD_REQUEST.value());
        verifyEmailIdResponse.setMessage(
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue());
        return verifyEmailIdResponse;
      case AppConstants.INVALID_EMAIL_CODE_EXCEPTION:
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.STATUS_400.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_EMAIL_CODE.getValue(),
            response);
        verifyEmailIdResponse.setCode(HttpStatus.BAD_REQUEST.value());
        verifyEmailIdResponse.setMessage(
            MyStudiesUserRegUtil.ErrorCodes.INVALID_EMAIL_CODE.getValue());
        return verifyEmailIdResponse;
      case AppConstants.INVALID_USERID_EXCEPTION:
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.STATUS_400.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_USER_ID.getValue(),
            response);
        verifyEmailIdResponse.setCode(HttpStatus.BAD_REQUEST.value());
        verifyEmailIdResponse.setMessage(
            MyStudiesUserRegUtil.ErrorCodes.INVALID_USER_ID.getValue());
        return verifyEmailIdResponse;
      case AppConstants.SYSTEM_EXCEPTION:
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.EC_500.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.CONNECTION_ERROR_MSG.getValue(),
            response);
        verifyEmailIdResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        verifyEmailIdResponse.setMessage(
            MyStudiesUserRegUtil.ErrorCodes.CONNECTION_ERROR_MSG.getValue());
        return verifyEmailIdResponse;
      case AppConstants.SUCCESS_RESPONSE:
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.STATUS_200.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue(),
            response);
        verifyEmailIdResponse.setCode(ErrorCode.EC_200.code());
        verifyEmailIdResponse.setMessage(ErrorCode.EC_200.errorMessage());
        verifyEmailIdResponse.setVerified(true);
        return verifyEmailIdResponse;

      default:
        return null;
    }
  }

  /*
   * prepare email doesn't exists Error Response
   */
  private ResponseEntity<?> prepareEmailNotExistsErrorResponse(HttpServletResponse response) {
    MyStudiesUserRegUtil.getFailureResponse(
        MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
        MyStudiesUserRegUtil.ErrorCodes.EMAIL_NOT_EXISTS.getValue(),
        MyStudiesUserRegUtil.ErrorCodes.EMAIL_NOT_EXISTS.getValue(),
        response);
    return null;
  }

  /*
   * changing status in both servers
   */
  private boolean changeStatusInBothServers(UserDetailsBO participantDetails)
      throws InvalidRequestException, SystemException {
    if (participantDetails != null) {
      return userDetailsService.updateStatus(participantDetails);
    } else {
      return false;
    }
  }

  /*
   * verifying EmailCode(OTP)
   */
  private boolean verifyEmailCode(String verificationCode, String userId)
      throws SystemException, InvalidEmailCodeException, InvalidUserIdException {
    if (verificationCode == null || userId == null) {
      return false;
    }
    return userDetailsService.verifyCode(verificationCode, userId);
  }
}
