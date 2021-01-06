/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Collections;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "Get Health", description = "Provides an indication about the health of the service")
public class HealthController {

  private static Map<String, String> alwaysUp = Collections.singletonMap("status", "UP");

  @ApiOperation(value = "Provides an indication about the health of the service")
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Service is Up and Running"),
        @ApiResponse(code = 500, message = "Service is down")
      })
  @GetMapping(
      value = "/healthCheck",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public Map<String, String> health() {
    return alwaysUp;
  }
}
