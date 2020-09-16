/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.LocationDetailsResponse;
import com.google.cloud.healthcare.fdamystudies.beans.LocationResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateLocationRequest;
import com.google.cloud.healthcare.fdamystudies.exceptions.ErrorCodeException;
import com.google.cloud.healthcare.fdamystudies.model.LocationEntity;

public interface LocationService {

  public LocationEntity addNewLocation(
      LocationEntity location, String userId, AuditLogEventRequest auditRequest)
      throws ErrorCodeException;

  public LocationDetailsResponse updateLocation(
      UpdateLocationRequest locationRequest, AuditLogEventRequest auditRequest);

  public LocationResponse getLocations(String userId, int page, int limit);

  public LocationResponse getLocationsForSite(String userId, Integer status, String excludeStudyId);

  public LocationDetailsResponse getLocationById(String userId, String locationId);
}
