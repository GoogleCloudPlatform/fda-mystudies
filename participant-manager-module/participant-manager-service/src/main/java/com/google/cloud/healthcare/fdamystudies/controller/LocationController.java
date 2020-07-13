/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.USER_ID_HEADER;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.healthcare.fdamystudies.beans.LocationRequest;
import com.google.cloud.healthcare.fdamystudies.beans.LocationResponse;
import com.google.cloud.healthcare.fdamystudies.service.LocationService;

@RestController
public class LocationController {

  private static final String BEGIN_REQUEST_LOG = "%s request";

  private XLogger logger = XLoggerFactory.getXLogger(LocationController.class.getName());

  @Autowired private LocationService locationService;

  @PostMapping("/locations")
  public ResponseEntity<LocationResponse> addNewLocation(
      @RequestHeader(name = USER_ID_HEADER) String userId,
      @Valid @RequestBody LocationRequest locationRequest,
      HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    locationRequest.setUserId(userId);

    LocationResponse locationResponse = locationService.addNewLocation(locationRequest);

    logger.exit(
        String.format(
            "status=%d and locationId=%s",
            locationResponse.getHttpStatusCode(), locationResponse.getLocationId()));
    return ResponseEntity.status(locationResponse.getHttpStatusCode()).body(locationResponse);
  }
}
