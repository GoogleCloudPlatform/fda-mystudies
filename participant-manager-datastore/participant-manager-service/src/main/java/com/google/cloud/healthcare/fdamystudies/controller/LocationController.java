/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.USER_ID_HEADER;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.LocationDetailsResponse;
import com.google.cloud.healthcare.fdamystudies.beans.LocationRequest;
import com.google.cloud.healthcare.fdamystudies.beans.LocationResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateLocationRequest;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.exceptions.ErrorCodeException;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.mapper.LocationMapper;
import com.google.cloud.healthcare.fdamystudies.model.LocationEntity;
import com.google.cloud.healthcare.fdamystudies.service.LocationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Api(
    tags = "Locations",
    value = "Location related api's",
    description = "Operations pertaining to Locations in participant manager")
@RestController
public class LocationController {

  private static final String BEGIN_REQUEST_LOG = "%s request";

  private static final String STATUS_LOG = "status=%d";

  private XLogger logger = XLoggerFactory.getXLogger(LocationController.class.getName());

  @Autowired private LocationService locationService;

  @ApiOperation(value = "create a new location")
  @PostMapping("/locations")
  @ResponseStatus(HttpStatus.CREATED)
  public LocationDetailsResponse addNewLocation(
      @RequestHeader(name = USER_ID_HEADER) String userId,
      @Valid @RequestBody LocationRequest locationRequest,
      HttpServletRequest request)
      throws Exception {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);

    LocationEntity location = LocationMapper.fromLocationRequest(locationRequest);
    LocationEntity created = locationService.addNewLocation(location, userId, auditRequest);

    logger.exit(String.format("locationId=%s", created.getId()));
    // TODO(675): return created instead of response
    return LocationMapper.toLocationDetailsResponse(created, MessageCode.ADD_LOCATION_SUCCESS);
  }

  @ApiOperation(value = "update existing location")
  @PutMapping("/locations/{locationId}")
  public ResponseEntity<LocationDetailsResponse> updateLocation(
      @RequestHeader(name = USER_ID_HEADER) String userId,
      @Valid @RequestBody UpdateLocationRequest locationRequest,
      @PathVariable String locationId,
      HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);

    locationRequest.setLocationId(locationId);
    locationRequest.setUserId(userId);
    LocationDetailsResponse locationResponse =
        locationService.updateLocation(locationRequest, auditRequest);

    logger.exit(
        String.format(
            "status=%d and locationId=%s",
            locationResponse.getHttpStatusCode(), locationResponse.getLocationId()));
    return ResponseEntity.status(locationResponse.getHttpStatusCode()).body(locationResponse);
  }

  @ApiOperation(value = "fetch location details")
  @GetMapping(value = {"/locations"})
  public ResponseEntity<LocationResponse> getLocations(
      @RequestHeader(name = USER_ID_HEADER) String userId,
      @RequestParam(required = false) Integer status,
      @RequestParam(required = false) String excludeStudyId,
      @RequestParam(defaultValue = "10") Integer limit,
      @RequestParam(defaultValue = "0") Integer offset,
      @RequestParam(defaultValue = "locationId") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDirection,
      @RequestParam(required = false) String searchTerm,
      HttpServletRequest request) {
    logger.entry(
        String.format(
            "%s request with status=%s and excludeStudyId=%s",
            request.getRequestURI(), status, excludeStudyId));

    String[] allowedSortByValues = {"locationId", "locationName", "status"};
    if (!ArrayUtils.contains(allowedSortByValues, sortBy)) {
      throw new ErrorCodeException(ErrorCode.UNSUPPORTED_SORTBY_VALUE);
    }

    String[] allowedSortDirection = {"asc", "desc"};
    if (!ArrayUtils.contains(allowedSortDirection, sortDirection)) {
      throw new ErrorCodeException(ErrorCode.UNSUPPORTED_SORT_DIRECTION_VALUE);
    }

    LocationResponse locationResponse;
    if (status != null && StringUtils.isNotEmpty(excludeStudyId)) {
      locationResponse = locationService.getLocationsForSite(userId, status, excludeStudyId);
    } else {

      locationResponse =
          locationService.getLocations(
              userId,
              limit,
              offset,
              sortBy + "_" + sortDirection,
              StringUtils.defaultString(searchTerm));
    }

    logger.exit(String.format(STATUS_LOG, locationResponse.getHttpStatusCode()));
    return ResponseEntity.status(locationResponse.getHttpStatusCode()).body(locationResponse);
  }

  @ApiOperation(value = "fetch location based on location id")
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
