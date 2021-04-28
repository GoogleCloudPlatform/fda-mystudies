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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.core.Context;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Api(
    tags = "Support",
    value = "Support",
    description = "Operations pertaining to support in user management service")
@RestController
public class UserSupportController {

  private XLogger logger = XLoggerFactory.getXLogger(UserSupportController.class.getName());

  private static final String STATUS_LOG = "status=%d";

  private static final String BEGIN_REQUEST_LOG = "%s request";

  @Autowired UserSupportService supportService;

  @ApiOperation(
      value = "Triggers sending of 'Feedback' e-mail with data submitted with the request")
  @PostMapping(
      value = "/feedback",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> feedbackDetails(
      @Valid @RequestBody FeedbackReqBean reqBean,
      @RequestHeader String appName,
      @Context HttpServletResponse response,
      HttpServletRequest request)
      throws Exception {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);

    ResponseBean responseBean = new ResponseBean();

    reqBean.setAppName(appName);
    EmailResponse emailResponse = supportService.feedback(reqBean, auditRequest);

    if (MessageCode.EMAIL_ACCEPTED_BY_MAIL_SERVER.getMessage().equals(emailResponse.getMessage())) {
      responseBean.setMessage(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());
    } else {
      throw new ErrorCodeException(ErrorCode.FEEDBACK_ERROR_MESSAGE);
    }

    logger.exit(String.format(STATUS_LOG, HttpStatus.OK.value()));
    return new ResponseEntity<>(responseBean, HttpStatus.OK);
  }

  @ApiOperation(
      value = "Triggers sending of 'Contact Us' e-mail with data submitted with the request")
  @PostMapping(
      value = "/contactUs",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> contactUsDetails(
      @Valid @RequestBody ContactUsReqBean reqBean,
      @RequestHeader String appName,
      @Context HttpServletResponse response,
      HttpServletRequest request)
      throws Exception {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);

    ResponseBean responseBean = new ResponseBean();
    reqBean.setAppName(appName);
    EmailResponse emailResponse = supportService.contactUsDetails(reqBean, auditRequest);

    if (MessageCode.EMAIL_ACCEPTED_BY_MAIL_SERVER.getMessage().equals(emailResponse.getMessage())) {
      responseBean.setMessage(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());
    } else {
      throw new ErrorCodeException(ErrorCode.CONTACT_US_ERROR_MESSAGE);
    }

    logger.exit(String.format(STATUS_LOG, HttpStatus.OK.value()));
    return new ResponseEntity<>(responseBean, HttpStatus.OK);
  }
}
