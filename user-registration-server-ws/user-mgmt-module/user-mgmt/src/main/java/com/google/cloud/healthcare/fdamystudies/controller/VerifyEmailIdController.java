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
import com.google.cloud.healthcare.fdamystudies.service.CommonService;
import com.google.cloud.healthcare.fdamystudies.service.FdaEaUserDetailsService;
import com.google.cloud.healthcare.fdamystudies.service.UserManagementProfileService;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.UserDetailsBO;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;
import com.google.cloud.healthcare.fdamystudies.util.ResponseUtil;
import java.time.LocalDateTime;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.core.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VerifyEmailIdController {

  private static final Logger logger = LogManager.getLogger(VerifyEmailIdController.class);

  @Autowired private FdaEaUserDetailsService userDetailsService;

  @Autowired private CommonService commonService;

  @Autowired UserManagementProfileService userManagementProfService;

  @Autowired UserMgmntAuditHelper userMgmntAuditHelper;

  @PostMapping("/verifyEmailId")
  public ResponseEntity<?> verifyEmailId(
      @Valid @RequestBody EmailIdVerificationForm verificationForm,
      @RequestHeader("appId") String appId,
      @Context HttpServletResponse response,
      HttpServletRequest request) {
    logger.info("VerifyEmailIdController verifyEmailId() - starts");
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    auditRequest.setAppId(appId);

    VerifyEmailIdResponse verifyEmailIdResponse = null;
    String isValidAppMsg = "";
    UserDetailsBO participantDetails = null;

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
    boolean verifyEmailCodeResponse =
        userDetailsService.verifyCode(verificationForm.getCode().trim(), participantDetails);

    if (!verificationForm.getCode().trim().equals(participantDetails.getEmailCode())) {
      userMgmntAuditHelper.logEvent(
          ACCOUNT_ACTIVATION_USER_EMAIL_VERIFICATION_FAILED_WRONG_CODE, auditRequest);
    } else if (LocalDateTime.now().isAfter(participantDetails.getCodeExpireDate())) {
      userMgmntAuditHelper.logEvent(
          ACCOUNT_ACTIVATION_USER_EMAIL_VERIFICATION_FAILED_EXPIRED_CODE, auditRequest);
    }

    if (!verifyEmailCodeResponse) {
      ResponseBean respBean =
          ResponseUtil.prepareBadRequestResponse(
              response, AppConstants.INVALID_EMAIL_CODE_EXCEPTION);
      return new ResponseEntity<>(respBean, HttpStatus.BAD_REQUEST);
    }

    String tempRegId = userDetailsService.updateStatus(participantDetails);

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

    return new ResponseEntity<>(verifyEmailIdResponse, HttpStatus.OK);
  }

  private UserDetailsBO getParticipantDetails(
      EmailIdVerificationForm verificationForm, AppOrgInfoBean appOrgInfoBean) {
    UserDetailsBO participantDetails = null;
    participantDetails =
        userManagementProfService.getParticipantDetailsByEmail(
            verificationForm.getEmailId(), appOrgInfoBean.getAppInfoId());
    return participantDetails;
  }
}
