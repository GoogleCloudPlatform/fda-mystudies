/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import javax.servlet.http.HttpServletRequest;
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

  @Autowired private OAuthService oauthService;

  @GetMapping(
      value = "/healthCheck",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<JsonNode> health(HttpServletRequest request) {
    return oauthService.health();
  }
}
