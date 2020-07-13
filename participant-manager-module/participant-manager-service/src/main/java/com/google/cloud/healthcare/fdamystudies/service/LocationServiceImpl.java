/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import java.util.Optional;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.cloud.healthcare.fdamystudies.beans.LocationRequest;
import com.google.cloud.healthcare.fdamystudies.beans.LocationResponse;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.ManageLocation;
import com.google.cloud.healthcare.fdamystudies.mapper.LocationMapper;
import com.google.cloud.healthcare.fdamystudies.model.LocationEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import com.google.cloud.healthcare.fdamystudies.repository.LocationRepository;
import com.google.cloud.healthcare.fdamystudies.repository.UserRegAdminRepository;

@Service
public class LocationServiceImpl implements LocationService {

  private XLogger logger = XLoggerFactory.getXLogger(LocationServiceImpl.class.getName());

  @Autowired private UserRegAdminRepository userRegAdminRepository;

  @Autowired private LocationRepository locationRepository;

  @Override
  @Transactional
  public LocationResponse addNewLocation(LocationRequest locationRequest) {
    logger.entry("begin addNewLocation()");

    Optional<UserRegAdminEntity> optUserRegAdminUser =
        userRegAdminRepository.findById(locationRequest.getUserId());

    UserRegAdminEntity adminUser = optUserRegAdminUser.get();
    ManageLocation manageLocation = ManageLocation.valueOf(adminUser.getManageLocations());
    if (ManageLocation.DENY.equals(manageLocation)) {
      logger.exit(
          String.format(
              "Add location failed with error code=%s", ErrorCode.LOCATION_ACCESS_DENIED));
      return new LocationResponse(ErrorCode.LOCATION_ACCESS_DENIED);
    }
    LocationEntity locationEntity = LocationMapper.fromLocationRequest(locationRequest);
    locationEntity.setCreatedBy(adminUser.getId());
    locationEntity = locationRepository.saveAndFlush(locationEntity);
    logger.exit(String.format("locationId=%s", locationEntity.getId()));

    return LocationMapper.toLocationResponse(locationEntity);
  }
}
