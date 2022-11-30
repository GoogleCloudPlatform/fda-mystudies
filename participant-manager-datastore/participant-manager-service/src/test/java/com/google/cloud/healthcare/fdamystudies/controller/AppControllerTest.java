/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.ACTIVE_STATUS;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.OPEN_STUDY;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.USER_ID_HEADER;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.APP_PARTICIPANT_REGISTRY_VIEWED;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.common.EnrollmentStatus;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.IdGenerator;
import com.google.cloud.healthcare.fdamystudies.common.Permission;
import com.google.cloud.healthcare.fdamystudies.common.UserStatus;
import com.google.cloud.healthcare.fdamystudies.helper.TestDataHelper;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.LocationEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantEnrollmentHistoryEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.SitePermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import com.google.cloud.healthcare.fdamystudies.service.AppService;
import java.util.Map;
import org.apache.commons.collections4.map.HashedMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

public class AppControllerTest extends BaseMockIT {

  @Autowired private AppController controller;

  @Autowired private AppService appService;

  @Autowired private TestDataHelper testDataHelper;

  private ParticipantRegistrySiteEntity participantRegistrySiteEntity;
  private ParticipantStudyEntity participantStudyEntity;
  private UserRegAdminEntity userRegAdminEntity;
  private AppEntity appEntity;
  private StudyEntity studyEntity;
  private SiteEntity siteEntity;
  private UserDetailsEntity userDetailsEntity;
  private LocationEntity locationEntity;
  private ParticipantEnrollmentHistoryEntity participantEnrollmentHistoryEntity;

  public static final String EMAIL_VALUE = "mockit_email@grr.la";

  @BeforeEach
  public void setUp() {
    userRegAdminEntity = testDataHelper.createUserRegAdminEntity();
    appEntity = testDataHelper.createAppEntity(userRegAdminEntity);
    studyEntity = testDataHelper.createStudyEntity(userRegAdminEntity, appEntity);
    siteEntity = testDataHelper.createSiteEntity(studyEntity, userRegAdminEntity, appEntity);
    userDetailsEntity = testDataHelper.createUserDetails(appEntity);
    participantRegistrySiteEntity =
        testDataHelper.createParticipantRegistrySite(siteEntity, studyEntity);
    participantStudyEntity =
        testDataHelper.createParticipantStudyEntity(
            siteEntity, studyEntity, participantRegistrySiteEntity);
  }

  @Test
  public void contextLoads() {
    assertNotNull(controller);
    assertNotNull(mockMvc);
    assertNotNull(appService);
  }

  @Test
  public void shouldReturnAppsRegisteredByUserForSuperAdmin() throws Exception {
    participantRegistrySiteEntity.setOnboardingStatus("I");
    testDataHelper.getParticipantRegistrySiteRepository().save(participantRegistrySiteEntity);
    participantStudyEntity.setStatus(EnrollmentStatus.ENROLLED.getStatus());
    testDataHelper.getParticipantStudyRepository().save(participantStudyEntity);
    userDetailsEntity.setStatus(ACTIVE_STATUS);
    testDataHelper.getUserDetailsRepository().save(userDetailsEntity);
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(get(ApiEndpoint.GET_APPS.getPath()).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.apps").isArray())
        .andExpect(jsonPath("$.apps", hasSize(1)))
        .andExpect(jsonPath("$.apps[0].customId").value(appEntity.getAppId()))
        .andExpect(jsonPath("$.apps[0].name").value(appEntity.getAppName()))
        .andExpect(jsonPath("$.superAdmin").value(true))
        .andExpect(jsonPath("$.apps[0].invitedCount").value(1))
        .andExpect(jsonPath("$.apps[0].enrolledCount").value(1))
        .andExpect(jsonPath("$.apps[0].enrollmentPercentage").value(100))
        .andExpect(jsonPath("$.apps[0].appUsersCount").value(1))
        .andExpect(jsonPath("$.apps[0].studiesCount").value(1));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnAppsRegisteredByUser() throws Exception {
    participantRegistrySiteEntity.setOnboardingStatus("I");
    testDataHelper.getParticipantRegistrySiteRepository().save(participantRegistrySiteEntity);
    participantStudyEntity.setStatus(EnrollmentStatus.ENROLLED.getStatus());
    testDataHelper.getParticipantStudyRepository().save(participantStudyEntity);
    userDetailsEntity.setStatus(ACTIVE_STATUS);
    testDataHelper.getUserDetailsRepository().save(userDetailsEntity);
    userRegAdminEntity.setSuperAdmin(false);
    testDataHelper.getUserRegAdminRepository().save(userRegAdminEntity);

    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(get(ApiEndpoint.GET_APPS.getPath()).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.apps").isArray())
        .andExpect(jsonPath("$.apps", hasSize(1)))
        .andExpect(jsonPath("$.apps[0].customId").value(appEntity.getAppId()))
        .andExpect(jsonPath("$.apps[0].name").value(appEntity.getAppName()))
        .andExpect(jsonPath("$.studyPermissionCount").value(1))
        .andExpect(jsonPath("$.superAdmin").value(false))
        .andExpect(jsonPath("$.apps[0].appUsersCount").value(1))
        .andExpect(jsonPath("$.apps[0].invitedCount").value(1))
        .andExpect(jsonPath("$.apps[0].enrolledCount").value(1))
        .andExpect(jsonPath("$.apps[0].enrollmentPercentage").value(100));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnReturnAppsWithEnrolledCount() throws Exception {
    participantRegistrySiteEntity.setOnboardingStatus("I");
    testDataHelper.getParticipantRegistrySiteRepository().save(participantRegistrySiteEntity);
    participantStudyEntity.setStatus(EnrollmentStatus.ENROLLED.getStatus());
    testDataHelper.getParticipantStudyRepository().save(participantStudyEntity);
    userDetailsEntity.setStatus(ACTIVE_STATUS);
    testDataHelper.getUserDetailsRepository().save(userDetailsEntity);
    studyEntity.setType(OPEN_STUDY);
    testDataHelper.getStudyRepository().save(studyEntity);
    siteEntity.setTargetEnrollment(0);
    testDataHelper.getSiteRepository().save(siteEntity);
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(get(ApiEndpoint.GET_APPS.getPath()).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.apps").isArray())
        .andExpect(jsonPath("$.apps", hasSize(1)))
        .andExpect(jsonPath("$.apps[0].invitedCount").value(0))
        .andExpect(jsonPath("$.apps[0].enrolledCount").value(1))
        .andExpect(jsonPath("$.apps[0].enrollmentPercentage").isEmpty());

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnAppsWithPagination() throws Exception {
    userRegAdminEntity.setSuperAdmin(false);
    testDataHelper.getUserRegAdminRepository().save(userRegAdminEntity);

    for (int i = 1; i <= 20; i++) {
      appEntity = testDataHelper.newAppEntity();
      appEntity.setAppId("AppCustomId" + String.valueOf(i));
      testDataHelper.getAppRepository().saveAndFlush(appEntity);
      studyEntity = testDataHelper.newStudyEntity();
      testDataHelper.getStudyRepository().saveAndFlush(studyEntity);
      SitePermissionEntity sitePermissionEntity = new SitePermissionEntity();
      sitePermissionEntity.setUrAdminUser(userRegAdminEntity);
      sitePermissionEntity.setCanEdit(Permission.EDIT);
      sitePermissionEntity.setApp(appEntity);
      sitePermissionEntity.setStudy(studyEntity);
      sitePermissionEntity.setSite(siteEntity);
      testDataHelper.getSitePermissionRepository().saveAndFlush(sitePermissionEntity);
      // Pagination records should be in descending order of created timestamp
      // Entities are not saved in sequential order so adding delay
      Thread.sleep(500);
    }

    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            get(ApiEndpoint.GET_APPS.getPath())
                .param("limit", "5")
                .param("offset", "0")
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.apps").isArray())
        .andExpect(jsonPath("$.apps", hasSize(5)))
        .andExpect(jsonPath("$.apps[0].customId").value("AppCustomId20"))
        .andExpect(jsonPath("$.apps[4].customId").value("AppCustomId16"));

    verifyTokenIntrospectRequest(1);

    // test case for searchTerm
    mockMvc
        .perform(
            get(ApiEndpoint.GET_APPS.getPath())
                .param("limit", "5")
                .param("offset", "1")
                .param("searchTerm", "AppCustomId")
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.apps").isArray())
        .andExpect(jsonPath("$.apps", hasSize(5)))
        .andExpect(jsonPath("$.apps[0].customId").value("AppCustomId19"));

    verifyTokenIntrospectRequest(2);
  }

  @Test
  public void shouldReturnAppsWithPaginationForSuperAdmin() throws Exception {
    userRegAdminEntity.setSuperAdmin(true);
    testDataHelper.getUserRegAdminRepository().save(userRegAdminEntity);

    for (int i = 1; i <= 20; i++) {
      appEntity = testDataHelper.newAppEntity();
      appEntity.setAppId("AppCustomId" + String.valueOf(i));
      testDataHelper.getAppRepository().saveAndFlush(appEntity);
      studyEntity = testDataHelper.newStudyEntity();
      testDataHelper.getStudyRepository().saveAndFlush(studyEntity);
      siteEntity = testDataHelper.newSiteEntity();
      siteEntity.setStudy(studyEntity);
      testDataHelper.getSiteRepository().saveAndFlush(siteEntity);
      // Pagination records should be in descending order of created timestamp
      // Entities are not saved in sequential order so adding delay
      Thread.sleep(500);
    }

    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            get(ApiEndpoint.GET_APPS.getPath())
                .param("limit", "20")
                .param("offset", "0")
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.apps").isArray())
        .andExpect(jsonPath("$.apps", hasSize(20)))
        .andExpect(jsonPath("$.apps[0].customId").value("AppCustomId20"))
        .andExpect(jsonPath("$.apps[19].customId").value("AppCustomId1"));

    verifyTokenIntrospectRequest(1);

    // offset with non-zero value
    mockMvc
        .perform(
            get(ApiEndpoint.GET_APPS.getPath())
                .param("limit", "10")
                .param("offset", "5")
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.apps").isArray())
        .andExpect(jsonPath("$.apps", hasSize(10)))
        .andExpect(jsonPath("$.apps[0].customId").value("AppCustomId15"))
        .andExpect(jsonPath("$.apps[9].customId").value("AppCustomId6"));

    verifyTokenIntrospectRequest(2);

    // test case for searchTerm
    mockMvc
        .perform(
            get(ApiEndpoint.GET_APPS.getPath())
                .param("limit", "10")
                .param("offset", "1")
                .param("searchTerm", "AppCustomId")
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.apps").isArray())
        .andExpect(jsonPath("$.apps", hasSize(10)))
        .andExpect(jsonPath("$.apps[0].customId").value("AppCustomId19"));

    verifyTokenIntrospectRequest(3);
  }

  @Test
  public void shouldReturnBadRequestForGetApps() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();

    mockMvc
        .perform(get(ApiEndpoint.GET_APPS.getPath()).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.violations").isArray())
        .andExpect(jsonPath("$.violations[0].path").value("userId"))
        .andExpect(jsonPath("$.violations[0].message").value("header is required"));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldNotReturnApp() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    userRegAdminEntity.setSuperAdmin(false);
    testDataHelper.getUserRegAdminRepository().save(userRegAdminEntity);
    testDataHelper.getSitePermissionRepository().deleteAll();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(get(ApiEndpoint.GET_APPS.getPath()).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.apps").isEmpty());

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnUserNotFoundForApps() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, IdGenerator.id());

    mockMvc
        .perform(get(ApiEndpoint.GET_APPS.getPath()).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath("$.error_description").value(ErrorCode.USER_NOT_FOUND.getDescription()));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnAppsWithOptionalStudiesAndSites() throws Exception {
    // Step 1: set app,study and location
    studyEntity.setApp(appEntity);
    siteEntity.setStudy(studyEntity);
    locationEntity = testDataHelper.createLocation();
    siteEntity.setLocation(locationEntity);
    testDataHelper.getSiteRepository().save(siteEntity);

    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());
    String[] fields = {"studies", "sites"};

    // Step 2: Call API and expect success message
    mockMvc
        .perform(
            get(ApiEndpoint.GET_APPS.getPath())
                .headers(headers)
                .contextPath(getContextPath())
                .queryParam("fields", fields))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.apps").isArray())
        .andExpect(jsonPath("$.apps", hasSize(1)))
        .andExpect(jsonPath("$.apps[0].studies").isArray())
        .andExpect(jsonPath("$.apps[0].studies[0].sites").isArray())
        .andExpect(jsonPath("$.apps[0].customId").value(appEntity.getAppId()))
        .andExpect(jsonPath("$.apps[0].name").value(appEntity.getAppName()))
        .andExpect(jsonPath("$.apps[0].totalSitesCount").value(1))
        .andExpect(jsonPath("$.apps[0].studies[0].totalSitesCount").value(1));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnSortedStudiesAndSites() throws Exception {
    // set study and site
    studyEntity.setApp(appEntity);
    siteEntity.setStudy(studyEntity);
    locationEntity = testDataHelper.createLocation();
    siteEntity.setLocation(locationEntity);
    testDataHelper.getSiteRepository().save(siteEntity);

    // set few more study and sites
    studyEntity = testDataHelper.newStudyEntity();
    studyEntity.setName("Study");
    studyEntity.setApp(appEntity);
    testDataHelper.getStudyRepository().saveAndFlush(studyEntity);
    testDataHelper.createMultipleSiteEntity(studyEntity);

    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());
    String[] fields = {"studies", "sites"};

    // Step 2: Call API and expect success message
    mockMvc
        .perform(
            get(ApiEndpoint.GET_APPS.getPath())
                .headers(headers)
                .contextPath(getContextPath())
                .queryParam("fields", fields))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.apps").isArray())
        .andExpect(jsonPath("$.apps", hasSize(1)))
        .andExpect(jsonPath("$.apps[0].studies").isArray())
        .andExpect(jsonPath("$.apps[0].studies[0].sites").isArray())
        .andExpect(jsonPath("$.apps[0].studies[0].studyName").value("COVID Study"))
        .andExpect(jsonPath("$.apps[0].studies[1].studyName").value("Study"))
        .andExpect(jsonPath("$.apps[0].studies[1].sites[0].locationName").value("Marlborough1"))
        .andExpect(jsonPath("$.apps[0].studies[1].sites[1].locationName").value("Marlborough2"));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnAppsWithOptionalStudies() throws Exception {
    // Step 1: set app and study
    studyEntity.setApp(appEntity);
    siteEntity.setStudy(studyEntity);
    locationEntity = testDataHelper.createLocation();
    siteEntity.setLocation(locationEntity);
    testDataHelper.getSiteRepository().save(siteEntity);

    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());
    String[] fields = {"studies"};

    // Step 2: Call API and expect success message
    mockMvc
        .perform(
            get(ApiEndpoint.GET_APPS.getPath())
                .headers(headers)
                .contextPath(getContextPath())
                .queryParam("fields", fields))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.apps").isArray())
        .andExpect(jsonPath("$.apps", hasSize(1)))
        .andExpect(jsonPath("$.apps[0].studies").isArray())
        .andExpect(jsonPath("$.apps[0].studies[0].sites").isEmpty())
        .andExpect(jsonPath("$.apps[0].studies[0].studyName").value(studyEntity.getName()))
        .andExpect(jsonPath("$.apps[0].customId").value(appEntity.getAppId()))
        .andExpect(jsonPath("$.apps[0].name").value(appEntity.getAppName()));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnForbiddenForGetAppDetailsAccessDenied() throws Exception {
    // Step 1 : set SuperAdmin to false
    userRegAdminEntity.setSuperAdmin(false);
    testDataHelper.getUserRegAdminRepository().save(userRegAdminEntity);

    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());
    String[] fields = {"studies", "sites"};

    // Step 2: Call API and expect USER_ADMIN_ACCESS_DENIED
    mockMvc
        .perform(
            get(ApiEndpoint.GET_APPS.getPath())
                .headers(headers)
                .contextPath(getContextPath())
                .queryParam("fields", fields))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(
            jsonPath("$.error_description", is(ErrorCode.NOT_SUPER_ADMIN_ACCESS.getDescription())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnGetAppParticipantsForSuperAdmin() throws Exception {
    // Step 1 : Set studyEntity,siteEntity,locationEntity,userDetailsEntity
    studyEntity.setApp(appEntity);
    siteEntity.setStudy(studyEntity);
    locationEntity = testDataHelper.createLocation();
    siteEntity.setLocation(locationEntity);
    participantEnrollmentHistoryEntity =
        testDataHelper.createEnrollmentHistory(appEntity, studyEntity, siteEntity);
    participantEnrollmentHistoryEntity.setUserDetails(userDetailsEntity);
    participantEnrollmentHistoryEntity.setStatus(EnrollmentStatus.ENROLLED.getStatus());
    testDataHelper
        .getParticipantEnrollmentHistoryRepository()
        .saveAndFlush(participantEnrollmentHistoryEntity);

    // Step 2: Call API to return GET_APPS_PARTICIPANTS
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            get(ApiEndpoint.GET_APP_PARTICIPANTS.getPath(), appEntity.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.participants").isArray())
        .andExpect(jsonPath("$.participants", hasSize(1)))
        .andExpect(jsonPath("$.participants[0].enrolledStudies").isArray())
        .andExpect(jsonPath("$.participants[0].enrolledStudies", hasSize(1)))
        .andExpect(jsonPath("$.participants[0].email").value(userDetailsEntity.getEmail()))
        .andExpect(
            jsonPath("$.participants[0].enrolledStudies[0].studyName").value(studyEntity.getName()))
        .andExpect(jsonPath("$.participants[0].enrolledStudies[0].sites", hasSize(1)))
        .andExpect(
            jsonPath("$.participants[0].enrolledStudies[0].sites[0].locationName")
                .value(locationEntity.getName()))
        .andExpect(jsonPath("$.customId").value(appEntity.getAppId()))
        .andExpect(jsonPath("$.name").value(appEntity.getAppName()));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userRegAdminEntity.getId());
    auditRequest.setAppId(appEntity.getAppId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(APP_PARTICIPANT_REGISTRY_VIEWED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, APP_PARTICIPANT_REGISTRY_VIEWED);
    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnGetAppParticipants() throws Exception {
    // Step 1 : Set studyEntity,siteEntity,locationEntity,userDetailsEntity and superAdmin to false
    userRegAdminEntity.setSuperAdmin(false);
    testDataHelper.getUserRegAdminRepository().save(userRegAdminEntity);
    studyEntity.setApp(appEntity);
    siteEntity.setStudy(studyEntity);
    locationEntity = testDataHelper.createLocation();
    siteEntity.setLocation(locationEntity);
    participantEnrollmentHistoryEntity =
        testDataHelper.createEnrollmentHistory(appEntity, studyEntity, siteEntity);
    participantEnrollmentHistoryEntity.setUserDetails(userDetailsEntity);
    participantEnrollmentHistoryEntity.setStatus(EnrollmentStatus.ENROLLED.getStatus());
    testDataHelper
        .getParticipantEnrollmentHistoryRepository()
        .saveAndFlush(participantEnrollmentHistoryEntity);

    // Step 2: Call API to return GET_APPS_PARTICIPANTS
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            get(ApiEndpoint.GET_APP_PARTICIPANTS.getPath(), appEntity.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.participants").isArray())
        .andExpect(jsonPath("$.participants", hasSize(1)))
        .andExpect(jsonPath("$.participants[0].enrolledStudies").isArray())
        .andExpect(jsonPath("$.participants[0].enrolledStudies", hasSize(1)))
        .andExpect(jsonPath("$.participants[0].email").value(userDetailsEntity.getEmail()))
        .andExpect(
            jsonPath("$.participants[0].enrolledStudies[0].studyName").value(studyEntity.getName()))
        .andExpect(jsonPath("$.customId").value(appEntity.getAppId()))
        .andExpect(jsonPath("$.name").value(appEntity.getAppName()))
        .andExpect(jsonPath("$.participants[0].enrolledStudies").isNotEmpty())
        .andExpect(jsonPath("$.participants[0].enrolledStudies[0].sites", hasSize(1)))
        .andExpect(
            jsonPath("$.participants[0].enrolledStudies[0].sites[0].locationName")
                .value(locationEntity.getName()));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userRegAdminEntity.getId());
    auditRequest.setAppId(appEntity.getAppId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(APP_PARTICIPANT_REGISTRY_VIEWED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, APP_PARTICIPANT_REGISTRY_VIEWED);
    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnGetAppParticipantsWithoutParticipants() throws Exception {
    userRegAdminEntity.setSuperAdmin(false);
    testDataHelper.getUserRegAdminRepository().save(userRegAdminEntity);
    userDetailsEntity.setApp(null);
    testDataHelper.getUserDetailsRepository().save(userDetailsEntity);
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            get(ApiEndpoint.GET_APP_PARTICIPANTS.getPath(), appEntity.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.customId").value(appEntity.getAppId()))
        .andExpect(jsonPath("$.name").value(appEntity.getAppName()))
        .andExpect(jsonPath("$.participants").isEmpty());

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userRegAdminEntity.getId());
    auditRequest.setAppId(appEntity.getAppId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(APP_PARTICIPANT_REGISTRY_VIEWED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, APP_PARTICIPANT_REGISTRY_VIEWED);
    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnAppParticipantsWithPagination() throws Exception {
    // Step 1 : Set studyEntity,siteEntity,locationEntity,userDetailsEntity and superAdmin to false
    userRegAdminEntity.setSuperAdmin(false);
    testDataHelper.getUserRegAdminRepository().save(userRegAdminEntity);
    studyEntity.setApp(appEntity);
    siteEntity.setStudy(studyEntity);
    locationEntity = testDataHelper.createLocation();
    siteEntity.setLocation(locationEntity);
    participantStudyEntity.setUserDetails(userDetailsEntity);
    testDataHelper.getParticipantStudyRepository().saveAndFlush(participantStudyEntity);
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    // Step 1: 1 Participants for app already added in @BeforeEach, add 20 new Participants for
    // app
    for (int i = 1; i <= 20; i++) {
      userDetailsEntity = testDataHelper.newUserDetails();
      userDetailsEntity.setApp(appEntity);
      userDetailsEntity.setEmail(EMAIL_VALUE + String.valueOf(i));
      testDataHelper.getUserDetailsRepository().saveAndFlush(userDetailsEntity);
      // Pagination records should be in descending order of created timestamp
      // Entities are not saved in sequential order so adding delay
      Thread.sleep(500);
    }

    // Step 2: Call API and expect GET_PARTICIPANT_REGISTRY_SUCCESS message and fetch 11 data
    // out of 21
    mockMvc
        .perform(
            get(ApiEndpoint.GET_APP_PARTICIPANTS.getPath(), appEntity.getId())
                .headers(headers)
                .param("limit", "20")
                .param("offset", "10")
                .param("sortBy", "email")
                .param("sortDirection", "desc")
                .param("searchTerm", EMAIL_VALUE)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.appId").value(appEntity.getId()))
        .andExpect(jsonPath("$.participants").isArray())
        .andExpect(jsonPath("$.participants", hasSize(11)))
        .andExpect(jsonPath("$.participants[0].email").value(EMAIL_VALUE + String.valueOf(18)))
        .andExpect(jsonPath("$.participants[1].email").value(EMAIL_VALUE + String.valueOf(17)))
        .andExpect(jsonPath("$.participants[2].email").value(EMAIL_VALUE + String.valueOf(16)))
        .andExpect(jsonPath("$.totalParticipantCount").value(21));

    verifyTokenIntrospectRequest(1);

    // get  app participants for the default values of limit and offset
    mockMvc
        .perform(
            get(ApiEndpoint.GET_APP_PARTICIPANTS.getPath(), appEntity.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.participants").isArray())
        .andExpect(jsonPath("$.participants", hasSize(10)))
        .andExpect(jsonPath("$.participants[0].email").value(EMAIL_VALUE));

    verifyTokenIntrospectRequest(2);
  }

  @Test
  public void shouldReturnInvalidSortByValue() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());
    mockMvc
        .perform(
            get(ApiEndpoint.GET_APP_PARTICIPANTS.getPath(), IdGenerator.id())
                .headers(headers)
                .param("sortBy", "abc")
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.error_description")
                .value(ErrorCode.UNSUPPORTED_SORTBY_VALUE.getDescription()));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnInvalidSortDirectionValue() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());
    mockMvc
        .perform(
            get(ApiEndpoint.GET_APP_PARTICIPANTS.getPath(), IdGenerator.id())
                .headers(headers)
                .param("sortDirection", "abcd")
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.error_description")
                .value(ErrorCode.UNSUPPORTED_SORT_DIRECTION_VALUE.getDescription()));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldNotReturnAppParticipantsWithDeactivatedStatus() throws Exception {
    // Step 1 : Set studyEntity,siteEntity,locationEntity,userDetailsEntity
    studyEntity.setApp(appEntity);
    // set status to Deactivated
    userDetailsEntity.setStatus(UserStatus.DEACTIVATED.getValue());
    testDataHelper.getUserDetailsRepository().saveAndFlush(userDetailsEntity);
    participantStudyEntity.setUserDetails(userDetailsEntity);

    // Step 2: Call API to return GET_APPS_PARTICIPANTS
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            get(ApiEndpoint.GET_APP_PARTICIPANTS.getPath(), appEntity.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.participants").isArray())
        .andExpect(jsonPath("$.participants").isEmpty());

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldNotReturnAppsForGetAppParticipants() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    testDataHelper.getAppRepository().deleteAll();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            get(ApiEndpoint.GET_APP_PARTICIPANTS.getPath(), appEntity.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error_description").value(ErrorCode.APP_NOT_FOUND.getDescription()));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnAppPermissionAccessDenied() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    userRegAdminEntity.setSuperAdmin(false);
    testDataHelper.getUserRegAdminRepository().save(userRegAdminEntity);
    testDataHelper.getAppRepository().deleteAll();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            get(ApiEndpoint.GET_APP_PARTICIPANTS.getPath(), appEntity.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(
            jsonPath("$.error_description")
                .value(ErrorCode.APP_PERMISSION_ACCESS_DENIED.getDescription()));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnBadRequestForGetAppParticipants() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();

    mockMvc
        .perform(
            get(ApiEndpoint.GET_APP_PARTICIPANTS.getPath(), appEntity.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.violations").isArray())
        .andExpect(jsonPath("$.violations[0].path").value("userId"))
        .andExpect(jsonPath("$.violations[0].message").value("header is required"));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnUserNotFoundForGetAppParticipants() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, IdGenerator.id());

    mockMvc
        .perform(
            get(ApiEndpoint.GET_APP_PARTICIPANTS.getPath(), appEntity.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath("$.error_description").value(ErrorCode.USER_NOT_FOUND.getDescription()));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldNotReturnNotEligibleStatusforGetAppParticipants() throws Exception {
    // Step 1 : Set studyEntity,siteEntity,locationEntity,userDetailsEntity and superAdmin to false
    userRegAdminEntity.setSuperAdmin(false);
    testDataHelper.getUserRegAdminRepository().save(userRegAdminEntity);
    studyEntity.setApp(appEntity);
    siteEntity.setStudy(studyEntity);
    locationEntity = testDataHelper.createLocation();
    siteEntity.setLocation(locationEntity);
    participantStudyEntity.setUserDetails(userDetailsEntity);
    // set status to notEligible
    participantStudyEntity.setStatus(EnrollmentStatus.NOT_ELIGIBLE.getStatus());
    testDataHelper.getParticipantStudyRepository().saveAndFlush(participantStudyEntity);

    // Step 2: Call API to return GET_APPS_PARTICIPANTS
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());
    mockMvc
        .perform(
            get(ApiEndpoint.GET_APP_PARTICIPANTS.getPath(), appEntity.getId())
                .headers(headers)
                .queryParam(
                    "excludeParticipantStudyStatus", EnrollmentStatus.NOT_ELIGIBLE.getStatus())
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.participants").isArray())
        .andExpect(jsonPath("$.participants", hasSize(1)))
        .andExpect(jsonPath("$.participants[0].enrolledStudies").isArray())
        .andExpect(jsonPath("$.participants[0].enrolledStudies", hasSize(0)));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnInvalidAppsFieldsValues() throws Exception {
    // Step 1: set app and study
    studyEntity.setApp(appEntity);
    testDataHelper.getStudyRepository().save(studyEntity);

    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());
    String[] fields = {"apps"};

    // Step 2: Call API
    mockMvc
        .perform(
            get(ApiEndpoint.GET_APPS.getPath())
                .headers(headers)
                .contextPath(getContextPath())
                .queryParam("fields", fields))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.error_description")
                .value(ErrorCode.INVALID_APPS_FIELDS_VALUES.getDescription()));

    verifyTokenIntrospectRequest();
  }

  @AfterEach
  public void clean() {
    testDataHelper.cleanUp();
  }
}
