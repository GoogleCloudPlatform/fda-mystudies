/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import com.google.cloud.healthcare.fdamystudies.beans.AppContactEmailsResponse;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.exceptions.ErrorCodeException;
import com.google.cloud.healthcare.fdamystudies.service.AppServices;
import io.micrometer.core.instrument.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

  private static final String STATUS_LOG = "status=%d";

  private static final String BEGIN_REQUEST_LOG = "%s request";

  @Autowired private AppServices appServices;

  @ApiOperation(value = "Fetch app contact us and from email id")
  @GetMapping()
  public ResponseEntity<AppContactEmailsResponse> getAppContactEmails(
      @RequestParam String customAppId, HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    if (StringUtils.isBlank(customAppId)) {
      throw new ErrorCodeException(ErrorCode.BAD_REQUEST);
    }

    AppContactEmailsResponse appResponse = appServices.getAppContactEmails(customAppId);

    logger.exit(String.format(STATUS_LOG, appResponse.getHttpStatusCode()));
    return new ResponseEntity<>(appResponse, HttpStatus.OK);
  }
}
