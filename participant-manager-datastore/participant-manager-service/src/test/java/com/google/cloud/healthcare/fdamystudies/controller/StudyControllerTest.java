/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.CLOSE_STUDY;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.ENROLLED_STATUS;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.OPEN;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.OPEN_STUDY;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.USER_ID_HEADER;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.YET_TO_ENROLL;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.asJsonString;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.ENROLLMENT_TARGET_UPDATED;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.STUDY_PARTICIPANT_REGISTRY_VIEWED;
import static com.google.cloud.healthcare.fdamystudies.common.TestConstants.CUSTOM_ID_VALUE;
import static com.google.cloud.healthcare.fdamystudies.common.TestConstants.LOCATION_NAME_VALUE;
import static com.google.cloud.healthcare.fdamystudies.common.TestConstants.NO_OF_RECORDS;
import static com.google.cloud.healthcare.fdamystudies.common.TestConstants.PAGE_NO;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateTargetEnrollmentRequest;
import com.google.cloud.healthcare.fdamystudies.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.common.CommonConstants;
import com.google.cloud.healthcare.fdamystudies.common.EnrollmentStatus;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.IdGenerator;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.common.OnboardingStatus;
import com.google.cloud.healthcare.fdamystudies.common.Permission;
import com.google.cloud.healthcare.fdamystudies.common.SiteStatus;
import com.google.cloud.healthcare.fdamystudies.common.TestConstants;
import com.google.cloud.healthcare.fdamystudies.helper.TestDataHelper;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.LocationEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyPermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import com.google.cloud.healthcare.fdamystudies.repository.SiteRepository;
import com.google.cloud.healthcare.fdamystudies.service.StudyService;
import com.jayway.jsonpath.JsonPath;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.collections4.map.HashedMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;

public class StudyControllerTest extends BaseMockIT {

  @Autowired private StudyService studyService;

  @Autowired private StudyController controller;

  @Autowired private TestDataHelper testDataHelper;

  @Autowired private SiteRepository siteRepository;

  private UserRegAdminEntity userRegAdminEntity;

  private SiteEntity siteEntity;

  private StudyEntity studyEntity;

  protected MvcResult result;

  private ParticipantRegistrySiteEntity participantRegistrySiteEntity;

  private ParticipantStudyEntity participantStudyEntity;

  private AppEntity appEntity;

  private LocationEntity locationEntity;

  @BeforeEach
  public void setUp() {
    userRegAdminEntity = testDataHelper.createUserRegAdminEntity();
    appEntity = testDataHelper.createAppEntity(userRegAdminEntity);
    studyEntity = testDataHelper.createStudyEntity(userRegAdminEntity, appEntity);
    siteEntity = testDataHelper.createSiteEntity(studyEntity, userRegAdminEntity, appEntity);
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
    assertNotNull(studyService);
  }

  @Test
  public void shouldReturnStudiesForSuperAdmin() throws Exception {
    participantRegistrySiteEntity.setOnboardingStatus("I");
    testDataHelper.getParticipantRegistrySiteRepository().save(participantRegistrySiteEntity);
    participantStudyEntity.setStatus("inProgress");
    testDataHelper.getParticipantStudyRepository().save(participantStudyEntity);
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            get(ApiEndpoint.GET_STUDIES.getPath()).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.studies").isArray())
        .andExpect(jsonPath("$.studies", hasSize(1)))
        .andExpect(jsonPath("$.studies[0].id").isNotEmpty())
        .andExpect(jsonPath("$.studies[0].type").value(studyEntity.getType()))
        .andExpect(jsonPath("$.superAdmin").value(true))
        .andExpect(jsonPath("$.studies[0].logoImageUrl").value(studyEntity.getLogoImageUrl()))
        .andExpect(jsonPath("$.studies[0].invited").value(1))
        .andExpect(jsonPath("$.studies[0].enrolled").value(1))
        .andExpect(jsonPath("$.studies[0].enrollmentPercentage").value(100));

    verifyTokenIntrospectRequest(1);

    /* Set study in the site to null and expect no study if there is no site associated with that study*/
    siteEntity.setStudy(null);
    testDataHelper.getSiteRepository().saveAndFlush(siteEntity);

    mockMvc
        .perform(
            get(ApiEndpoint.GET_STUDIES.getPath()).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.studies").isArray())
        .andExpect(jsonPath("$.studies", hasSize(0)));

    verifyTokenIntrospectRequest(2);
  }

  @Test
  public void shouldReturnStudies() throws Exception {
    participantRegistrySiteEntity.setOnboardingStatus("I");
    testDataHelper.getParticipantRegistrySiteRepository().save(participantRegistrySiteEntity);
    participantStudyEntity.setStatus("inProgress");
    testDataHelper.getParticipantStudyRepository().save(participantStudyEntity);
    userRegAdminEntity.setSuperAdmin(false);
    testDataHelper.getUserRegAdminRepository().save(userRegAdminEntity);

    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            get(ApiEndpoint.GET_STUDIES.getPath()).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.studies").isArray())
        .andExpect(jsonPath("$.studies", hasSize(1)))
        .andExpect(jsonPath("$.studies[0].id").isNotEmpty())
        .andExpect(jsonPath("$.studies[0].type").value(studyEntity.getType()))
        .andExpect(jsonPath("$.sitePermissionCount").value(1))
        .andExpect(jsonPath("$.superAdmin").value(false))
        .andExpect(jsonPath("$.studies[0].logoImageUrl").value(studyEntity.getLogoImageUrl()))
        .andExpect(jsonPath("$.studies[0].invited").value(1))
        .andExpect(jsonPath("$.studies[0].enrolled").value(1))
        .andExpect(jsonPath("$.studies[0].enrollmentPercentage").value(100));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnBadRequestForGetStudies() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();

    mockMvc
        .perform(
            get(ApiEndpoint.GET_STUDIES.getPath()).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.violations").isArray())
        .andExpect(jsonPath("$.violations[0].path").value("userId"))
        .andExpect(jsonPath("$.violations[0].message").value("header is required"));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnStudyNotFound() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    userRegAdminEntity.setSuperAdmin(false);
    testDataHelper.getUserRegAdminRepository().save(userRegAdminEntity);
    testDataHelper.getAppRepository().deleteAll();
    testDataHelper.getStudyRepository().deleteAll();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            get(ApiEndpoint.GET_STUDIES.getPath()).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath("$.error_description").value(ErrorCode.STUDY_NOT_FOUND.getDescription()));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnUserNotFoundForGetStudies() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, IdGenerator.id());

    mockMvc
        .perform(
            get(ApiEndpoint.GET_STUDIES.getPath()).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath("$.error_description").value(ErrorCode.USER_NOT_FOUND.getDescription()));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnStudyNotFoundForStudyParticipants() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());
    mockMvc
        .perform(
            get(ApiEndpoint.GET_STUDY_PARTICIPANT.getPath(), IdGenerator.id())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath("$.error_description").value(ErrorCode.STUDY_NOT_FOUND.getDescription()));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnStudyParticipantsForDisabled() throws Exception {

    locationEntity = testDataHelper.createLocation();
    studyEntity.setType(OPEN_STUDY);
    siteEntity.setLocation(locationEntity);
    siteEntity.setTargetEnrollment(0);
    siteEntity.setStudy(studyEntity);
    testDataHelper.getSiteRepository().saveAndFlush(siteEntity);
    participantRegistrySiteEntity.setEmail(TestConstants.EMAIL_VALUE);
    participantRegistrySiteEntity.setOnboardingStatus(OnboardingStatus.DISABLED.getCode());
    testDataHelper.getParticipantStudyRepository().deleteAll();
    testDataHelper
        .getParticipantRegistrySiteRepository()
        .saveAndFlush(participantRegistrySiteEntity);

    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            get(ApiEndpoint.GET_STUDY_PARTICIPANT.getPath(), studyEntity.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.participantRegistryDetail.registryParticipants").isArray())
        .andExpect(
            jsonPath(
                "$.participantRegistryDetail.registryParticipants[0].enrollmentStatus",
                is(CommonConstants.YET_TO_ENROLL)));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnAppNotFoundForStudyParticipants() throws Exception {
    userRegAdminEntity.setSuperAdmin(false);
    testDataHelper.getUserRegAdminRepository().save(userRegAdminEntity);

    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    StudyPermissionEntity studyPermission = studyEntity.getStudyPermissions().get(0);
    studyPermission.setApp(null);
    studyEntity = testDataHelper.getStudyRepository().saveAndFlush(studyEntity);
    mockMvc
        .perform(
            get(ApiEndpoint.GET_STUDY_PARTICIPANT.getPath(), studyEntity.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error_description").value(ErrorCode.APP_NOT_FOUND.getDescription()));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnAccessDeniedForStudyParticipants() throws Exception {
    userRegAdminEntity.setSuperAdmin(false);
    testDataHelper.getUserRegAdminRepository().save(userRegAdminEntity);

    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    StudyEntity study = testDataHelper.newStudyEntity();
    testDataHelper.getStudyRepository().saveAndFlush(study);
    mockMvc
        .perform(
            get(ApiEndpoint.GET_STUDY_PARTICIPANT.getPath(), study.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(
            jsonPath("$.error_description")
                .value(ErrorCode.STUDY_PERMISSION_ACCESS_DENIED.getDescription()));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnStudyParticipantsForSuperAdmin() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    locationEntity = testDataHelper.createLocation();
    studyEntity.setType(OPEN_STUDY);
    siteEntity.setLocation(locationEntity);
    siteEntity.setTargetEnrollment(0);
    siteEntity.setStudy(studyEntity);
    participantRegistrySiteEntity.setEmail(TestConstants.EMAIL_VALUE);
    participantRegistrySiteEntity.setOnboardingStatus(OnboardingStatus.ENROLLED.getCode());
    participantStudyEntity.setStudy(studyEntity);
    participantStudyEntity.setStatus(EnrollmentStatus.ENROLLED.getStatus());
    participantStudyEntity.setParticipantRegistrySite(participantRegistrySiteEntity);
    testDataHelper.getParticipantStudyRepository().saveAndFlush(participantStudyEntity);

    mockMvc
        .perform(
            get(ApiEndpoint.GET_STUDY_PARTICIPANT.getPath(), studyEntity.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.participantRegistryDetail.studyId").value(studyEntity.getId()))
        .andExpect(jsonPath("$.participantRegistryDetail.registryParticipants").isArray())
        .andExpect(jsonPath("$.participantRegistryDetail.registryParticipants", hasSize(1)))
        .andExpect(
            jsonPath("$.participantRegistryDetail.registryParticipants[0].siteId")
                .value(siteEntity.getId()))
        .andExpect(
            jsonPath("$.participantRegistryDetail.registryParticipants[0].locationName")
                .value(locationEntity.getName()))
        .andExpect(
            jsonPath("$.participantRegistryDetail.registryParticipants[0].enrollmentStatus")
                .value(participantStudyEntity.getStatus()))
        .andExpect(
            jsonPath("$.participantRegistryDetail.targetEnrollment")
                .value(siteEntity.getTargetEnrollment()));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userRegAdminEntity.getId());
    auditRequest.setStudyId(studyEntity.getId());
    auditRequest.setAppId(appEntity.getId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(STUDY_PARTICIPANT_REGISTRY_VIEWED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, STUDY_PARTICIPANT_REGISTRY_VIEWED);
    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnStudyParticipants() throws Exception {
    userRegAdminEntity.setSuperAdmin(false);
    testDataHelper.getUserRegAdminRepository().save(userRegAdminEntity);
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    locationEntity = testDataHelper.createLocation();
    studyEntity.setType(OPEN_STUDY);
    siteEntity.setLocation(locationEntity);
    siteEntity.setTargetEnrollment(0);
    siteEntity.setStudy(studyEntity);
    participantRegistrySiteEntity.setEmail(TestConstants.EMAIL_VALUE);
    participantRegistrySiteEntity.setOnboardingStatus(OnboardingStatus.ENROLLED.getCode());
    participantStudyEntity.setStudy(studyEntity);
    participantStudyEntity.setStatus(EnrollmentStatus.IN_PROGRESS.getStatus());
    participantStudyEntity.setParticipantRegistrySite(participantRegistrySiteEntity);
    testDataHelper.getParticipantStudyRepository().saveAndFlush(participantStudyEntity);

    mockMvc
        .perform(
            get(ApiEndpoint.GET_STUDY_PARTICIPANT.getPath(), studyEntity.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.participantRegistryDetail.studyId").value(studyEntity.getId()))
        .andExpect(jsonPath("$.participantRegistryDetail.registryParticipants").isArray())
        .andExpect(jsonPath("$.participantRegistryDetail.registryParticipants", hasSize(1)))
        .andExpect(
            jsonPath("$.participantRegistryDetail.registryParticipants[0].siteId")
                .value(siteEntity.getId()))
        .andExpect(
            jsonPath("$.participantRegistryDetail.registryParticipants[0].locationName")
                .value(locationEntity.getName()))
        .andExpect(
            jsonPath("$.participantRegistryDetail.registryParticipants[0].enrollmentStatus")
                .value(ENROLLED_STATUS))
        .andExpect(
            jsonPath("$.participantRegistryDetail.targetEnrollment")
                .value(siteEntity.getTargetEnrollment()));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userRegAdminEntity.getId());
    auditRequest.setStudyId(studyEntity.getId());
    auditRequest.setAppId(appEntity.getId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(STUDY_PARTICIPANT_REGISTRY_VIEWED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, STUDY_PARTICIPANT_REGISTRY_VIEWED);
    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnYetToEnrollStatusForEnrollmentStatusWithdrawn() throws Exception {
    userRegAdminEntity.setSuperAdmin(false);
    testDataHelper.getUserRegAdminRepository().save(userRegAdminEntity);
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    locationEntity = testDataHelper.createLocation();
    studyEntity.setType(OPEN_STUDY);
    siteEntity.setLocation(locationEntity);
    siteEntity.setTargetEnrollment(0);
    siteEntity.setStudy(studyEntity);
    participantRegistrySiteEntity.setEmail(TestConstants.EMAIL_VALUE);
    participantRegistrySiteEntity.setOnboardingStatus(OnboardingStatus.INVITED.getCode());
    participantStudyEntity.setStudy(studyEntity);
    participantStudyEntity.setStatus(EnrollmentStatus.WITHDRAWN.getStatus());
    participantStudyEntity.setParticipantRegistrySite(participantRegistrySiteEntity);
    testDataHelper.getParticipantStudyRepository().saveAndFlush(participantStudyEntity);

    mockMvc
        .perform(
            get(ApiEndpoint.GET_STUDY_PARTICIPANT.getPath(), studyEntity.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.participantRegistryDetail.studyId").value(studyEntity.getId()))
        .andExpect(jsonPath("$.participantRegistryDetail.registryParticipants").isArray())
        .andExpect(jsonPath("$.participantRegistryDetail.registryParticipants", hasSize(1)))
        .andExpect(
            jsonPath("$.participantRegistryDetail.registryParticipants[0].siteId")
                .value(siteEntity.getId()))
        .andExpect(
            jsonPath("$.participantRegistryDetail.registryParticipants[0].locationName")
                .value(locationEntity.getName()))
        .andExpect(
            jsonPath("$.participantRegistryDetail.registryParticipants[0].enrollmentStatus")
                .value(YET_TO_ENROLL))
        .andExpect(
            jsonPath("$.participantRegistryDetail.targetEnrollment")
                .value(siteEntity.getTargetEnrollment()));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userRegAdminEntity.getId());
    auditRequest.setStudyId(studyEntity.getId());
    auditRequest.setAppId(appEntity.getId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(STUDY_PARTICIPANT_REGISTRY_VIEWED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, STUDY_PARTICIPANT_REGISTRY_VIEWED);
    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnYetToEnrollStatusForOnboardingStatusNew() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    locationEntity = testDataHelper.createLocation();
    studyEntity.setType(OPEN_STUDY);
    siteEntity.setLocation(locationEntity);
    siteEntity.setTargetEnrollment(0);
    siteEntity.setStudy(studyEntity);
    participantRegistrySiteEntity.setEmail(TestConstants.EMAIL_VALUE);
    participantRegistrySiteEntity.setOnboardingStatus(OnboardingStatus.NEW.getCode());
    testDataHelper.getParticipantStudyRepository().deleteAll();
    testDataHelper
        .getParticipantRegistrySiteRepository()
        .saveAndFlush(participantRegistrySiteEntity);

    mockMvc
        .perform(
            get(ApiEndpoint.GET_STUDY_PARTICIPANT.getPath(), studyEntity.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.participantRegistryDetail.registryParticipants[0].enrollmentStatus")
                .value(YET_TO_ENROLL));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userRegAdminEntity.getId());
    auditRequest.setStudyId(studyEntity.getId());
    auditRequest.setAppId(appEntity.getId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(STUDY_PARTICIPANT_REGISTRY_VIEWED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, STUDY_PARTICIPANT_REGISTRY_VIEWED);
    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnStudyParticipantsForInProgressStatus() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    locationEntity = testDataHelper.createLocation();
    studyEntity.setType(OPEN_STUDY);
    siteEntity.setLocation(locationEntity);
    siteEntity.setTargetEnrollment(0);
    siteEntity.setStudy(studyEntity);
    participantRegistrySiteEntity.setEmail(TestConstants.EMAIL_VALUE);
    participantRegistrySiteEntity.setOnboardingStatus(OnboardingStatus.ENROLLED.getCode());
    participantStudyEntity.setStudy(studyEntity);
    participantStudyEntity.setStatus(EnrollmentStatus.IN_PROGRESS.getStatus());
    participantStudyEntity.setParticipantRegistrySite(participantRegistrySiteEntity);
    testDataHelper.getParticipantStudyRepository().saveAndFlush(participantStudyEntity);

    mockMvc
        .perform(
            get(ApiEndpoint.GET_STUDY_PARTICIPANT.getPath(), studyEntity.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.participantRegistryDetail.studyId")
                .value(participantRegistrySiteEntity.getStudy().getId()))
        .andExpect(jsonPath("$.participantRegistryDetail.registryParticipants").isArray())
        .andExpect(jsonPath("$.participantRegistryDetail.registryParticipants", hasSize(1)))
        .andExpect(
            jsonPath("$.participantRegistryDetail.registryParticipants[0].siteId")
                .value(siteEntity.getId()))
        .andExpect(
            jsonPath("$.participantRegistryDetail.registryParticipants[0].locationName")
                .value(locationEntity.getName()))
        .andExpect(
            jsonPath("$.participantRegistryDetail.registryParticipants[0].enrollmentStatus")
                .value(EnrollmentStatus.ENROLLED.getStatus()))
        .andExpect(
            jsonPath("$.participantRegistryDetail.registryParticipants[0].siteId")
                .value(participantRegistrySiteEntity.getSite().getId()))
        .andExpect(
            jsonPath("$.participantRegistryDetail.targetEnrollment")
                .value(siteEntity.getTargetEnrollment()));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userRegAdminEntity.getId());
    auditRequest.setStudyId(studyEntity.getId());
    auditRequest.setAppId(appEntity.getId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(STUDY_PARTICIPANT_REGISTRY_VIEWED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, STUDY_PARTICIPANT_REGISTRY_VIEWED);
    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnStudyParticipantsForPagination() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, userRegAdminEntity.getId());
    locationEntity = testDataHelper.createLocation();
    siteEntity.setLocation(locationEntity);
    testDataHelper.getParticipantStudyRepository().saveAndFlush(participantStudyEntity);

    // Step 1: 1 Participants for study already added in @BeforeEach, add 20 new Participants for
    // study
    for (int i = 1; i <= 20; i++) {
      locationEntity = testDataHelper.newLocationEntity();
      locationEntity.setCustomId(CUSTOM_ID_VALUE + String.valueOf(i));
      locationEntity.setName(LOCATION_NAME_VALUE + String.valueOf(i));
      testDataHelper.getLocationRepository().saveAndFlush(locationEntity);
      siteEntity = testDataHelper.createSiteEntity(studyEntity, userRegAdminEntity, appEntity);
      siteEntity.setLocation(locationEntity);
      testDataHelper.getSiteRepository().saveAndFlush(siteEntity);
      participantRegistrySiteEntity =
          testDataHelper.createParticipantRegistrySite(siteEntity, studyEntity);
      participantStudyEntity =
          testDataHelper.createParticipantStudyEntity(
              siteEntity, studyEntity, participantRegistrySiteEntity);
    }

    // Step 2: Call API and expect GET_PARTICIPANT_REGISTRY_SUCCESS message and fetch only 5 data
    // out of 21
    mockMvc
        .perform(
            get(ApiEndpoint.GET_STUDY_PARTICIPANT.getPath(), studyEntity.getId())
                .headers(headers)
                .queryParam("page", PAGE_NO)
                .queryParam("limit", NO_OF_RECORDS)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.message", is(MessageCode.GET_PARTICIPANT_REGISTRY_SUCCESS.getMessage())))
        .andExpect(jsonPath("$.participantRegistryDetail.studyId").value(studyEntity.getId()))
        .andExpect(jsonPath("$.participantRegistryDetail.registryParticipants").isArray())
        .andExpect(jsonPath("$.participantRegistryDetail.registryParticipants", hasSize(5)))
        .andExpect(
            jsonPath("$.participantRegistryDetail.registryParticipants[0].locationName")
                .value(LOCATION_NAME_VALUE + String.valueOf(20)))
        .andExpect(jsonPath("$.totalParticipantCount", is(21)));

    verifyTokenIntrospectRequest(1);

    // page index starts with 0, get Study Participant for 3rd page
    mockMvc
        .perform(
            get(ApiEndpoint.GET_STUDY_PARTICIPANT.getPath(), studyEntity.getId())
                .headers(headers)
                .queryParam("page", "2")
                .queryParam("limit", "9")
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.message", is(MessageCode.GET_PARTICIPANT_REGISTRY_SUCCESS.getMessage())))
        .andExpect(jsonPath("$.participantRegistryDetail.registryParticipants").isArray())
        .andExpect(
            jsonPath("$.participantRegistryDetail.registryParticipants[0].locationName")
                .value(LOCATION_NAME_VALUE + String.valueOf(2)))
        .andExpect(jsonPath("$.participantRegistryDetail.registryParticipants", hasSize(3)))
        .andExpect(jsonPath("$.totalParticipantCount", is(21)));

    verifyTokenIntrospectRequest(2);

    // get study participant if page and limit values are null
    mockMvc
        .perform(
            get(ApiEndpoint.GET_STUDY_PARTICIPANT.getPath(), studyEntity.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.message", is(MessageCode.GET_PARTICIPANT_REGISTRY_SUCCESS.getMessage())))
        .andExpect(jsonPath("$.participantRegistryDetail.registryParticipants").isArray())
        .andExpect(jsonPath("$.participantRegistryDetail.registryParticipants", hasSize(21)))
        .andExpect(jsonPath("$.totalParticipantCount", is(21)));

    verifyTokenIntrospectRequest(3);
  }

  @Test
  public void shouldReturnUserNotFoundForStudyParticipants() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, IdGenerator.id());

    mockMvc
        .perform(
            get(ApiEndpoint.GET_STUDY_PARTICIPANT.getPath(), studyEntity.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath("$.error_description").value(ErrorCode.USER_NOT_FOUND.getDescription()));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnUserNotFound() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();

    mockMvc
        .perform(
            get(ApiEndpoint.GET_STUDY_PARTICIPANT.getPath(), studyEntity.getId())
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
  public void shouldUpdateTargetEnrollmentForSuperAdmin() throws Exception {
    // Step 1:Set request body
    UpdateTargetEnrollmentRequest targetEnrollmentRequest = newUpdateEnrollmentTargetRequest();

    // Step 2: Call API and expect TARGET_ENROLLMENT_UPDATE_SUCCESS message
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());
    result =
        mockMvc
            .perform(
                patch(ApiEndpoint.UPDATE_TARGET_ENROLLMENT.getPath(), studyEntity.getId())
                    .headers(headers)
                    .content(asJsonString(targetEnrollmentRequest))
                    .contextPath(getContextPath()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.siteId", notNullValue()))
            .andExpect(
                jsonPath(
                    "$.message", is(MessageCode.TARGET_ENROLLMENT_UPDATE_SUCCESS.getMessage())))
            .andReturn();

    String siteId = JsonPath.read(result.getResponse().getContentAsString(), "$.siteId");

    // Step 3: verify updated values
    Optional<SiteEntity> optSiteEntity = siteRepository.findById(siteId);
    SiteEntity siteEntity = optSiteEntity.get();
    assertNotNull(siteEntity);
    assertEquals(siteEntity.getStudy().getId(), studyEntity.getId());
    assertEquals(siteEntity.getTargetEnrollment(), targetEnrollmentRequest.getTargetEnrollment());

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userRegAdminEntity.getId());
    auditRequest.setStudyId(studyEntity.getId());
    auditRequest.setSiteId(siteEntity.getId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(ENROLLMENT_TARGET_UPDATED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, ENROLLMENT_TARGET_UPDATED);
    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldUpdateTargetEnrollment() throws Exception {
    // Step 1:Set request body
    UpdateTargetEnrollmentRequest targetEnrollmentRequest = newUpdateEnrollmentTargetRequest();
    userRegAdminEntity.setSuperAdmin(false);
    testDataHelper.getUserRegAdminRepository().save(userRegAdminEntity);

    // Step 2: Call API and expect TARGET_ENROLLMENT_UPDATE_SUCCESS message
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());
    result =
        mockMvc
            .perform(
                patch(ApiEndpoint.UPDATE_TARGET_ENROLLMENT.getPath(), studyEntity.getId())
                    .headers(headers)
                    .content(asJsonString(targetEnrollmentRequest))
                    .contextPath(getContextPath()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.siteId", notNullValue()))
            .andExpect(
                jsonPath(
                    "$.message", is(MessageCode.TARGET_ENROLLMENT_UPDATE_SUCCESS.getMessage())))
            .andReturn();

    String siteId = JsonPath.read(result.getResponse().getContentAsString(), "$.siteId");

    // Step 3: verify updated values
    Optional<SiteEntity> optSiteEntity = siteRepository.findById(siteId);
    SiteEntity siteEntity = optSiteEntity.get();
    assertNotNull(siteEntity);
    assertEquals(siteEntity.getStudy().getId(), studyEntity.getId());
    assertEquals(siteEntity.getTargetEnrollment(), targetEnrollmentRequest.getTargetEnrollment());

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userRegAdminEntity.getId());
    auditRequest.setStudyId(studyEntity.getId());
    auditRequest.setSiteId(siteEntity.getId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(ENROLLMENT_TARGET_UPDATED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, ENROLLMENT_TARGET_UPDATED);
    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnNotFoundForUpdateTargetEnrollment() throws Exception {
    // Step 1:Set studyId to invalid
    UpdateTargetEnrollmentRequest targetEnrollmentRequest = newUpdateEnrollmentTargetRequest();
    StudyEntity study = testDataHelper.newStudyEntity();
    study.setCustomId("CovidStudy1");
    study.setApp(appEntity);
    siteEntity.setStudy(study);
    testDataHelper.getSiteRepository().saveAndFlush(siteEntity);

    // Step 2: Call API and expect SITE_NOT_FOUND error
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());
    mockMvc
        .perform(
            patch(ApiEndpoint.UPDATE_TARGET_ENROLLMENT.getPath(), studyEntity.getId())
                .headers(headers)
                .content(asJsonString(targetEnrollmentRequest))
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error_description", is(ErrorCode.SITE_NOT_FOUND.getDescription())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnStudyPermissionAccessDeniedForUpdateTargetEnrollment() throws Exception {
    // Step 1:Set permission to READ_VIEW
    userRegAdminEntity.setSuperAdmin(false);
    testDataHelper.getUserRegAdminRepository().save(userRegAdminEntity);
    UpdateTargetEnrollmentRequest targetEnrollmentRequest = newUpdateEnrollmentTargetRequest();
    StudyPermissionEntity studyPermissionEntity = studyEntity.getStudyPermissions().get(0);
    studyPermissionEntity.setEdit(Permission.VIEW);
    studyEntity = testDataHelper.getStudyRepository().saveAndFlush(studyEntity);

    // Step 2: Call API and expect STUDY_PERMISSION_ACCESS_DENIED error
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());
    mockMvc
        .perform(
            patch(ApiEndpoint.UPDATE_TARGET_ENROLLMENT.getPath(), studyEntity.getId())
                .headers(headers)
                .content(asJsonString(targetEnrollmentRequest))
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(
            jsonPath(
                "$.error_description",
                is(ErrorCode.STUDY_PERMISSION_ACCESS_DENIED.getDescription())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnCannotUpdateTargetEnrollmentForCloseStudy() throws Exception {
    // Step 1:Set study type to close
    UpdateTargetEnrollmentRequest targetEnrollmentRequest = newUpdateEnrollmentTargetRequest();
    studyEntity.setType(CLOSE_STUDY);
    testDataHelper.getStudyRepository().saveAndFlush(studyEntity);

    // Step 2: Call API and expect CANNOT_UPDATE_ENROLLMENT_TARGET_FOR_CLOSE_STUDY error
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());
    mockMvc
        .perform(
            patch(ApiEndpoint.UPDATE_TARGET_ENROLLMENT.getPath(), studyEntity.getId())
                .headers(headers)
                .content(asJsonString(targetEnrollmentRequest))
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath(
                "$.error_description",
                is(ErrorCode.CANNOT_UPDATE_ENROLLMENT_TARGET_FOR_CLOSE_STUDY.getDescription())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnCannotUpdateTargetEnrollmentForDeactiveSite() throws Exception {
    // Step 1:Set site status to DEACTIVE
    UpdateTargetEnrollmentRequest targetEnrollmentRequest = newUpdateEnrollmentTargetRequest();
    siteEntity.setStatus(SiteStatus.DEACTIVE.value());
    testDataHelper.getSiteRepository().saveAndFlush(siteEntity);

    // Step 2: Call API and expect CANNOT_UPDATE_ENROLLMENT_TARGET_FOR_DEACTIVE_SITE error
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());
    mockMvc
        .perform(
            patch(ApiEndpoint.UPDATE_TARGET_ENROLLMENT.getPath(), studyEntity.getId())
                .headers(headers)
                .content(asJsonString(targetEnrollmentRequest))
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath(
                "$.error_description",
                is(
                    ErrorCode.CANNOT_UPDATE_ENROLLMENT_TARGET_FOR_DECOMMISSIONED_SITE
                        .getDescription())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnUserNotFoundForUpdateTargetEnrollment() throws Exception {
    UpdateTargetEnrollmentRequest targetEnrollmentRequest = newUpdateEnrollmentTargetRequest();

    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, IdGenerator.id());
    mockMvc
        .perform(
            patch(ApiEndpoint.UPDATE_TARGET_ENROLLMENT.getPath(), studyEntity.getId())
                .headers(headers)
                .content(asJsonString(targetEnrollmentRequest))
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error_description", is(ErrorCode.USER_NOT_FOUND.getDescription())));

    verifyTokenIntrospectRequest();
  }

  @AfterEach
  public void clean() {
    testDataHelper.cleanUp();
  }

  private UpdateTargetEnrollmentRequest newUpdateEnrollmentTargetRequest() {
    UpdateTargetEnrollmentRequest request = new UpdateTargetEnrollmentRequest();
    request.setTargetEnrollment(150);
    studyEntity.setType(OPEN);
    testDataHelper.getStudyRepository().saveAndFlush(studyEntity);
    return request;
  }
}
