/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.dao;

import java.util.List;
import java.util.Map;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.LocationBo;

public interface LocationsDAO {

  public List<LocationBo> getLocations(Integer locationId);

  public Map<Integer, Integer> getStudiesCountForLocations(List<Integer> locationIds);

  public void addNewLocation(LocationBo locationBo);

  public void updateLocation(LocationBo locationBo);

  public LocationBo getLocationInfo(Integer locationId) throws SystemException;

  public Map<Integer, List<String>> getStudiesForLocations(List<Integer> locationIds);
}
