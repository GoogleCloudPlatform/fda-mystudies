/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import com.google.cloud.healthcare.fdamystudies.beans.DeactivateAccountResponse;
import com.google.cloud.healthcare.fdamystudies.beans.SetUpAccountRequest;
import com.google.cloud.healthcare.fdamystudies.beans.SetUpAccountResponse;
import com.google.cloud.healthcare.fdamystudies.service.UserProfileService;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserProfileController {

  private XLogger logger = XLoggerFactory.getXLogger(UserProfileController.class.getName());

  private static final String STATUS_LOG = "status=%d";

  private static final String BEGIN_REQUEST_LOG = "%s request";

  @Autowired private UserProfileService userProfileService;

  @PostMapping(
      value = "/users/",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SetUpAccountResponse> setUpAccount(
      @Valid @RequestBody SetUpAccountRequest setUpAccountRequest, HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));

    SetUpAccountResponse setUpAccountResponse = userProfileService.saveUser(setUpAccountRequest);

    logger.exit(String.format(STATUS_LOG, setUpAccountResponse.getHttpStatusCode()));
    return ResponseEntity.status(setUpAccountResponse.getHttpStatusCode())
        .body(setUpAccountResponse);
  }

  @PatchMapping(
      value = "/users/{userId}/deactivate",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<DeactivateAccountResponse> deactivateAccount(
      @PathVariable String userId, HttpServletRequest request) {

    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));

    DeactivateAccountResponse deactivateResponse = userProfileService.deactivateAccount(userId);

    logger.exit(String.format(STATUS_LOG, deactivateResponse.getHttpStatusCode()));
    return ResponseEntity.status(deactivateResponse.getHttpStatusCode()).body(deactivateResponse);
  }
}
