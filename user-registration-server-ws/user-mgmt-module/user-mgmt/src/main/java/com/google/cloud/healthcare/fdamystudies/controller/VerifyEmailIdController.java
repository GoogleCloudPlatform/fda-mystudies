/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import com.google.cloud.healthcare.fdamystudies.bean.ResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.VerifyEmailIdResponse;
import com.google.cloud.healthcare.fdamystudies.beans.AppOrgInfoBean;
import com.google.cloud.healthcare.fdamystudies.beans.EmailIdVerificationForm;
import com.google.cloud.healthcare.fdamystudies.exceptions.InvalidEmailCodeException;
import com.google.cloud.healthcare.fdamystudies.service.CommonService;
import com.google.cloud.healthcare.fdamystudies.service.FdaEaUserDetailsService;
import com.google.cloud.healthcare.fdamystudies.service.UserManagementProfileService;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.UserDetailsBO;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;
import com.google.cloud.healthcare.fdamystudies.util.ResponseUtil;
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

  @PostMapping("/verifyEmailId")
  public ResponseEntity<?> verifyEmailId(
      @Valid @RequestBody EmailIdVerificationForm verificationForm,
      @RequestHeader("appId") String appId,
      @RequestHeader("orgId") String orgId,
      @Context HttpServletResponse response) {

    logger.info("VerifyEmailIdController verifyEmailId() - starts");
    VerifyEmailIdResponse verifyEmailIdResponse = null;
    String isValidAppMsg = "";
    UserDetailsBO participantDetails = null;

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
        participantDetails = getParticipantDetails(verificationForm, appOrgInfoBean);
      }
      if (participantDetails == null) {
        ResponseBean responseBean =
            ResponseUtil.prepareBadRequestResponse(response, AppConstants.EMAIL_NOT_EXISTS);
        return new ResponseEntity<>(responseBean, HttpStatus.BAD_REQUEST);
      }
      boolean verifyEmailCodeResponse =
          userDetailsService.verifyCode(verificationForm.getCode().trim(), participantDetails);

      if (!verifyEmailCodeResponse) {
        ResponseBean respBean =
            ResponseUtil.prepareBadRequestResponse(response, new InvalidEmailCodeException());
        return new ResponseEntity<>(respBean, HttpStatus.BAD_REQUEST);
      }

      boolean serviceResponse = userDetailsService.updateStatus(participantDetails);
      if (!serviceResponse) {
        ResponseBean respBean = ResponseUtil.prepareSystemExceptionResponse(response);
        return new ResponseEntity<>(respBean, HttpStatus.INTERNAL_SERVER_ERROR);
      }

      ResponseBean respBean = ResponseUtil.prepareSuccessResponse(response);
      verifyEmailIdResponse =
          new VerifyEmailIdResponse(respBean.getCode(), respBean.getMessage(), true);
      return new ResponseEntity<>(verifyEmailIdResponse, HttpStatus.OK);

    } catch (IllegalArgumentException e) {
      ResponseBean respBean = ResponseUtil.prepareBadRequestResponse(response, e);
      logger.error(AppConstants.VERIFY_EMAILID_CONTROLLER_ENDS_MESSAGE + ": ", e);
      return new ResponseEntity<>(respBean, HttpStatus.BAD_REQUEST);

    } catch (Exception e) {
      ResponseBean respBean = ResponseUtil.prepareSystemExceptionResponse(response);
      logger.error(AppConstants.VERIFY_EMAILID_CONTROLLER_ENDS_MESSAGE + ": ", e);
      return new ResponseEntity<>(respBean, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private UserDetailsBO getParticipantDetails(
      EmailIdVerificationForm verificationForm, AppOrgInfoBean appOrgInfoBean) {
    UserDetailsBO participantDetails = null;
    participantDetails =
        userManagementProfService.getParticipantDetailsByEmail(
            verificationForm.getEmailId(),
            appOrgInfoBean.getAppInfoId(),
            appOrgInfoBean.getOrgInfoId());
    return participantDetails;
  }
}
