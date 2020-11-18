/*
 * Copyright 2020 Google LLC
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

@RestController
@RequestMapping("/apps")
public class AppController {

  private XLogger logger = XLoggerFactory.getXLogger(AppController.class.getName());

  @Autowired private AppService appService;

  private static final String STATUS_LOG = "status=%d";

  private static final String BEGIN_REQUEST_LOG = "%s request";

  @GetMapping
  public ResponseEntity<AppResponse> getApps(
      @RequestHeader(name = USER_ID_HEADER) String userId,
      @RequestParam(name = "fields", required = false) String[] fields,
      HttpServletRequest request) {
    fields = Optional.ofNullable(fields).orElse(new String[] {});
    logger.entry(
        String.format(
            "%s request with fields=%s", request.getRequestURI(), String.join(",", fields)));

    String[] allowedFields = {"studies", "sites"};
    AppResponse appResponse;
    if (ArrayUtils.isEmpty(fields)) {
      appResponse = appService.getApps(userId);
    } else if (Arrays.asList(allowedFields).containsAll(Arrays.asList(fields))) {
      appResponse = appService.getAppsWithOptionalFields(userId, fields);
    } else {
      throw new ErrorCodeException(ErrorCode.INVALID_APPS_FIELDS_VALUES);
    }

    logger.exit(String.format(STATUS_LOG, appResponse.getHttpStatusCode()));
    return ResponseEntity.status(appResponse.getHttpStatusCode()).body(appResponse);
  }

  @GetMapping("/{appId}/participants")
  public ResponseEntity<AppParticipantsResponse> getAppParticipants(
      @PathVariable String appId,
      @RequestParam(required = false) String[] excludeSiteStatus,
      @RequestHeader(name = USER_ID_HEADER) String userId,
      HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);

    AppParticipantsResponse appParticipantsResponse =
        appService.getAppParticipants(appId, userId, auditRequest, excludeSiteStatus);

    logger.exit(String.format(STATUS_LOG, appParticipantsResponse.getHttpStatusCode()));
    return ResponseEntity.status(appParticipantsResponse.getHttpStatusCode())
        .body(appParticipantsResponse);
  }
}
