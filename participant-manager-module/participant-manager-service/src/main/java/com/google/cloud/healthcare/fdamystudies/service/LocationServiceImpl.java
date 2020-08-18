/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.ACTIVE_STATUS;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.INACTIVE_STATUS;

import com.google.cloud.healthcare.fdamystudies.beans.LocationDetails;
import com.google.cloud.healthcare.fdamystudies.beans.LocationDetailsResponse;
import com.google.cloud.healthcare.fdamystudies.beans.LocationResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateLocationRequest;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.common.Permission;
import com.google.cloud.healthcare.fdamystudies.exceptions.ErrorCodeException;
import com.google.cloud.healthcare.fdamystudies.mapper.LocationMapper;
import com.google.cloud.healthcare.fdamystudies.model.LocationEntity;
import com.google.cloud.healthcare.fdamystudies.model.LocationIdStudyNamesPair;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import com.google.cloud.healthcare.fdamystudies.repository.LocationRepository;
import com.google.cloud.healthcare.fdamystudies.repository.SiteRepository;
import com.google.cloud.healthcare.fdamystudies.repository.StudyRepository;
import com.google.cloud.healthcare.fdamystudies.repository.UserRegAdminRepository;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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

  @Autowired private SiteRepository siteRepository;

  @Autowired private StudyRepository studyRepository;

  @Override
  @Transactional
  public LocationEntity addNewLocation(LocationEntity location, String userId)
      throws ErrorCodeException {
    logger.entry("begin addNewLocation()");

    Optional<UserRegAdminEntity> optUserRegAdminUser = userRegAdminRepository.findById(userId);
    if (!optUserRegAdminUser.isPresent()) {
      throw new ErrorCodeException(ErrorCode.LOCATION_ACCESS_DENIED);
    }

    UserRegAdminEntity adminUser = optUserRegAdminUser.get();
    if (Permission.READ_EDIT != Permission.fromValue(adminUser.getEditPermission())) {
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

  @Override
  @Transactional
  public LocationDetailsResponse updateLocation(UpdateLocationRequest locationRequest) {
    logger.entry("begin updateLocation()");

    Optional<LocationEntity> optLocation =
        locationRepository.findById(locationRequest.getLocationId());

    ErrorCode errorCode = validateUpdateLocationRequest(locationRequest, optLocation);
    if (errorCode != null) {
      logger.exit(errorCode);
      return new LocationDetailsResponse(errorCode);
    }

    LocationEntity locationEntity = optLocation.get();
    locationEntity.setName(
        StringUtils.defaultString(locationRequest.getName(), locationEntity.getName()));
    locationEntity.setDescription(
        StringUtils.defaultString(
            locationRequest.getDescription(), locationEntity.getDescription()));

    if (locationRequest.getStatus() != null) {
      locationEntity.setStatus(locationRequest.getStatus());
    }
    locationEntity = locationRepository.saveAndFlush(locationEntity);

    MessageCode messageCode = getMessageCodeByLocationStatus(locationRequest.getStatus());
    LocationDetailsResponse locationResponse =
        LocationMapper.toLocationDetailsResponse(locationEntity, messageCode);

    logger.exit(String.format("locationId=%s", locationEntity.getId()));
    return locationResponse;
  }

  private MessageCode getMessageCodeByLocationStatus(Integer status) {
    if (ACTIVE_STATUS.equals(status)) {
      return MessageCode.REACTIVE_SUCCESS;
    } else if (INACTIVE_STATUS.equals(status)) {
      return MessageCode.DECOMMISSION_SUCCESS;
    }
    return MessageCode.LOCATION_UPDATE_SUCCESS;
  }

  private ErrorCode validateUpdateLocationRequest(
      UpdateLocationRequest locationRequest, Optional<LocationEntity> optLocation) {
    Optional<UserRegAdminEntity> optUserRegAdminUser =
        userRegAdminRepository.findById(locationRequest.getUserId());

    if (optUserRegAdminUser.isPresent()) {
      UserRegAdminEntity adminUser = optUserRegAdminUser.get();
      if (Permission.READ_EDIT != Permission.fromValue(adminUser.getEditPermission())) {
        return ErrorCode.LOCATION_UPDATE_DENIED;
      }
    }

    if (!optLocation.isPresent()) {
      return ErrorCode.LOCATION_NOT_FOUND;
    }

    LocationEntity locationEntity = optLocation.get();
    if (locationEntity.isDefault()) {
      return ErrorCode.DEFAULT_SITE_MODIFY_DENIED;
    }

    if (INACTIVE_STATUS.equals(locationRequest.getStatus())
        && INACTIVE_STATUS.equals(locationEntity.getStatus())) {
      return ErrorCode.ALREADY_DECOMMISSIONED;
    }

    List<SiteEntity> listOfSite =
        siteRepository.findByLocationIdAndStatus(locationRequest.getLocationId(), ACTIVE_STATUS);
    if (INACTIVE_STATUS.equals(locationRequest.getStatus())
        && CollectionUtils.isNotEmpty(listOfSite)) {
      return ErrorCode.CANNOT_DECOMMISSIONED;
    }

    if (ACTIVE_STATUS.equals(locationRequest.getStatus())
        && ACTIVE_STATUS.equals(locationEntity.getStatus())) {
      return ErrorCode.CANNOT_REACTIVATE;
    }

    return null;
  }

  @Override
  @Transactional
  public LocationResponse getLocations(String userId) {
    logger.entry("begin getLocations()");

    Optional<UserRegAdminEntity> optUserRegAdminUser = userRegAdminRepository.findById(userId);
    UserRegAdminEntity adminUser = optUserRegAdminUser.get();
    if (Permission.NO_PERMISSION == Permission.fromValue(adminUser.getEditPermission())) {
      logger.exit(ErrorCode.LOCATION_ACCESS_DENIED);
      return new LocationResponse(ErrorCode.LOCATION_ACCESS_DENIED);
    }

    List<LocationEntity> locations =
        (List<LocationEntity>) CollectionUtils.emptyIfNull(locationRepository.findAll());
    List<String> locationIds =
        locations.stream().map(LocationEntity::getId).distinct().collect(Collectors.toList());
    Map<String, List<String>> locationStudies = getStudiesAndGroupByLocationId(locationIds);

    List<LocationDetails> locationDetailsList =
        locations.stream().map(LocationMapper::toLocationDetails).collect(Collectors.toList());
    for (LocationDetails locationDetails : locationDetailsList) {
      List<String> studies = locationStudies.get(locationDetails.getLocationId());
      locationDetails.getStudyNames().addAll(studies);
      locationDetails.setStudiesCount(locationDetails.getStudyNames().size());
    }
    LocationResponse locationResponse =
        new LocationResponse(MessageCode.GET_LOCATION_SUCCESS, locationDetailsList);
    logger.exit(String.format("locations size=%d", locationResponse.getLocations().size()));
    return locationResponse;
  }

  private Map<String, List<String>> getStudiesAndGroupByLocationId(List<String> locationIds) {
    List<LocationIdStudyNamesPair> studyNames =
        (List<LocationIdStudyNamesPair>)
            CollectionUtils.emptyIfNull(studyRepository.getStudyNameLocationIdPairs(locationIds));

    Map<String, List<String>> locationStudies = new HashMap<>();
    for (LocationIdStudyNamesPair locationIdStudyNames : studyNames) {
      String locationId = locationIdStudyNames.getLocationId();
      String studiesString = locationIdStudyNames.getStudyNames();
      if (StringUtils.isNotBlank(studiesString)) {
        List<String> studies = Arrays.asList(studiesString.split(","));
        locationStudies.put(locationId, studies);
      }
    }

    return locationStudies;
  }

  @Override
  @Transactional
  public LocationDetailsResponse getLocationById(String userId, String locationId) {
    logger.entry("begin getLocationById()");

    Optional<UserRegAdminEntity> optUserRegAdminUser = userRegAdminRepository.findById(userId);
    UserRegAdminEntity adminUser = optUserRegAdminUser.get();
    if (Permission.NO_PERMISSION == Permission.fromValue(adminUser.getEditPermission())) {
      logger.exit(ErrorCode.LOCATION_ACCESS_DENIED);
      return new LocationDetailsResponse(ErrorCode.LOCATION_ACCESS_DENIED);
    }

    Optional<LocationEntity> optOfEntity = locationRepository.findById(locationId);
    if (!optOfEntity.isPresent()) {
      logger.exit(ErrorCode.LOCATION_NOT_FOUND);
      return new LocationDetailsResponse(ErrorCode.LOCATION_NOT_FOUND);
    }

    LocationEntity locationEntity = optOfEntity.get();
    String studyNames = studyRepository.getStudyNamesByLocationId(locationId);

    LocationDetailsResponse locationResponse =
        LocationMapper.toLocationDetailsResponse(locationEntity, MessageCode.GET_LOCATION_SUCCESS);
    if (!StringUtils.isEmpty(studyNames)) {
      locationResponse.getStudies().addAll(Arrays.asList(studyNames.split(",")));
    }

    logger.exit(String.format("locationId=%s", locationEntity.getId()));
    return locationResponse;
  }

  @Override
  @Transactional
  public LocationResponse getLocationsForSite(
      String userId, Integer status, String excludeStudyId) {
    Optional<UserRegAdminEntity> optUserRegAdminUser = userRegAdminRepository.findById(userId);

    UserRegAdminEntity adminUser = optUserRegAdminUser.get();
    if (Permission.NO_PERMISSION == Permission.fromValue(adminUser.getEditPermission())) {
      logger.exit(ErrorCode.LOCATION_ACCESS_DENIED);
      return new LocationResponse(ErrorCode.LOCATION_ACCESS_DENIED);
    }
    List<LocationEntity> listOfLocation =
        (List<LocationEntity>)
            CollectionUtils.emptyIfNull(
                locationRepository.findByStatusAndExcludeStudyId(status, excludeStudyId));
    List<LocationDetails> locationDetails =
        listOfLocation.stream().map(LocationMapper::toLocationDetails).collect(Collectors.toList());
    logger.exit(String.format("locations size=%d", locationDetails.size()));
    return new LocationResponse(MessageCode.GET_LOCATION_FOR_SITE_SUCCESS, locationDetails);
  }
}
