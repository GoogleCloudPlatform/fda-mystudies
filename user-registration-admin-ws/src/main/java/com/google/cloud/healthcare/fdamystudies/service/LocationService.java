/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.bean.LocationBean;
import com.google.cloud.healthcare.fdamystudies.bean.LocationResponseBean;

public interface LocationService {

  public LocationResponseBean getLocations(String authUserId, Integer locationId);

  public void addNewLocation(String authUserId, LocationBean locationBean);

  public void updateLocation(String authUserId, LocationBean locationBean);
}
