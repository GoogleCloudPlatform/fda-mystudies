/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.PatchUserRequest;
import com.google.cloud.healthcare.fdamystudies.beans.PatchUserResponse;
import com.google.cloud.healthcare.fdamystudies.beans.SetUpAccountRequest;
import com.google.cloud.healthcare.fdamystudies.beans.SetUpAccountResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UserProfileRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UserProfileResponse;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.service.UserProfileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Api(
    tags = "User Profile",
    value = "User profile related api's",
    description = "Operations pertaining to user profile in participant manager")
@RestController
public class UserProfileController {

  private XLogger logger = XLoggerFactory.getXLogger(UserProfileController.class.getName());

  private static final String STATUS_LOG = "status=%d";

  private static final String BEGIN_REQUEST_LOG = "%s request";

  @Autowired private UserProfileService userProfileService;

  @ApiOperation(value = "fetch user profile")
  @GetMapping(value = "/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UserProfileResponse> getUserProfile(
      @PathVariable String userId, HttpServletRequest request) {
    logger.entry(BEGIN_REQUEST_LOG, request.getRequestURI());
    UserProfileResponse profileResponse = userProfileService.getUserProfile(userId);

    logger.exit(String.format(STATUS_LOG, profileResponse.getHttpStatusCode()));
    return ResponseEntity.status(profileResponse.getHttpStatusCode()).body(profileResponse);
  }

  @ApiOperation(value = "fetch user profile by security code")
  @GetMapping(
      value = "/users/securitycodes/{securityCode}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UserProfileResponse> getUserDetails(
      @PathVariable String securityCode, HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    UserProfileResponse userProfileResponse =
        userProfileService.findUserProfileBySecurityCode(securityCode, auditRequest);

    logger.exit(String.format(STATUS_LOG, userProfileResponse.getHttpStatusCode()));
    return ResponseEntity.status(userProfileResponse.getHttpStatusCode()).body(userProfileResponse);
  }

  @ApiOperation(value = "update user account details")
  @PutMapping(
      value = "/users/{userId}/profile",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UserProfileResponse> updateUserProfile(
      @Valid @RequestBody UserProfileRequest userProfileRequest,
      @PathVariable String userId,
      HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);

    userProfileRequest.setUserId(userId);
    UserProfileResponse userProfileResponse =
        userProfileService.updateUserProfile(userProfileRequest, auditRequest);

    logger.exit(String.format(STATUS_LOG, userProfileResponse.getHttpStatusCode()));
    return ResponseEntity.status(userProfileResponse.getHttpStatusCode()).body(userProfileResponse);
  }

  @ApiOperation(value = "set up account by activating user after registration")
  @PostMapping(
      value = "/users/setUpAccount",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SetUpAccountResponse> setUpAccount(
      @Valid @RequestBody SetUpAccountRequest setUpAccountRequest, HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);

    SetUpAccountResponse setUpAccountResponse =
        userProfileService.saveUser(setUpAccountRequest, auditRequest);

    logger.exit(String.format(STATUS_LOG, setUpAccountResponse.getHttpStatusCode()));
    return ResponseEntity.status(setUpAccountResponse.getHttpStatusCode())
        .body(setUpAccountResponse);
  }

  @ApiOperation(value = "update user account status")
  @PatchMapping(
      value = "/users/{userId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PatchUserResponse> updateUserAccountStatus(
      @RequestHeader(name = "userId") String signedInUserId,
      @PathVariable String userId,
      @Valid @RequestBody PatchUserRequest userRequest,
      HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);

    userRequest.setUserId(userId);
    userRequest.setSignedInUserId(signedInUserId);
    PatchUserResponse deactivateResponse =
        userProfileService.updateUserAccountStatus(userRequest, auditRequest);

    logger.exit(String.format(STATUS_LOG, deactivateResponse.getHttpStatusCode()));
    return ResponseEntity.status(deactivateResponse.getHttpStatusCode()).body(deactivateResponse);
  }

  @ApiOperation(value = "delete invitation of the user")
  @DeleteMapping(value = "/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> deleteInvitation(
      @RequestHeader(name = "userId") String signedInUserId,
      @PathVariable String userId,
      HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);

    userProfileService.deleteInvitation(signedInUserId, userId, auditRequest);

    logger.exit("Sucessfully deleted invitation");
    return ResponseEntity.status(HttpStatus.OK).body(MessageCode.INVITATION_DELETED_SUCCESSFULLY);
  }
}
