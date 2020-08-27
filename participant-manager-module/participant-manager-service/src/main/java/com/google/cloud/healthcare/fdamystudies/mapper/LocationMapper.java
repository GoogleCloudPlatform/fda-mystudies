/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.mapper;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.ACTIVE_STATUS;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.NO;

import com.google.cloud.healthcare.fdamystudies.beans.LocationDetails;
import com.google.cloud.healthcare.fdamystudies.beans.LocationDetailsResponse;
import com.google.cloud.healthcare.fdamystudies.beans.LocationRequest;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.model.LocationEntity;

public final class LocationMapper {

  private LocationMapper() {}

  public static LocationDetailsResponse toLocationDetailsResponse(
      LocationEntity location, MessageCode messageCode) {
    LocationDetailsResponse response = new LocationDetailsResponse(messageCode);
    response.setLocationId(location.getId());
    response.setCustomId(location.getCustomId());
    response.setDescription(location.getDescription());
    response.setName(location.getName());
    response.setStatus(location.getStatus());
    return response;
  }

  public static LocationEntity fromLocationRequest(LocationRequest locationRequest) {
    LocationEntity locationEntity = new LocationEntity();
    locationEntity.setName(locationRequest.getName());
    locationEntity.setDescription(locationRequest.getDescription());
    locationEntity.setCustomId(locationRequest.getCustomId());
    locationEntity.setStatus(ACTIVE_STATUS);
    locationEntity.setIsDefault(NO);
    return locationEntity;
  }

  public static LocationDetails toLocationDetails(LocationEntity locationEntity) {
    LocationDetails location = new LocationDetails();
    location.setLocationId(locationEntity.getId());
    location.setName(locationEntity.getName());
    location.setDescription(locationEntity.getDescription());
    location.setCustomId(locationEntity.getCustomId());
    location.setStatus(locationEntity.getStatus());
    return location;
  }
}
