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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import com.google.cloud.healthcare.fdamystudies.exceptions.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.UserDetails;
import com.google.cloud.healthcare.fdamystudies.service.FdaEaUserDetailsService;
import com.google.cloud.healthcare.fdamystudies.service.UserRegAdminUserService;
import com.google.cloud.healthcare.fdamystudies.util.EmailNotification;
import com.google.cloud.healthcare.fdamystudies.util.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;
import com.google.cloud.healthcare.fdamystudies.util.UserManagementUtil;

/**
 * Project Name: UserManagementServiceBundle
 *
 * @author Chiranjibi Dash
 */
@RestController
public class UserRegistrationController {

  private static final Logger logger = LoggerFactory.getLogger(UserRegistrationController.class);

  @Autowired private FdaEaUserDetailsService service;

  @Autowired private EmailNotification emailNotification;

  @Autowired private CommonDao profiledao;

  @Autowired private UserManagementUtil userManagementUtil;

  @Autowired private UserRegAdminUserService adminUserService;

  @Value("${email.code.expire_time}")
  private long expireTime;

  @GetMapping("/healthCheck")
  public ResponseEntity<?> healthCheck() {
    return ResponseEntity.ok("Up and Running");
  }

  /**
   * @author Chiranjibi Dash
   * @param userForm
   * @param applicationId
   * @param orgId
   * @param accessToken
   * @param response
   * @return ResponseEntity<?>
   */
  @PostMapping("/register")
  public ResponseEntity<?> registerUser(
      @RequestBody UserRegistrationForm userForm,
      @RequestHeader("appId") String appId,
      @RequestHeader("orgId") String orgId,
      @RequestHeader("clientId") String clientId,
      @RequestHeader("secretKey") String secretKey,
      @Context HttpServletResponse response) {

    logger.info("UserRegistrationController.registerUser() Started");
    UserRegistrationResponse registrationResponse = null;

    if ((clientId.length() == 0 || clientId == null && StringUtils.isEmpty(clientId))
        || (secretKey.length() == 0 || secretKey == null && StringUtils.isEmpty(secretKey))
        || (userForm.getEmailId() == null && StringUtils.isEmpty(userForm.getEmailId()))
        || (userForm.getPassword() == null && StringUtils.isEmpty(userForm.getPassword()))) {
      MyStudiesUserRegUtil.getFailureResponse(
          400 + "",
          MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
          response);
      registrationResponse = new UserRegistrationResponse();
      registrationResponse.setCode(400);
      registrationResponse.setMessage(
          MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue());
      logger.info("UserRegistrationController.registerUser() ENDED with BAD_REQUEST");
      return new ResponseEntity<>(registrationResponse, HttpStatus.BAD_REQUEST);
    }

    boolean emailResponse = false;
    try {

      if ((userForm.getEmailId() != null && StringUtils.isNotEmpty(userForm.getEmailId()))
          && (userForm.getPassword() != null && StringUtils.isNotEmpty(userForm.getPassword()))) {

        // SAVING USER INFORMATION in AUTH SERVER By calling registerUserInAuthServer()
        AuthRegistrationResponseBean authServerResponse =
            userManagementUtil.registerUserInAuthServer(
                userForm, appId, orgId, clientId, secretKey);
        logger.info("(C)...REGISTRATION RESPONSE BEAN: " + authServerResponse);

        if (authServerResponse != null && "OK".equals(authServerResponse.getMessage())) {
          logger.info("providerResponse: " + authServerResponse.getMessage());

          // save userId and email in the UserRegistration server

          UserDetails userDetailsUsingUserId = prepareUserDetails(authServerResponse.getUserId());
          if (userDetailsUsingUserId != null) {
            // prepare error response here
            MyStudiesUserRegUtil.getFailureResponse(
                400 + "",
                MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
                MyStudiesUserRegUtil.ErrorCodes.EMAIL_EXISTS.getValue(),
                response);

            registrationResponse = new UserRegistrationResponse();
            registrationResponse.setCode(400);
            registrationResponse.setMessage(
                MyStudiesUserRegUtil.ErrorCodes.EMAIL_EXISTS.getValue());
            logger.info("UserRegistrationController.registerUser() ENDED");
            return new ResponseEntity<>(registrationResponse, HttpStatus.BAD_REQUEST);
          }

          UserDetails userDetails = null;
          try {

            // prepare the UserDetails and save in DB
            logger.info("GETTING PRIMERY KEY OF APPID AND ORGID");
            AppOrgInfoBean appInfo = profiledao.getUserAppDetailsByAllApi(null, appId, orgId);
            logger.info(appInfo.getAppInfoId() + "   " + appInfo.getOrgInfoId());
            if (appInfo == null || appInfo.getAppInfoId() == 0) {
              logger.info(
                  "(URS)...DELETING record in Auth Server STARTED. Though appId and orgId are not valid in UserRegistration server");
              DeleteAccountInfoResponseBean deleteResponse =
                  userManagementUtil.deleteUserInfoInAuthServer(
                      authServerResponse.getUserId(),
                      authServerResponse.getClientToken(),
                      authServerResponse.getAccessToken());

              if (deleteResponse != null && "200".equals(deleteResponse.getCode())) {
                logger.info(
                    "Due to System failure in User Registration Server, user is deleted in Auth Server: "
                        + userForm.getEmailId());
                logger.info("(URS)...DELETING record in Auth Server ENDED.");
              }
              MyStudiesUserRegUtil.getFailureResponse(
                  401 + "",
                  MyStudiesUserRegUtil.ErrorCodes.UNAUTHORIZED.getValue(),
                  MyStudiesUserRegUtil.ErrorCodes.UNAUTHORIZED.getValue(),
                  response);

              registrationResponse = new UserRegistrationResponse();
              registrationResponse.setCode(401);
              registrationResponse.setMessage(
                  MyStudiesUserRegUtil.ErrorCodes.UNAUTHORIZED.getValue());
              logger.info("UserRegistrationController.registerUser() ENDED");
              return new ResponseEntity<>(registrationResponse, HttpStatus.UNAUTHORIZED);
            } else {
              userForm.setUserId(authServerResponse.getUserId());
              userForm.setPassword(null);

              userDetails = getUserDetails(userForm);
              userDetails.setAppInfoId(appInfo.getAppInfoId());
              userDetails.setEmailCode(RandomStringUtils.randomAlphanumeric(6));
              // set the Otp Expire Time
              userDetails.setCodeExpireDate(LocalDateTime.now().plusMinutes(expireTime));
              logger.info("USERDETAILS TO BE SAVED IN DB" + userDetails);
              UserDetails serviceResp = service.saveUser(userDetails);
              logger.info("(C)...serviceResp: " + serviceResp);
              // TODO: serviceResp null check
              if (serviceResp != null) {
                List<String> emailContent = prepareEmailContent(serviceResp.getEmailCode());
                logger.info("emailContent: " + emailContent);
                emailResponse =
                    emailNotification.sendEmailNotification(
                        emailContent.get(0),
                        emailContent.get(1),
                        serviceResp.getEmail(),
                        null,
                        null);
                logger.info("emailNotification; " + emailResponse);

                MyStudiesUserRegUtil.getFailureResponse(
                    MyStudiesUserRegUtil.ErrorCodes.STATUS_200.getValue(),
                    MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue(),
                    MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue(),
                    response);
                registrationResponse = new UserRegistrationResponse();
                registrationResponse.setCode(ErrorCode.EC_200.code());
                registrationResponse.setMessage(ErrorCode.EC_200.errorMessage());
                registrationResponse.setAccessToken(authServerResponse.getAccessToken());
                registrationResponse.setRefreshToken(authServerResponse.getRefreshToken());
                registrationResponse.setUserId(authServerResponse.getUserId());
                registrationResponse.setClientToken(authServerResponse.getClientToken());
                logger.info("UserRegistrationController.registerUser() ENDED");
                return new ResponseEntity<>(registrationResponse, HttpStatus.OK);
              } else throw new SystemException();
            }
          } catch (SystemException e) {
            logger.error("UserRegistrationController.registerUser(): ", e);
            logger.info(
                "(URS)...DELETING record in Auth Server STARTED. Though it could not able to save record in UserRegistration server");
            DeleteAccountInfoResponseBean deleteResponse =
                userManagementUtil.deleteUserInfoInAuthServer(
                    authServerResponse.getUserId(),
                    authServerResponse.getClientToken(),
                    authServerResponse.getClientToken());

            if (deleteResponse != null && "200".equals(deleteResponse.getCode())) {
              logger.info(
                  "Due to System failure in User Registration Server, user is deleted in Auth Server: "
                      + userForm.getEmailId());
              logger.info("(URS)...DELETING record in Auth Server ENDED.");
            }
            MyStudiesUserRegUtil.getFailureResponse(
                500 + "",
                MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue(),
                MyStudiesUserRegUtil.ErrorCodes.CONNECTION_ERROR_MSG.getValue(),
                response);
            logger.info("UserRegistrationController.registerUser() ENDED");
            return new ResponseEntity<>(registrationResponse, HttpStatus.INTERNAL_SERVER_ERROR);
          } catch (Exception e) {
            logger.error("UserRegistrationController.registerUser(): ", e);
            DeleteAccountInfoResponseBean deleteResponse =
                userManagementUtil.deleteUserInfoInAuthServer(
                    authServerResponse.getUserId(),
                    authServerResponse.getClientToken(),
                    authServerResponse.getClientToken());
            if (deleteResponse != null && "200".equals(deleteResponse.getCode())) {
              logger.info(
                  "Due to System failure in User Registration Server, user is deleted in Auth Server: "
                      + userForm.getEmailId());
              logger.info("(URS)...DELETING record in Auth Server ENDED.");
            }
            MyStudiesUserRegUtil.getFailureResponse(
                500 + "",
                MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue(),
                MyStudiesUserRegUtil.ErrorCodes.CONNECTION_ERROR_MSG.getValue(),
                response);

            registrationResponse = new UserRegistrationResponse();
            registrationResponse.setCode(500);
            registrationResponse.setMessage(
                MyStudiesUserRegUtil.ErrorCodes.CONNECTION_ERROR_MSG.getValue());
            logger.info("UserRegistrationController.registerUser() ENDED");
            return new ResponseEntity<>(registrationResponse, HttpStatus.INTERNAL_SERVER_ERROR);
          }

        } else {
          if ("400".equals(authServerResponse.getHttpStatusCode())) {
            MyStudiesUserRegUtil.getFailureResponse(
                authServerResponse.getCode(),
                authServerResponse.getTitle(),
                authServerResponse.getMessage(),
                response);

            registrationResponse = new UserRegistrationResponse();
            registrationResponse.setCode(400);
            registrationResponse.setMessage(authServerResponse.getMessage());
            logger.info("UserRegistrationController.registerUser() ENDED");
            return new ResponseEntity<>(registrationResponse, HttpStatus.BAD_REQUEST);
          } else if ("401".equals(authServerResponse.getHttpStatusCode())) {
            MyStudiesUserRegUtil.getFailureResponse(
                authServerResponse.getCode(),
                authServerResponse.getTitle(),
                authServerResponse.getMessage(),
                response);
            registrationResponse = new UserRegistrationResponse();
            registrationResponse.setCode(401);
            registrationResponse.setMessage(authServerResponse.getMessage());
            logger.info("UserRegistrationController.registerUser() ENDED");
            return new ResponseEntity<>(registrationResponse, HttpStatus.UNAUTHORIZED);
          } else {
            MyStudiesUserRegUtil.getFailureResponse(
                500 + "",
                MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue(),
                MyStudiesUserRegUtil.ErrorCodes.CONNECTION_ERROR_MSG.getValue(),
                response);

            registrationResponse = new UserRegistrationResponse();
            registrationResponse.setCode(500);
            registrationResponse.setMessage(
                MyStudiesUserRegUtil.ErrorCodes.CONNECTION_ERROR_MSG.getValue());
            logger.info("UserRegistrationController.registerUser() ENDED");
            return new ResponseEntity<>(registrationResponse, HttpStatus.INTERNAL_SERVER_ERROR);
          }
        }
      } else {
        MyStudiesUserRegUtil.getFailureResponse(
            400 + "",
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.USER_FORM_EMPTY.getValue(),
            response);
        registrationResponse = new UserRegistrationResponse();
        registrationResponse.setCode(400);
        registrationResponse.setMessage(MyStudiesUserRegUtil.ErrorCodes.USER_FORM_EMPTY.getValue());
        logger.info("UserRegistrationController.registerUser() ENDED");
        return new ResponseEntity<>(registrationResponse, HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      logger.error("UserRegistrationController.registerUser(): ", e);
      MyStudiesUserRegUtil.getFailureResponse(
          500 + "",
          MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.CONNECTION_ERROR_MSG.getValue(),
          response);
      registrationResponse = new UserRegistrationResponse();
      registrationResponse.setCode(500);
      registrationResponse.setMessage(
          MyStudiesUserRegUtil.ErrorCodes.CONNECTION_ERROR_MSG.getValue());
      logger.error("UserRegistrationController.registerUser() ENDED");
      return new ResponseEntity<>(registrationResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * @author Chiranjibi Dash
   * @param otp
   * @return List<String>
   */
  private List<String> prepareEmailContent(String otp) {
    List<String> emailContent = null;
    if (otp != null) {
      emailContent = new ArrayList<>();
      String message =
          "<html>"
              + "<body>"
              + "<div style='margin:20px;padding:10px;font-family: sans-serif;font-size: 14px;'>"
              + "<span>Hi,</span><br/><br/>"
              + "<span>Thank you for registering with us! We look forward to having you on board and actively taking part in<br/>research studies conducted by the FDA and its partners.</span><br/><br/>"
              + "<span>Your sign-up process is almost complete. Please use the verification code provided below to<br/>complete the Verification step in the mobile app. </span><br/><br/>"
              + "<span><strong>Verification Code: </strong>"
              + otp
              + "</span><br/><br/>"
              + "<span>This code can be used only once and is valid for a period of 48 hours only.</span><br/><br/>"
              + "<span>Please note that  registration (or sign up) for the app  is requested only to provide you with a <br/>seamless experience of using the app. Your registration information does not become part of <br/>the data collected for any study housed in the app. Each study has its own consent process <br/> and no data for any study will not be collected unless and until you provide an informed consent<br/> prior to joining the study </span><br/><br/>"
              + "<span>For any questions or assistance, please write to <a>FDAMyStudiesContact@harvardpilgrim.org</a> </span><br/><br/>"
              + "<span style='font-size:15px;'>Thanks,</span><br/><span>The FDA MyStudies Platform Team</span>"
              + "<br/><span>----------------------------------------------------</span><br/>"
              + "<span style='font-size:10px;'>PS - This is an auto-generated email. Please do not reply.</span>"
              + "</div>"
              + "</body>"
              + "</html>";
      String subject = "Welcome to the FDA MyStudies App!";
      emailContent.add(subject);
      emailContent.add(message);
      return emailContent;
    }
    return emailContent;
  }

  /**
   * @author Chiranjibi Dash
   * @param form
   * @param appId
   * @param orgId
   * @return UserDetails
   */
  private UserDetails getUserDetails(UserRegistrationForm form) {
    UserDetails userDetails = new UserDetails();

    userDetails.setStatus(2);
    userDetails.setVerificationDate(MyStudiesUserRegUtil.getCurrentUtilDateTime());
    // userDetails.setPasswordUpdatedDate(MyStudiesUserRegUtil.getCurrentUtilDateTime());

    if (form.getUserId() != null) userDetails.setUserId(form.getUserId());

    if (form.getEmailId() != null) userDetails.setEmail(form.getEmailId());

    if (form.isUsePassCode() != false) userDetails.setUsePassCode(form.isUsePassCode());

    if (form.isLocalNotification() != false)
      userDetails.setLocalNotificationFlag(form.isLocalNotification());

    if (form.isRemoteNotification() != false)
      userDetails.setRemoteNotificationFlag(form.isRemoteNotification());

    if (form.isTouchId() != false) userDetails.setTouchId(form.isTouchId());

    return userDetails;
  }

  /**
   * @author Chiranjibi Dash
   * @param userId0
   * @return UserDetails
   * @throws SystemException
   */
  private UserDetails prepareUserDetails(String userId) throws SystemException {
    UserDetails serviceResponse = null;
    if (userId != null) {
      serviceResponse = service.loadUserDetailsByUserId(userId);
      logger.info("loadUserDetailsByUserId(userId) called");
    }
    return serviceResponse;
  }
}
