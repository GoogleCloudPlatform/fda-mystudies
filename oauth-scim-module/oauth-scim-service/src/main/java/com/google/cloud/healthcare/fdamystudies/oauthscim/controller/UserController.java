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
import com.google.cloud.healthcare.fdamystudies.beans.ChangePasswordRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ChangePasswordResponse;
import com.google.cloud.healthcare.fdamystudies.beans.ResetPasswordRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ResetPasswordResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateEmailStatusRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateEmailStatusResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UserResponse;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.oauthscim.service.UserService;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

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

  @PostMapping(
      value = "/user/reset_password",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> resetPassword(
      @Valid @RequestBody ResetPasswordRequest resetPasswordRequest, HttpServletRequest request)
      throws JsonProcessingException {
    logger.entry(String.format(BEGIN_S_REQUEST_LOG, request.getRequestURI()));
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    ResetPasswordResponse resetPasswordResponse =
        userService.resetPassword(resetPasswordRequest, auditRequest);

    logger.exit(String.format(STATUS_LOG, resetPasswordResponse.getHttpStatusCode()));
    return ResponseEntity.status(resetPasswordResponse.getHttpStatusCode())
        .body(resetPasswordResponse);
  }

  @PutMapping(
      value = "/users/{userId}/change_password",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> changePassword(
      @PathVariable String userId,
      @Valid @RequestBody ChangePasswordRequest userRequest,
      HttpServletRequest request)
      throws JsonProcessingException {
    logger.entry(String.format("begin %s request", request.getRequestURI()));
    userRequest.setUserId(userId);

    ChangePasswordResponse userResponse = userService.changePassword(userRequest);

    logger.exit(String.format("status=%d", userResponse.getHttpStatusCode()));
    return ResponseEntity.status(userResponse.getHttpStatusCode()).body(userResponse);
  }

  @PutMapping(
      value = "/users/{userId}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UpdateEmailStatusResponse> updateEmailStatus(
      @PathVariable String userId,
      @Valid @RequestBody UpdateEmailStatusRequest userRequest,
      HttpServletRequest request)
      throws JsonProcessingException {
    logger.entry(String.format(BEGIN_S_REQUEST_LOG, request.getRequestURI()));
    userRequest.setUserId(userId);

    if (!userRequest.hasAtleastOneRequiredValue()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new UpdateEmailStatusResponse(ErrorCode.INVALID_UPDATE_USER_REQUEST));
    }

    UpdateEmailStatusResponse userResponse = userService.updateEmailStatusAndTempRegId(userRequest);

    logger.exit(String.format(STATUS_LOG, userResponse.getHttpStatusCode()));
    return ResponseEntity.status(userResponse.getHttpStatusCode()).body(userResponse);
  }

  @PostMapping(value = "/users/{userId}/logout", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UserResponse> logout(
      @PathVariable String userId,
      @RequestHeader(name = "Authorization") String token,
      HttpServletRequest request)
      throws JsonProcessingException {
    logger.entry(String.format(BEGIN_S_REQUEST_LOG, request.getRequestURI()));

    UserResponse userResponse = userService.logout(userId);

    logger.exit(String.format(STATUS_LOG, userResponse.getHttpStatusCode()));
    return ResponseEntity.status(userResponse.getHttpStatusCode()).body(userResponse);
  }
}
