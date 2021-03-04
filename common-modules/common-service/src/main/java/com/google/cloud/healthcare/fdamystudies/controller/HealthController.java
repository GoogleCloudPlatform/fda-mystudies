/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import com.google.cloud.healthcare.fdamystudies.beans.BaseResponse;
import com.google.cloud.healthcare.fdamystudies.beans.ValidationErrorResponse;
import com.google.cloud.healthcare.fdamystudies.common.CommonConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Collections;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Get Health", description = "Provides an indication about the health of the service")
// Swagger Issue - Default response message models are not recognized directly but works fine if the
// response models are referred from the @ApiResponse at least once. Refer SwaggerConfig class for
// default response messages configuration.
@ApiResponses(
    value = {
      @ApiResponse(
          code = 400,
          message = CommonConstants.BAD_REQUEST_MESSAGE,
          response = ValidationErrorResponse.class),
      @ApiResponse(
          code = 401,
          message = CommonConstants.UNAUTHORIZED_MESSAGE,
          response = BaseResponse.class),
      @ApiResponse(
          code = 500,
          message = CommonConstants.APPLICATION_ERROR_MESSAGE,
          response = BaseResponse.class)
    })
@RestController
public class HealthController {

  private static Map<String, String> alwaysUp = Collections.singletonMap("status", "UP");

  @ApiOperation(
      value = "Provides an indication about the health of the service",
      notes = "Default response codes 400 and 401 are not applicable for this operation")
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Service is Up and Running"),
      })
  @GetMapping(
      value = "/healthCheck",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public Map<String, String> health() {
    return alwaysUp;
  }
}
