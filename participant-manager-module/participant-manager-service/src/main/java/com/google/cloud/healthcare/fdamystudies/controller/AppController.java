/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import com.google.cloud.healthcare.fdamystudies.beans.AppResponse;
import com.google.cloud.healthcare.fdamystudies.service.AppService;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.USER_ID_HEADER;

@RestController
@RequestMapping("/apps")
public class AppController {

  private XLogger logger = XLoggerFactory.getXLogger(AppController.class.getName());

  @Autowired private AppService appService;

  private static final String STATUS_LOG = "status=%d";

  private static final String BEGIN_REQUEST_LOG = "%s request";

  @GetMapping
  public ResponseEntity<AppResponse> getApps(
      @RequestHeader(name = USER_ID_HEADER) String userId, HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    AppResponse appResponse = appService.getApps(userId);
    logger.exit(String.format(STATUS_LOG, appResponse.getHttpStatusCode()));
    return ResponseEntity.status(appResponse.getHttpStatusCode()).body(appResponse);
  }
}
