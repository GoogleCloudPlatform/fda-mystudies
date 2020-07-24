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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.healthcare.fdamystudies.beans.LocationDetailsResponse;
import com.google.cloud.healthcare.fdamystudies.beans.LocationRequest;
import com.google.cloud.healthcare.fdamystudies.beans.LocationResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateLocationRequest;
import com.google.cloud.healthcare.fdamystudies.service.LocationService;

@RestController
public class LocationController {

  private static final String BEGIN_REQUEST_LOG = "%s request";

  private static final String STATUS_LOG = "status=%d";

  private XLogger logger = XLoggerFactory.getXLogger(LocationController.class.getName());

  @Autowired private LocationService locationService;

  @PostMapping("/locations")
  public ResponseEntity<LocationDetailsResponse> addNewLocation(
      @RequestHeader(name = USER_ID_HEADER) String userId,
      @Valid @RequestBody LocationRequest locationRequest,
      HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    locationRequest.setUserId(userId);

    LocationDetailsResponse locationResponse = locationService.addNewLocation(locationRequest);

    logger.exit(
        String.format(
            "status=%d and locationId=%s",
            locationResponse.getHttpStatusCode(), locationResponse.getLocationId()));
    return ResponseEntity.status(locationResponse.getHttpStatusCode()).body(locationResponse);
  }

  @PutMapping("/locations/{locationId}")
  public ResponseEntity<LocationDetailsResponse> updateLocation(
      @RequestHeader(name = USER_ID_HEADER) String userId,
      @Valid @RequestBody UpdateLocationRequest locationRequest,
      @PathVariable String locationId,
      HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));

    locationRequest.setLocationId(locationId);
    locationRequest.setUserId(userId);
    LocationDetailsResponse locationResponse = locationService.updateLocation(locationRequest);

    logger.exit(
        String.format(
            "status=%d and locationId=%s",
            locationResponse.getHttpStatusCode(), locationResponse.getLocationId()));
    return ResponseEntity.status(locationResponse.getHttpStatusCode()).body(locationResponse);
  }

  @GetMapping(value = {"/locations"})
  public ResponseEntity<LocationResponse> getLocations(
      @RequestHeader(name = USER_ID_HEADER) String userId,
      @RequestParam(required = false) Integer status,
      @RequestParam(required = false) String excludeStudyId,
      HttpServletRequest request) {
    logger.entry(
        String.format(
            "%s request with status=%s and excludeStudyId=%s",
            request.getRequestURI(), status, excludeStudyId));

    LocationResponse locationResponse;
    if (status != null) {
      locationResponse = locationService.getLocationsForSite(userId, status, excludeStudyId);
    } else {
      locationResponse = locationService.getLocations(userId);
    }

    logger.exit(String.format(STATUS_LOG, locationResponse.getHttpStatusCode()));
    return ResponseEntity.status(locationResponse.getHttpStatusCode()).body(locationResponse);
  }

  @GetMapping(value = {"/locations/{locationId}"})
  public ResponseEntity<LocationDetailsResponse> getLocationById(
      @RequestHeader(name = USER_ID_HEADER) String userId,
      @PathVariable String locationId,
      HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    LocationDetailsResponse locationResponse = locationService.getLocationById(userId, locationId);

    logger.exit(String.format(STATUS_LOG, locationResponse.getHttpStatusCode()));
    return ResponseEntity.status(locationResponse.getHttpStatusCode()).body(locationResponse);
  }
}
