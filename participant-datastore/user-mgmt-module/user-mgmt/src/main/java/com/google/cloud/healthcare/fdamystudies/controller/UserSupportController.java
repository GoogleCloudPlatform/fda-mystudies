/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ContactUsReqBean;
import com.google.cloud.healthcare.fdamystudies.beans.EmailResponse;
import com.google.cloud.healthcare.fdamystudies.beans.FeedbackReqBean;
import com.google.cloud.healthcare.fdamystudies.beans.ResponseBean;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.exceptions.ErrorCodeException;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.service.UserSupportService;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.core.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserSupportController {

  private static final Logger logger = LoggerFactory.getLogger(UserSupportController.class);

  @Autowired UserSupportService supportService;

  @PostMapping(
      value = "/feedback",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> feedbackDetails(
      @Valid @RequestBody FeedbackReqBean reqBean,
      @Context HttpServletResponse response,
      HttpServletRequest request)
      throws Exception {
    logger.info("INFO: UserSupportController - feedbackDetails() :: Starts");
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);

    ResponseBean responseBean = new ResponseBean();

    EmailResponse emailResponse =
        supportService.feedback(reqBean.getSubject(), reqBean.getBody(), auditRequest);
    if (MessageCode.EMAIL_ACCEPTED_BY_MAIL_SERVER.getMessage().equals(emailResponse.getMessage())) {
      responseBean.setMessage(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());
    } else {
      throw new ErrorCodeException(ErrorCode.FEEDBACK_ERROR_MESSAGE);
    }

    logger.info("INFO: UserSupportController - feedbackDetails() :: Ends");
    return new ResponseEntity<>(responseBean, HttpStatus.OK);
  }

  @PostMapping(
      value = "/contactUs",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> contactUsDetails(
      @RequestBody ContactUsReqBean reqBean,
      @Context HttpServletResponse response,
      HttpServletRequest request)
      throws Exception {
    logger.info("INFO: UserSupportController - contactUsDetails() :: Starts");
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);

    ResponseBean responseBean = new ResponseBean();
    EmailResponse emailResponse =
        supportService.contactUsDetails(
            // TODO(#2115): remove once the bug is fixed.
            "PlaceHolder App Name",
            reqBean.getSubject(),
            reqBean.getBody(),
            reqBean.getFirstName(),
            reqBean.getEmail(),
            auditRequest);
    if (MessageCode.EMAIL_ACCEPTED_BY_MAIL_SERVER.getMessage().equals(emailResponse.getMessage())) {
      responseBean.setMessage(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());
    } else {
      throw new ErrorCodeException(ErrorCode.CONTACT_US_ERROR_MESSAGE);
    }

    logger.info("INFO: UserSupportController - contactUsDetails() :: Ends");
    return new ResponseEntity<>(responseBean, HttpStatus.OK);
  }
}
