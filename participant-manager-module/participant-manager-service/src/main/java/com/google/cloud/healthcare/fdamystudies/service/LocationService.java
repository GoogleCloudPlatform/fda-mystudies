/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.beans.LocationDetailsResponse;
import com.google.cloud.healthcare.fdamystudies.beans.LocationRequest;
import com.google.cloud.healthcare.fdamystudies.beans.LocationResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateLocationRequest;

public interface LocationService {

  public LocationDetailsResponse addNewLocation(LocationRequest locationRequest);

  public LocationDetailsResponse updateLocation(UpdateLocationRequest locationRequest);

  public LocationResponse getLocations(String userId);

  public LocationResponse getLocationsForSite(String userId, Integer status, String excludeStudyId);

  public LocationDetailsResponse getLocationById(String userId, String locationId);
}
