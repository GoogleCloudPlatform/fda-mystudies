/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import com.google.cloud.healthcare.fdamystudies.beans.AppInfoBean;
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
import com.google.cloud.healthcare.fdamystudies.util.AppUtil;
import com.google.cloud.healthcare.fdamystudies.util.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        commonService.createActivityLog(
            userId,
            "PROFILE UPDATE",
            "User " + userId + " Profile/Preferences updated successfully.");
        errorBean = new ErrorBean(HttpStatus.OK.value(), ErrorCode.EC_30.errorMessage());
      } else {
        return new ResponseEntity<>(errorBean, HttpStatus.CONFLICT);
      }
    } catch (Exception e) {
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
        commonService.createActivityLog(
            userId,
            "ACCOUNT DELETE(Deactivation of an user)",
            "Account deactivated for user " + userId + ".");
        responseBean.setMessage(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());
      } else {
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.STATUS_104.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue(),
            response);
        return null;
      }
    } catch (Exception e) {
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
                  commonService.createActivityLog(
                      null,
                      "Requested Confirmation mail",
                      "Confirmation mail sent to email " + loginBean.getEmailId() + ".");
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

  @PutMapping(
      value = "/removeDeviceToken",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> removeDeviceToken(@RequestHeader("userId") String userId) {
    logger.info("UserProfileController removeDeviceToken() - Starts ");
    ErrorBean errorBean = null;
    try {
      errorBean = userManagementProfService.removeDeviceToken(userId);
      if (errorBean.getCode() == ErrorCode.EC_500.code()) {
        return new ResponseEntity<>(errorBean, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } catch (Exception e) {
      logger.error("UserProfileController removeDeviceToken() - error ", e);
      errorBean = new ErrorBean(ErrorCode.EC_500.code(), ErrorCode.EC_500.errorMessage());
      return new ResponseEntity<>(errorBean, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    logger.info("UserProfileController removeDeviceToken() - Ends ");
    return new ResponseEntity<>(errorBean, HttpStatus.OK);
  }

  @PostMapping(
      value = "/updateAppVersion",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> updateAppVersion(
      @RequestHeader("userId") String userId,
      @RequestBody AppInfoBean appInfo,
      @Context HttpServletResponse response) {
    logger.info("UserProfileController updateAppVersion() - Starts ");
    ErrorBean errorBean = null;
    if (org.apache.commons.lang3.StringUtils.isBlank(userId)
        || org.apache.commons.lang3.StringUtils.isBlank(appInfo.getOs())
        || org.apache.commons.lang3.StringUtils.isBlank(appInfo.getAppVersion())) {
      errorBean = new ErrorBean(ErrorCode.EC_711.code(), ErrorCode.EC_711.errorMessage());
      return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
    }
    try {
      errorBean = userManagementProfService.updateAppVersion(appInfo, userId);
      if (errorBean.getCode() == ErrorCode.EC_500.code()) {
        return new ResponseEntity<>(errorBean, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } catch (Exception e) {
      logger.error("UserProfileController updateAppVersion() - error ", e);
      return AppUtil.httpResponseForInternalServerError();
    }
    logger.info("UserProfileController updateAppVersion() - Ends ");
    return new ResponseEntity<>(errorBean, HttpStatus.OK);
  }
}
