/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.auditlog.controller;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping("/v1")
public class HealthController extends BaseController {

  private static final Logger LOG = LoggerFactory.getLogger(HealthController.class);

  @GetMapping(
      value = "/health",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<JsonNode> health(HttpServletRequest request) {
    if (LOG.isInfoEnabled()) {
      LOG.info(String.format("--- BEGIN %s request", request.getRequestURL()));
    }

    ResponseEntity<JsonNode> healthResponse = getOAuthService().health();

    if (LOG.isInfoEnabled()) {
      LOG.info(String.format("oauth health response %s", healthResponse.toString()));
    }
    return healthResponse;
  }
}
