/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.ACCOUNT_UPDATE_EMAIL_FAILED;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.ACCOUNT_UPDATE_EMAIL_SENT;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.NEW_USER_CREATED;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.NEW_USER_INVITATION_EMAIL_FAILED;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.NEW_USER_INVITATION_EMAIL_SENT;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.USER_RECORD_UPDATED;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.USER_REGISTRY_VIEWED;

import com.google.cloud.healthcare.fdamystudies.beans.AdminUserResponse;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.EmailRequest;
import com.google.cloud.healthcare.fdamystudies.beans.EmailResponse;
import com.google.cloud.healthcare.fdamystudies.beans.GetAdminDetailsResponse;
import com.google.cloud.healthcare.fdamystudies.beans.GetUsersResponse;
import com.google.cloud.healthcare.fdamystudies.beans.User;
import com.google.cloud.healthcare.fdamystudies.beans.UserAppDetails;
import com.google.cloud.healthcare.fdamystudies.beans.UserAppPermissionRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UserSiteDetails;
import com.google.cloud.healthcare.fdamystudies.beans.UserSitePermissionRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UserStudyDetails;
import com.google.cloud.healthcare.fdamystudies.beans.UserStudyPermissionRequest;
import com.google.cloud.healthcare.fdamystudies.common.CommonConstants;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.IdGenerator;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerAuditLogHelper;
import com.google.cloud.healthcare.fdamystudies.common.Permission;
import com.google.cloud.healthcare.fdamystudies.config.AppPropertyConfig;
import com.google.cloud.healthcare.fdamystudies.exceptions.ErrorCodeException;
import com.google.cloud.healthcare.fdamystudies.mapper.UserMapper;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.AppPermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.AppStudySiteInfo;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.SitePermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyPermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import com.google.cloud.healthcare.fdamystudies.repository.AppPermissionRepository;
import com.google.cloud.healthcare.fdamystudies.repository.AppRepository;
import com.google.cloud.healthcare.fdamystudies.repository.SitePermissionRepository;
import com.google.cloud.healthcare.fdamystudies.repository.SiteRepository;
import com.google.cloud.healthcare.fdamystudies.repository.StudyPermissionRepository;
import com.google.cloud.healthcare.fdamystudies.repository.StudyRepository;
import com.google.cloud.healthcare.fdamystudies.repository.UserRegAdminRepository;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ManageUserServiceImpl implements ManageUserService {

  private XLogger logger = XLoggerFactory.getXLogger(ManageUserServiceImpl.class.getName());

  @Autowired private UserRegAdminRepository userAdminRepository;

  @Autowired private AppRepository appRepository;

  @Autowired private StudyRepository studyRepository;

  @Autowired private SiteRepository siteRepository;

  @Autowired private AppPermissionRepository appPermissionRepository;

  @Autowired private StudyPermissionRepository studyPermissionRepository;

  @Autowired private SitePermissionRepository sitePermissionRepository;

  @Autowired private AppPropertyConfig appConfig;

  @Autowired private EmailService emailService;

  @Autowired private ParticipantManagerAuditLogHelper participantManagerHelper;

  @Override
  @Transactional
  public AdminUserResponse createUser(UserRequest user, AuditLogEventRequest auditRequest) {
    logger.entry(String.format("createUser() with isSuperAdmin=%b", user.isSuperAdmin()));
    ErrorCode errorCode = validateUserRequest(user);
    if (errorCode != null) {
      throw new ErrorCodeException(errorCode);
    }

    AdminUserResponse userResponse =
        user.isSuperAdmin()
            ? saveSuperAdminDetails(user, auditRequest)
            : saveAdminDetails(user, auditRequest);

    String accessLevel = user.isSuperAdmin() ? CommonConstants.SUPER_ADMIN : CommonConstants.ADMIN;
    if (userResponse.getUserId() != null) {
      Map<String, String> map = new HashMap<>();
      map.put(CommonConstants.NEW_USER_ID, userResponse.getUserId());
      map.put("new_user_access_level", accessLevel);
      participantManagerHelper.logEvent(NEW_USER_CREATED, auditRequest, map);
    }

    logger.exit(String.format(CommonConstants.STATUS_LOG, userResponse.getHttpStatusCode()));
    return userResponse;
  }

  private EmailResponse sendInvitationEmail(String email, String firstName, String securityCode) {
    Map<String, String> templateArgs = new HashMap<>();
    templateArgs.put("ORG_NAME", appConfig.getOrgName());
    templateArgs.put("FIRST_NAME", firstName);
    templateArgs.put("ACTIVATION_LINK", appConfig.getUserDetailsLink() + securityCode);
    templateArgs.put("CONTACT_EMAIL_ADDRESS", appConfig.getContactEmail());
    EmailRequest emailRequest =
        new EmailRequest(
            appConfig.getFromEmail(),
            new String[] {email},
            null,
            null,
            appConfig.getRegisterUserSubject(),
            appConfig.getRegisterUserBody(),
            templateArgs);
    return emailService.sendMimeMail(emailRequest);
  }

  private ErrorCode validateUserRequest(UserRequest user) {
    logger.entry("validateUserRequest()");
    Optional<UserRegAdminEntity> optAdminDetails =
        userAdminRepository.findById(user.getSuperAdminUserId());
    if (!optAdminDetails.isPresent()) {
      return ErrorCode.USER_NOT_FOUND;
    }

    UserRegAdminEntity loggedInUserDetails = optAdminDetails.get();
    if (!loggedInUserDetails.isSuperAdmin()) {
      return ErrorCode.NOT_SUPER_ADMIN_ACCESS;
    }

    if (!user.isSuperAdmin() && !hasAtleastOnePermission(user)) {
      return ErrorCode.PERMISSION_MISSING;
    }

    Optional<UserRegAdminEntity> optUsers = userAdminRepository.findByEmail(user.getEmail());
    logger.exit("Successfully validated user request");
    return optUsers.isPresent() ? ErrorCode.EMAIL_EXISTS : null;
  }

  private boolean hasAtleastOnePermission(UserRequest user) {
    logger.entry("hasAtleastOnePermission()");
    if (user.getManageLocations() != Permission.NO_PERMISSION.value()) {
      return true;
    } else if (CollectionUtils.isEmpty(user.getApps())) {
      return false;
    }

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

  private AdminUserResponse saveAdminDetails(UserRequest user, AuditLogEventRequest auditRequest) {
    logger.entry("saveAdminDetails()");
    UserRegAdminEntity adminDetails =
        UserMapper.fromUserRequest(user, Long.valueOf(appConfig.getSecurityCodeExpireDate()));
    adminDetails = userAdminRepository.saveAndFlush(adminDetails);

    if (CollectionUtils.isNotEmpty(user.getApps())) {
      Map<Boolean, List<UserAppPermissionRequest>> groupBySelectedAppMap =
          user.getApps()
              .stream()
              .collect(Collectors.groupingBy(UserAppPermissionRequest::isSelected));

      // save permissions for selected apps
      for (UserAppPermissionRequest app :
          CollectionUtils.emptyIfNull(groupBySelectedAppMap.get(CommonConstants.SELECTED))) {
        saveAppStudySitePermissions(user, adminDetails, app);
      }

      // save permissions for unselected apps
      for (UserAppPermissionRequest app :
          CollectionUtils.emptyIfNull(groupBySelectedAppMap.get(CommonConstants.UNSELECTED))) {
        for (UserStudyPermissionRequest study : CollectionUtils.emptyIfNull(app.getStudies())) {
          if (study.isSelected()) {
            saveStudySitePermissions(user, adminDetails, study);
          } else if (CollectionUtils.isNotEmpty(study.getSites())) {
            saveSitePermissions(user, adminDetails, study);
          }
        }
      }
    }

    EmailResponse emailResponse =
        sendInvitationEmail(user.getEmail(), user.getFirstName(), adminDetails.getSecurityCode());
    logger.debug(
        String.format("send add new user email status=%s", emailResponse.getHttpStatusCode()));

    Map<String, String> map =
        Collections.singletonMap(CommonConstants.NEW_USER_ID, adminDetails.getId());
    if (MessageCode.EMAIL_ACCEPTED_BY_MAIL_SERVER.getMessage().equals(emailResponse.getMessage())) {
      participantManagerHelper.logEvent(NEW_USER_INVITATION_EMAIL_SENT, auditRequest, map);
    } else {
      participantManagerHelper.logEvent(NEW_USER_INVITATION_EMAIL_FAILED, auditRequest, map);
    }

    logger.exit("Successfully saved admin details.");
    return new AdminUserResponse(MessageCode.ADD_NEW_USER_SUCCESS, adminDetails.getId());
  }

  private void saveAppStudySitePermissions(
      UserRequest user, UserRegAdminEntity adminDetails, UserAppPermissionRequest app) {
    logger.entry("saveAppStudySitePermissions()");
    Optional<AppEntity> optApp = appRepository.findById(app.getId());
    if (!optApp.isPresent()) {
      return;
    }

    AppEntity appDetails = optApp.get();
    AppPermissionEntity appPermission =
        UserMapper.newAppPermissionEntity(user, adminDetails, app, appDetails);
    appPermissionRepository.saveAndFlush(appPermission);

    List<StudyEntity> studies =
        (List<StudyEntity>) CollectionUtils.emptyIfNull(studyRepository.findByAppId(app.getId()));

    List<StudyPermissionEntity> studyPermissions =
        UserMapper.newStudyPermissionList(user, adminDetails, app, appDetails, studies);
    studyPermissionRepository.saveAll(studyPermissions);

    List<String> studyIds = studies.stream().map(StudyEntity::getId).collect(Collectors.toList());

    List<SiteEntity> sites =
        (List<SiteEntity>) CollectionUtils.emptyIfNull(siteRepository.findByStudyIds(studyIds));

    List<SitePermissionEntity> sitePermissions =
        UserMapper.newSitePermissionList(user, adminDetails, app, appDetails, sites);
    sitePermissionRepository.saveAll(sitePermissions);

    logger.exit("Successfully saved app study and site permissions.");
  }

  private void saveStudySitePermissions(
      UserRequest userId, UserRegAdminEntity superAdminDetails, UserStudyPermissionRequest study) {
    logger.entry("saveStudySitePermissions()");
    Optional<StudyEntity> optStudyInfo = studyRepository.findById(study.getStudyId());
    if (!optStudyInfo.isPresent()) {
      return;
    }
    List<SiteEntity> sites = siteRepository.findAll();
    StudyEntity studyDetails = optStudyInfo.get();
    StudyPermissionEntity studyPermission =
        UserMapper.newStudyPermissionEntity(userId, superAdminDetails, study, studyDetails);
    studyPermissionRepository.saveAndFlush(studyPermission);
    if (CollectionUtils.isNotEmpty(sites)) {
      for (SiteEntity site : sites) {
        if (site.getStudy().getId().equals(study.getStudyId())) {
          SitePermissionEntity sitePermission =
              UserMapper.newSitePermissionEntity(
                  userId, superAdminDetails, study, studyDetails, site);
          sitePermissionRepository.saveAndFlush(sitePermission);
        }
      }
    }
    logger.exit("Successfully saved study and site permissions.");
  }

  private void saveSitePermissions(
      UserRequest user, UserRegAdminEntity superAdminDetails, UserStudyPermissionRequest study) {
    for (UserSitePermissionRequest site : study.getSites()) {
      logger.entry("saveSitePermission()");
      if (site.isSelected()) {
        Optional<SiteEntity> optSite = siteRepository.findById(site.getSiteId());
        if (!optSite.isPresent()) {
          return;
        }
        SiteEntity siteDetails = optSite.get();
        SitePermissionEntity sitePermission =
            UserMapper.newSitePermissionEntity(user, site, superAdminDetails, siteDetails);
        sitePermissionRepository.saveAndFlush(sitePermission);
      }
    }
    logger.exit("Successfully saved site permissions.");
  }

  private AdminUserResponse saveSuperAdminDetails(
      UserRequest user, AuditLogEventRequest auditRequest) {
    logger.entry("saveSuperAdminDetails()");
    UserRegAdminEntity superAdminDetails =
        UserMapper.fromUserRequest(user, Long.valueOf(appConfig.getSecurityCodeExpireDate()));

    superAdminDetails = userAdminRepository.saveAndFlush(superAdminDetails);

    EmailResponse emailResponse =
        sendInvitationEmail(
            user.getEmail(), user.getFirstName(), superAdminDetails.getSecurityCode());
    logger.debug(
        String.format("send add new user email status=%s", emailResponse.getHttpStatusCode()));

    Map<String, String> map =
        Collections.singletonMap(CommonConstants.NEW_USER_ID, superAdminDetails.getId());
    if (MessageCode.EMAIL_ACCEPTED_BY_MAIL_SERVER.getMessage().equals(emailResponse.getMessage())) {
      participantManagerHelper.logEvent(NEW_USER_INVITATION_EMAIL_SENT, auditRequest, map);
    } else {
      participantManagerHelper.logEvent(NEW_USER_INVITATION_EMAIL_FAILED, auditRequest, map);
    }

    logger.exit(String.format(CommonConstants.MESSAGE_CODE_LOG, MessageCode.ADD_NEW_USER_SUCCESS));
    return new AdminUserResponse(MessageCode.ADD_NEW_USER_SUCCESS, superAdminDetails.getId());
  }

  @Override
  @Transactional
  public AdminUserResponse updateUser(
      UserRequest user, String superAdminUserId, AuditLogEventRequest auditRequest) {
    logger.entry(String.format("updateUser() with isSuperAdmin=%b", user.isSuperAdmin()));
    ErrorCode errorCode = validateUpdateUserRequest(user, superAdminUserId);
    if (errorCode != null) {
      throw new ErrorCodeException(errorCode);
    }

    AdminUserResponse userResponse =
        user.isSuperAdmin()
            ? updateSuperAdminDetails(user, superAdminUserId, auditRequest)
            : updateAdminDetails(user, superAdminUserId, auditRequest);
    String accessLevel = user.isSuperAdmin() ? CommonConstants.SUPER_ADMIN : CommonConstants.ADMIN;
    if (MessageCode.UPDATE_USER_SUCCESS.getMessage().equals(userResponse.getMessage())) {
      Map<String, String> map = new HashedMap<>();
      map.put(CommonConstants.EDITED_USER_ID, user.getId());
      map.put("edited_user_access_level", accessLevel);
      participantManagerHelper.logEvent(USER_RECORD_UPDATED, auditRequest, map);
    }

    logger.exit(String.format(CommonConstants.STATUS_LOG, userResponse.getHttpStatusCode()));
    return userResponse;
  }

  private ErrorCode validateUpdateUserRequest(UserRequest user, String superAdminUserId) {
    logger.entry("validateUpdateUserRequest()");
    Optional<UserRegAdminEntity> optSuperAdmin =
        userAdminRepository.findById(user.getSignedInUserId());
    UserRegAdminEntity admin =
        optSuperAdmin.orElseThrow(() -> new ErrorCodeException(ErrorCode.USER_NOT_FOUND));
    if (!admin.isSuperAdmin()) {
      throw new ErrorCodeException(ErrorCode.NOT_SUPER_ADMIN_ACCESS);
    }

    Optional<UserRegAdminEntity> optAdminDetails = userAdminRepository.findById(superAdminUserId);
    if (!optAdminDetails.isPresent() || user.getId() == null) {
      return ErrorCode.USER_NOT_FOUND;
    }

    if (!user.isSuperAdmin() && !hasAtleastOnePermission(user)) {
      return ErrorCode.PERMISSION_MISSING;
    }

    logger.exit("Successfully validated user request");
    return null;
  }

  private AdminUserResponse updateSuperAdminDetails(
      UserRequest user, String superAdminUserId, AuditLogEventRequest auditRequest) {
    logger.entry("updateSuperAdminDetails()");
    Optional<UserRegAdminEntity> optAdminDetails = userAdminRepository.findById(user.getId());

    if (!optAdminDetails.isPresent()) {
      throw new ErrorCodeException(ErrorCode.USER_NOT_FOUND);
    }

    UserRegAdminEntity adminDetails = optAdminDetails.get();
    adminDetails = UserMapper.fromUpdateUserRequest(user, adminDetails);

    userAdminRepository.saveAndFlush(adminDetails);

    deleteAppStudySiteLevelPermissions(user.getId());

    EmailResponse emailResponse = sendUserUpdatedEmail(user);
    logger.debug(String.format("send update email status=%s", emailResponse.getHttpStatusCode()));

    Map<String, String> map =
        Collections.singletonMap(CommonConstants.EDITED_USER_ID, adminDetails.getId());
    if (MessageCode.EMAIL_ACCEPTED_BY_MAIL_SERVER.getMessage().equals(emailResponse.getMessage())) {
      participantManagerHelper.logEvent(ACCOUNT_UPDATE_EMAIL_SENT, auditRequest, map);
    } else {
      participantManagerHelper.logEvent(ACCOUNT_UPDATE_EMAIL_FAILED, auditRequest, map);
    }

    logger.exit(String.format(CommonConstants.MESSAGE_CODE_LOG, MessageCode.UPDATE_USER_SUCCESS));
    return new AdminUserResponse(MessageCode.UPDATE_USER_SUCCESS, adminDetails.getId());
  }

  private EmailResponse sendUserUpdatedEmail(UserRequest user) {
    Map<String, String> templateArgs = new HashMap<>();
    templateArgs.put("ORG_NAME", appConfig.getOrgName());
    templateArgs.put("FIRST_NAME", user.getFirstName());
    templateArgs.put("CONTACT_EMAIL_ADDRESS", appConfig.getContactEmail());
    EmailRequest emailRequest =
        new EmailRequest(
            appConfig.getFromEmail(),
            new String[] {user.getEmail()},
            null,
            null,
            appConfig.getUpdateUserSubject(),
            appConfig.getUpdateUserBody(),
            templateArgs);
    return emailService.sendMimeMail(emailRequest);
  }

  private AdminUserResponse updateAdminDetails(
      UserRequest user, String superAdminUserId, AuditLogEventRequest auditRequest) {
    logger.entry("updateAdminDetails()");

    Optional<UserRegAdminEntity> optAdminDetails = userAdminRepository.findById(user.getId());

    if (!optAdminDetails.isPresent()) {
      throw new ErrorCodeException(ErrorCode.USER_NOT_FOUND);
    }

    UserRegAdminEntity adminDetails = optAdminDetails.get();
    adminDetails = UserMapper.fromUpdateUserRequest(user, adminDetails);
    userAdminRepository.saveAndFlush(adminDetails);

    deleteAppStudySiteLevelPermissions(user.getId());

    if (CollectionUtils.isNotEmpty(user.getApps())) {
      user.setSuperAdminUserId(superAdminUserId);
      Map<Boolean, List<UserAppPermissionRequest>> groupBySelectedAppMap =
          user.getApps()
              .stream()
              .collect(Collectors.groupingBy(UserAppPermissionRequest::isSelected));

      // save permissions for selected apps
      for (UserAppPermissionRequest app :
          CollectionUtils.emptyIfNull(groupBySelectedAppMap.get(CommonConstants.SELECTED))) {
        saveAppStudySitePermissions(user, adminDetails, app);
      }

      // save permissions for unselected apps
      for (UserAppPermissionRequest app :
          CollectionUtils.emptyIfNull(groupBySelectedAppMap.get(CommonConstants.UNSELECTED))) {
        for (UserStudyPermissionRequest study : CollectionUtils.emptyIfNull(app.getStudies())) {
          if (study.isSelected()) {
            saveStudySitePermissions(user, adminDetails, study);
          } else if (CollectionUtils.isNotEmpty(study.getSites())) {
            saveSitePermissions(user, adminDetails, study);
          }
        }
      }
    }

    EmailResponse emailResponse = sendUserUpdatedEmail(user);
    logger.debug(String.format("send update email status=%s", emailResponse.getHttpStatusCode()));

    Map<String, String> map =
        Collections.singletonMap(CommonConstants.EDITED_USER_ID, adminDetails.getId());
    if (MessageCode.EMAIL_ACCEPTED_BY_MAIL_SERVER.getMessage().equals(emailResponse.getMessage())) {
      participantManagerHelper.logEvent(ACCOUNT_UPDATE_EMAIL_SENT, auditRequest, map);
    } else {
      participantManagerHelper.logEvent(ACCOUNT_UPDATE_EMAIL_FAILED, auditRequest, map);
    }

    logger.exit("Successfully updated admin details.");
    return new AdminUserResponse(MessageCode.UPDATE_USER_SUCCESS, adminDetails.getId());
  }

  private void deleteAppStudySiteLevelPermissions(String userId) {
    logger.entry("deleteAllPermissions()");
    sitePermissionRepository.deleteByAdminUserId(userId);
    studyPermissionRepository.deleteByAdminUserId(userId);
    appPermissionRepository.deleteByAdminUserId(userId);
    logger.exit("Successfully deleted all the assigned permissions.");
  }

  @Override
  public GetAdminDetailsResponse getAdminDetails(
      String signedInUserId, String adminId, boolean includeUnselected) {
    logger.entry("getAdminDetails()");
    validateSignedInUser(signedInUserId);

    Optional<UserRegAdminEntity> optAdminDetails = userAdminRepository.findById(adminId);

    UserRegAdminEntity adminDetails =
        optAdminDetails.orElseThrow(() -> new ErrorCodeException(ErrorCode.ADMIN_NOT_FOUND));

    User user = UserMapper.prepareUserInfo(adminDetails);
    if (adminDetails.isSuperAdmin()) {
      logger.exit(String.format("superadmin=%b, status=%s", user.isSuperAdmin(), user.getStatus()));
      return new GetAdminDetailsResponse(MessageCode.GET_ADMIN_DETAILS_SUCCESS, user);
    }

    List<AppStudySiteInfo> selectedAppsStudiesSitesInfoList =
        appRepository.findAppsStudiesSitesByUserId(adminId);

    List<String> selectedAppIds = new ArrayList<>();
    selectedAppsStudiesSitesInfoList.forEach(
        p -> {
          if (!selectedAppIds.contains(p.getAppId())) {
            selectedAppIds.add(p.getAppId());
          }
        });

    if (selectedAppIds.isEmpty()) {
      logger.exit(String.format("superadmin=%b, status=%s", user.isSuperAdmin(), user.getStatus()));
      return new GetAdminDetailsResponse(MessageCode.GET_ADMIN_DETAILS_SUCCESS, user);
    }

    Map<String, UserAppDetails> unselectedAppsMap = new HashMap<>();
    Map<String, UserStudyDetails> unselectedStudiesMap = new HashMap<>();
    Map<String, UserSiteDetails> unselectedSitesMap = new HashMap<>();

    if (includeUnselected) {
      putUnselectedAppsStudiesSites(
          adminId, selectedAppIds, unselectedAppsMap, unselectedStudiesMap, unselectedSitesMap);
    }

    Map<String, UserAppDetails> appsMap = new HashMap<>();
    Map<String, UserStudyDetails> studiesMap = new HashMap<>();
    Map<String, UserSiteDetails> sitesMap = new HashMap<>();
    for (AppStudySiteInfo app : selectedAppsStudiesSitesInfoList) {
      UserAppDetails appDetails = null;
      if (!appsMap.containsKey(app.getAppId())) {
        appDetails = UserMapper.toUserAppDetails(app);
        if (unselectedAppsMap.containsKey(app.getAppId())) {
          UserAppDetails unselectedAppsDetails = unselectedAppsMap.get(app.getAppId());
          appDetails.getStudies().addAll(unselectedAppsDetails.getStudies());
          appDetails.setTotalSitesCount(unselectedAppsDetails.getTotalSitesCount());
        }

        appsMap.put(app.getAppId(), appDetails);
      }

      appDetails = appsMap.get(app.getAppId());

      UserStudyDetails userStudyDetails = null;
      if (!studiesMap.containsKey(app.getAppStudyIdKey())) {
        userStudyDetails = UserMapper.toUserStudyDetails(app);
        studiesMap.put(app.getAppStudyIdKey(), userStudyDetails);
        // add all unselected sites
        if (unselectedStudiesMap.containsKey(app.getAppStudyIdKey())) {
          userStudyDetails
              .getSites()
              .addAll(unselectedStudiesMap.get(app.getAppStudyIdKey()).getSites());
        }

        // replace unselected study with selected study
        appDetails
            .getStudies()
            .removeIf(study -> StringUtils.equals(study.getStudyId(), app.getStudyId()));
        appDetails.getStudies().add(userStudyDetails);

        if (userStudyDetails.isSelected()) {
          appDetails.setSelectedStudiesCount(appDetails.getSelectedStudiesCount() + 1);
        }
      }
      userStudyDetails = studiesMap.get(app.getAppStudyIdKey());

      if (StringUtils.isNotEmpty(app.getLocationName())
          && userStudyDetails != null
          && !sitesMap.containsKey(app.getAppStudySiteIdKey())) {
        UserSiteDetails userSiteDetails = UserMapper.toUserSiteDetails(app);
        sitesMap.put(app.getAppStudySiteIdKey(), userSiteDetails);
        // replace unselected site with selected site
        userStudyDetails
            .getSites()
            .removeIf(site -> StringUtils.equals(site.getSiteId(), app.getSiteId()));
        userStudyDetails.getSites().add(userSiteDetails);

        if (userSiteDetails.isSelected()) {
          appDetails.setSelectedSitesCount(appDetails.getSelectedSitesCount() + 1);
          userStudyDetails.setSelectedSitesCount(userStudyDetails.getSelectedSitesCount() + 1);
        }
        userStudyDetails.setTotalSitesCount(userStudyDetails.getSites().size());
        appDetails.setTotalSitesCount(appDetails.getTotalSitesCount() + 1);

        sortUserSites(userStudyDetails);
      }

      sortUserStudies(appDetails);

      appDetails.setTotalStudiesCount(appDetails.getStudies().size());
    }

    List<UserAppDetails> apps = appsMap.values().stream().collect(Collectors.toList());
    List<UserAppDetails> sortedApps =
        apps.stream()
            .sorted(Comparator.comparing(UserAppDetails::getName))
            .collect(Collectors.toList());
    user.getApps().addAll(sortedApps);

    logger.exit(
        String.format(
            "total apps=%d, superadmin=%b, status=%s",
            user.getApps().size(), user.isSuperAdmin(), user.getStatus()));
    return new GetAdminDetailsResponse(MessageCode.GET_ADMIN_DETAILS_SUCCESS, user);
  }

  private void sortUserSites(UserStudyDetails userStudyDetails) {
    List<UserSiteDetails> sortedSites =
        userStudyDetails
            .getSites()
            .stream()
            .sorted(Comparator.comparing(UserSiteDetails::getLocationName))
            .collect(Collectors.toList());
    userStudyDetails.getSites().clear();
    userStudyDetails.getSites().addAll(sortedSites);
  }

  private void sortUserStudies(UserAppDetails appDetails) {
    List<UserStudyDetails> sortedStudies =
        appDetails
            .getStudies()
            .stream()
            .sorted(Comparator.comparing(UserStudyDetails::getStudyName))
            .collect(Collectors.toList());
    appDetails.getStudies().clear();
    appDetails.getStudies().addAll(sortedStudies);
  }

  private void putUnselectedAppsStudiesSites(
      String adminId,
      List<String> selectedAppIds,
      Map<String, UserAppDetails> unselectedAppsMap,
      Map<String, UserStudyDetails> unselectedStudiesMap,
      Map<String, UserSiteDetails> unselectedSitesMap) {

    List<AppStudySiteInfo> unselectedAppsStudiesSitesInfoList =
        appRepository.findUnSelectedAppsStudiesSites(selectedAppIds, adminId);

    for (AppStudySiteInfo app : unselectedAppsStudiesSitesInfoList) {
      UserAppDetails appDetails = null;
      if (!unselectedAppsMap.containsKey(app.getAppId())) {
        appDetails = UserMapper.toUserAppDetails(app);
        unselectedAppsMap.put(app.getAppId(), appDetails);
      }

      appDetails = unselectedAppsMap.get(app.getAppId());
      UserStudyDetails userStudyDetails = null;
      if (!unselectedStudiesMap.containsKey(app.getAppStudyIdKey())) {
        userStudyDetails = UserMapper.toUserStudyDetails(app);
        unselectedStudiesMap.put(app.getAppStudyIdKey(), userStudyDetails);
        appDetails.getStudies().add(userStudyDetails);
      }

      userStudyDetails = unselectedStudiesMap.get(app.getAppStudyIdKey());

      if (userStudyDetails != null
          && StringUtils.isNotEmpty(app.getLocationName())
          && !unselectedSitesMap.containsKey(app.getAppStudySiteIdKey())) {
        UserSiteDetails userSiteDetails = UserMapper.toUserSiteDetails(app);
        unselectedSitesMap.put(app.getAppStudySiteIdKey(), userSiteDetails);
        userStudyDetails.getSites().add(userSiteDetails);
        appDetails.setTotalSitesCount(appDetails.getTotalSitesCount() + 1);
        userStudyDetails.setTotalSitesCount(userStudyDetails.getSites().size());
      }
    }
  }

  private void validateSignedInUser(String adminUserId) {
    Optional<UserRegAdminEntity> optAdminDetails = userAdminRepository.findById(adminUserId);
    UserRegAdminEntity user =
        optAdminDetails.orElseThrow(() -> new ErrorCodeException(ErrorCode.USER_NOT_FOUND));

    if (!user.isSuperAdmin()) {
      throw new ErrorCodeException(ErrorCode.NOT_SUPER_ADMIN_ACCESS);
    }
  }

  @Override
  public GetUsersResponse getUsers(
      String superAdminUserId, Integer page, Integer limit, AuditLogEventRequest auditRequest) {
    logger.entry("getUsers()");
    validateSignedInUser(superAdminUserId);

    List<User> users = new ArrayList<>();
    List<UserRegAdminEntity> adminList = null;
    if (page != null && limit != null) {
      Page<UserRegAdminEntity> adminPage =
          userAdminRepository.findAll(PageRequest.of(page, limit, Sort.by("created").descending()));
      adminList = (List<UserRegAdminEntity>) CollectionUtils.emptyIfNull(adminPage.getContent());
    } else {
      adminList = userAdminRepository.findAll();
    }

    adminList
        .stream()
        .map(admin -> users.add(UserMapper.prepareUserInfo(admin)))
        .collect(Collectors.toList());

    participantManagerHelper.logEvent(USER_REGISTRY_VIEWED, auditRequest);
    logger.exit(String.format("total users=%d", adminList.size()));
    return new GetUsersResponse(MessageCode.GET_USERS_SUCCESS, users, userAdminRepository.count());
  }

  @Override
  @Transactional
  public AdminUserResponse sendInvitation(String userId, String superAdminUserId) {

    validateInviteRequest(superAdminUserId);

    Optional<UserRegAdminEntity> optUser = userAdminRepository.findById(userId);
    UserRegAdminEntity user =
        optUser.orElseThrow(() -> new ErrorCodeException(ErrorCode.USER_NOT_FOUND));

    Timestamp now = new Timestamp(Instant.now().toEpochMilli());
    if (now.before(user.getSecurityCodeExpireDate())) {
      logger.info("Valid security code found, skip send invite email");
      return new AdminUserResponse(MessageCode.INVITATION_SENT_SUCCESSFULLY, user.getId());
    }

    user.setSecurityCode(IdGenerator.id());
    user.setSecurityCodeExpireDate(
        new Timestamp(
            Instant.now()
                .plus(Long.valueOf(appConfig.getSecurityCodeExpireDate()), ChronoUnit.MINUTES)
                .toEpochMilli()));
    user = userAdminRepository.saveAndFlush(user);

    EmailResponse emailResponse =
        sendInvitationEmail(user.getEmail(), user.getFirstName(), user.getSecurityCode());

    if (!MessageCode.EMAIL_ACCEPTED_BY_MAIL_SERVER
        .getMessage()
        .equals(emailResponse.getMessage())) {
      throw new ErrorCodeException(ErrorCode.APPLICATION_ERROR);
    }
    return new AdminUserResponse(MessageCode.INVITATION_SENT_SUCCESSFULLY, user.getId());
  }

  private void validateInviteRequest(String superAdminUserId) {
    Optional<UserRegAdminEntity> optAdminDetails = userAdminRepository.findById(superAdminUserId);

    UserRegAdminEntity loggedInUserDetails =
        optAdminDetails.orElseThrow(() -> new ErrorCodeException(ErrorCode.USER_NOT_FOUND));

    if (!loggedInUserDetails.isSuperAdmin()) {
      logger.error("Signed in user is not having super admin privileges");
      throw new ErrorCodeException(ErrorCode.NOT_SUPER_ADMIN_ACCESS);
    }
  }
}
