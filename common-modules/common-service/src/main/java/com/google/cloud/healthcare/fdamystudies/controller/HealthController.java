/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.cloud.healthcare.fdamystudies.service.OAuthService;

@RestController
@RequestMapping("/v1")
public class HealthController {

  private XLogger logger = XLoggerFactory.getXLogger(HealthController.class.getName());

  @Autowired private OAuthService oauthService;

  @GetMapping(
      value = "/healthCheck",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<JsonNode> health(HttpServletRequest request) {
    logger.entry(String.format("begin %s request with no args", request.getRequestURI()));

    ResponseEntity<JsonNode> healthResponse = oauthService.health();
    logger.exit(
        String.format(
            "status=%d and response=%s",
            healthResponse.getStatusCodeValue(), healthResponse.getBody()));
    return healthResponse;
  }
}
