/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UserRegistrationForm;
import com.google.cloud.healthcare.fdamystudies.beans.UserRegistrationResponse;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.exceptions.ErrorCodeException;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.service.UserRegistrationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Api(
    tags = "User Registration",
    value = "User Registration",
    description = "Operations pertaining to register the user in user management service")
@RestController
public class UserRegistrationController {

  private XLogger logger = XLoggerFactory.getXLogger(UserRegistrationController.class.getName());

  @Autowired private UserRegistrationService userRegistrationService;

  private static final String BEGIN_REQUEST_LOG = "%s request";

  @Value("${email.code.expire_time}")
  private long expireTime;

  @ApiOperation(value = "Register the new user")
  @PostMapping("/register")
  public ResponseEntity<UserRegistrationResponse> registerUser(
      @Valid @RequestBody UserRegistrationForm user,
      @RequestHeader String appName,
      @RequestHeader("appId") String appId,
      HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);

    user.setAppId(appId);
    user.setAppName(appName);
    UserRegistrationResponse userRegistrationResponse =
        userRegistrationService.register(user, auditRequest);

    if (userRegistrationResponse.getErrorCode() != null) {
      throw new ErrorCodeException(ErrorCode.REGISTRATION_EMAIL_SEND_FAILED);
    }

    logger.exit("User registration successful");
    return ResponseEntity.status(HttpStatus.CREATED).body(userRegistrationResponse);
  }
}
