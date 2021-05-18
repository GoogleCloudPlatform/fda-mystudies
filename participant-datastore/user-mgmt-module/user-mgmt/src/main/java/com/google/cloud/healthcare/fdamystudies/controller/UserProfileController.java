/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.READ_OPERATION_FAILED_FOR_USER_PROFILE;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.READ_OPERATION_SUCCEEDED_FOR_USER_PROFILE;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.USER_PROFILE_UPDATED;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.USER_PROFILE_UPDATE_FAILED;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.VERIFICATION_EMAIL_RESEND_REQUEST_RECEIVED;

import com.google.cloud.healthcare.fdamystudies.beans.AppOrgInfoBean;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.DeactivateAcctBean;
import com.google.cloud.healthcare.fdamystudies.beans.EmailResponse;
import com.google.cloud.healthcare.fdamystudies.beans.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.beans.ResetPasswordBean;
import com.google.cloud.healthcare.fdamystudies.beans.ResponseBean;
import com.google.cloud.healthcare.fdamystudies.beans.UserProfileRespBean;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequestBean;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.common.UserMgmntAuditHelper;
import com.google.cloud.healthcare.fdamystudies.common.UserStatus;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.exceptions.ErrorCodeException;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.service.CommonService;
import com.google.cloud.healthcare.fdamystudies.service.UserManagementProfileService;
import com.google.cloud.healthcare.fdamystudies.util.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.core.Context;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Api(
    tags = "User Profile",
    value = "User Profile",
    description = "Operations pertaining to user profile in user management service")
@RestController
@Validated
public class UserProfileController {

  private XLogger logger = XLoggerFactory.getXLogger(UserProfileController.class.getName());

  private static final String STATUS_LOG = "status=%d";

  private static final String BEGIN_REQUEST_LOG = "%s request";

  @Autowired UserManagementProfileService userManagementProfService;

  @Autowired CommonService commonService;

  @Autowired ApplicationPropertyConfiguration appConfig;

  @Autowired UserMgmntAuditHelper userMgmntAuditHelper;

  @Value("${email.code.expire_time}")
  private long expireTime;

  @ApiOperation(value = "Returns a response containing user profile information.")
  @GetMapping(value = "/userProfile", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getUserProfile(
      @RequestHeader("userId") String userId,
      @Context HttpServletResponse response,
      HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    auditRequest.setUserId(userId);

    UserProfileRespBean userProfileRespBean = null;

    userProfileRespBean = userManagementProfService.getParticipantInfoDetails(userId, 0);
    if (userProfileRespBean != null) {
      userMgmntAuditHelper.logEvent(READ_OPERATION_SUCCEEDED_FOR_USER_PROFILE, auditRequest);

      userProfileRespBean.setMessage(
          MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());

    } else {
      userMgmntAuditHelper.logEvent(READ_OPERATION_FAILED_FOR_USER_PROFILE, auditRequest);

      MyStudiesUserRegUtil.getFailureResponse(
          MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.NO_DATA_AVAILABLE.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.NO_DATA_AVAILABLE.getValue(),
          response);
    }

    logger.exit(String.format(STATUS_LOG, HttpStatus.OK.value()));
    return new ResponseEntity<>(userProfileRespBean, HttpStatus.OK);
  }

  @ApiOperation(value = "Updates the profile of the currently logged in user.")
  @PostMapping(
      value = "/updateUserProfile",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> updateUserProfile(
      @RequestHeader("userId") String userId,
      @RequestBody UserRequestBean user,
      @Context HttpServletResponse response,
      HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    auditRequest.setUserId(userId);

    ErrorBean errorBean = null;
    errorBean = userManagementProfService.updateUserProfile(userId, user);
    if (errorBean.getCode() == ErrorCode.EC_200.code()) {
      userMgmntAuditHelper.logEvent(USER_PROFILE_UPDATED, auditRequest);

      errorBean = new ErrorBean(HttpStatus.OK.value(), ErrorCode.EC_30.errorMessage());
    } else {
      userMgmntAuditHelper.logEvent(USER_PROFILE_UPDATE_FAILED, auditRequest);

      return new ResponseEntity<>(errorBean, HttpStatus.CONFLICT);
    }
    logger.exit(String.format(STATUS_LOG, errorBean.getCode()));
    return new ResponseEntity<>(errorBean, HttpStatus.OK);
  }

  @ApiOperation(value = "Deactivate the user")
  @DeleteMapping(
      value = "/deactivate",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> deactivateAccount(
      @RequestHeader("userId") String userId,
      @RequestBody DeactivateAcctBean deactivateAcctBean,
      @Context HttpServletResponse response,
      HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);

    String message = MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue();
    ResponseBean responseBean = new ResponseBean();

    message = userManagementProfService.deactivateAccount(userId, deactivateAcctBean, auditRequest);

    if (message.equalsIgnoreCase(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue())) {
      responseBean.setMessage(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());
    } else {
      MyStudiesUserRegUtil.getFailureResponse(
          MyStudiesUserRegUtil.ErrorCodes.STATUS_104.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue(),
          response);
      return null;
    }

    logger.exit(String.format(STATUS_LOG, HttpStatus.OK.value()));
    return new ResponseEntity<>(responseBean, HttpStatus.OK);
  }

  @ApiOperation(value = "Resend confirmation to the user via email")
  @PostMapping(
      value = "/resendConfirmation",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> resendConfirmation(
      @RequestHeader("appId") String appId,
      @RequestHeader String appName,
      @Valid @RequestBody ResetPasswordBean resetPasswordBean,
      @Context HttpServletResponse response,
      HttpServletRequest request)
      throws Exception {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    auditRequest.setAppId(appId);

    UserDetailsEntity participantDetails = null;
    ResponseBean responseBean = new ResponseBean();
    String isValidAppMsg =
        commonService.validatedUserAppDetailsByAllApi("", resetPasswordBean.getEmailId(), appId);
    if (!StringUtils.isEmpty(isValidAppMsg)) {
      AppOrgInfoBean appOrgInfoBean =
          commonService.getUserAppDetailsByAllApi("", resetPasswordBean.getEmailId(), appId);
      if (appOrgInfoBean != null) {
        participantDetails =
            userManagementProfService.getParticipantDetailsByEmail(
                resetPasswordBean.getEmailId(), appOrgInfoBean.getAppInfoId());
      }
      if (participantDetails != null) {
        if (UserStatus.PENDING_EMAIL_CONFIRMATION.getValue() == participantDetails.getStatus()) {
          String code = RandomStringUtils.randomAlphanumeric(6);
          participantDetails.setEmailCode(code);
          participantDetails.setCodeExpireDate(
              Timestamp.valueOf(LocalDateTime.now().plusHours(expireTime)));
          participantDetails.setVerificationDate(Timestamp.from(Instant.now()));
          UserDetailsEntity updParticipantDetails =
              userManagementProfService.saveParticipant(participantDetails);
          if (updParticipantDetails != null) {
            EmailResponse emailResponse =
                userManagementProfService.resendConfirmationthroughEmail(
                    appId,
                    participantDetails.getEmailCode(),
                    participantDetails.getEmail(),
                    appName);
            if (MessageCode.EMAIL_ACCEPTED_BY_MAIL_SERVER
                .getMessage()
                .equals(emailResponse.getMessage())) {
              auditRequest.setUserId(updParticipantDetails.getUserId());
              userMgmntAuditHelper.logEvent(
                  VERIFICATION_EMAIL_RESEND_REQUEST_RECEIVED, auditRequest);
              responseBean.setMessage(
                  MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());
            } else {
              throw new ErrorCodeException(
                  com.google.cloud.healthcare.fdamystudies.common.ErrorCode
                      .REGISTRATION_EMAIL_SEND_FAILED);
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

    logger.exit(String.format(STATUS_LOG, HttpStatus.OK.value()));
    return new ResponseEntity<>(responseBean, HttpStatus.OK);
  }
}
