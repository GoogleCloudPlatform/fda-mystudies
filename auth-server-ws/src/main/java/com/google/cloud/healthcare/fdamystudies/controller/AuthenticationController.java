/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import com.google.cloud.healthcare.fdamystudies.bean.RefreshTokenBean;
import com.google.cloud.healthcare.fdamystudies.bean.ValidateClientCredentialsResponse;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.controller.bean.AuthInfoBean;
import com.google.cloud.healthcare.fdamystudies.controller.bean.AuthServerRegistrationResponse;
import com.google.cloud.healthcare.fdamystudies.controller.bean.ChangePasswordBean;
import com.google.cloud.healthcare.fdamystudies.controller.bean.DeleteUserResponse;
import com.google.cloud.healthcare.fdamystudies.controller.bean.ForgotPwdBean;
import com.google.cloud.healthcare.fdamystudies.controller.bean.LoginRequest;
import com.google.cloud.healthcare.fdamystudies.controller.bean.LoginResponse;
import com.google.cloud.healthcare.fdamystudies.controller.bean.LogoutResponse;
import com.google.cloud.healthcare.fdamystudies.controller.bean.RefreshTokenControllerResponse;
import com.google.cloud.healthcare.fdamystudies.controller.bean.RegisterUser;
import com.google.cloud.healthcare.fdamystudies.controller.bean.ResponseBean;
import com.google.cloud.healthcare.fdamystudies.controller.bean.UpdateInfo;
import com.google.cloud.healthcare.fdamystudies.controller.bean.UpdateUserAccountStatusResponse;
import com.google.cloud.healthcare.fdamystudies.exception.DuplicateUserRegistrationException;
import com.google.cloud.healthcare.fdamystudies.exception.EmailIdAlreadyVerifiedException;
import com.google.cloud.healthcare.fdamystudies.exception.InvalidClientException;
import com.google.cloud.healthcare.fdamystudies.exception.InvalidUserIdException;
import com.google.cloud.healthcare.fdamystudies.exception.PasswordExpiredException;
import com.google.cloud.healthcare.fdamystudies.exception.UserNotFoundException;
import com.google.cloud.healthcare.fdamystudies.model.AuthInfoBO;
import com.google.cloud.healthcare.fdamystudies.model.DaoUserBO;
import com.google.cloud.healthcare.fdamystudies.model.LoginAttemptsBO;
import com.google.cloud.healthcare.fdamystudies.service.ActivityLogService;
import com.google.cloud.healthcare.fdamystudies.service.ClientService;
import com.google.cloud.healthcare.fdamystudies.service.UserDetailsService;
import com.google.cloud.healthcare.fdamystudies.service.UserSessionService;
import com.google.cloud.healthcare.fdamystudies.service.bean.RefreshTokenServiceResponse;
import com.google.cloud.healthcare.fdamystudies.service.bean.ServiceRegistrationSuccessResponse;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.BeanUtil;
import com.google.cloud.healthcare.fdamystudies.utils.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.utils.JwtTokenUtil;
import com.google.cloud.healthcare.fdamystudies.utils.MyStudiesUserRegUtil;

@RestController
public class AuthenticationController {

  private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

  @Autowired private JwtTokenUtil jwtTokenUtil;

  @Autowired private UserDetailsService userDetailsService;

  @Autowired private UserSessionService session;

  @Autowired private ClientService clientInfoService;

  @Autowired ApplicationPropertyConfiguration appConfig;

  @Autowired private ActivityLogService activityLogService;

  @DeleteMapping("/deleteUser")
  public ResponseEntity<?> deleteUser(
      @RequestHeader("userId") String userId,
      @RequestHeader("clientToken") String clientToken,
      @RequestHeader("accessToken") String accessToken,
      @Context HttpServletResponse response) {
    logger.info("AuthenticationController deleteUser() - starts");
    DeleteUserResponse deleteUser = null;
    try {
      String message = userDetailsService.deleteUserDetails(userId);
      if (MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue().equalsIgnoreCase(message)) {
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.STATUS_200.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue(),
            response);
        deleteUser = new DeleteUserResponse();
        deleteUser.setCode(ErrorCode.EC_200.code());
        deleteUser.setMessage(ErrorCode.EC_200.errorMessage());
        logger.info("AuthenticationController deleteUser() - ends");
        return new ResponseEntity<>(deleteUser, HttpStatus.OK);
      } else {
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_USER_ID.getValue(),
            response);
        logger.info("AuthenticationController deleteUser() - ends with BAD_REQUEST");
        return new ResponseEntity<>(deleteUser, HttpStatus.BAD_REQUEST);
      }
    } catch (UserNotFoundException e) {
      MyStudiesUserRegUtil.getFailureResponse(
          400 + "",
          MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.INVALID_USER_ID.getValue(),
          response);
      logger.error("AuthenticationController deleteUser() - ends with BAD_REQUEST: ", e);
      return new ResponseEntity<>(deleteUser, HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      MyStudiesUserRegUtil.getFailureResponse(
          500 + "",
          MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue(),
          response);
      logger.info("AuthenticationController deleteUser() - ends with INTERNAL_SERVER_ERROR: ", e);
      return new ResponseEntity<>(deleteUser, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/updateStatus")
  public ResponseEntity<?> updateUserAccountStatus(
      @RequestBody UpdateInfo userInfo,
      @RequestHeader("userId") String userId,
      @RequestHeader("accessToken") String accessToken,
      @RequestHeader("clientToken") String clientToken,
      @Context HttpServletResponse response) {
    logger.info("AuthenticationController updateUserAccountStatus() - starts");
    UpdateUserAccountStatusResponse controllerResp = null;
    try {
      if (userInfo.isEmailVerified()) {
        String serviceResponse = userDetailsService.updateStatus(userInfo, userId);
        if (MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue().equalsIgnoreCase(serviceResponse)) {
          MyStudiesUserRegUtil.getFailureResponse(
              MyStudiesUserRegUtil.ErrorCodes.STATUS_200.getValue(),
              MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue(),
              MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue(),
              response);
          controllerResp = new UpdateUserAccountStatusResponse();
          controllerResp.setCode(ErrorCode.EC_200.code());
          controllerResp.setMessage(ErrorCode.EC_200.errorMessage());
          logger.info("AuthenticationController updateUserAccountStatus() - ends");
          return new ResponseEntity<>(controllerResp, HttpStatus.OK);
        } else {
          MyStudiesUserRegUtil.getFailureResponse(
              400 + "",
              MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
              MyStudiesUserRegUtil.ErrorCodes.EMAIL_ID_NOT_VERIFIED.getValue(),
              response);
          logger.info("AuthenticationController updateUserAccountStatus() - ends with BAD_REQUEST");
          return new ResponseEntity<>(controllerResp, HttpStatus.BAD_REQUEST);
        }

      } else {
        MyStudiesUserRegUtil.getFailureResponse(
            400 + "",
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.EMAIL_ID_NOT_VERIFIED.getValue(),
            response);
        logger.info("AuthenticationController updateUserAccountStatus() - ends with BAD_REQUEST");
        return new ResponseEntity<>(controllerResp, HttpStatus.BAD_REQUEST);
      }

    } catch (UserNotFoundException e) {
      MyStudiesUserRegUtil.getFailureResponse(
          400 + "",
          MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.INVALID_USER_ID.getValue(),
          response);
      logger.error(
          "AuthenticationController updateUserAccountStatus() - ends with BAD_REQUEST: ", e);
      return new ResponseEntity<>(controllerResp, HttpStatus.BAD_REQUEST);

    } catch (EmailIdAlreadyVerifiedException e) {
      MyStudiesUserRegUtil.getFailureResponse(
          400 + "",
          MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.EMAIL_ID_VERIFIED.getValue(),
          response);
      logger.error(
          "AuthenticationController updateUserAccountStatus() - ends with BAD_REQUEST: ", e);
      return new ResponseEntity<>(controllerResp, HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      MyStudiesUserRegUtil.getFailureResponse(
          500 + "",
          MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.SYSTEM_ERROR_FOUND.getValue(),
          response);
      logger.error(
          "AuthenticationController updateUserAccountStatus() - ends with INTERNAL_SERVER_ERROR: ",
          e);
      return new ResponseEntity<>(controllerResp, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/tokenAuthentication")
  public ResponseEntity<?> authenticateToken(
      @RequestHeader("accessToken") String accessToken,
      @RequestHeader("userId") String userId,
      @RequestHeader("clientToken") String clientToken,
      @Context HttpServletResponse response) {
    logger.info("AuthenticationController authenticate() - starts");
    boolean isvalidClient = false;
    Integer value = null;
    try {
      isvalidClient = clientInfoService.isValidClient(clientToken, userId);
      if (isvalidClient) {
        AuthInfoBean authInfo = BeanUtil.getBean(AuthInfoBean.class);
        authInfo.setAccessToken(accessToken);
        authInfo.setUserId(userId);
        value = userDetailsService.validateAccessToken(authInfo);
      } else {
        value = 3; // Invalid client
      }
    } catch (Exception e) {
      logger.error("AuthenticationController authenticate() - error ", e);
    }
    logger.info("AuthenticationController authenticate() - ends ");
    return ResponseEntity.ok(value);
  }

  @PostMapping("/validateClientCredentials")
  public ResponseEntity<?> validateClientCredentials(
      @RequestHeader("clientId") String clientId,
      @RequestHeader("secretKey") String secretKey,
      @Context HttpServletResponse response) {
    logger.info("AuthenticationController validateClientCredentials() - starts");

    ValidateClientCredentialsResponse responseEntity = null;
    if (((clientId.length() == 0) || (StringUtils.isEmpty(clientId)))
        || ((secretKey.length() == 0) || (StringUtils.isEmpty(secretKey)))) {
      MyStudiesUserRegUtil.getFailureResponse(
          401 + "",
          MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
          response);

      responseEntity = new ValidateClientCredentialsResponse();
      responseEntity.setCode(String.valueOf(HttpStatus.BAD_REQUEST.value()));
      responseEntity.setMessage(MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue());

      logger.info("AuthenticationController validateClientCredentials() - ends with BadRequest");
      return new ResponseEntity<>(responseEntity, HttpStatus.BAD_REQUEST);
    }
    String appCode = null;
    try {
      appCode = clientInfoService.checkClientInfo(clientId, secretKey);
      if (AppConstants.MA.equals(appCode)
          || AppConstants.USWS.equals(appCode)
          || AppConstants.URS.equals(appCode)
          || AppConstants.RS.equals(appCode)
          || AppConstants.WCP.equals(appCode)) {

        responseEntity = new ValidateClientCredentialsResponse();
        responseEntity.setCode(MyStudiesUserRegUtil.ErrorCodes.STATUS_200.getValue());
        responseEntity.setMessage(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue());
        responseEntity.setIsValidClient(true);
        return new ResponseEntity<>(responseEntity, HttpStatus.OK);
      } else {
        MyStudiesUserRegUtil.getFailureResponse(
            401 + "",
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_CLIENTID_OR_SECRET_KEY.getValue(),
            response);
        responseEntity = new ValidateClientCredentialsResponse();
        responseEntity.setCode(String.valueOf(HttpStatus.UNAUTHORIZED.value()));
        responseEntity.setMessage(
            MyStudiesUserRegUtil.ErrorCodes.INVALID_CLIENTID_OR_SECRET_KEY.getValue());
        return new ResponseEntity<>(responseEntity, HttpStatus.UNAUTHORIZED);
      }
    } catch (Exception e) {
      MyStudiesUserRegUtil.getFailureResponse(
          500 + "",
          MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.SYSTEM_ERROR_FOUND.getValue(),
          response);

      responseEntity = new ValidateClientCredentialsResponse();
      responseEntity.setCode(MyStudiesUserRegUtil.ErrorCodes.STATUS_500.getValue());
      responseEntity.setMessage(
          MyStudiesUserRegUtil.ErrorCodes.INVALID_CLIENTID_OR_SECRET_KEY.getValue());
      logger.error("AuthenticationController validateClientCredentials() - error ", e);
      return new ResponseEntity<>(responseEntity, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/getRefreshedToken")
  public ResponseEntity<?> getRefreshedToken(
      @RequestBody RefreshTokenBean refreshToken,
      @RequestHeader("userId") String userId,
      @RequestHeader("clientId") String clientId,
      @RequestHeader("secretKey") String secretKey,
      @Context HttpServletResponse response) {

    logger.info("AuthenticationController getRefreshedToken() - starts");
    RefreshTokenControllerResponse responseEntity = null;
    if (((clientId.length() == 0) || (StringUtils.isEmpty(clientId)))
        || ((secretKey.length() == 0) || (StringUtils.isEmpty(secretKey)))
        || ((userId.length() == 0) || (StringUtils.isEmpty(userId)))
        || ((refreshToken.getRefreshToken() == null)
            && StringUtils.isEmpty(refreshToken.getRefreshToken()))) {
      MyStudiesUserRegUtil.getFailureResponse(
          401 + "",
          MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
          response);
      responseEntity = new RefreshTokenControllerResponse();
      responseEntity.setCode(HttpStatus.BAD_REQUEST.value());
      responseEntity.setMessage(MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue());
      logger.info("AuthenticationController getRefreshedToken() - ends with BadRequest");
      return new ResponseEntity<>(responseEntity, HttpStatus.BAD_REQUEST);
    }
    try {
      String appCode = clientInfoService.checkClientInfo(clientId, secretKey);
      if (appCode == null) {
        MyStudiesUserRegUtil.getFailureResponse(
            401 + "",
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_CLIENT_TOKEN.getValue(),
            response);
        responseEntity = new RefreshTokenControllerResponse();
        responseEntity.setCode(HttpStatus.UNAUTHORIZED.value());
        responseEntity.setMessage(MyStudiesUserRegUtil.ErrorCodes.INVALID_CLIENT_TOKEN.getValue());
        logger.info(
            "AuthenticationController getRefreshedToken() - ends with UNAUTHORIZED request");
        return new ResponseEntity<>(responseEntity, HttpStatus.UNAUTHORIZED);
      }
      RefreshTokenServiceResponse serviceResponse = null;
      serviceResponse = userDetailsService.generateNewTokens(refreshToken, userId, appCode);
      if (serviceResponse != null) {
        responseEntity = new RefreshTokenControllerResponse();
        responseEntity.setCode(ErrorCode.EC_200.code());
        responseEntity.setMessage(ErrorCode.EC_200.errorMessage());
        responseEntity.setAccessToken(serviceResponse.getAccessToken());
        responseEntity.setRefreshToken(serviceResponse.getRefreshToken());
        responseEntity.setClientToken(serviceResponse.getClientToken());
        responseEntity.setUserId(serviceResponse.getUserId());
        logger.info("AuthenticationController getRefreshedToken() - ends");
        return ResponseEntity.ok().body(responseEntity);
      } else {
        MyStudiesUserRegUtil.getFailureResponse(
            500 + "",
            MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.ACCESS_TOKEN_GENERATION_ERROR.getValue(),
            response);
        responseEntity = new RefreshTokenControllerResponse();
        responseEntity.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        responseEntity.setMessage(ErrorCode.EC_138.errorMessage());
        logger.info(
            "AuthenticationController getRefreshedToken() - ends with INTERNAL_SERVER_ERROR");
        return new ResponseEntity<>(responseEntity, HttpStatus.INTERNAL_SERVER_ERROR);
      }

    } catch (UserNotFoundException e) {
      MyStudiesUserRegUtil.getFailureResponse(
          400 + "",
          MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
          //            MyStudiesUserRegUtil.ErrorCodes.INVALID_REFRESH_TOKEN.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.SESSION_EXPIRED_MSG.getValue(),
          response);
      responseEntity = new RefreshTokenControllerResponse();
      responseEntity.setCode(HttpStatus.UNAUTHORIZED.value());
      responseEntity.setMessage(ErrorCode.EC_1003.errorMessage());

      activityLogService.createActivityLog(
          userId,
          AppConstants.AUDIT_EVENT_FAILED_REFRESH_TOKEN_NAME,
          AppConstants.AUDIT_EVENT_FAILED_REFRESH_TOKEN_DESC);
      logger.error(
          "AuthenticationController getRefreshedToken() - error with UNAUTHORIZED request: ", e);
      return new ResponseEntity<>(responseEntity, HttpStatus.UNAUTHORIZED);

    } catch (InvalidUserIdException e) {
      MyStudiesUserRegUtil.getFailureResponse(
          400 + "",
          MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.INVALID_USER_ID.getValue(),
          response);
      responseEntity = new RefreshTokenControllerResponse();
      responseEntity.setCode(HttpStatus.UNAUTHORIZED.value());
      responseEntity.setMessage(ErrorCode.EC_134.errorMessage());
      logger.error("AuthenticationController getRefreshedToken() - error with UNAUTHORIZED: ", e);
      return new ResponseEntity<>(responseEntity, HttpStatus.UNAUTHORIZED);

    } catch (InvalidClientException e) {
      MyStudiesUserRegUtil.getFailureResponse(
          401 + "",
          MyStudiesUserRegUtil.ErrorCodes.UNAUTHORIZED.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.INVALID_CLIENTID_OR_SECRET_KEY.getValue(),
          response);
      responseEntity = new RefreshTokenControllerResponse();
      responseEntity.setCode(HttpStatus.UNAUTHORIZED.value());
      responseEntity.setMessage(ErrorCode.EC_118.errorMessage());
      logger.error("AuthenticationController getRefreshedToken() - error with UNAUTHORIZED: ", e);
      return new ResponseEntity<>(responseEntity, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      MyStudiesUserRegUtil.getFailureResponse(
          500 + "",
          MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.SYSTEM_ERROR_FOUND.getValue(),
          response);
      responseEntity = new RefreshTokenControllerResponse();
      responseEntity.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
      responseEntity.setMessage(ErrorCode.EC_118.errorMessage());
      logger.error(
          "AuthenticationController getRefreshedToken() - error with INTERNAL_SERVER_ERROR: ", e);
      return new ResponseEntity<>(responseEntity, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/register")
  public ResponseEntity<?> registerUser(
      @RequestBody RegisterUser user,
      @RequestHeader("appId") String appId,
      @RequestHeader("orgId") String orgId,
      @RequestHeader("clientId") String clientId,
      @RequestHeader("secretKey") String secretKey,
      @Context HttpServletResponse response) {

    logger.info("AuthenticationController registerUser() - starts");
    AuthServerRegistrationResponse controllerResp = null;
    try {
      String appCode = null;
      if (((clientId.length() == 0) || StringUtils.isBlank(clientId))
          || ((secretKey.length() == 0) || StringUtils.isBlank(secretKey))) {

        MyStudiesUserRegUtil.getFailureResponse(
            400 + "",
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
            response);
        logger.info("AuthenticationController registerUser() - ends with BAD_REQUEST");
        return new ResponseEntity<>(controllerResp, HttpStatus.BAD_REQUEST);
      }

      if (((user.getEmailId() == null) && StringUtils.isBlank(user.getEmailId()))
          || (!MyStudiesUserRegUtil.isValidEmailId(user.getEmailId()))) {
        MyStudiesUserRegUtil.getFailureResponse(
            400 + "",
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_EMAIL_ID.getValue(),
            response);
        logger.info("AuthenticationController registerUser() - ends with BAD_REQUEST");
        return new ResponseEntity<>(controllerResp, HttpStatus.BAD_REQUEST);
      }

      if (((user.getPassword() == null) && StringUtils.isBlank(user.getPassword()))
          || (!MyStudiesUserRegUtil.isPasswordStrong(user.getPassword()))
          || (user.getEmailId().equalsIgnoreCase(user.getPassword()))) {
        MyStudiesUserRegUtil.getFailureResponse(
            400 + "",
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.NEW_PASSWORD_IS_INVALID.getValue(),
            response);
        logger.info("AuthenticationController registerUser() - ends with BAD_REQUEST");
        return new ResponseEntity<>(controllerResp, HttpStatus.BAD_REQUEST);
      }

      appCode = clientInfoService.checkClientInfo(clientId, secretKey);
      logger.info(appCode);
      if (appCode == null || StringUtils.isEmpty(appCode)) {
        MyStudiesUserRegUtil.getFailureResponse(
            401 + "",
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_CLIENTID_OR_SECRET_KEY.getValue(),
            response);
        logger.info(
            "AuthenticationController registerUser() - ends with INVALID CLIENTID OR SECRET KEY");
        return new ResponseEntity<>(controllerResp, HttpStatus.UNAUTHORIZED);
      }
      if ("MA".equals(appCode)) {
        if (((appId.length() == 0) || StringUtils.isBlank(appId))
            || ((orgId.length() == 0) || StringUtils.isBlank(orgId))) {
          MyStudiesUserRegUtil.getFailureResponse(
              400 + "",
              MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
              MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
              response);
          logger.info("AuthenticationController registerUser() - ends");
          return new ResponseEntity<>(controllerResp, HttpStatus.BAD_REQUEST);
        }
      }

      ServiceRegistrationSuccessResponse serviceResp = null;
      if (!"MA".equals(appCode)) {
        serviceResp = userDetailsService.save(user, null, null, appCode);
      } else {
        serviceResp = userDetailsService.save(user, appId, orgId, appCode);
      }
      if (serviceResp != null && serviceResp.getDaoUser() != null) {
        controllerResp = prepareSuccessResponse(serviceResp, appCode);
      }
      return ResponseEntity.ok().body(controllerResp);

    } catch (DuplicateUserRegistrationException e) {
      MyStudiesUserRegUtil.getFailureResponse(
          400 + "",
          MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.EMAIL_EXISTS.getValue(),
          response);
      logger.error(
          "AuthenticationController getRefreshedToken() - error with DUPLICATE REGISTRATION: ", e);
      return new ResponseEntity<>(controllerResp, HttpStatus.BAD_REQUEST);

    } catch (Exception e) {
      MyStudiesUserRegUtil.getFailureResponse(
          500 + "",
          MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.SYSTEM_ERROR_FOUND.getValue(),
          response);
      logger.error(
          "AuthenticationController getRefreshedToken() - error with INTERNAL_SERVER_ERROR: ", e);
      return new ResponseEntity<>(controllerResp, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private AuthServerRegistrationResponse prepareSuccessResponse(
      ServiceRegistrationSuccessResponse serviceResp, String appCode) {
    logger.info("AuthenticationController prepareSuccessResponse() - starts");
    AuthServerRegistrationResponse controllerResp = new AuthServerRegistrationResponse();
    controllerResp.setUserId(serviceResp.getDaoUser().getUserId());
    controllerResp.setAccessToken(serviceResp.getAccessToken());
    controllerResp.setRefreshToken(serviceResp.getRefreshToken());
    controllerResp.setClientToken(serviceResp.getClientToken());
    controllerResp.setAppCode(appCode);
    controllerResp.setCode(ErrorCode.EC_200.code());
    controllerResp.setMessage(ErrorCode.EC_200.errorMessage());
    logger.info("AuthenticationController prepareSuccessResponse() - ends");
    return controllerResp;
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(
      @RequestBody LoginRequest loginRequest,
      @RequestHeader("clientId") String clientId,
      @RequestHeader("secretKey") String secretKey,
      @RequestHeader("appId") String appId,
      @RequestHeader("orgId") String orgId,
      @Context HttpServletResponse response) {
    logger.info("AuthenticationController login() - starts");
    LoginResponse loginResp = null;
    String appCode = null;
    DaoUserBO participantDetails = null;
    Integer maxAttemptsCount = Integer.valueOf(appConfig.getMaxLoginAttempts());
    try {
      if (((clientId.length() == 0) || StringUtils.isBlank(clientId))
          || ((secretKey.length() == 0) || StringUtils.isBlank(secretKey))) {

        MyStudiesUserRegUtil.getFailureResponse(
            401 + "",
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
            response);
        logger.info("AuthenticationController login() - ends with UNAUTHORIZED request");
        return new ResponseEntity<>(loginResp, HttpStatus.UNAUTHORIZED);
      }
      if (((loginRequest.getEmailId() == null) && StringUtils.isBlank(loginRequest.getEmailId()))
          || (loginRequest.getPassword() == null)) {
        MyStudiesUserRegUtil.getFailureResponse(
            400 + "",
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
            response);
        logger.info("AuthenticationController login() - ends with BAD_REQUEST");
        return new ResponseEntity<>(loginResp, HttpStatus.BAD_REQUEST);
      }
      appCode = clientInfoService.checkClientInfo(clientId, secretKey);

      if (appCode == null || StringUtils.isBlank(appCode)) {
        MyStudiesUserRegUtil.getFailureResponse(
            401 + "",
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_CLIENTID_OR_SECRET_KEY.getValue(),
            response);
        logger.info("AuthenticationController login() - ends with INVALID CLIENTID OR SECRET KEY");
        return new ResponseEntity<>(loginResp, HttpStatus.UNAUTHORIZED);
      }

      if ("MA".equals(appCode)) {
        if (((appId.length() == 0) || StringUtils.isBlank(appId))
            || ((orgId.length() == 0) || StringUtils.isBlank(orgId))) {
          MyStudiesUserRegUtil.getFailureResponse(
              400 + "",
              MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
              MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
              response);
          logger.info("AuthenticationController login() - ends with INVALID_INPUT");
          return new ResponseEntity<>(loginResp, HttpStatus.BAD_REQUEST);
        }
      }

      if (!"MA".equals(appCode)) {
        participantDetails =
            userDetailsService.loadUserByEmailIdAndAppCode(loginRequest.getEmailId(), appCode);
      } else {
        participantDetails =
            userDetailsService.loadUserByEmailIdAndAppIdAndOrgIdAndAppCode(
                loginRequest.getEmailId(), appId, orgId, appCode);
      }
      if (participantDetails != null
          && participantDetails.getAccountStatus().equalsIgnoreCase(AppConstants.ACTIVE)) {

        LoginAttemptsBO loginAttempts =
            userDetailsService.getLoginAttempts(loginRequest.getEmailId());
        Set<GrantedAuthority> roles = new HashSet<>();
        UserDetails userDetails =
            new User(participantDetails.getEmailId(), participantDetails.getPassword(), roles);

        if (loginAttempts != null && loginAttempts.getAttempts() == maxAttemptsCount) {
          int count = Integer.parseInt(appConfig.getExpirationLoginAttemptsMinute());
          Date attemptsExpireDate =
              MyStudiesUserRegUtil.addMinutes(loginAttempts.getLastModified().toString(), count);

          if (attemptsExpireDate.before(MyStudiesUserRegUtil.getCurrentUtilDateTime())
              || attemptsExpireDate.equals(MyStudiesUserRegUtil.getCurrentUtilDateTime())) {

            userDetailsService.resetLoginAttempts(loginRequest.getEmailId());
            loginResp =
                getLoginInformation(
                    participantDetails,
                    loginRequest.getEmailId(),
                    loginRequest.getPassword(),
                    maxAttemptsCount,
                    appId,
                    orgId,
                    appCode,
                    userDetails,
                    response);
          } else {
            loginResp = new LoginResponse();
            MyStudiesUserRegUtil.getFailureResponse(
                MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
                MyStudiesUserRegUtil.ErrorCodes.ACCOUNT_LOCKED.name(),
                MyStudiesUserRegUtil.ErrorCodes.ACCOUNT_LOCKED.getValue(),
                response);
            loginResp.setCode(ErrorCode.EC_102.code());
            loginResp.setMessage(MyStudiesUserRegUtil.ErrorCodes.ACCOUNT_LOCKED.getValue());
            logger.info("AuthenticationController login() - ends with ACCOUNT_LOCKED");
            return new ResponseEntity<>(loginResp, HttpStatus.BAD_REQUEST);
          }
        } else {
          loginResp =
              getLoginInformation(
                  participantDetails,
                  loginRequest.getEmailId(),
                  loginRequest.getPassword(),
                  maxAttemptsCount,
                  appId,
                  orgId,
                  appCode,
                  userDetails,
                  response);
        }
        if (loginResp != null && loginResp.getCode() != ErrorCode.EC_200.code()) {
          if (loginResp.getCode() == ErrorCode.EC_113.code()) {
            loginResp.setMessage(MyStudiesUserRegUtil.ErrorCodes.CONNECTION_ERROR_MSG.getValue());
            MyStudiesUserRegUtil.getFailureResponse(
                MyStudiesUserRegUtil.ErrorCodes.STATUS_104.getValue(),
                MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.name(),
                MyStudiesUserRegUtil.ErrorCodes.CONNECTION_ERROR_MSG.getValue(),
                response);
            logger.info("AuthenticationController login() - ends with BAD_REQUEST");
            return new ResponseEntity<>(loginResp, HttpStatus.BAD_REQUEST);
          } else if (loginResp.getCode() == ErrorCode.EC_140.code()) {
            MyStudiesUserRegUtil.getFailureResponse(
                MyStudiesUserRegUtil.ErrorCodes.STATUS_103.getValue(),
                MyStudiesUserRegUtil.ErrorCodes.CODE_EXPIRED.getValue(),
                MyStudiesUserRegUtil.ErrorCodes.CODE_EXPIRED.getValue(),
                response);
            loginResp.setMessage(MyStudiesUserRegUtil.ErrorCodes.CODE_EXPIRED.getValue());
            logger.info("AuthenticationController login() - ends with CODE_EXPIRED");
            return new ResponseEntity<>(loginResp, HttpStatus.BAD_REQUEST);
          } else if (loginResp.getCode() == ErrorCode.EC_102.code()) {
            MyStudiesUserRegUtil.getFailureResponse(
                MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
                MyStudiesUserRegUtil.ErrorCodes.ACCOUNT_LOCKED.name(),
                MyStudiesUserRegUtil.ErrorCodes.ACCOUNT_LOCKED.getValue(),
                response);
            loginResp.setMessage(MyStudiesUserRegUtil.ErrorCodes.ACCOUNT_LOCKED.getValue());
            activityLogService.createActivityLog(
                participantDetails.getUserId(),
                AppConstants.AUDIT_EVENT_FAILED_SIGN_IN_NAME,
                String.format(
                    AppConstants.AUDIT_EVENT_FAILED_SIGN_IN_DESC, participantDetails.getUserId()));
            logger.info("AuthenticationController login() - ends with ACCOUNT_LOCKED");
            return new ResponseEntity<>(loginResp, HttpStatus.BAD_REQUEST);
          } else if (loginResp.getCode() == ErrorCode.EC_92.code()) {
            MyStudiesUserRegUtil.getFailureResponse(
                MyStudiesUserRegUtil.ErrorCodes.STATUS_101.getValue(),
                MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.name(),
                MyStudiesUserRegUtil.ErrorCodes.INVALID_USERNAME_PASSWORD_MSG.getValue(),
                response);
            loginResp.setCode(HttpStatus.UNAUTHORIZED.value());
            loginResp.setMessage(
                MyStudiesUserRegUtil.ErrorCodes.INVALID_USERNAME_PASSWORD_MSG.getValue());
            activityLogService.createActivityLog(
                participantDetails.getUserId(),
                AppConstants.AUDIT_EVENT_FAILED_SIGN_IN_NAME,
                String.format(
                    AppConstants.AUDIT_EVENT_FAILED_SIGN_IN_DESC, participantDetails.getUserId()));
            logger.info("AuthenticationController login() - ends with UNAUTHORIZED request");
            return new ResponseEntity<>(loginResp, HttpStatus.UNAUTHORIZED);
          }
        }

        String eventName = AppConstants.AUDIT_EVENT_SIGN_IN_NAME;
        String eventDesc = AppConstants.AUDIT_EVENT_SIGN_IN_DESC;
        if (loginResp.isResetPassword()) {
          eventName = AppConstants.AUDIT_EVENT_SIGN_IN_WITH_TMP_PASSD_NAME;
          eventDesc = AppConstants.AUDIT_EVENT_SIGN_IN_WITH_TMP_PASSD_DESC;
        }
        activityLogService.createActivityLog(
            participantDetails.getUserId(),
            eventName,
            String.format(eventDesc, participantDetails.getUserId()));
      } else {
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_USERNAME_PASSWORD_MSG.getValue(),
            response);
        activityLogService.createActivityLog(
            loginRequest.getEmailId(),
            AppConstants.AUDIT_EVENT_FAILED_SIGN_IN_NAME,
            String.format(
                AppConstants.AUDIT_EVENT_FAILED_SIGN_IN_WRONG_EMAIL_DESC,
                loginRequest.getEmailId()));
        logger.info("AuthenticationController login() - ends with account deactivated");
        return new ResponseEntity<>(loginResp, HttpStatus.UNAUTHORIZED);
      }
    } catch (PasswordExpiredException e) {
      MyStudiesUserRegUtil.getFailureResponse(
          401 + "",
          MyStudiesUserRegUtil.ErrorCodes.UNAUTHORIZED.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.PASSWORD_EXPIRED.getValue(),
          response);
      logger.error("AuthenticationController login() - error with UNAUTHORIZED request: ", e);
      return new ResponseEntity<>(loginResp, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      MyStudiesUserRegUtil.getFailureResponse(
          500 + "",
          MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.SYSTEM_ERROR_FOUND.getValue(),
          response);
      logger.error("AuthenticationController login() - error with INTERNAL_SERVER_ERROR: ", e);
      return new ResponseEntity<>(loginResp, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<>(loginResp, HttpStatus.OK);
  }

  @DeleteMapping("/logout")
  public ResponseEntity<?> logout(
      @RequestHeader("userId") String userId,
      @RequestHeader("accessToken") String accessToken,
      @RequestHeader("clientToken") String clientToken,
      @Context HttpServletResponse response) {
    logger.info("AuthenticationController logout() - starts");
    LogoutResponse logoutResponse = null;
    try {
      String serviceResponse = null;
      try {
        serviceResponse = session.deleteTokenExpireDateByUserId(userId);
        if ("SUCCESS".equals(serviceResponse)) {
          MyStudiesUserRegUtil.getFailureResponse(
              MyStudiesUserRegUtil.ErrorCodes.STATUS_200.getValue(),
              MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue(),
              MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue(),
              response);
          logoutResponse = new LogoutResponse();
          logoutResponse.setCode(ErrorCode.EC_200.code());
          logoutResponse.setMessage(ErrorCode.EC_200.errorMessage());
          activityLogService.createActivityLog(
              userId,
              AppConstants.AUDIT_EVENT_SIGN_OUT_NAME,
              String.format(AppConstants.AUDIT_EVENT_SIGN_OUT_DESC, userId));
          logger.info("AuthenticationController logout() - ends");
          return new ResponseEntity<>(logoutResponse, HttpStatus.OK);
        } else {
          MyStudiesUserRegUtil.getFailureResponse(
              401 + "",
              MyStudiesUserRegUtil.ErrorCodes.UNAUTHORIZED.getValue(),
              MyStudiesUserRegUtil.ErrorCodes.UNAUTHORIZED.getValue(),
              response);
          logoutResponse = new LogoutResponse();
          logoutResponse.setCode(HttpStatus.UNAUTHORIZED.value());
          logoutResponse.setMessage(ErrorCode.EC_1001.errorMessage());
          activityLogService.createActivityLog(
              userId,
              AppConstants.AUDIT_EVENT_SIGN_OUT_UNSUCCESSFUL_NAME,
              String.format(AppConstants.AUDIT_EVENT_SIGN_OUT_UNSUCCESSFUL_DESC, userId));
          logger.info("AuthenticationController logout() - ends with UNAUTHORIZED request");
          return new ResponseEntity<>(logoutResponse, HttpStatus.UNAUTHORIZED);
        }

      } catch (UserNotFoundException e) {
        activityLogService.createActivityLog(
            userId,
            AppConstants.AUDIT_EVENT_SIGN_OUT_UNSUCCESSFUL_NAME,
            String.format(AppConstants.AUDIT_EVENT_SIGN_OUT_UNSUCCESSFUL_DESC, userId));
        MyStudiesUserRegUtil.getFailureResponse(
            400 + "",
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_USER_ID.getValue(),
            response);
        logoutResponse = new LogoutResponse();
        logoutResponse.setCode(HttpStatus.BAD_REQUEST.value());
        logoutResponse.setMessage(ErrorCode.EC_116.errorMessage());
        logger.error("AuthenticationController logout() - error with BAD_REQUEST: ", e);
        return new ResponseEntity<>(logoutResponse, HttpStatus.BAD_REQUEST);
      }

    } catch (Exception e) {
      MyStudiesUserRegUtil.getFailureResponse(
          500 + "",
          MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.SYSTEM_ERROR_FOUND.getValue(),
          response);
      activityLogService.createActivityLog(
          userId,
          AppConstants.AUDIT_EVENT_SIGN_OUT_UNSUCCESSFUL_NAME,
          String.format(AppConstants.AUDIT_EVENT_SIGN_OUT_UNSUCCESSFUL_DESC, userId));
      logoutResponse = new LogoutResponse();
      logoutResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
      logoutResponse.setMessage(ErrorCode.EC_118.errorMessage());
      logger.error("AuthenticationController logout() - error with INTERNAL_SERVER_ERROR: ", e);
      return new ResponseEntity<>(logoutResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping(
      value = "/forgotPassword",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> forgotPassword(
      @RequestHeader("appId") String appId,
      @RequestHeader("orgId") String orgId,
      @RequestHeader("clientId") String clientId,
      @RequestHeader("secretKey") String secretKey,
      @RequestBody ForgotPwdBean forgotPwdBean,
      @Context HttpServletResponse response) {
    logger.info("AuthenticationController forgotPassword() - Starts ");
    ResponseBean responseBean = null;
    LoginAttemptsBO loginAttempts = null;
    int maxAttemptsCount = 0;
    int count = 0;
    boolean isValid = false;
    String tempValue = "";
    int isSent = 0;
    String appCode = null;
    try {
      if ((forgotPwdBean != null)
          && (forgotPwdBean.getEmailId() != null)
          && !StringUtils.isEmpty(forgotPwdBean.getEmailId())
          && (appId != null)
          && !StringUtils.isEmpty(appId)
          && (orgId != null)
          && !StringUtils.isEmpty(orgId)
          && (clientId != null)
          && !StringUtils.isEmpty(clientId)
          && (secretKey != null)
          && !StringUtils.isEmpty(secretKey)) {
        appCode = clientInfoService.checkClientInfo(clientId, secretKey);
        if (appCode != null) {
          DaoUserBO serviceResp =
              userDetailsService.loadUserByEmailIdAndAppIdAndOrgIdAndAppCode(
                  forgotPwdBean.getEmailId(), appId, orgId, appCode);
          if (serviceResp != null) {
            if (appCode.equals(serviceResp.getAppCode())) {
              if (serviceResp.getAccountStatus().equalsIgnoreCase(AppConstants.ACTIVE)
                  && serviceResp
                      .getEmailVerificationStatus()
                      .equalsIgnoreCase(AppConstants.VERIFIED)) {
                maxAttemptsCount = Integer.valueOf(appConfig.getMaxLoginAttempts());
                count = Integer.valueOf(appConfig.getExpirationLoginAttemptsMinute());
                loginAttempts = userDetailsService.getLoginAttempts(forgotPwdBean.getEmailId());
                if ((loginAttempts != null) && (loginAttempts.getAttempts() == maxAttemptsCount)) {
                  Date attemptsExpireDate =
                      MyStudiesUserRegUtil.addMinutes(
                          loginAttempts.getLastModified().toString(), count);
                  if (attemptsExpireDate.before(MyStudiesUserRegUtil.getCurrentUtilDateTime())
                      || attemptsExpireDate.equals(MyStudiesUserRegUtil.getCurrentUtilDateTime())) {
                    isValid = true;
                  } else {
                    MyStudiesUserRegUtil.getFailureResponse(
                        MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
                        MyStudiesUserRegUtil.ErrorCodes.ACCOUNT_TEMP_LOCKED.name(),
                        MyStudiesUserRegUtil.ErrorCodes.ACCOUNT_TEMP_LOCKED.getValue(),
                        response);
                    responseBean = new ResponseBean();
                    responseBean.setCode(HttpStatus.BAD_REQUEST.value());
                    responseBean.setMessage(
                        MyStudiesUserRegUtil.ErrorCodes.ACCOUNT_TEMP_LOCKED.getValue());
                    return new ResponseEntity<>(responseBean, HttpStatus.BAD_REQUEST);
                  }
                } else {
                  isValid = true;
                }
                tempValue = RandomStringUtils.randomAlphanumeric(6);
                if (isValid) {
                  DaoUserBO userDetails =
                      userDetailsService.loadUserByEmailIdAndAppIdAndOrgIdAndAppCode(
                          forgotPwdBean.getEmailId(), appId, orgId, appCode);
                  userDetails.setTempPassword(true);
                  userDetails.setResetPassword(
                      MyStudiesUserRegUtil.getEncryptedString(tempValue, userDetails.getSalt()));
                  userDetails.setTempPasswordDate(MyStudiesUserRegUtil.getCurrentUtilDateTime());
                  isSent =
                      userDetailsService.sendPasswordResetLinkthroughEmail(
                          userDetails.getEmailId(), tempValue, userDetails);
                  if (isSent == 2) {
                    responseBean = new ResponseBean();
                    responseBean.setCode(ErrorCode.EC_200.code());
                    responseBean.setMessage(
                        MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());
                    userDetailsService.resetLoginAttempts(forgotPwdBean.getEmailId());
                    activityLogService.createActivityLog(
                        userDetails.getUserId(),
                        AppConstants.AUDIT_EVENT_PASSWORD_HELP_NAME,
                        String.format(
                            AppConstants.AUDIT_EVENT_PASSWORD_HELP_DESC, userDetails.getUserId()));
                  } else {
                    MyStudiesUserRegUtil.getFailureResponse(
                        MyStudiesUserRegUtil.ErrorCodes.STATUS_104.getValue(),
                        MyStudiesUserRegUtil.ErrorCodes.EMAIL_NOT_VERIFIED.getValue(),
                        MyStudiesUserRegUtil.ErrorCodes.EMAIL_NOT_VERIFIED.getValue(),
                        response);
                    responseBean = new ResponseBean();
                    responseBean.setCode(HttpStatus.FORBIDDEN.value());
                    responseBean.setMessage(
                        MyStudiesUserRegUtil.ErrorCodes.EMAIL_NOT_VERIFIED.getValue());
                    return new ResponseEntity<>(responseBean, HttpStatus.FORBIDDEN);
                  }
                }
              } else {
                MyStudiesUserRegUtil.getFailureResponse(
                    MyStudiesUserRegUtil.ErrorCodes.STATUS_103.getValue(),
                    MyStudiesUserRegUtil.ErrorCodes.EMAIL_NOT_VERIFIED.getValue(),
                    MyStudiesUserRegUtil.ErrorCodes.EMAIL_NOT_VERIFIED.getValue(),
                    response);
                responseBean = new ResponseBean();
                responseBean.setCode(HttpStatus.FORBIDDEN.value());
                responseBean.setMessage(ErrorCode.EC_139.errorMessage());
                return new ResponseEntity<>(responseBean, HttpStatus.FORBIDDEN);
              }
            } else {
              MyStudiesUserRegUtil.getFailureResponse(
                  MyStudiesUserRegUtil.ErrorCodes.STATUS_123.getValue(),
                  MyStudiesUserRegUtil.ErrorCodes.UNAUTHORIZED.getValue(),
                  MyStudiesUserRegUtil.ErrorCodes.INVALID_CLIENTID_OR_SECRET_KEY.getValue(),
                  response);
              responseBean = new ResponseBean();
              responseBean.setCode(HttpStatus.UNAUTHORIZED.value());
              responseBean.setMessage(ErrorCode.EC_139.errorMessage());
              return new ResponseEntity<>(responseBean, HttpStatus.UNAUTHORIZED);
            }
          } else {
            MyStudiesUserRegUtil.getFailureResponse(
                MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
                MyStudiesUserRegUtil.ErrorCodes.INVALID_CREDENTIALS.getValue(),
                MyStudiesUserRegUtil.ErrorCodes.INVALID_CREDENTIALS.getValue(),
                response);
            responseBean = new ResponseBean();
            responseBean.setCode(HttpStatus.BAD_REQUEST.value());
            responseBean.setMessage(ErrorCode.EC_136.errorMessage());
            return new ResponseEntity<>(responseBean, HttpStatus.BAD_REQUEST);
          }
        } else {
          MyStudiesUserRegUtil.getFailureResponse(
              MyStudiesUserRegUtil.ErrorCodes.STATUS_123.getValue(),
              MyStudiesUserRegUtil.ErrorCodes.UNAUTHORIZED.getValue(),
              MyStudiesUserRegUtil.ErrorCodes.UNAUTHORIZED.getValue(),
              response);
          responseBean = new ResponseBean();
          responseBean.setCode(HttpStatus.UNAUTHORIZED.value());
          responseBean.setMessage(ErrorCode.EC_131.errorMessage());
          return new ResponseEntity<>(responseBean, HttpStatus.UNAUTHORIZED);
        }
      } else {
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
            response);
        responseBean = new ResponseBean();
        responseBean.setCode(HttpStatus.BAD_REQUEST.value());
        responseBean.setMessage(ErrorCode.EC_109.errorMessage());
        return new ResponseEntity<>(responseBean, HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      logger.error("AuthenticationController forgotPassword() - error ", e);
      MyStudiesUserRegUtil.getFailureResponse(
          MyStudiesUserRegUtil.ErrorCodes.STATUS_104.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.CONNECTION_ERROR_MSG.getValue(),
          response);
      responseBean = new ResponseBean();
      responseBean.setCode(HttpStatus.BAD_REQUEST.value());
      responseBean.setMessage(ErrorCode.EC_113.errorMessage());
      return new ResponseEntity<>(responseBean, HttpStatus.BAD_REQUEST);
    }
    logger.info("AuthenticationController forgotPassword() - Ends ");
    return new ResponseEntity<>(responseBean, HttpStatus.OK);
  }

  @PostMapping(
      value = "/changePassword",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> changePassword(
      @RequestHeader("userId") String userId,
      @RequestBody ChangePasswordBean changePasswordBean,
      @Context HttpServletResponse response) {
    logger.info("AuthenticationController changePassword() - Starts ");
    ResponseBean responseBean = null;
    boolean isValidPassword = true;
    try {
      if ((changePasswordBean != null)
          && (changePasswordBean.getCurrentPassword() != null)
          && !StringUtils.isEmpty(changePasswordBean.getCurrentPassword())
          && (changePasswordBean.getNewPassword() != null)
          && !StringUtils.isEmpty(changePasswordBean.getNewPassword())
          && (userId != null)
          && !StringUtils.isEmpty(userId)) {
        DaoUserBO userInfo = userDetailsService.loadUserByUserId(userId);
        if (userInfo != null) {
          if ((userInfo.getPassword() != null
                  && userInfo
                      .getPassword()
                      .equalsIgnoreCase(
                          MyStudiesUserRegUtil.getEncryptedString(
                              changePasswordBean.getCurrentPassword(), userInfo.getSalt())))
              || (userInfo.getResetPassword() != null
                  && userInfo
                      .getResetPassword()
                      .equalsIgnoreCase(
                          MyStudiesUserRegUtil.getEncryptedString(
                              changePasswordBean.getCurrentPassword(), userInfo.getSalt())))) {
            if (!changePasswordBean
                .getCurrentPassword()
                .equals(changePasswordBean.getNewPassword())) {

              if (MyStudiesUserRegUtil.isPasswordStrong(changePasswordBean.getNewPassword())
                  && !(userInfo
                      .getEmailId()
                      .equalsIgnoreCase(changePasswordBean.getNewPassword()))) {

                isValidPassword =
                    userDetailsService.getPasswordHistory(
                        userId, changePasswordBean.getNewPassword());
                if (isValidPassword) {
                  String salt =
                      RandomStringUtils.randomAlphanumeric(15)
                          + "-"
                          + RandomStringUtils.randomAlphanumeric(15)
                          + "-"
                          + RandomStringUtils.randomAlphanumeric(15);
                  userInfo.setSalt(salt);
                  userInfo.setPassword(
                      MyStudiesUserRegUtil.getEncryptedString(
                          changePasswordBean.getNewPassword(), salt));
                  if (userInfo.getTempPassword()) {
                    userInfo.setTempPassword(false);
                  }
                  userInfo.setResetPassword(null);
                  userInfo.setTempPasswordDate(MyStudiesUserRegUtil.getCurrentUtilDateTime());
                  userInfo.setPasswordUpdatedDate(MyStudiesUserRegUtil.getCurrentUtilDateTime());
                  responseBean = userDetailsService.changePassword(userInfo);
                  if (responseBean
                          .getMessage()
                          .equalsIgnoreCase(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue())
                      && !userInfo.getTempPassword()) {
                    String password =
                        MyStudiesUserRegUtil.getEncryptedString(
                            changePasswordBean.getNewPassword(), salt);
                    String message = userDetailsService.savePasswordHistory(userId, password, salt);
                    if (message.equalsIgnoreCase(
                        MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue())) {
                      responseBean = new ResponseBean();
                      responseBean.setCode(ErrorCode.EC_200.code());
                      responseBean.setMessage(
                          MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());
                      activityLogService.createActivityLog(
                          userId,
                          AppConstants.AUDIT_EVENT_CHANGE_PASSWORD_NAME,
                          String.format(AppConstants.AUDIT_EVENT_CHANGE_PASSWORD_DESC, userId));
                    }
                  } else if (MyStudiesUserRegUtil.ErrorCodes.FAILURE
                      .getValue()
                      .equalsIgnoreCase(responseBean.getMessage())) {
                    activityLogService.createActivityLog(
                        userId,
                        AppConstants.AUDIT_EVENT_CHANGE_PASSWORD_UNSUCCESSFUL_NAME,
                        String.format(
                            AppConstants.AUDIT_EVENT_CHANGE_PASSWORD_UNSUCCESSFUL_DESC, userId));
                  }
                } else {
                  MyStudiesUserRegUtil.getFailureResponse(
                      MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
                      MyStudiesUserRegUtil.ErrorCodes.NEW_PASSWORD_NOT_SAME_LAST_PASSWORD
                          .getValue(),
                      MyStudiesUserRegUtil.ErrorCodes.NEW_PASSWORD_NOT_SAME_LAST_PASSWORD
                          .getValue(),
                      response);
                  activityLogService.createActivityLog(
                      userId,
                      AppConstants.AUDIT_EVENT_CHANGE_PASSWORD_UNSUCCESSFUL_NAME,
                      String.format(
                          AppConstants.AUDIT_EVENT_CHANGE_PASSWORD_UNSUCCESSFUL_DESC, userId));
                  responseBean = new ResponseBean();
                  responseBean.setCode(HttpStatus.BAD_REQUEST.value());
                  responseBean.setMessage(
                      MyStudiesUserRegUtil.ErrorCodes.NEW_PASSWORD_NOT_SAME_LAST_PASSWORD
                          .getValue());
                  return new ResponseEntity<>(responseBean, HttpStatus.BAD_REQUEST);
                }
              } else {
                // End here invalid new password
                MyStudiesUserRegUtil.getFailureResponse(
                    MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
                    MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
                    MyStudiesUserRegUtil.ErrorCodes.NEW_PASSWORD_IS_INVALID.getValue(),
                    response);
                activityLogService.createActivityLog(
                    userId,
                    AppConstants.AUDIT_EVENT_CHANGE_PASSWORD_UNSUCCESSFUL_NAME,
                    String.format(
                        AppConstants.AUDIT_EVENT_CHANGE_PASSWORD_UNSUCCESSFUL_DESC, userId));
                responseBean = new ResponseBean();
                responseBean.setCode(HttpStatus.BAD_REQUEST.value());
                responseBean.setMessage(
                    MyStudiesUserRegUtil.ErrorCodes.NEW_PASSWORD_IS_INVALID.getValue());
                return new ResponseEntity<>(responseBean, HttpStatus.BAD_REQUEST);
              }
            } else {
              MyStudiesUserRegUtil.getFailureResponse(
                  MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
                  MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
                  MyStudiesUserRegUtil.ErrorCodes.OLD_PASSWORD_AND_NEW_PASSWORD_NOT_SAME.getValue(),
                  response);
              activityLogService.createActivityLog(
                  userId,
                  AppConstants.AUDIT_EVENT_CHANGE_PASSWORD_UNSUCCESSFUL_NAME,
                  String.format(
                      AppConstants.AUDIT_EVENT_CHANGE_PASSWORD_UNSUCCESSFUL_DESC, userId));
              responseBean = new ResponseBean();
              responseBean.setCode(HttpStatus.BAD_REQUEST.value());
              responseBean.setMessage(
                  MyStudiesUserRegUtil.ErrorCodes.OLD_PASSWORD_AND_NEW_PASSWORD_NOT_SAME
                      .getValue());
              return new ResponseEntity<>(responseBean, HttpStatus.BAD_REQUEST);
            }
          } else {

            MyStudiesUserRegUtil.getFailureResponse(
                MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
                MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
                MyStudiesUserRegUtil.ErrorCodes.OLD_PASSWORD_NOT_EXISTS.getValue(),
                response);
            activityLogService.createActivityLog(
                userId,
                AppConstants.AUDIT_EVENT_CHANGE_PASSWORD_UNSUCCESSFUL_NAME,
                String.format(AppConstants.AUDIT_EVENT_CHANGE_PASSWORD_UNSUCCESSFUL_DESC, userId));
            responseBean = new ResponseBean();
            responseBean.setCode(HttpStatus.BAD_REQUEST.value());
            responseBean.setMessage(
                MyStudiesUserRegUtil.ErrorCodes.OLD_PASSWORD_NOT_EXISTS.getValue());
            return new ResponseEntity<>(responseBean, HttpStatus.BAD_REQUEST);
          }
        } else {
          MyStudiesUserRegUtil.getFailureResponse(
              MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
              MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
              MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
              response);
          responseBean = new ResponseBean();
          responseBean.setCode(HttpStatus.BAD_REQUEST.value());
          responseBean.setMessage(
              MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue());
          return new ResponseEntity<>(responseBean, HttpStatus.BAD_REQUEST);
        }
      } else {
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
            response);
        responseBean = new ResponseBean();
        responseBean.setCode(HttpStatus.BAD_REQUEST.value());
        responseBean.setMessage(ErrorCode.EC_109.errorMessage());
        return new ResponseEntity<>(responseBean, HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      logger.error("AuthenticationController forgotPassword() - error ", e);
      MyStudiesUserRegUtil.getFailureResponse(
          MyStudiesUserRegUtil.ErrorCodes.STATUS_104.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.CONNECTION_ERROR_MSG.getValue(),
          response);
      activityLogService.createActivityLog(
          userId,
          AppConstants.AUDIT_EVENT_CHANGE_PASSWORD_UNSUCCESSFUL_NAME,
          String.format(AppConstants.AUDIT_EVENT_CHANGE_PASSWORD_UNSUCCESSFUL_DESC, userId));
      responseBean = new ResponseBean();
      responseBean.setCode(HttpStatus.BAD_REQUEST.value());
      responseBean.setMessage(ErrorCode.EC_113.errorMessage());
      return new ResponseEntity<>(responseBean, HttpStatus.BAD_REQUEST);
    }
    logger.info("AuthenticationController forgotPassword() - Ends ");
    return new ResponseEntity<>(responseBean, HttpStatus.OK);
  }

  private LoginResponse getLoginInformation(
      DaoUserBO participantDetails,
      String email,
      String password,
      int maxAttemptsCount,
      String appId,
      String orgId,
      String appCode,
      UserDetails userDetails,
      HttpServletResponse response)
      throws PasswordExpiredException {
    logger.info("AuthenticationController getLoginInformation() - Starts ");
    LoginResponse responseBean = null;
    AuthInfoBO authInfo = null;
    try {

      if (participantDetails != null
          && LocalDateTime.now(ZoneId.systemDefault())
              .isBefore(participantDetails.getPasswordExpireDate())) {
        logger.info("TRUE");
      } else throw new PasswordExpiredException();

      if (participantDetails.getPassword() != null
          && participantDetails
              .getPassword()
              .equalsIgnoreCase(
                  MyStudiesUserRegUtil.getEncryptedString(
                      password, participantDetails.getSalt()))) {
        if (participantDetails.getTempPassword()) {
          participantDetails.setResetPassword(null);
          participantDetails.setTempPassword(false);
          participantDetails.setTempPasswordDate(MyStudiesUserRegUtil.getCurrentUtilDateTime());
          userDetailsService.saveUserDetails(participantDetails);
        }
        authInfo = jwtTokenUtil.generateToken(userDetails, appId, orgId, appCode);
        if (authInfo != null) {
          responseBean = new LoginResponse();
          responseBean.setCode(ErrorCode.EC_200.code());
          responseBean.setMessage(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());
          responseBean.setUserId(authInfo.getUserId());
          if (AppConstants.ACTIVE.equalsIgnoreCase(participantDetails.getAccountStatus())
              && AppConstants.VERIDIED.equalsIgnoreCase(
                  participantDetails.getEmailVerificationStatus())) {
            responseBean.setVerified(true);
          } else {
            responseBean.setVerified(false);
          }
          responseBean.setRefreshToken(authInfo.getRefreshToken());
          responseBean.setAccessToken(authInfo.getAccessToken());
          responseBean.setClientToken(authInfo.getClientToken());
          if (participantDetails.getPasswordUpdatedDate() != null) {
            String days = appConfig.getPasswdExpiryInDay();
            Date expiredDate =
                MyStudiesUserRegUtil.addDays(
                    MyStudiesUserRegUtil.getCurrentDateTime(), Integer.parseInt(days));
            if (expiredDate.before(participantDetails.getPasswordUpdatedDate())
                || expiredDate.equals(participantDetails.getPasswordUpdatedDate())) {
              responseBean.setResetPassword(true);
            }
          }
          userDetailsService.resetLoginAttempts(email);
        } else {
          responseBean = new LoginResponse();
          responseBean.setCode(ErrorCode.EC_140.code());
        }
      } else if (participantDetails.getResetPassword() != null
          && participantDetails
              .getResetPassword()
              .equalsIgnoreCase(
                  MyStudiesUserRegUtil.getEncryptedString(
                      password, participantDetails.getSalt()))) {
        if (Boolean.TRUE.equals(participantDetails.getTempPassword())) {
          String hours = appConfig.getVerificationExpInHr();
          Date validateDate =
              MyStudiesUserRegUtil.addHours(
                  MyStudiesUserRegUtil.getCurrentDateTime(), Integer.parseInt(hours));
          if (participantDetails.getTempPasswordDate().before(validateDate)
              || participantDetails.getTempPasswordDate().equals(validateDate)) {
            authInfo = jwtTokenUtil.generateToken(userDetails, appId, orgId, appCode);
            if (authInfo != null) {
              responseBean = new LoginResponse();
              responseBean.setCode(ErrorCode.EC_200.code());
              responseBean.setMessage(
                  MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());
              responseBean.setUserId(authInfo.getUserId());
              if (AppConstants.ACTIVE.equalsIgnoreCase(participantDetails.getAccountStatus())
                  && "Verified".equalsIgnoreCase(participantDetails.getEmailVerificationStatus())) {
                responseBean.setVerified(true);
              } else {
                responseBean.setVerified(false);
              }

              if (participantDetails.getPasswordUpdatedDate() != null) {
                String days = appConfig.getPasswdExpiryInDay();
                Date expiredDate =
                    MyStudiesUserRegUtil.addDays(
                        MyStudiesUserRegUtil.getCurrentDateTime(), Integer.parseInt(days));
                if (expiredDate.before(participantDetails.getPasswordUpdatedDate())
                    || expiredDate.equals(participantDetails.getPasswordUpdatedDate())) {
                  responseBean.setResetPassword(true);
                }
              }
              responseBean.setRefreshToken(authInfo.getRefreshToken());
              responseBean.setAccessToken(authInfo.getAccessToken());
              responseBean.setClientToken(authInfo.getClientToken());
              responseBean.setResetPassword(participantDetails.getTempPassword());
              userDetailsService.resetLoginAttempts(email);
            } else {
              responseBean = new LoginResponse();
              responseBean.setCode(ErrorCode.EC_113.code());
            }
          } else {
            responseBean = new LoginResponse();
            responseBean.setCode(ErrorCode.EC_140.code());
          }
        }
      } else {
        LoginAttemptsBO failAttempts = userDetailsService.updateLoginFailureAttempts(email);
        if (failAttempts != null && failAttempts.getAttempts() == maxAttemptsCount) {
          responseBean = new LoginResponse();
          responseBean.setCode(ErrorCode.EC_102.code());
        } else {
          responseBean = new LoginResponse();
          responseBean.setCode(ErrorCode.EC_92.code());
        }
      }
    } catch (PasswordExpiredException e) {
      logger.error("AuthenticationController getLoginInformation() - error ", e);
      throw e;
    } catch (Exception e) {
      logger.error("AuthenticationController getLoginInformation() - error ", e);
    }
    logger.info("AuthenticationController getLoginInformation() - ends ");
    return responseBean;
  }
}
