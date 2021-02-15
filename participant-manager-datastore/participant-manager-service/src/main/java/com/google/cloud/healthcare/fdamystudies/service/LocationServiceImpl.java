/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.ACTIVE_STATUS;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.INACTIVE_STATUS;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.LOCATION_ACTIVATED;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.LOCATION_DECOMMISSIONED;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.LOCATION_EDITED;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.NEW_LOCATION_ADDED;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.LocationDetails;
import com.google.cloud.healthcare.fdamystudies.beans.LocationDetailsResponse;
import com.google.cloud.healthcare.fdamystudies.beans.LocationResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateLocationRequest;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerAuditLogHelper;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

  @Autowired private ParticipantManagerAuditLogHelper participantManagerHelper;

  @Override
  @Transactional
  public LocationEntity addNewLocation(
      LocationEntity location, String userId, AuditLogEventRequest auditRequest)
      throws ErrorCodeException {
    logger.entry("begin addNewLocation()");

    Optional<UserRegAdminEntity> optUserRegAdminUser = userRegAdminRepository.findById(userId);
    if (!optUserRegAdminUser.isPresent()) {
      throw new ErrorCodeException(ErrorCode.LOCATION_ACCESS_DENIED);
    }

    UserRegAdminEntity adminUser = optUserRegAdminUser.get();
    if (Permission.EDIT != Permission.fromValue(adminUser.getLocationPermission())) {
      logger.exit(
          String.format(
              "Add location failed with error code=%s", ErrorCode.LOCATION_ACCESS_DENIED));

      throw new ErrorCodeException(ErrorCode.LOCATION_ACCESS_DENIED);
    }

    Optional<LocationEntity> optLocation =
        locationRepository.findByCustomId(location.getCustomId());

    if (optLocation.isPresent()) {
      throw new ErrorCodeException(ErrorCode.LOCATION_ID_UNIQUE);
    }

    Optional<LocationEntity> optLocationEntity = locationRepository.findByName(location.getName());
    if (optLocationEntity.isPresent()) {
      throw new ErrorCodeException(ErrorCode.LOCATION_NAME_EXISTS);
    }

    location.setCreatedBy(adminUser.getId());
    LocationEntity created = locationRepository.saveAndFlush(location);

    auditRequest.setUserId(adminUser.getId());
    Map<String, String> map = Collections.singletonMap("location_id", created.getCustomId());

    participantManagerHelper.logEvent(NEW_LOCATION_ADDED, auditRequest, map);

    logger.exit(String.format("locationId=%s", created.getId()));
    return created;
  }

  @Override
  @Transactional
  public LocationDetailsResponse updateLocation(
      UpdateLocationRequest locationRequest, AuditLogEventRequest auditRequest) {
    logger.entry("begin updateLocation()");

    Optional<LocationEntity> optLocation =
        locationRepository.findById(locationRequest.getLocationId());

    ErrorCode errorCode = validateUpdateLocationRequest(locationRequest, optLocation);
    if (errorCode != null) {
      throw new ErrorCodeException(errorCode);
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

    auditRequest.setUserId(locationRequest.getUserId());
    Map<String, String> map = Collections.singletonMap("location_id", locationEntity.getCustomId());

    if (messageCode == MessageCode.REACTIVE_SUCCESS) {
      participantManagerHelper.logEvent(LOCATION_ACTIVATED, auditRequest, map);
    } else if (messageCode == MessageCode.DECOMMISSION_SUCCESS) {
      participantManagerHelper.logEvent(LOCATION_DECOMMISSIONED, auditRequest, map);
    } else {
      participantManagerHelper.logEvent(LOCATION_EDITED, auditRequest, map);
    }

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
      if (Permission.EDIT != Permission.fromValue(adminUser.getLocationPermission())) {
        return ErrorCode.LOCATION_UPDATE_DENIED;
      }
    }

    if (!optLocation.isPresent()) {
      return ErrorCode.LOCATION_NOT_FOUND;
    }

    LocationEntity locationEntity = optLocation.get();
    Optional<LocationEntity> optLocationEntityForName =
        locationRepository.findByName(locationRequest.getName());
    if (optLocationEntityForName.isPresent()
        && !locationEntity.getId().equals(optLocationEntityForName.get().getId())) {
      throw new ErrorCodeException(ErrorCode.LOCATION_NAME_EXISTS);
    }

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
      return ErrorCode.CANNOT_DECOMMISSION_SITE_FOR_ENROLLED_ACTIVE_STATUS;
    }

    if (ACTIVE_STATUS.equals(locationRequest.getStatus())
        && ACTIVE_STATUS.equals(locationEntity.getStatus())) {
      return ErrorCode.CANNOT_REACTIVATE;
    }

    return null;
  }

  @Override
  @Transactional
  public LocationResponse getLocations(
      String userId, Integer limit, Integer offset, String orderByCondition, String searchTerm) {
    logger.entry("begin getLocations()");

    Optional<UserRegAdminEntity> optUserRegAdminUser = userRegAdminRepository.findById(userId);
    UserRegAdminEntity adminUser = optUserRegAdminUser.get();
    if (Permission.NO_PERMISSION == Permission.fromValue(adminUser.getLocationPermission())) {
      throw new ErrorCodeException(ErrorCode.LOCATION_ACCESS_DENIED);
    }
    List<LocationEntity> locations =
        locationRepository.findAll(limit, offset, orderByCondition, searchTerm.toLowerCase());

    List<LocationDetails> locationDetailsList = new ArrayList<>();

    if (CollectionUtils.isEmpty(locations)) {
      LocationResponse locationResponse =
          new LocationResponse(MessageCode.GET_LOCATION_SUCCESS, locationDetailsList);
      locationResponse.setLocationPermission(adminUser.getLocationPermission());
      return locationResponse;
    }

    List<String> locationIds =
        locations.stream().map(LocationEntity::getId).distinct().collect(Collectors.toList());
    Map<String, List<String>> locationStudies = getStudiesAndGroupByLocationId(locationIds);

    locationDetailsList =
        locations.stream().map(LocationMapper::toLocationDetails).collect(Collectors.toList());
    for (LocationDetails locationDetails : locationDetailsList) {
      List<String> studies = locationStudies.get(locationDetails.getLocationId());
      if (CollectionUtils.isNotEmpty(studies)) {
        locationDetails.getStudyNames().addAll(studies);
      }
      locationDetails.setStudiesCount(locationDetails.getStudyNames().size());
    }
    LocationResponse locationResponse =
        new LocationResponse(MessageCode.GET_LOCATION_SUCCESS, locationDetailsList);
    locationResponse.setTotalLocationsCount(
        locationRepository.countLocationBySearchTerm(searchTerm));
    locationResponse.setLocationPermission(adminUser.getLocationPermission());
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
    if (Permission.NO_PERMISSION == Permission.fromValue(adminUser.getLocationPermission())) {
      throw new ErrorCodeException(ErrorCode.LOCATION_ACCESS_DENIED);
    }

    Optional<LocationEntity> optOfEntity = locationRepository.findById(locationId);
    if (!optOfEntity.isPresent()) {
      throw new ErrorCodeException(ErrorCode.LOCATION_NOT_FOUND);
    }

    LocationEntity locationEntity = optOfEntity.get();
    String studyNames = studyRepository.getStudyNamesByLocationId(locationId);

    LocationDetailsResponse locationResponse =
        LocationMapper.toLocationDetailsResponse(locationEntity, MessageCode.GET_LOCATION_SUCCESS);
    if (!StringUtils.isEmpty(studyNames)) {
      locationResponse.getStudyNames().addAll(Arrays.asList(studyNames.split(",")));
    }
    locationResponse.setLocationPermission(adminUser.getLocationPermission());
    logger.exit(String.format("locationId=%s", locationEntity.getId()));
    return locationResponse;
  }

  @Override
  @Transactional
  public LocationResponse getLocationsForSite(
      String userId, Integer status, String excludeStudyId) {

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
