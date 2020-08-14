/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.mapper;

import com.google.cloud.healthcare.fdamystudies.beans.UserAppPermissionRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UserSitePermissionRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UserStudyPermissionRequest;
import com.google.cloud.healthcare.fdamystudies.common.CommonConstants;
import com.google.cloud.healthcare.fdamystudies.common.IdGenerator;
import com.google.cloud.healthcare.fdamystudies.common.Permission;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.AppPermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.SitePermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyPermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

public final class UserMapper {

  private UserMapper() {}

  public static UserRegAdminEntity fromUserRequest(
      UserRequest userRequest, long securityCodeExpireTime) {
    UserRegAdminEntity admin = new UserRegAdminEntity();
    admin.setEmail(userRequest.getEmail());
    admin.setFirstName(userRequest.getFirstName());
    admin.setLastName(userRequest.getLastName());
    admin.setCreatedBy(userRequest.getSuperAdminUserId());
    admin.setEmailChanged(false);
    admin.setStatus(CommonConstants.INVITED_STATUS); // 2-> Invited, 0-> Deactivated, 1-> Active
    admin.setSuperAdmin(userRequest.isSuperAdmin());
    admin.setSecurityCode(IdGenerator.id());
    admin.setSecurityCodeExpireDate(
        new Timestamp(
            Instant.now().plus(securityCodeExpireTime, ChronoUnit.MINUTES).toEpochMilli()));
    int manageLocation = userRequest.isSuperAdmin() ? 2 : userRequest.getManageLocations();
    admin.setEditPermission(manageLocation);
    return admin;
  }

  public static UserRegAdminEntity fromUpdateUserRequest(
      UserRequest userRequest, UserRegAdminEntity adminDetails) {
    adminDetails.setEmail(userRequest.getEmail());
    adminDetails.setFirstName(userRequest.getFirstName());
    adminDetails.setLastName(userRequest.getLastName());
    adminDetails.setSuperAdmin(userRequest.isSuperAdmin());
    int manageLocation = userRequest.isSuperAdmin() ? 2 : userRequest.getManageLocations();
    adminDetails.setEditPermission(manageLocation);
    return adminDetails;
  }

  public static SitePermissionEntity newSitePermissionEntity(
      UserRequest user,
      UserSitePermissionRequest site,
      UserRegAdminEntity superAdminDeatils,
      SiteEntity siteDetails) {
    SitePermissionEntity sitePermission = new SitePermissionEntity();
    sitePermission.setApp(siteDetails.getStudy().getApp());
    sitePermission.setStudy(siteDetails.getStudy());
    sitePermission.setSite(siteDetails);
    sitePermission.setCreatedBy(user.getSuperAdminUserId());
    Permission edit = site != null && site.getPermission() == 1 ? Permission.VIEW : Permission.EDIT;
    sitePermission.setCanEdit(edit);
    sitePermission.setUrAdminUser(superAdminDeatils);
    return sitePermission;
  }

  public static SitePermissionEntity newSitePermissionEntity(
      UserRequest user,
      UserRegAdminEntity superAdminDeatils,
      UserStudyPermissionRequest study,
      StudyEntity studyDetails,
      SiteEntity site) {
    SitePermissionEntity sitePermission = new SitePermissionEntity();
    sitePermission.setApp(studyDetails.getApp());
    sitePermission.setCreatedBy(user.getSuperAdminUserId());
    Permission edit =
        study != null && study.getPermission() == 1 ? Permission.VIEW : Permission.EDIT;
    sitePermission.setCanEdit(edit);
    sitePermission.setStudy(studyDetails);
    sitePermission.setSite(site);
    sitePermission.setUrAdminUser(superAdminDeatils);
    return sitePermission;
  }

  public static List<SitePermissionEntity> newSitePermissionList(
      UserRequest user,
      UserRegAdminEntity superAdminDeatils,
      UserAppPermissionRequest app,
      AppEntity appDetails,
      List<SiteEntity> sites) {
    List<SitePermissionEntity> sitePermissions = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(sites)) {
      for (SiteEntity siteEntity : sites) {
        SitePermissionEntity sitePermission = new SitePermissionEntity();
        sitePermission.setApp(appDetails);
        sitePermission.setCreatedBy(user.getSuperAdminUserId());
        Permission edit =
            app != null && app.getPermission() == 1 ? Permission.VIEW : Permission.EDIT;
        sitePermission.setCanEdit(edit);
        sitePermission.setStudy(siteEntity.getStudy());
        sitePermission.setSite(siteEntity);
        sitePermission.setUrAdminUser(superAdminDeatils);
        sitePermissions.add(sitePermission);
      }
    }
    return sitePermissions;
  }

  public static StudyPermissionEntity newStudyPermissionEntity(
      UserRequest user,
      UserRegAdminEntity superAdminDeatils,
      UserStudyPermissionRequest study,
      StudyEntity studyDetails) {
    StudyPermissionEntity studyPermission = new StudyPermissionEntity();
    studyPermission.setApp(studyDetails.getApp());
    studyPermission.setStudy(studyDetails);
    studyPermission.setCreatedBy(user.getSuperAdminUserId());
    Permission edit =
        study != null && study.getPermission() == 1 ? Permission.VIEW : Permission.EDIT;
    studyPermission.setEdit(edit);
    studyPermission.setUrAdminUser(superAdminDeatils);
    return studyPermission;
  }

  public static List<StudyPermissionEntity> newStudyPermissionList(
      UserRequest userRequest,
      UserRegAdminEntity superAdminDeatils,
      UserAppPermissionRequest appRequest,
      AppEntity appDetails,
      List<StudyEntity> studies) {
    List<StudyPermissionEntity> studyPermissions = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(studies)) {
      for (StudyEntity studyEntity : studies) {
        StudyPermissionEntity studyPermission = new StudyPermissionEntity();
        studyPermission.setApp(appDetails);
        studyPermission.setCreatedBy(userRequest.getSuperAdminUserId());
        Permission edit =
            appRequest != null && appRequest.getPermission() == 1
                ? Permission.VIEW
                : Permission.EDIT;
        studyPermission.setEdit(edit);
        studyPermission.setStudy(studyEntity);
        studyPermission.setUrAdminUser(superAdminDeatils);
        studyPermissions.add(studyPermission);
      }
    }
    return studyPermissions;
  }

  public static AppPermissionEntity newAppPermissionEntity(
      UserRequest user, UserRegAdminEntity superAdminDeatils, AppEntity app) {
    AppPermissionEntity appPermission = new AppPermissionEntity();
    appPermission.setApp(app);
    appPermission.setCreatedBy(user.getSuperAdminUserId());
    appPermission.setEdit(Permission.EDIT);
    appPermission.setUrAdminUser(superAdminDeatils);
    return appPermission;
  }

  public static AppPermissionEntity newAppPermissionEntity(
      UserRequest userRequest,
      UserRegAdminEntity superAdminDeatils,
      UserAppPermissionRequest app,
      AppEntity appDetails) {
    AppPermissionEntity appPermission = new AppPermissionEntity();
    appPermission.setApp(appDetails);
    appPermission.setCreatedBy(userRequest.getSuperAdminUserId());
    Permission edit = app != null && app.getPermission() == 1 ? Permission.VIEW : Permission.EDIT;
    appPermission.setEdit(edit);
    appPermission.setUrAdminUser(superAdminDeatils);
    return appPermission;
  }
}
