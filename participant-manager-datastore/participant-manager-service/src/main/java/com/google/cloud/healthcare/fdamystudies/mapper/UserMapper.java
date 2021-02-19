/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.mapper;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.User;
import com.google.cloud.healthcare.fdamystudies.beans.UserAppDetails;
import com.google.cloud.healthcare.fdamystudies.beans.UserAppPermissionRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UserSiteDetails;
import com.google.cloud.healthcare.fdamystudies.beans.UserSitePermissionRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UserStudyDetails;
import com.google.cloud.healthcare.fdamystudies.beans.UserStudyPermissionRequest;
import com.google.cloud.healthcare.fdamystudies.common.EmailTemplate;
import com.google.cloud.healthcare.fdamystudies.common.IdGenerator;
import com.google.cloud.healthcare.fdamystudies.common.Permission;
import com.google.cloud.healthcare.fdamystudies.common.UserStatus;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.AppPermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.AppStudySiteInfo;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.SitePermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyPermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserAccountEmailSchedulerTaskEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

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
    admin.setStatus(UserStatus.INVITED.getValue());
    admin.setSuperAdmin(userRequest.isSuperAdmin());
    admin.setSecurityCode(IdGenerator.id());
    admin.setSecurityCodeExpireDate(
        new Timestamp(Instant.now().plus(securityCodeExpireTime, ChronoUnit.HOURS).toEpochMilli()));
    Integer manageLocation =
        userRequest.isSuperAdmin() ? Permission.EDIT.value() : userRequest.getManageLocations();
    admin.setLocationPermission(manageLocation);
    return admin;
  }

  public static UserRegAdminEntity fromUpdateUserRequest(
      UserRequest userRequest, UserRegAdminEntity adminDetails) {
    adminDetails.setFirstName(userRequest.getFirstName());
    adminDetails.setLastName(userRequest.getLastName());
    adminDetails.setSuperAdmin(userRequest.isSuperAdmin());
    Integer manageLocation =
        userRequest.isSuperAdmin() ? Permission.EDIT.value() : userRequest.getManageLocations();
    adminDetails.setLocationPermission(manageLocation);
    return adminDetails;
  }

  public static SitePermissionEntity newSitePermissionEntity(
      UserRequest user,
      UserSitePermissionRequest site,
      UserRegAdminEntity superAdminDetails,
      SiteEntity siteDetails) {
    SitePermissionEntity sitePermission = new SitePermissionEntity();
    sitePermission.setApp(siteDetails.getStudy().getApp());
    sitePermission.setStudy(siteDetails.getStudy());
    sitePermission.setSite(siteDetails);
    sitePermission.setCreatedBy(user.getSuperAdminUserId());
    Permission edit =
        site != null && site.getPermission() == Permission.VIEW.value()
            ? Permission.VIEW
            : Permission.EDIT;
    sitePermission.setCanEdit(edit);
    sitePermission.setUrAdminUser(superAdminDetails);
    return sitePermission;
  }

  public static SitePermissionEntity newSitePermissionEntity(
      UserRequest user,
      UserRegAdminEntity superAdminDetails,
      UserStudyPermissionRequest study,
      StudyEntity studyDetails,
      SiteEntity site) {
    SitePermissionEntity sitePermission = new SitePermissionEntity();
    sitePermission.setApp(studyDetails.getApp());
    sitePermission.setCreatedBy(user.getSuperAdminUserId());
    Permission edit =
        study != null && study.getPermission() == Permission.VIEW.value()
            ? Permission.VIEW
            : Permission.EDIT;
    sitePermission.setCanEdit(edit);
    sitePermission.setStudy(studyDetails);
    sitePermission.setSite(site);
    sitePermission.setUrAdminUser(superAdminDetails);
    return sitePermission;
  }

  public static List<SitePermissionEntity> newSitePermissionList(
      UserRequest user,
      UserRegAdminEntity superAdminDetails,
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
            app != null && app.getPermission() == Permission.VIEW.value()
                ? Permission.VIEW
                : Permission.EDIT;
        sitePermission.setCanEdit(edit);
        sitePermission.setStudy(siteEntity.getStudy());
        sitePermission.setSite(siteEntity);
        sitePermission.setUrAdminUser(superAdminDetails);
        sitePermissions.add(sitePermission);
      }
    }
    return sitePermissions;
  }

  public static StudyPermissionEntity newStudyPermissionEntity(
      UserRequest user,
      UserRegAdminEntity superAdminDetails,
      UserStudyPermissionRequest study,
      StudyEntity studyDetails) {
    StudyPermissionEntity studyPermission = new StudyPermissionEntity();
    studyPermission.setApp(studyDetails.getApp());
    studyPermission.setStudy(studyDetails);
    studyPermission.setCreatedBy(user.getSuperAdminUserId());
    Permission edit =
        study != null && study.getPermission() == Permission.VIEW.value()
            ? Permission.VIEW
            : Permission.EDIT;
    studyPermission.setEdit(edit);
    studyPermission.setUrAdminUser(superAdminDetails);
    return studyPermission;
  }

  public static List<StudyPermissionEntity> newStudyPermissionList(
      UserRequest userRequest,
      UserRegAdminEntity superAdminDetails,
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
            appRequest != null && appRequest.getPermission() == Permission.VIEW.value()
                ? Permission.VIEW
                : Permission.EDIT;
        studyPermission.setEdit(edit);
        studyPermission.setStudy(studyEntity);
        studyPermission.setUrAdminUser(superAdminDetails);
        studyPermissions.add(studyPermission);
      }
    }
    return studyPermissions;
  }

  public static AppPermissionEntity newAppPermissionEntity(
      UserRequest user, UserRegAdminEntity superAdminDetails, AppEntity app) {
    AppPermissionEntity appPermission = new AppPermissionEntity();
    appPermission.setApp(app);
    appPermission.setCreatedBy(user.getSuperAdminUserId());
    appPermission.setEdit(Permission.EDIT);
    appPermission.setUrAdminUser(superAdminDetails);
    return appPermission;
  }

  public static AppPermissionEntity newAppPermissionEntity(
      UserRequest userRequest,
      UserRegAdminEntity superAdminDetails,
      UserAppPermissionRequest app,
      AppEntity appDetails) {
    AppPermissionEntity appPermission = new AppPermissionEntity();
    appPermission.setApp(appDetails);
    appPermission.setCreatedBy(userRequest.getSuperAdminUserId());
    Permission edit =
        app != null && app.getPermission() == Permission.VIEW.value()
            ? Permission.VIEW
            : Permission.EDIT;
    appPermission.setEdit(edit);
    appPermission.setUrAdminUser(superAdminDetails);
    return appPermission;
  }

  public static User prepareUserInfo(UserRegAdminEntity admin) {
    User user = new User();
    user.setId(admin.getId());
    user.setEmail(admin.getEmail());
    user.setFirstName(admin.getFirstName());
    user.setLastName(admin.getLastName());
    user.setSuperAdmin(admin.isSuperAdmin());
    user.setManageLocations(admin.getLocationPermission());
    UserStatus userStatus = UserStatus.fromValue(admin.getStatus());
    user.setStatus(userStatus.getDescription());
    return user;
  }

  public static UserAppDetails toUserAppDetails(AppStudySiteInfo appStudySiteInfo) {
    UserAppDetails userApp = new UserAppDetails();
    userApp.setId(appStudySiteInfo.getAppId());
    userApp.setCustomId(appStudySiteInfo.getCustomAppId());
    userApp.setName(appStudySiteInfo.getAppName());
    if ("app".equals(appStudySiteInfo.getPermissionLevel())) {
      userApp.setPermission(appStudySiteInfo.getEdit());
      userApp.setSelected(true);
    }
    return userApp;
  }

  public static UserStudyDetails toUserStudyDetails(AppStudySiteInfo appStudySiteInfo) {
    UserStudyDetails studyDetails = new UserStudyDetails();
    studyDetails.setStudyId(appStudySiteInfo.getStudyId());
    studyDetails.setCustomStudyId(appStudySiteInfo.getCustomStudyId());
    studyDetails.setStudyName(appStudySiteInfo.getStudyName());
    if ("study".equals(appStudySiteInfo.getPermissionLevel())
        || "app".equals(appStudySiteInfo.getPermissionLevel())) {
      studyDetails.setPermission(appStudySiteInfo.getEdit());
      studyDetails.setSelected(true);
    }
    return studyDetails;
  }

  public static UserSiteDetails toUserSiteDetails(AppStudySiteInfo appStudySiteInfo) {
    UserSiteDetails siteDetails = new UserSiteDetails();
    siteDetails.setSiteId(appStudySiteInfo.getSiteId());
    siteDetails.setLocationId(appStudySiteInfo.getLocationId());
    siteDetails.setCustomLocationId(appStudySiteInfo.getLocationCustomId());
    siteDetails.setLocationName(appStudySiteInfo.getLocationName());
    siteDetails.setLocationDescription(appStudySiteInfo.getLocationDescription());
    if (StringUtils.isNotEmpty(appStudySiteInfo.getPermissionLevel())) {
      siteDetails.setPermission(appStudySiteInfo.getEdit());
      siteDetails.setSelected(true);
    }

    return siteDetails;
  }

  public static UserAccountEmailSchedulerTaskEntity toUserAccountEmailSchedulerTaskEntity(
      AuditLogEventRequest auditRequest,
      UserRegAdminEntity adminDetails,
      EmailTemplate emailTemplate) {
    UserAccountEmailSchedulerTaskEntity userAccountEmailTaskEntity =
        new UserAccountEmailSchedulerTaskEntity();
    if (auditRequest != null) {
      userAccountEmailTaskEntity.setAppId(auditRequest.getAppId());
      userAccountEmailTaskEntity.setAppVersion(auditRequest.getAppVersion());
      userAccountEmailTaskEntity.setCorrelationId(auditRequest.getCorrelationId());
      userAccountEmailTaskEntity.setSource(auditRequest.getSource());
      userAccountEmailTaskEntity.setMobilePlatform(auditRequest.getMobilePlatform());
      userAccountEmailTaskEntity.setCreatedBy(auditRequest.getUserId());
    }
    userAccountEmailTaskEntity.setUserId(adminDetails.getId());
    userAccountEmailTaskEntity.setEmailTemplateType(emailTemplate.getTemplate());
    return userAccountEmailTaskEntity;
  }
}
