/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.ManageLocation;
import com.google.cloud.healthcare.fdamystudies.exceptions.ErrorCodeException;
import com.google.cloud.healthcare.fdamystudies.model.LocationEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import com.google.cloud.healthcare.fdamystudies.repository.LocationRepository;
import com.google.cloud.healthcare.fdamystudies.repository.UserRegAdminRepository;
import java.util.Optional;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LocationServiceImpl implements LocationService {

  private XLogger logger = XLoggerFactory.getXLogger(LocationServiceImpl.class.getName());

  @Autowired private UserRegAdminRepository userRegAdminRepository;

  @Autowired private LocationRepository locationRepository;

  @Override
  @Transactional
  public LocationEntity addNewLocation(LocationEntity location, String userId) throws ErrorCodeException {
    logger.entry("begin addNewLocation()");

    Optional<UserRegAdminEntity> optUserRegAdminUser =
        userRegAdminRepository.findById(userId);
    if (!optUserRegAdminUser.isPresent()) {
      throw new ErrorCodeException(ErrorCode.LOCATION_ACCESS_DENIED);
    }

    UserRegAdminEntity adminUser = optUserRegAdminUser.get();
    ManageLocation manageLocation = ManageLocation.valueOf(adminUser.getManageLocations());
    if (ManageLocation.DENY.equals(manageLocation)) {
      logger.exit(
          String.format(
              "Add location failed with error code=%s", ErrorCode.LOCATION_ACCESS_DENIED));
      throw new ErrorCodeException(ErrorCode.LOCATION_ACCESS_DENIED);
    }

    location.setCreatedBy(adminUser.getId());
    LocationEntity created = locationRepository.saveAndFlush(location);

    logger.exit(String.format("locationId=%s", created.getId()));
    return created;
  }
}
