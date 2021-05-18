/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.USER_ID_HEADER;

import com.google.cloud.healthcare.fdamystudies.beans.AppParticipantsResponse;
import com.google.cloud.healthcare.fdamystudies.beans.AppResponse;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.exceptions.ErrorCodeException;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.service.AppService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.Arrays;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(
    tags = "Apps",
    value = "apps related api's",
    description = "Operations pertaining to apps in participant manager")
@RestController
@RequestMapping("/apps")
public class AppController {

  private XLogger logger = XLoggerFactory.getXLogger(AppController.class.getName());

  @Autowired private AppService appService;

  private static final String STATUS_LOG = "status=%d";

  private static final String BEGIN_REQUEST_LOG = "%s request";

  @ApiOperation(
      value = "fetch a list of apps that correspond to the Studies to which user have permissions")
  @GetMapping
  public ResponseEntity<AppResponse> getApps(
      @RequestHeader(name = USER_ID_HEADER) String userId,
      @RequestParam(defaultValue = "10") Integer limit,
      @RequestParam(defaultValue = "0") Integer offset,
      @RequestParam(required = false) String searchTerm,
      @RequestParam(name = "fields", required = false) String[] fields,
      HttpServletRequest request) {
    fields = Optional.ofNullable(fields).orElse(new String[] {});
    logger.entry(
        String.format(
            "%s request with fields=%s", request.getRequestURI(), String.join(",", fields)));

    String[] allowedFields = {"studies", "sites"};
    AppResponse appResponse;
    if (ArrayUtils.isEmpty(fields)) {
      appResponse = appService.getApps(userId, limit, offset, searchTerm);
    } else if (Arrays.asList(allowedFields).containsAll(Arrays.asList(fields))) {
      appResponse = appService.getAppsWithOptionalFields(userId, fields);
    } else {
      throw new ErrorCodeException(ErrorCode.INVALID_APPS_FIELDS_VALUES);
    }

    logger.exit(String.format(STATUS_LOG, appResponse.getHttpStatusCode()));
    return ResponseEntity.status(appResponse.getHttpStatusCode()).body(appResponse);
  }

  @ApiOperation(value = "fetch app registrants with enrolled studies")
  @GetMapping("/{appId}/participants")
  public ResponseEntity<AppParticipantsResponse> getAppParticipants(
      @PathVariable String appId,
      @RequestHeader(name = USER_ID_HEADER) String userId,
      @RequestParam(defaultValue = "10") Integer limit,
      @RequestParam(defaultValue = "0") Integer offset,
      @RequestParam(defaultValue = "email") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDirection,
      @RequestParam(required = false) String searchTerm,
      HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    String[] allowedSortByValues = {"email", "registrationDate", "registrationStatus"};
    if (!ArrayUtils.contains(allowedSortByValues, sortBy)) {
      throw new ErrorCodeException(ErrorCode.UNSUPPORTED_SORTBY_VALUE);
    }

    String[] allowedSortDirection = {"asc", "desc"};
    if (!ArrayUtils.contains(allowedSortDirection, sortDirection)) {
      throw new ErrorCodeException(ErrorCode.UNSUPPORTED_SORT_DIRECTION_VALUE);
    }

    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);

    AppParticipantsResponse appParticipantsResponse =
        appService.getAppParticipants(
            appId, userId, auditRequest, limit, offset, sortBy + "_" + sortDirection, searchTerm);

    logger.exit(String.format(STATUS_LOG, appParticipantsResponse.getHttpStatusCode()));
    return ResponseEntity.status(appParticipantsResponse.getHttpStatusCode())
        .body(appParticipantsResponse);
  }
}
