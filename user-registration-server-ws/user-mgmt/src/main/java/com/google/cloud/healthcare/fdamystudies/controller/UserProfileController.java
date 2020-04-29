/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import java.time.LocalDateTime;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.cloud.healthcare.fdamystudies.beans.AppOrgInfoBean;
import com.google.cloud.healthcare.fdamystudies.beans.DeactivateAcctBean;
import com.google.cloud.healthcare.fdamystudies.beans.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.beans.LoginBean;
import com.google.cloud.healthcare.fdamystudies.beans.ResponseBean;
import com.google.cloud.healthcare.fdamystudies.beans.UserProfileRespBean;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequestBean;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsBO;
import com.google.cloud.healthcare.fdamystudies.service.CommonService;
import com.google.cloud.healthcare.fdamystudies.service.UserManagementProfileService;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import com.google.cloud.healthcare.fdamystudies.util.AppUtil;
import com.google.cloud.healthcare.fdamystudies.util.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;

@RestController
public class UserProfileController {

  private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

  @Autowired UserManagementProfileService userManagementProfService;

  @Autowired CommonService commonService;

  @Autowired ApplicationPropertyConfiguration appConfig;

  @Value("${email.code.expire_time}")
  private long expireTime;

  @RequestMapping(value = "/ping")
  public String ping() {
    logger.info(" UserProfileController - ping()  ");
    return "Mystudies UserRegistration Webservice User Management Bundle Started !!!";
  }

  @GetMapping(value = "/userProfile", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getUserProfile(
      @RequestHeader("userId") String userId, @Context HttpServletResponse response) {
    logger.info("UserProfileController getUserProfile() - starts ");
    UserProfileRespBean userPrlofileRespBean = null;
    try {
      userPrlofileRespBean = userManagementProfService.getParticipantInfoDetails(userId, 0, 0);
      if (userPrlofileRespBean != null) {
        commonService.createAuditLog(
            userId,
            "Read operation successful for user profile info",
            "App user's profile information read by Mobile App.  (Web Service name: userProfile) "
                + userId
                + " .",
            AppConstants.AUDIT_LOG_MOBILE_APP_CLIENT_ID,
            "",
            "",
            AppConstants.APP_LEVEL_ACCESS);
        userPrlofileRespBean.setMessage(
            MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());

      } else {
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.NO_DATA_AVAILABLE.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.NO_DATA_AVAILABLE.getValue(),
            response);
      }
    } catch (Exception e) {
      logger.error("UserProfileController getUserProfile() - error ", e);
      return AppUtil.httpResponseForInternalServerError();
    }
    logger.info("UserProfileController getUserProfile() - Ends ");
    return new ResponseEntity<>(userPrlofileRespBean, HttpStatus.OK);
  }

  @PostMapping(
      value = "/updateUserProfile",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> updateUserProfile(
      @RequestHeader("userId") String userId,
      @RequestBody UserRequestBean user,
      @Context HttpServletResponse response) {
    logger.info("UserProfileController updateUserProfile() - Starts ");
    ErrorBean errorBean = null;
    try {
      errorBean = userManagementProfService.updateUserProfile(userId, user);
      if (errorBean.getCode() == ErrorCode.EC_200.code()) {
        commonService.createAuditLog(
            userId,
            "App user profile update: success",
            "Profile/Preferences updated successfully for app user with User ID " + userId + " .",
            AppConstants.AUDIT_LOG_MOBILE_APP_CLIENT_ID,
            "",
            "",
            AppConstants.APP_LEVEL_ACCESS);
        errorBean = new ErrorBean(HttpStatus.OK.value(), ErrorCode.EC_30.errorMessage());
      } else {
        commonService.createAuditLog(
            userId,
            "App user profile update: failed ",
            "Profile/Preferences failed to update for app user with User ID " + userId + " .",
            AppConstants.AUDIT_LOG_MOBILE_APP_CLIENT_ID,
            "",
            "",
            AppConstants.APP_LEVEL_ACCESS);
        return new ResponseEntity<>(errorBean, HttpStatus.CONFLICT);
      }
    } catch (Exception e) {
      commonService.createAuditLog(
          userId,
          "App user profile update: failed ",
          "Profile/Preferences failed to update for app user with User ID " + userId + " .",
          AppConstants.AUDIT_LOG_MOBILE_APP_CLIENT_ID,
          "",
          "",
          AppConstants.APP_LEVEL_ACCESS);
      logger.error("UserProfileController getUserProfile() - error ", e);
      return AppUtil.httpResponseForInternalServerError();
    }
    logger.info("UserProfileController updateUserProfile() - Ends ");
    return new ResponseEntity<>(errorBean, HttpStatus.OK);
  }

  @DeleteMapping(
      value = "/deactivate",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> deactivateAccount(
      @RequestHeader("userId") String userId,
      @RequestHeader("accessToken") String accessToken,
      @RequestHeader("clientToken") String clientToken,
      @RequestBody DeactivateAcctBean deactivateAcctBean,
      @Context HttpServletResponse response) {
    logger.info("UserProfileController deactivateAccount() - Starts ");
    String message = MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue();
    ResponseBean responseBean = new ResponseBean();
    try {
      message =
          userManagementProfService.deActivateAcct(
              userId, deactivateAcctBean, accessToken, clientToken);
      if (message.equalsIgnoreCase(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue())) {
        commonService.createAuditLog(
            userId,
            "App user account deactivation success",
            "User account successfully deactivated for User ID " + userId + " .",
            AppConstants.AUDIT_LOG_MOBILE_APP_CLIENT_ID,
            "",
            "",
            AppConstants.APP_LEVEL_ACCESS);
        responseBean.setMessage(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());
      } else {
        commonService.createAuditLog(
            userId,
            "App user account deactivation failure",
            "User account deactivation failed for User ID " + userId + " .",
            AppConstants.AUDIT_LOG_MOBILE_APP_CLIENT_ID,
            "",
            "",
            AppConstants.APP_LEVEL_ACCESS);
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.STATUS_104.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue(),
            response);
        return null;
      }
    } catch (Exception e) {
      commonService.createAuditLog(
          userId,
          "App user account deactivation failure",
          "User account deactivation failed for User ID " + userId + " .",
          AppConstants.AUDIT_LOG_MOBILE_APP_CLIENT_ID,
          "",
          "",
          AppConstants.APP_LEVEL_ACCESS);
      logger.error("UserProfileController deactivateAccount() - error ", e);
    }
    logger.info("UserProfileController deactivateAccount() - Ends ");
    return new ResponseEntity<>(responseBean, HttpStatus.OK);
  }

  @PostMapping(
      value = "/resendConfirmation",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> resendConfirmation(
      @RequestHeader("appId") String appId,
      @RequestHeader("orgId") String orgId,
      @RequestBody LoginBean loginBean,
      @Context HttpServletResponse response) {
    logger.info("UserProfileController resendConfirmation() - Starts ");
    String isValidAppMsg = "";
    UserDetailsBO participantDetails = null;
    UserDetailsBO updParticipantDetails = null;
    int isSent = 0;
    String code = "";
    ResponseBean responseBean = new ResponseBean();
    try {
      if ((loginBean != null)
          && StringUtils.hasText(loginBean.getEmailId())
          && StringUtils.hasText(appId)
          && StringUtils.hasText(orgId)) {
        isValidAppMsg =
            commonService.validatedUserAppDetailsByAllApi("", loginBean.getEmailId(), appId, orgId);
        if (!StringUtils.isEmpty(isValidAppMsg)) {
          AppOrgInfoBean appOrgInfoBean =
              commonService.getUserAppDetailsByAllApi("", loginBean.getEmailId(), appId, orgId);
          if (appOrgInfoBean != null) {
            participantDetails =
                userManagementProfService.getParticipantDetailsByEmail(
                    loginBean.getEmailId(),
                    appOrgInfoBean.getAppInfoId(),
                    appOrgInfoBean.getOrgInfoId());
          }
          if (participantDetails != null) {
            commonService.createAuditLog(
                participantDetails.getUserId(),
                "Verification email: resend request received",
                "Request received for resend of verification email, from app user with email ID "
                    + loginBean.getEmailId()
                    + " .",
                AppConstants.AUDIT_LOG_MOBILE_APP_CLIENT_ID,
                "",
                "",
                AppConstants.APP_LEVEL_ACCESS);
            if (participantDetails.getStatus() == 2) {
              code = RandomStringUtils.randomAlphanumeric(6);
              participantDetails.setEmailCode(code);
              participantDetails.setCodeExpireDate(LocalDateTime.now().plusMinutes(expireTime));
              participantDetails.setVerificationDate(MyStudiesUserRegUtil.getCurrentUtilDateTime());
              updParticipantDetails = userManagementProfService.saveParticipant(participantDetails);
              if (updParticipantDetails != null) {
                isSent =
                    userManagementProfService.resendConfirmationthroughEmail(
                        appId, participantDetails.getEmailCode(), participantDetails.getEmail());
                if (isSent == 2) {

                  responseBean.setMessage(
                      MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());
                }
              }
            } else {
              MyStudiesUserRegUtil.getFailureResponse(
                  MyStudiesUserRegUtil.ErrorCodes.STATUS_103.getValue(),
                  MyStudiesUserRegUtil.ErrorCodes.USER_ALREADY_VERIFIED.getValue(),
                  MyStudiesUserRegUtil.ErrorCodes.USER_ALREADY_VERIFIED.getValue(),
                  response);
              return null;
            }
          } else {
            MyStudiesUserRegUtil.getFailureResponse(
                MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
                MyStudiesUserRegUtil.ErrorCodes.EMAIL_NOT_EXISTS.getValue(),
                MyStudiesUserRegUtil.ErrorCodes.EMAIL_NOT_EXISTS.getValue(),
                response);
            return null;
          }
        } else {
          MyStudiesUserRegUtil.getFailureResponse(
              MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
              MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
              MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
              response);
          return null;
        }
      } else {
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
            response);
        return null;
      }
    } catch (Exception e) {
      logger.error("UserProfileController resendConfirmation() - error ", e);
    }
    logger.info("UserProfileController resendConfirmation() - Ends ");
    return new ResponseEntity<>(responseBean, HttpStatus.OK);
  }
}
