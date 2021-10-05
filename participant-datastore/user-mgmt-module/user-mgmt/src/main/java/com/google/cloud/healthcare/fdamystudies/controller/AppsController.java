/*
 * Copyright 2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import com.google.cloud.healthcare.fdamystudies.bean.AppMetadataBean;
import com.google.cloud.healthcare.fdamystudies.beans.AppContactEmailsResponse;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.exceptions.ErrorCodeException;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.service.AppsService;
import com.google.cloud.healthcare.fdamystudies.util.ErrorCode;
import io.micrometer.core.instrument.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(
    tags = "Apps",
    value = "Apps",
    description = "Operations pertaining to Apps in user management service")
@RestController
@RequestMapping("/apps")
public class AppsController {

  private XLogger logger = XLoggerFactory.getXLogger(AppsController.class.getName());

  @Autowired private AppsService appsServices;

  private static final String BEGIN_REQUEST_LOG = "%s request";

  private static final String STATUS_LOG = "status=%d";

  @ApiOperation(value = "Add or update appmetadata")
  @PostMapping("/appmetadata")
  public ResponseEntity<?> addUpdateAppMetadata(
      @Valid @RequestBody AppMetadataBean appMetadataBean, HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));

    ErrorBean errorBean = appsServices.saveAppMetadata(appMetadataBean);
    if (errorBean.getCode() != ErrorCode.EC_200.code()) {
      return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
    }

    logger.exit(String.format(STATUS_LOG, errorBean.getCode()));
    return new ResponseEntity<>(errorBean, HttpStatus.OK);
  }

  @ApiOperation(value = "Deactivate apps and associated users")
  @PutMapping("/{customAppId}/appDeactivate")
  public ResponseEntity<?> deactivateAppAndUsers(
      @PathVariable String customAppId, HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);

    ErrorBean errorBean = appsServices.deactivateAppAndUsers(customAppId, auditRequest);
    if (errorBean.getCode() != ErrorCode.EC_200.code()) {
      return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
    }

    logger.exit(String.format(STATUS_LOG, errorBean.getCode()));
    return new ResponseEntity<>(errorBean, HttpStatus.OK);
  }

  @ApiOperation(value = "Fetch app contact us and from email id")
  @GetMapping()
  public ResponseEntity<AppContactEmailsResponse> getAppContactEmails(
      @RequestParam String appId, HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    if (StringUtils.isBlank(appId)) {
      throw new ErrorCodeException(
          com.google.cloud.healthcare.fdamystudies.common.ErrorCode.BAD_REQUEST);
    }

    AppContactEmailsResponse appResponse = appsServices.getAppContactEmails(appId);

    logger.exit(String.format(STATUS_LOG, appResponse.getHttpStatusCode()));
    return new ResponseEntity<>(appResponse, HttpStatus.OK);
  }
}
