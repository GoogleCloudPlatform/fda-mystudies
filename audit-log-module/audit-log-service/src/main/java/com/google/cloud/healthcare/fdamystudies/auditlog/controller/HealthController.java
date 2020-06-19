/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.auditlog.controller;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping("/v1")
public class HealthController extends BaseController {

  private XLogger logger = XLoggerFactory.getXLogger(HealthController.class.getName());

  @GetMapping(
      value = "/health",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<JsonNode> health(HttpServletRequest request) {
    logger.entry(String.format("begin %s request with no args", request.getRequestURI()));

    ResponseEntity<JsonNode> healthResponse = getOAuthService().health();
    logger.exit(
        String.format(
            "status=%d and response=%s",
            healthResponse.getStatusCodeValue(), healthResponse.getBody()));
    return healthResponse;
  }
}
