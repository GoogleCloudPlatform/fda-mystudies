/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.mapper;

import com.google.cloud.healthcare.fdamystudies.beans.LocationDetails;
import com.google.cloud.healthcare.fdamystudies.beans.LocationDetailsResponse;
import com.google.cloud.healthcare.fdamystudies.beans.LocationRequest;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.model.LocationEntity;
import java.util.ArrayList;
import java.util.List;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.ACTIVE_STATUS;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.NO;

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

  public static List<LocationDetails> toLocations(List<LocationEntity> locations) {
    List<LocationDetails> list = new ArrayList<>();
    for (LocationEntity locationEntity : locations) {
      LocationDetails location = new LocationDetails();
      location.setLocationId(locationEntity.getId());
      location.setName(locationEntity.getName());
      location.setDescription(locationEntity.getDescription());
      location.setCustomId(locationEntity.getCustomId());
      location.setStatus(locationEntity.getStatus());
      list.add(location);
    }
    return list;
  }
}
