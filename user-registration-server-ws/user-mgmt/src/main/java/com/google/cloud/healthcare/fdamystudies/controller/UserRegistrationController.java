/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.core.Context;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import com.google.cloud.healthcare.fdamystudies.bean.UserRegistrationResponse;
import com.google.cloud.healthcare.fdamystudies.beans.AppOrgInfoBean;
import com.google.cloud.healthcare.fdamystudies.beans.AuthRegistrationResponseBean;
import com.google.cloud.healthcare.fdamystudies.beans.DeleteAccountInfoResponseBean;
import com.google.cloud.healthcare.fdamystudies.beans.UserRegistrationForm;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import com.google.cloud.healthcare.fdamystudies.exceptions.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsBO;
import com.google.cloud.healthcare.fdamystudies.service.CommonService;
import com.google.cloud.healthcare.fdamystudies.service.FdaEaUserDetailsService;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import com.google.cloud.healthcare.fdamystudies.util.EmailNotification;
import com.google.cloud.healthcare.fdamystudies.util.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;
import com.google.cloud.healthcare.fdamystudies.util.UserDomainWhitelist;
import com.google.cloud.healthcare.fdamystudies.util.UserManagementUtil;

@RestController
public class UserRegistrationController {

  private static final Logger logger = LoggerFactory.getLogger(UserRegistrationController.class);

  @Autowired private FdaEaUserDetailsService service;

  @Autowired private EmailNotification emailNotification;

  @Autowired private CommonDao profiledao;

  @Autowired private UserDomainWhitelist userDomainWhitelist;

  @Autowired private UserManagementUtil userManagementUtil;

  @Autowired private CommonService commonService;

  @Autowired private ApplicationPropertyConfiguration appConfig;

  @Value("${email.code.expire_time}")
  private long expireTime;

  @GetMapping("/healthCheck")
  public ResponseEntity<?> healthCheck() {
    return ResponseEntity.ok("Up and Running");
  }

  @PostMapping("/register")
  public ResponseEntity<?> registerUser(
      @Valid @RequestBody UserRegistrationForm userForm,
      @RequestHeader("appId") String appId,
      @RequestHeader("orgId") String orgId,
      @RequestHeader("clientId") String clientId,
      @RequestHeader("secretKey") String secretKey,
      @Context HttpServletResponse response) {

    logger.info("UserRegistrationController registerUser() - starts");
    AuthRegistrationResponseBean authServerResponse = null;

    if (!userDomainWhitelist.isValidDomain(userForm.getEmailId())) {
      return makeServerError(401,
                             MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
                             MyStudiesUserRegUtil.ErrorCodes.DOMAIN_NOT_WHITELISTED.getValue(),
                             response);
    }

    try {
      authServerResponse =
            userManagementUtil.registerUserInAuthServer(
                userForm, appId, orgId, clientId, secretKey);

      if (authServerResponse == null || !"OK".equals(authServerResponse.getMessage())) {
        commonService.createActivityLog(null, AppConstants.USER_REGD_FAILURE,
                                        AppConstants.USER_REGD_FAILURE_DESC + userForm.getEmailId() + " .");
        return makeAuthServerErrorResponse(authServerResponse, response);
      }
      UserDetailsBO userDetailsUsingUserId = prepareUserDetails(authServerResponse.getUserId());
      if (userDetailsUsingUserId != null) {
        commonService.createActivityLog(
          null,
          AppConstants.USER_REGD_FAILURE,
          AppConstants.USER_REGD_FAILURE_DESC + userForm.getEmailId() + " .");
        return makeServerError(400, 
                               MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
                               MyStudiesUserRegUtil.ErrorCodes.EMAIL_EXISTS.getValue(),
                               response);
      }

      UserDetailsBO userDetailsBO = null;
      AppOrgInfoBean appInfo = profiledao.getUserAppDetailsByAllApi(null, appId, orgId);
      if (appInfo == null || appInfo.getAppInfoId() == 0) {
        commonService.createActivityLog(
          null,
          AppConstants.USER_REGD_FAILURE,
          AppConstants.USER_REGD_FAILURE_DESC + userForm.getEmailId() + " .");
        logger.info(
          "(URS)...DELETING record in Auth Server STARTED. Though appId and orgId are not valid in UserRegistration server");
        deleteUserFromAuthServer(authServerResponse, userForm.getEmailId());
        return makeServerError(401, 
                               MyStudiesUserRegUtil.ErrorCodes.UNAUTHORIZED.getValue(),
                               MyStudiesUserRegUtil.ErrorCodes.UNAUTHORIZED.getValue(),
                               response);
      } else {
        userForm.setUserId(authServerResponse.getUserId());
        userForm.setPassword(null);

        userDetailsBO = getUserDetails(userForm);
        userDetailsBO.setAppInfoId(appInfo.getAppInfoId());
        userDetailsBO.setEmailCode(RandomStringUtils.randomAlphanumeric(6));

        userDetailsBO.setCodeExpireDate(LocalDateTime.now().plusMinutes(expireTime));
        UserDetailsBO serviceResp = service.saveUser(userDetailsBO);

        if (serviceResp == null) {
          throw new SystemException();
        }

        commonService.createActivityLog(
          authServerResponse.getUserId(),
          "User Registration Success",
          "User Registration Successful for email" + serviceResp.getEmail() + ".");
        List<String> emailContent = prepareEmailContent(serviceResp.getEmailCode());

        if (emailContent != null && !emailContent.isEmpty()) {
          emailNotification.sendEmailNotification(
            emailContent.get(0), emailContent.get(1), serviceResp.getEmail(), null, null);
        }

        MyStudiesUserRegUtil.getFailureResponse(
          MyStudiesUserRegUtil.ErrorCodes.STATUS_200.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue(),
          response);
        UserRegistrationResponse registrationResponse = new UserRegistrationResponse();
        registrationResponse.setCode(ErrorCode.EC_200.code());
        registrationResponse.setMessage(ErrorCode.EC_200.errorMessage());
        registrationResponse.setAccessToken(authServerResponse.getAccessToken());
        registrationResponse.setRefreshToken(authServerResponse.getRefreshToken());
        registrationResponse.setUserId(authServerResponse.getUserId());
        registrationResponse.setClientToken(authServerResponse.getClientToken());
        logger.info(AppConstants.USER_REGISTRATION_CONTROLLER_ENDS_MESSAGE);
        return new ResponseEntity<>(registrationResponse, HttpStatus.OK);
      }
    } catch (Exception e) {
      logger.error("UserRegistrationController.registerUser(): ", e);
      commonService.createActivityLog(
          null,
          AppConstants.USER_REGD_FAILURE,
          AppConstants.USER_REGD_FAILURE_DESC + userForm.getEmailId() + " .");
      if (authServerResponse != null) {
        deleteUserFromAuthServer(authServerResponse, userForm.getEmailId());
      }

      logger.error("UserRegistrationController.registerUser() ENDED");
      return makeServerError(500, 
                             MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue(),
                             MyStudiesUserRegUtil.ErrorCodes.CONNECTION_ERROR_MSG.getValue(),
                             response);
    }
  }

  private ResponseEntity<?> makeServerError(Integer errorCode, String errorTitle, String errorMessage,
                                            HttpServletResponse response) {
    UserRegistrationResponse registrationResponse = new UserRegistrationResponse();
    MyStudiesUserRegUtil.getFailureResponse(
          errorCode + "", errorTitle,
          errorMessage, response);
    registrationResponse.setCode(errorCode);
    registrationResponse.setMessage(errorMessage);

    logger.info(AppConstants.USER_REGISTRATION_CONTROLLER_ENDS_MESSAGE);
    return new ResponseEntity<>(registrationResponse, toHttpStatus(errorCode));
  }

  void deleteUserFromAuthServer(AuthRegistrationResponseBean authServerResponse, String emailId) {
    DeleteAccountInfoResponseBean deleteResponse = 
      userManagementUtil.deleteUserInfoInAuthServer(
        authServerResponse.getUserId(),
        authServerResponse.getClientToken(),
        authServerResponse.getClientToken());

    if (deleteResponse != null && "200".equals(deleteResponse.getCode())) {
      logger.info(
        "Due to System failure in User Registration Server, user is deleted in Auth Server: "
          + emailId);
      logger.info("(URS)...DELETING record in Auth Server ENDED.");
    }
  }

  // Returns a UserRegistrationResponse for a failed AuthServerResponse.
  private ResponseEntity<?> makeAuthServerErrorResponse(AuthRegistrationResponseBean authServerResponse,
                                                        HttpServletResponse response) {
    UserRegistrationResponse registrationResponse = new UserRegistrationResponse();
    Integer httpCode = authServerResponse != null ? Integer.parseInt(authServerResponse.getHttpStatusCode())
                                                  : 500;
    switch (httpCode) {
      case 400:
      case 401:
        return makeServerError(httpCode,
                               authServerResponse.getTitle(),
                               authServerResponse.getMessage(),
                               response);
      default:
        return makeServerError(500,
          MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.CONNECTION_ERROR_MSG.getValue(),
          response);
    }
  }

  private List<String> prepareEmailContent(String otp) {
    List<String> emailContent = null;
    if (otp != null) {
      emailContent = new ArrayList<>();
      Map<String, String> emailMap = new HashMap<String, String>();
      emailMap.put("$securitytoken", otp);
      // TODO(#496): replace with actual study's org name.
      emailMap.put("$orgName", "Test Org");
      String subject = appConfig.getConfirmationMailSubject();
      String message = MyStudiesUserRegUtil.generateEmailContent(appConfig.getConfirmationMail(), emailMap);
      emailContent.add(subject);
      emailContent.add(message);
      return emailContent;
    }
    return emailContent;
  }

  private UserDetailsBO getUserDetails(UserRegistrationForm form) {
    UserDetailsBO userDetailsBO = new UserDetailsBO();

    userDetailsBO.setStatus(2);
    userDetailsBO.setVerificationDate(MyStudiesUserRegUtil.getCurrentUtilDateTime());

    if (form.getUserId() != null) userDetailsBO.setUserId(form.getUserId());

    if (form.getEmailId() != null) userDetailsBO.setEmail(form.getEmailId());

    if (form.isUsePassCode() != AppConstants.FALSE)
      userDetailsBO.setUsePassCode(form.isUsePassCode());

    if (form.isLocalNotification() != AppConstants.FALSE)
      userDetailsBO.setLocalNotificationFlag(form.isLocalNotification());

    if (form.isRemoteNotification() != AppConstants.FALSE)
      userDetailsBO.setRemoteNotificationFlag(form.isRemoteNotification());

    if (form.isTouchId() != AppConstants.FALSE) userDetailsBO.setTouchId(form.isTouchId());

    return userDetailsBO;
  }

  private UserDetailsBO prepareUserDetails(String userId) throws SystemException {
    UserDetailsBO serviceResponse = null;
    if (userId != null) {
      serviceResponse = service.loadUserDetailsByUserId(userId);
      logger.info("loadUserDetailsByUserId(userId) called");
    }
    return serviceResponse;
  }

  private HttpStatus toHttpStatus(Integer statusCode) {
    switch (statusCode) {
      case 400:
        return HttpStatus.BAD_REQUEST;
      case 401:
        return HttpStatus.UNAUTHORIZED;
      case 500:
      default:
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
  }
}
