/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.google.cloud.healthcare.fdamystudies.beans.AdminUserResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UserAppPermissionRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UserSitePermissionRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UserStudyPermissionRequest;
import com.google.cloud.healthcare.fdamystudies.common.CommonConstants;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.config.AppPropertyConfig;
import com.google.cloud.healthcare.fdamystudies.mapper.UserMapper;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.AppPermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.SitePermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyPermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import com.google.cloud.healthcare.fdamystudies.repository.AppRepository;
import com.google.cloud.healthcare.fdamystudies.repository.SiteRepository;
import com.google.cloud.healthcare.fdamystudies.repository.StudyRepository;
import com.google.cloud.healthcare.fdamystudies.repository.UserRegAdminRepository;

@Service
public class ManageUserServiceImpl implements ManageUserService {

  private XLogger logger = XLoggerFactory.getXLogger(ManageUserServiceImpl.class.getName());

  @Autowired private UserRegAdminRepository userAdminRepository;

  @Autowired private AppRepository appRepository;

  @Autowired private StudyRepository studyRepository;

  @Autowired private SiteRepository siteRepository;

  @Autowired private AppPropertyConfig appConfig;

  @Override
  @Transactional
  public AdminUserResponse createUser(UserRequest user) {
    logger.entry(String.format("createUser() with isSuperAdmin=%b", user.isSuperAdmin()));
    ErrorCode errorCode = validateUserRequest(user);
    if (errorCode != null) {
      logger.exit(String.format(CommonConstants.ERROR_CODE_LOG, errorCode));
      return new AdminUserResponse(errorCode);
    }

    AdminUserResponse userResponse =
        user.isSuperAdmin() ? saveSuperAdminDetails(user) : saveAdminDetails(user);

    logger.exit(String.format(CommonConstants.STATUS_LOG, userResponse.getHttpStatusCode()));
    return userResponse;
  }

  private ErrorCode validateUserRequest(UserRequest user) {
    logger.entry("validateUserRequest()");
    Optional<UserRegAdminEntity> optAdminDetails =
        userAdminRepository.findById(user.getSuperAdminUserId());
    if (!optAdminDetails.isPresent()) {
      return ErrorCode.USER_NOT_FOUND;
    }

    UserRegAdminEntity loggedInUserDeatils = optAdminDetails.get();
    if (!loggedInUserDeatils.isSuperAdmin()) {
      return ErrorCode.NOT_SUPER_ADMIN_ACCESS;
    }

    if (!user.isSuperAdmin()
        && (CollectionUtils.isEmpty(user.getApps()) || !hasAtleastOnePermission(user))) {
      return ErrorCode.PERMISSION_MISSING;
    }

    Optional<UserRegAdminEntity> optUsers = userAdminRepository.findByEmail(user.getEmail());
    logger.exit("Successfully validated user request");
    return optUsers.isPresent() ? ErrorCode.EMAIL_EXISTS : null;
  }

  private boolean hasAtleastOnePermission(UserRequest user) {
    logger.entry("hasAtleastOnePermission()");
    Predicate<UserAppPermissionRequest> appPredicate = app -> app.isSelected();
    Predicate<UserStudyPermissionRequest> studyPredicate = study -> study.isSelected();
    Predicate<UserSitePermissionRequest> sitePredicate = site -> site.isSelected();

    List<UserAppPermissionRequest> selectedApps =
        user.getApps().stream().filter(appPredicate).collect(Collectors.toList());
    if (CollectionUtils.isNotEmpty(selectedApps)) {
      return true;
    }

    for (UserAppPermissionRequest appPermission : user.getApps()) {
      List<UserStudyPermissionRequest> selectedStudies =
          CollectionUtils.emptyIfNull(appPermission.getStudies())
              .stream()
              .filter(studyPredicate)
              .collect(Collectors.toList());
      if (CollectionUtils.isNotEmpty(selectedStudies)) {
        return true;
      }

      for (UserStudyPermissionRequest studyPermission : appPermission.getStudies()) {
        List<UserSitePermissionRequest> selectedSites =
            CollectionUtils.emptyIfNull(studyPermission.getSites())
                .stream()
                .filter(sitePredicate)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(selectedSites)) {
          return true;
        }
      }
    }

    logger.exit("No permissions found, return false");
    return false;
  }

  private AdminUserResponse saveAdminDetails(UserRequest user) {
    logger.entry("saveAdminDetails()");
    UserRegAdminEntity adminDetails =
        UserMapper.fromUserRequest(user, Long.valueOf(appConfig.getSecurityCodeExpireDate()));
    adminDetails = userAdminRepository.saveAndFlush(adminDetails);
    logger.exit("Successfully saved admin details.");
    return new AdminUserResponse(MessageCode.ADD_NEW_USER_SUCCESS, adminDetails.getId());
  }

  private AdminUserResponse saveSuperAdminDetails(UserRequest user) {
    logger.entry("saveSuperAdminDetails()");
    UserRegAdminEntity superAdminDeatils =
        UserMapper.fromUserRequest(user, Long.valueOf(appConfig.getSecurityCodeExpireDate()));

    List<AppPermissionEntity> appPermissions =
        getAppPermissisonsForSuperAdmin(user, superAdminDeatils);
    superAdminDeatils.getAppPermissions().addAll(appPermissions);

    List<StudyPermissionEntity> studyPermissions =
        getStudyPermissisonsForSuperAdmin(user, superAdminDeatils);
    superAdminDeatils.getStudyPermissions().addAll(studyPermissions);

    List<SitePermissionEntity> sitePermissions =
        getSitePermissisonsForSuperAdmin(user, superAdminDeatils);
    superAdminDeatils.getSitePermissions().addAll(sitePermissions);

    userAdminRepository.saveAndFlush(superAdminDeatils);

    logger.exit(String.format(CommonConstants.MESSAGE_CODE_LOG, MessageCode.ADD_NEW_USER_SUCCESS));
    return new AdminUserResponse(MessageCode.ADD_NEW_USER_SUCCESS, superAdminDeatils.getId());
  }

  private List<SitePermissionEntity> getSitePermissisonsForSuperAdmin(
      UserRequest user, UserRegAdminEntity superAdminDeatils) {
    logger.entry("getSitePermissisonsForSuperAdmin()");
    List<SiteEntity> sites =
        (List<SiteEntity>) CollectionUtils.emptyIfNull(siteRepository.findAll());
    List<SitePermissionEntity> sitePermissions = new ArrayList<>();
    for (SiteEntity siteInfo : sites) {
      SitePermissionEntity sitePermission =
          UserMapper.newSitePermissionEntity(user, null, superAdminDeatils, siteInfo);
      sitePermissions.add(sitePermission);
    }

    logger.exit(String.format("total site permissions=%d", sitePermissions.size()));
    return sitePermissions;
  }

  private List<StudyPermissionEntity> getStudyPermissisonsForSuperAdmin(
      UserRequest user, UserRegAdminEntity superAdminDeatils) {
    logger.entry("getStudyPermissisonsForSuperAdmin()");
    List<StudyEntity> studies =
        (List<StudyEntity>) CollectionUtils.emptyIfNull(studyRepository.findAll());
    List<StudyPermissionEntity> studyPermissions = new ArrayList<>();
    for (StudyEntity studyInfo : studies) {
      StudyPermissionEntity studyPermission =
          UserMapper.newStudyPermissionEntity(user, superAdminDeatils, null, studyInfo);
      studyPermissions.add(studyPermission);
    }

    logger.exit(String.format("total study permissions=%d", studyPermissions.size()));
    return studyPermissions;
  }

  private List<AppPermissionEntity> getAppPermissisonsForSuperAdmin(
      UserRequest user, UserRegAdminEntity superAdminDeatils) {
    logger.entry("getAppPermissisonsForSuperAdmin()");
    List<AppEntity> apps = (List<AppEntity>) CollectionUtils.emptyIfNull(appRepository.findAll());
    List<AppPermissionEntity> appPermissions = new ArrayList<>();
    for (AppEntity app : apps) {
      AppPermissionEntity appPermission =
          UserMapper.newAppPermissionEntity(user, superAdminDeatils, app);
      appPermissions.add(appPermission);
    }

    logger.exit(String.format("total app permissions=%d", appPermissions.size()));
    return appPermissions;
  }
}
