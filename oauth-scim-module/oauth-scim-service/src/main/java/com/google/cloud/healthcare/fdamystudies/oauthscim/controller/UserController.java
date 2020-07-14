/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateUserRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateUserResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UserResponse;
import com.google.cloud.healthcare.fdamystudies.beans.ValidationErrorResponse;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.oauthscim.service.UserService;
import com.google.cloud.healthcare.fdamystudies.oauthscim.validator.UserValidator;
import java.net.UnknownHostException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class UserController {

  private static final String VALIDATION_ERROS_LOG = "validation erros=%s";

  private static final String STATUS_LOG = "status=%d";

  private static final String BEGIN_S_REQUEST_LOG = "begin %s request";

  private XLogger logger = XLoggerFactory.getXLogger(UserController.class.getName());

  @Autowired private UserService userService;

  @PostMapping(
      value = "/users",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UserResponse> createUser(
      @Valid @RequestBody UserRequest userRequest, HttpServletRequest request) {
    logger.entry(String.format(BEGIN_S_REQUEST_LOG, request.getRequestURI()));
    UserResponse userResponse = userService.createUser(userRequest);

    int status =
        StringUtils.isEmpty(userResponse.getErrorDescription())
            ? HttpStatus.CREATED.value()
            : userResponse.getHttpStatusCode();

    logger.exit(String.format(STATUS_LOG, status));
    return ResponseEntity.status(status).body(userResponse);
  }

  @PutMapping(
      value = "/users/{userId}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> updateUser(
      @PathVariable String userId,
      @Valid @RequestBody UpdateUserRequest userRequest,
      HttpServletRequest request)
      throws JsonProcessingException {
    logger.entry(String.format(BEGIN_S_REQUEST_LOG, request.getRequestURI()));
    userRequest.setUserId(userId);
    ValidationErrorResponse validationResult = UserValidator.validate(userRequest);
    if (validationResult.hasErrors()) {
      logger.exit(String.format(VALIDATION_ERROS_LOG, validationResult));
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationResult);
    }

    UpdateUserResponse userResponse = userService.updateUser(userRequest);
    int status =
        StringUtils.isEmpty(userResponse.getErrorDescription())
            ? HttpStatus.OK.value()
            : userResponse.getHttpStatusCode();

    logger.exit(String.format(STATUS_LOG, status));
    return ResponseEntity.status(status).body(userResponse);
  }

  @PutMapping(
      value = "/user/reset_password",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> resetPassword(
      @Valid @RequestBody UpdateUserRequest userRequest, HttpServletRequest request)
      throws JsonProcessingException, UnknownHostException {
    logger.entry(String.format(BEGIN_S_REQUEST_LOG, request.getRequestURI()));
    ValidationErrorResponse validationResult = UserValidator.validate(userRequest);
    if (validationResult.hasErrors()) {
      logger.exit(String.format(VALIDATION_ERROS_LOG, validationResult));
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationResult);
    }

    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    UpdateUserResponse userResponse = userService.resetPassword(userRequest, auditRequest);
    int status =
        StringUtils.isEmpty(userResponse.getErrorDescription())
            ? HttpStatus.OK.value()
            : userResponse.getHttpStatusCode();

    logger.exit(String.format(STATUS_LOG, status));
    return ResponseEntity.status(status).body(userResponse);
  }
}
