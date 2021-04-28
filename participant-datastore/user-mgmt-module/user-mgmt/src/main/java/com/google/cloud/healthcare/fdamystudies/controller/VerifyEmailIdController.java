/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.ACCOUNT_ACTIVATION_FAILED;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.ACCOUNT_ACTIVATION_USER_EMAIL_VERIFICATION_FAILED;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.ACCOUNT_ACTIVATION_USER_EMAIL_VERIFICATION_FAILED_EXPIRED_CODE;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.ACCOUNT_ACTIVATION_USER_EMAIL_VERIFICATION_FAILED_WRONG_CODE;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.REGISTRATION_SUCCEEDED;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.USER_ACCOUNT_ACTIVATED;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.USER_EMAIL_VERIFIED_FOR_ACCOUNT_ACTIVATION;

import com.google.cloud.healthcare.fdamystudies.bean.ResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.VerifyEmailIdResponse;
import com.google.cloud.healthcare.fdamystudies.beans.AppOrgInfoBean;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.EmailIdVerificationForm;
import com.google.cloud.healthcare.fdamystudies.common.AuditLogEvent;
import com.google.cloud.healthcare.fdamystudies.common.UserMgmntAuditHelper;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.service.CommonService;
import com.google.cloud.healthcare.fdamystudies.service.FdaEaUserDetailsService;
import com.google.cloud.healthcare.fdamystudies.service.UserManagementProfileService;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;
import com.google.cloud.healthcare.fdamystudies.util.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.sql.Timestamp;
import java.time.Instant;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.core.Context;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Api(
    tags = "Verify Email",
    value = "Verify Email",
    description = "Operation pertaining to verify email in user management service")
@RestController
public class VerifyEmailIdController {

  private XLogger logger = XLoggerFactory.getXLogger(VerifyEmailIdController.class.getName());

  private static final String STATUS_LOG = "status=%d";

  private static final String BEGIN_REQUEST_LOG = "%s request";

  @Autowired private FdaEaUserDetailsService userDetailsService;

  @Autowired private CommonService commonService;

  @Autowired UserManagementProfileService userManagementProfService;

  @Autowired UserMgmntAuditHelper userMgmntAuditHelper;

  @ApiOperation(value = "Email verification based on the code")
  @PostMapping("/verifyEmailId")
  public ResponseEntity<?> verifyEmailId(
      @Valid @RequestBody EmailIdVerificationForm verificationForm,
      @RequestHeader("appId") String appId,
      @Context HttpServletResponse response,
      HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    auditRequest.setAppId(appId);

    VerifyEmailIdResponse verifyEmailIdResponse = null;
    String isValidAppMsg = "";
    UserDetailsEntity participantDetails = null;

    isValidAppMsg =
        commonService.validatedUserAppDetailsByAllApi("", verificationForm.getEmailId(), appId);

    if (!StringUtils.isNotEmpty(isValidAppMsg)) {

      MyStudiesUserRegUtil.getFailureResponse(
          MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
          response);
      return null;
    }
    AppOrgInfoBean appOrgInfoBean =
        commonService.getUserAppDetailsByAllApi("", verificationForm.getEmailId(), appId);
    if (appOrgInfoBean != null) {
      participantDetails = getParticipantDetails(verificationForm, appOrgInfoBean);
    }
    if (participantDetails == null) {
      userMgmntAuditHelper.logEvent(
          ACCOUNT_ACTIVATION_USER_EMAIL_VERIFICATION_FAILED, auditRequest);

      ResponseBean responseBean =
          ResponseUtil.prepareBadRequestResponse(response, AppConstants.EMAIL_NOT_EXISTS);
      return new ResponseEntity<>(responseBean, HttpStatus.BAD_REQUEST);
    }
    auditRequest.setUserId(participantDetails.getId());
    boolean verifyEmailCodeResponse =
        userDetailsService.verifyCode(verificationForm.getCode().trim(), participantDetails);

    if (!verificationForm.getCode().trim().equals(participantDetails.getEmailCode())) {
      userMgmntAuditHelper.logEvent(
          ACCOUNT_ACTIVATION_USER_EMAIL_VERIFICATION_FAILED_WRONG_CODE, auditRequest);
    } else if (Timestamp.from(Instant.now()).after(participantDetails.getCodeExpireDate())) {
      userMgmntAuditHelper.logEvent(
          ACCOUNT_ACTIVATION_USER_EMAIL_VERIFICATION_FAILED_EXPIRED_CODE, auditRequest);
    }

    if (!verifyEmailCodeResponse) {
      ResponseBean respBean =
          ResponseUtil.prepareBadRequestResponse(
              response, AppConstants.INVALID_EMAIL_CODE_EXCEPTION);
      return new ResponseEntity<>(respBean, HttpStatus.BAD_REQUEST);
    }

    String tempRegId = userDetailsService.updateStatus(participantDetails, auditRequest);

    AuditLogEvent auditEvent =
        StringUtils.isNotEmpty(tempRegId) ? USER_ACCOUNT_ACTIVATED : ACCOUNT_ACTIVATION_FAILED;
    userMgmntAuditHelper.logEvent(auditEvent, auditRequest);

    if (StringUtils.isEmpty(tempRegId)) {
      ResponseBean respBean = ResponseUtil.prepareSystemExceptionResponse(response);
      return new ResponseEntity<>(respBean, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    ResponseBean respBean = ResponseUtil.prepareSuccessResponse(response);
    verifyEmailIdResponse =
        new VerifyEmailIdResponse(respBean.getCode(), respBean.getMessage(), true, tempRegId);

    userMgmntAuditHelper.logEvent(USER_EMAIL_VERIFIED_FOR_ACCOUNT_ACTIVATION, auditRequest);
    userMgmntAuditHelper.logEvent(REGISTRATION_SUCCEEDED, auditRequest);

    logger.exit(String.format(STATUS_LOG, HttpStatus.OK.value()));
    return new ResponseEntity<>(verifyEmailIdResponse, HttpStatus.OK);
  }

  private UserDetailsEntity getParticipantDetails(
      EmailIdVerificationForm verificationForm, AppOrgInfoBean appOrgInfoBean) {
    UserDetailsEntity participantDetails = null;
    participantDetails =
        userManagementProfService.getParticipantDetailsByEmail(
            verificationForm.getEmailId(), appOrgInfoBean.getAppInfoId());
    return participantDetails;
  }
}
