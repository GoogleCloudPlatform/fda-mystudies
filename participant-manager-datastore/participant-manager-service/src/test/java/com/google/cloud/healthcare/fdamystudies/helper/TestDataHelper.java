/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.helper;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.ACTIVE_STATUS;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.NO;
import static com.google.cloud.healthcare.fdamystudies.common.TestConstants.CUSTOM_ID_VALUE;
import static com.google.cloud.healthcare.fdamystudies.common.TestConstants.LOCATION_DESCRIPTION_VALUE;
import static com.google.cloud.healthcare.fdamystudies.common.TestConstants.LOCATION_NAME_VALUE;
import static com.google.cloud.healthcare.fdamystudies.common.TestConstants.LOGO_IMAGE_URL;
import static com.google.cloud.healthcare.fdamystudies.common.TestConstants.VALID_BEARER_TOKEN;

import com.google.cloud.healthcare.fdamystudies.common.CommonConstants;
import com.google.cloud.healthcare.fdamystudies.common.DataSharingStatus;
import com.google.cloud.healthcare.fdamystudies.common.EnrollmentStatus;
import com.google.cloud.healthcare.fdamystudies.common.IdGenerator;
import com.google.cloud.healthcare.fdamystudies.common.OnboardingStatus;
import com.google.cloud.healthcare.fdamystudies.common.Permission;
import com.google.cloud.healthcare.fdamystudies.common.PlatformComponent;
import com.google.cloud.healthcare.fdamystudies.common.UserStatus;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.AppPermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.LocationEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.SitePermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyConsentEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyPermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import com.google.cloud.healthcare.fdamystudies.repository.AppPermissionRepository;
import com.google.cloud.healthcare.fdamystudies.repository.AppRepository;
import com.google.cloud.healthcare.fdamystudies.repository.InviteParticipantsEmailRepository;
import com.google.cloud.healthcare.fdamystudies.repository.LocationRepository;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantRegistrySiteRepository;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantStudyRepository;
import com.google.cloud.healthcare.fdamystudies.repository.SitePermissionRepository;
import com.google.cloud.healthcare.fdamystudies.repository.SiteRepository;
import com.google.cloud.healthcare.fdamystudies.repository.StudyConsentRepository;
import com.google.cloud.healthcare.fdamystudies.repository.StudyPermissionRepository;
import com.google.cloud.healthcare.fdamystudies.repository.StudyRepository;
import com.google.cloud.healthcare.fdamystudies.repository.UserAccountEmailSchedulerTaskRepository;
import com.google.cloud.healthcare.fdamystudies.repository.UserDetailsRepository;
import com.google.cloud.healthcare.fdamystudies.repository.UserRegAdminRepository;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Getter
@Component
public class TestDataHelper {

  public static final String ADMIN_LAST_NAME = "mockito_last_name";

  public static final String ADMIN_FIRST_NAME = "mockito";

  public static final String ADMIN_AUTH_ID_VALUE =
      "TuKUeFdyWz4E2A1-LqQcoYKBpMsfLnl-KjiuRFuxWcM3sQg";

  public static final String EMAIL_VALUE = "mockit_email@grr.la";

  public static final String NON_SUPER_ADMIN_EMAIL_ID = "mockit_non_super_admin_email@grr.la";

  public static final String SUPER_ADMIN_EMAIL_ID = "super_admin_email@grr.la";

  @Autowired private UserRegAdminRepository userRegAdminRepository;

  @Autowired private StudyRepository studyRepository;

  @Autowired private LocationRepository locationRepository;

  @Autowired private StudyPermissionRepository studyPermissionRepository;

  @Autowired private SitePermissionRepository sitePermissionRepository;

  @Autowired private AppPermissionRepository appPermissionRepository;

  @Autowired private AppRepository appRepository;

  @Autowired private SiteRepository siteRepository;

  @Autowired private UserDetailsRepository userDetailsRepository;

  @Autowired private ParticipantRegistrySiteRepository participantRegistrySiteRepository;

  @Autowired private ParticipantStudyRepository participantStudyRepository;

  @Autowired private StudyConsentRepository studyConsentRepository;

  @Autowired private InviteParticipantsEmailRepository invitedParticipantsEmailRepository;

  @Autowired private UserAccountEmailSchedulerTaskRepository addNewAdminEmailServiceRepository;

  public HttpHeaders newCommonHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add("Authorization", VALID_BEARER_TOKEN);
    headers.add("correlationId", IdGenerator.id());
    headers.add("appVersion", "1.0");
    headers.add("appId", "GCPMS001");
    headers.add("source", PlatformComponent.PARTICIPANT_MANAGER.getValue());
    return headers;
  }

  public UserRegAdminEntity newUserRegAdminEntity() {
    UserRegAdminEntity userRegAdminEntity = new UserRegAdminEntity();
    userRegAdminEntity.setEmail(EMAIL_VALUE);
    userRegAdminEntity.setFirstName(ADMIN_FIRST_NAME);
    userRegAdminEntity.setLastName(ADMIN_LAST_NAME);
    userRegAdminEntity.setLocationPermission(Permission.EDIT.value());
    userRegAdminEntity.setStatus(CommonConstants.ACTIVE_STATUS);
    userRegAdminEntity.setUrAdminAuthId(ADMIN_AUTH_ID_VALUE);
    userRegAdminEntity.setSuperAdmin(true);
    userRegAdminEntity.setSecurityCode("xnsxU1Ax1V2Xtpk-qNLeiZ-417JiqyjytC-706-km6gCq9HAXNYWd8");
    userRegAdminEntity.setSecurityCodeExpireDate(
        new Timestamp(Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli()));
    return userRegAdminEntity;
  }

  public UserRegAdminEntity createUserRegAdmin() {
    UserRegAdminEntity userRegAdminEntity = newUserRegAdminEntity();
    return userRegAdminRepository.saveAndFlush(userRegAdminEntity);
  }

  public UserRegAdminEntity newNonSuperAdmin() {
    UserRegAdminEntity userRegAdminEntity = new UserRegAdminEntity();
    userRegAdminEntity.setEmail(NON_SUPER_ADMIN_EMAIL_ID);
    userRegAdminEntity.setFirstName(ADMIN_FIRST_NAME);
    userRegAdminEntity.setLastName(ADMIN_LAST_NAME);
    userRegAdminEntity.setLocationPermission(Permission.NO_PERMISSION.value());
    userRegAdminEntity.setStatus(UserStatus.ACTIVE.getValue());
    userRegAdminEntity.setSuperAdmin(false);
    return userRegAdminEntity;
  }

  public UserRegAdminEntity createNonSuperAdmin() {
    UserRegAdminEntity userRegAdminEntity = newNonSuperAdmin();
    return userRegAdminRepository.saveAndFlush(userRegAdminEntity);
  }

  public UserRegAdminEntity newSuperAdminForUpdate() {
    UserRegAdminEntity userRegAdminEntity = new UserRegAdminEntity();
    userRegAdminEntity.setEmail(SUPER_ADMIN_EMAIL_ID);
    userRegAdminEntity.setFirstName("mockito_fname");
    userRegAdminEntity.setLastName("mockito__lname");
    userRegAdminEntity.setLocationPermission(Permission.EDIT.value());
    userRegAdminEntity.setStatus(UserStatus.ACTIVE.getValue());
    userRegAdminEntity.setSuperAdmin(true);
    return userRegAdminEntity;
  }

  public UserRegAdminEntity createSuperAdmin() {
    UserRegAdminEntity userRegAdminEntity = newSuperAdminForUpdate();
    return userRegAdminRepository.saveAndFlush(userRegAdminEntity);
  }

  public StudyEntity newStudyEntity() {
    StudyEntity studyEntity = new StudyEntity();
    studyEntity.setCustomId("StudyID01");
    studyEntity.setCategory("Public Health");
    studyEntity.setEnrolling("Yes");
    studyEntity.setStatus("Active");
    studyEntity.setName("Covid19");
    studyEntity.setSponsor("FDA");
    return studyEntity;
  }

  public AppEntity newAppEntity() {
    AppEntity appEntity = new AppEntity();
    appEntity.setAppId("MyStudies-Id-1");
    appEntity.setAppName("MyStudies-1");
    return appEntity;
  }

  public LocationEntity createLocation() {
    LocationEntity locationEntity = newLocationEntity();
    SiteEntity siteEntity = newSiteEntity();
    locationEntity.addSiteEntity(siteEntity);
    return locationRepository.saveAndFlush(locationEntity);
  }

  public LocationEntity newLocationEntity() {
    LocationEntity locationEntity = new LocationEntity();
    locationEntity.setCustomId(CUSTOM_ID_VALUE);
    locationEntity.setDescription(LOCATION_DESCRIPTION_VALUE);
    locationEntity.setName(RandomStringUtils.randomAlphanumeric(8));
    locationEntity.setStatus(ACTIVE_STATUS);
    locationEntity.setIsDefault(NO);
    return locationEntity;
  }

  public UserRegAdminEntity createUserRegAdminEntity() {
    return userRegAdminRepository.saveAndFlush(newUserRegAdminEntity());
  }

  public AppEntity createAppEntity(UserRegAdminEntity userEntity) {
    AppEntity appEntity = newAppEntity();
    AppPermissionEntity appPermissionEntity = new AppPermissionEntity();
    appPermissionEntity.setEdit(Permission.EDIT);
    appPermissionEntity.setUrAdminUser(userEntity);
    appEntity.addAppPermissionEntity(appPermissionEntity);
    return appRepository.saveAndFlush(appEntity);
  }

  public StudyEntity createStudyEntity(UserRegAdminEntity userEntity, AppEntity appEntity) {
    StudyEntity studyEntity = newStudyEntity();
    studyEntity.setType("CLOSE");
    studyEntity.setName("COVID Study");
    studyEntity.setCustomId("CovidStudy");
    studyEntity.setApp(appEntity);
    studyEntity.setLogoImageUrl(LOGO_IMAGE_URL);
    StudyPermissionEntity studyPermissionEntity = new StudyPermissionEntity();
    studyPermissionEntity.setUrAdminUser(userEntity);
    studyPermissionEntity.setEdit(Permission.EDIT);
    studyPermissionEntity.setApp(appEntity);
    studyEntity.addStudyPermissionEntity(studyPermissionEntity);
    return studyRepository.saveAndFlush(studyEntity);
  }

  public SiteEntity newSiteEntity() {
    SiteEntity siteEntity = new SiteEntity();
    siteEntity.setName("siteName");
    siteEntity.setStatus(ACTIVE_STATUS);
    return siteEntity;
  }

  public SiteEntity createSiteEntity(
      StudyEntity studyEntity, UserRegAdminEntity urAdminUser, AppEntity appEntity) {
    SiteEntity siteEntity = newSiteEntity();
    siteEntity.setStudy(studyEntity);
    SitePermissionEntity sitePermissionEntity = new SitePermissionEntity();
    sitePermissionEntity.setCanEdit(Permission.EDIT);
    sitePermissionEntity.setStudy(studyEntity);
    sitePermissionEntity.setUrAdminUser(urAdminUser);
    sitePermissionEntity.setApp(appEntity);
    siteEntity.addSitePermissionEntity(sitePermissionEntity);
    return siteRepository.saveAndFlush(siteEntity);
  }

  public ParticipantRegistrySiteEntity createParticipantRegistrySite(
      SiteEntity siteEntity, StudyEntity studyEntity) {
    ParticipantRegistrySiteEntity participantRegistrySiteEntity =
        new ParticipantRegistrySiteEntity();
    participantRegistrySiteEntity.setEnrollmentToken(RandomStringUtils.randomAlphanumeric(8));
    participantRegistrySiteEntity.setInvitationCount(2L);
    participantRegistrySiteEntity.setOnboardingStatus(OnboardingStatus.NEW.getCode());
    participantRegistrySiteEntity.setSite(siteEntity);
    participantRegistrySiteEntity.setStudy(studyEntity);
    return participantRegistrySiteRepository.saveAndFlush(participantRegistrySiteEntity);
  }

  public ParticipantStudyEntity createParticipantStudyEntity(
      SiteEntity siteEntity,
      StudyEntity studyEntity,
      ParticipantRegistrySiteEntity participantRegistrySiteEntity) {
    ParticipantStudyEntity participantStudyEntity = new ParticipantStudyEntity();
    participantStudyEntity.setSite(siteEntity);
    participantStudyEntity.setStudy(studyEntity);
    participantStudyEntity.setStatus(EnrollmentStatus.WITHDRAWN.getStatus());
    participantStudyEntity.setParticipantRegistrySite(participantRegistrySiteEntity);
    participantStudyEntity.setSharing(DataSharingStatus.PROVIDED.value());
    return participantStudyRepository.saveAndFlush(participantStudyEntity);
  }

  public UserDetailsEntity newUserDetails() {
    UserDetailsEntity userDetailsEntity = new UserDetailsEntity();
    userDetailsEntity.setEmail(EMAIL_VALUE);
    userDetailsEntity.setStatus(1);
    userDetailsEntity.setFirstName(ADMIN_FIRST_NAME);
    userDetailsEntity.setLastName(ADMIN_LAST_NAME);
    userDetailsEntity.setLocalNotificationFlag(false);
    userDetailsEntity.setRemoteNotificationFlag(false);
    userDetailsEntity.setTouchId(false);
    userDetailsEntity.setUsePassCode(false);
    return userDetailsEntity;
  }

  public UserDetailsEntity createUserDetails(AppEntity appEntity) {
    UserDetailsEntity userDetailsEntity = newUserDetails();
    userDetailsEntity.setApp(appEntity);
    return userDetailsRepository.saveAndFlush(userDetailsEntity);
  }

  public StudyConsentEntity createStudyConsentEntity(ParticipantStudyEntity participantStudy) {
    StudyConsentEntity studyConsent = new StudyConsentEntity();
    studyConsent.setPdfPath("documents/test-document.pdf");
    studyConsent.setPdfStorage(1);
    studyConsent.setVersion("1.0");
    studyConsent.setParticipantStudy(participantStudy);
    return studyConsentRepository.saveAndFlush(studyConsent);
  }

  public SiteEntity createSiteEntityForManageUsers(
      StudyEntity studyEntity, UserRegAdminEntity urAdminUser, AppEntity appEntity) {
    SiteEntity siteEntity = newSiteEntity();
    siteEntity.setStudy(studyEntity);
    LocationEntity location = createLocationEntity();
    siteEntity.setLocation(location);
    SitePermissionEntity sitePermissionEntity = new SitePermissionEntity();
    sitePermissionEntity.setCanEdit(Permission.EDIT);
    sitePermissionEntity.setStudy(studyEntity);
    sitePermissionEntity.setUrAdminUser(urAdminUser);
    sitePermissionEntity.setApp(appEntity);
    siteEntity.addSitePermissionEntity(sitePermissionEntity);
    return siteRepository.saveAndFlush(siteEntity);
  }

  public LocationEntity createLocationEntity() {
    LocationEntity locationEntity = newLocationEntity();
    return locationRepository.saveAndFlush(locationEntity);
  }

  public void createAppPermission(
      UserRegAdminEntity superAdmin, AppEntity appEntity, String adminId) {
    AppPermissionEntity appPermission = new AppPermissionEntity();
    appPermission.setApp(appEntity);
    appPermission.setCreatedBy(adminId);
    appPermission.setEdit(Permission.EDIT);
    appPermission.setUrAdminUser(superAdmin);
    appPermissionRepository.saveAndFlush(appPermission);
  }

  public void createStudyPermission(
      UserRegAdminEntity superAdmin,
      AppEntity appEntity,
      StudyEntity studyDetails,
      String adminId) {
    StudyPermissionEntity studyPermission = new StudyPermissionEntity();
    studyPermission.setApp(studyDetails.getApp());
    studyPermission.setStudy(studyDetails);
    studyPermission.setCreatedBy(adminId);
    studyPermission.setEdit(Permission.EDIT);
    studyPermission.setUrAdminUser(superAdmin);
    studyPermissionRepository.saveAndFlush(studyPermission);
  }

  public void createSitePermission(
      UserRegAdminEntity superAdmin,
      AppEntity appDetails,
      StudyEntity studyEntity,
      SiteEntity siteEntity,
      String adminId) {
    SitePermissionEntity sitePermission = new SitePermissionEntity();
    sitePermission.setApp(appDetails);
    sitePermission.setCreatedBy(adminId);
    sitePermission.setCanEdit(Permission.EDIT);
    sitePermission.setStudy(siteEntity.getStudy());
    sitePermission.setSite(siteEntity);
    sitePermission.setUrAdminUser(superAdmin);
    sitePermissionRepository.saveAndFlush(sitePermission);
  }

  public List<StudyEntity> createMultipleStudyEntity(AppEntity appEntity) {
    List<StudyEntity> studyList = new ArrayList<>();
    for (int i = 1; i <= 2; i++) {
      StudyEntity studyEntity = newStudyEntity();
      studyEntity.setType("CLOSE");
      studyEntity.setName("COVID Study" + i);
      studyEntity.setCustomId("CovidStudy" + i);
      studyEntity.setApp(appEntity);
      studyEntity.setLogoImageUrl(LOGO_IMAGE_URL);
      StudyEntity study = studyRepository.saveAndFlush(studyEntity);
      studyList.add(study);
    }
    return studyList;
  }

  public SiteEntity createMultipleSiteEntityWithPermission(
      StudyEntity studyEntity,
      UserRegAdminEntity urAdminUser,
      AppEntity appEntity,
      LocationEntity locationEntity) {
    SiteEntity siteEntity = newSiteEntity();
    siteEntity.setName(siteEntity.getName() + RandomStringUtils.random(2));
    siteEntity.setLocation(locationEntity);
    siteEntity.setStudy(studyEntity);
    SitePermissionEntity sitePermissionEntity = new SitePermissionEntity();
    sitePermissionEntity.setCanEdit(Permission.EDIT);
    sitePermissionEntity.setStudy(studyEntity);
    sitePermissionEntity.setUrAdminUser(urAdminUser);
    sitePermissionEntity.setApp(appEntity);
    siteEntity.addSitePermissionEntity(sitePermissionEntity);
    return siteRepository.saveAndFlush(siteEntity);
  }

  public List<SiteEntity> createMultipleSiteEntity(StudyEntity studyEntity) {
    List<SiteEntity> siteList = new ArrayList<>();
    for (int i = 1; i <= 2; i++) {
      LocationEntity locationEntity = newLocationEntity();
      locationEntity.setName(LOCATION_NAME_VALUE + String.valueOf(i));
      locationEntity.setCustomId(CUSTOM_ID_VALUE + String.valueOf(i));
      SiteEntity siteEntity = new SiteEntity();
      siteEntity.setLocation(locationEntity);
      siteEntity.setStudy(studyEntity);
      locationEntity = locationRepository.saveAndFlush(locationEntity);
      locationEntity.addSiteEntity(siteEntity);
      SiteEntity site = siteRepository.saveAndFlush(siteEntity);
      siteList.add(site);
    }
    return siteList;
  }

  public void cleanUp() {
    getAppPermissionRepository().deleteAll();
    getStudyPermissionRepository().deleteAll();
    getSitePermissionRepository().deleteAll();
    getStudyConsentRepository().deleteAll();
    getParticipantStudyRepository().deleteAll();
    getParticipantRegistrySiteRepository().deleteAll();
    getSiteRepository().deleteAll();
    getStudyRepository().deleteAll();
    getAppRepository().deleteAll();
    getUserRegAdminRepository().deleteAll();
    getLocationRepository().deleteAll();
    getUserDetailsRepository().deleteAll();
    getInvitedParticipantsEmailRepository().deleteAll();
    getAddNewAdminEmailServiceRepository().deleteAll();
  }
}
