/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import com.google.cloud.healthcare.fdamystudies.beans.AdminUserResponse;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.GetAdminDetailsResponse;
import com.google.cloud.healthcare.fdamystudies.beans.GetUsersResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequest;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.exceptions.ErrorCodeException;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.service.ManageUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(
    tags = "Users",
    value = "User related api's",
    description = "Operations pertaining to Users in participant manager")
@RestController
public class UserController {

  private XLogger logger = XLoggerFactory.getXLogger(UserController.class.getName());

  private static final String BEGIN_REQUEST_LOG = "%s request";

  private static final String EXIT_STATUS_LOG = "status=%d";

  @Autowired private ManageUserService manageUserService;

  @ApiOperation(value = "add new admin with permissions and invite through email")
  @PostMapping(
      value = "/users",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdminUserResponse> addNewUserSiteCoordinator(
      @Valid @RequestBody UserRequest user,
      @RequestHeader(name = "userId") String superAdminUserId,
      HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));

    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    auditRequest.setUserId(superAdminUserId);

    user.setSuperAdminUserId(superAdminUserId);
    AdminUserResponse userResponse = manageUserService.createUser(user, auditRequest);
    logger.exit(String.format(EXIT_STATUS_LOG, userResponse.getHttpStatusCode()));
    return ResponseEntity.status(userResponse.getHttpStatusCode()).body(userResponse);
  }

  @ApiOperation(value = "update admin with permissions and send permission update email")
  @PutMapping(
      value = "/users/{adminUserId}/",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdminUserResponse> updateUserSiteCoordinator(
      @RequestHeader(name = "userId") String signedInUserId,
      @Valid @RequestBody UserRequest user,
      @PathVariable String adminUserId,
      HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));

    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    auditRequest.setUserId(signedInUserId);

    user.setSuperAdminUserId(signedInUserId);
    user.setId(adminUserId);
    AdminUserResponse userResponse = manageUserService.updateUser(user, adminUserId, auditRequest);
    logger.exit(String.format(EXIT_STATUS_LOG, userResponse.getHttpStatusCode()));
    return ResponseEntity.status(userResponse.getHttpStatusCode()).body(userResponse);
  }

  @ApiOperation(value = "fetch particular admin detail")
  @GetMapping(value = {"/users/admin/{adminId}"})
  public ResponseEntity<GetAdminDetailsResponse> getAdminDetailsAndApps(
      @RequestHeader("userId") String signedInUserId,
      @PathVariable(value = "adminId", required = false) String adminId,
      @RequestParam(value = "includeUnselected", required = false) boolean includeUnselected,
      HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    GetAdminDetailsResponse userResponse =
        manageUserService.getAdminDetails(signedInUserId, adminId, includeUnselected);
    logger.exit(String.format(EXIT_STATUS_LOG, userResponse.getHttpStatusCode()));
    return ResponseEntity.status(userResponse.getHttpStatusCode()).body(userResponse);
  }

  @ApiOperation(value = "fetch all admin details")
  @GetMapping(value = {"/users"})
  public ResponseEntity<GetUsersResponse> getUsers(
      @RequestHeader("userId") String superAdminUserId,
      @RequestParam(defaultValue = "10") Integer limit,
      @RequestParam(defaultValue = "0") Integer offset,
      @RequestParam(defaultValue = "firstName") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDirection,
      @RequestParam(required = false) String searchTerm,
      HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    String[] allowedSortByValues = {"firstName", "lastName", "email", "status"};

    if (!ArrayUtils.contains(allowedSortByValues, sortBy)) {
      throw new ErrorCodeException(ErrorCode.UNSUPPORTED_SORTBY_VALUE);
    }

    String[] allowedSortDirection = {"asc", "desc"};
    if (!ArrayUtils.contains(allowedSortDirection, sortDirection)) {
      throw new ErrorCodeException(ErrorCode.UNSUPPORTED_SORT_DIRECTION_VALUE);
    }

    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    auditRequest.setUserId(superAdminUserId);

    GetUsersResponse userResponse =
        manageUserService.getUsers(
            superAdminUserId,
            limit,
            offset,
            auditRequest,
            sortBy + "_" + sortDirection,
            searchTerm);

    logger.exit(String.format(EXIT_STATUS_LOG, userResponse.getHttpStatusCode()));
    return ResponseEntity.status(userResponse.getHttpStatusCode()).body(userResponse);
  }

  @ApiOperation(value = "resend invitation email to the user")
  @PostMapping(
      value = "/users/{userId}/invite",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdminUserResponse> sendInvitation(
      @RequestHeader(name = "userId") String signedInUserId,
      @PathVariable String userId,
      HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);

    AdminUserResponse userResponse =
        manageUserService.sendInvitation(userId, signedInUserId, auditRequest);
    logger.exit(String.format(EXIT_STATUS_LOG, userResponse.getHttpStatusCode()));
    return ResponseEntity.status(userResponse.getHttpStatusCode()).body(userResponse);
  }
}
