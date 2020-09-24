/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.ENROLLED_STATUS;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.NO_OF_RECORDS;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.OPEN;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.PAGE_NO;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.USER_ID_HEADER;
import static com.google.cloud.healthcare.fdamystudies.common.ErrorCode.EMAIL_EXISTS;
import static com.google.cloud.healthcare.fdamystudies.common.ErrorCode.INVALID_ONBOARDING_STATUS;
import static com.google.cloud.healthcare.fdamystudies.common.ErrorCode.MANAGE_SITE_PERMISSION_ACCESS_DENIED;
import static com.google.cloud.healthcare.fdamystudies.common.ErrorCode.OPEN_STUDY;
import static com.google.cloud.healthcare.fdamystudies.common.ErrorCode.SITE_NOT_EXIST_OR_INACTIVE;
import static com.google.cloud.healthcare.fdamystudies.common.ErrorCode.SITE_NOT_FOUND;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.asJsonString;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.readJsonFile;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.INVITATION_EMAIL_SENT;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.PARTICIPANTS_EMAIL_LIST_IMPORTED;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.PARTICIPANTS_EMAIL_LIST_IMPORT_FAILED;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.PARTICIPANTS_EMAIL_LIST_IMPORT_PARTIAL_FAILED;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.PARTICIPANT_EMAIL_ADDED;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.PARTICIPANT_INVITATION_DISABLED;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.PARTICIPANT_INVITATION_EMAIL_RESENT;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.PARTICIPANT_INVITATION_ENABLED;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.SITE_ACTIVATED_FOR_STUDY;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.SITE_DECOMMISSIONED_FOR_STUDY;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.SITE_PARTICIPANT_REGISTRY_VIEWED;
import static com.google.cloud.healthcare.fdamystudies.common.TestConstants.CONSENT_VERSION;
import static com.google.cloud.healthcare.fdamystudies.common.TestConstants.DECOMMISSION_SITE_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.InviteParticipantRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantDetailRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantStatusRequest;
import com.google.cloud.healthcare.fdamystudies.beans.SiteRequest;
import com.google.cloud.healthcare.fdamystudies.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.common.CommonConstants;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.IdGenerator;
import com.google.cloud.healthcare.fdamystudies.common.JsonUtils;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.common.OnboardingStatus;
import com.google.cloud.healthcare.fdamystudies.common.Permission;
import com.google.cloud.healthcare.fdamystudies.common.SiteStatus;
import com.google.cloud.healthcare.fdamystudies.common.TestConstants;
import com.google.cloud.healthcare.fdamystudies.helper.TestDataHelper;
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
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantRegistrySiteRepository;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantStudyRepository;
import com.google.cloud.healthcare.fdamystudies.repository.SiteRepository;
import com.google.cloud.healthcare.fdamystudies.repository.StudyConsentRepository;
import com.google.cloud.healthcare.fdamystudies.service.SiteService;
import com.jayway.jsonpath.JsonPath;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.mail.internet.MimeMessage;
import org.apache.commons.collections4.map.HashedMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.ResourceUtils;

public class SiteControllerTest extends BaseMockIT {

  private static String siteId;

  protected MvcResult result;

  @Autowired private SiteController controller;
  @Autowired private SiteService siteService;
  @Autowired private TestDataHelper testDataHelper;
  @Autowired private SiteRepository siteRepository;
  @Autowired private ParticipantStudyRepository participantStudyRepository;
  @Autowired private ParticipantRegistrySiteRepository participantRegistrySiteRepository;
  @Autowired private JavaMailSender emailSender;
  @Autowired private StudyConsentRepository studyConsentRepository;

  private UserRegAdminEntity userRegAdminEntity;
  private StudyEntity studyEntity;
  private LocationEntity locationEntity;
  private AppEntity appEntity;
  private SiteEntity siteEntity;
  private ParticipantRegistrySiteEntity participantRegistrySiteEntity;
  private ParticipantStudyEntity participantStudyEntity;
  private SitePermissionEntity sitePermissionEntity;
  private StudyConsentEntity studyConsentEntity;

  private static final String IMPORT_EMAIL_1 = "mockitoimport01@grr.la";

  private static final String IMPORT_EMAIL_2 = "mockitoimport@grr.la";

  private static final String INVALID_TEST_EMAIL = "mockito";

  @BeforeEach
  public void setUp() {
    locationEntity = testDataHelper.createLocation();
    userRegAdminEntity = testDataHelper.createUserRegAdminEntity();
    appEntity = testDataHelper.createAppEntity(userRegAdminEntity);
    studyEntity = testDataHelper.createStudyEntity(userRegAdminEntity, appEntity);
    siteEntity = testDataHelper.createSiteEntity(studyEntity, userRegAdminEntity, appEntity);
    participantRegistrySiteEntity =
        testDataHelper.createParticipantRegistrySite(siteEntity, studyEntity);
    participantStudyEntity =
        testDataHelper.createParticipantStudyEntity(
            siteEntity, studyEntity, participantRegistrySiteEntity);
    studyConsentEntity = testDataHelper.createStudyConsentEntity(participantStudyEntity);
  }

  @Test
  public void contextLoads() {
    assertNotNull(controller);
    assertNotNull(mockMvc);
    assertNotNull(siteService);
  }

  @Test
  public void shouldReturnBadRequestForAddNewSite() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());
    SiteRequest siteRequest = new SiteRequest();
    MvcResult result =
        mockMvc
            .perform(
                post(ApiEndpoint.ADD_NEW_SITE.getPath())
                    .content(JsonUtils.asJsonString(siteRequest))
                    .headers(headers)
                    .contextPath(getContextPath()))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.violations").isArray())
            .andReturn();

    String actualResponse = result.getResponse().getContentAsString();
    String expectedResponse = readJsonFile("/responses/add_site_bad_request.json");
    JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.NON_EXTENSIBLE);

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnSitePermissionAccessDeniedForAddNewSite() throws Exception {
    // pre-condition: deny study permission
    StudyPermissionEntity studyPermissionEntity = studyEntity.getStudyPermissions().get(0);
    studyPermissionEntity.setEdit(Permission.VIEW);

    studyEntity = testDataHelper.getStudyRepository().saveAndFlush(studyEntity);

    // pre-condition: deny app permission
    AppPermissionEntity appPermissionEntity = appEntity.getAppPermissions().get(0);
    appPermissionEntity.setEdit(Permission.VIEW);
    appEntity = testDataHelper.getAppRepository().saveAndFlush(appEntity);
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());
    SiteRequest siteRequest = newSiteRequest();

    mockMvc
        .perform(
            post(ApiEndpoint.ADD_NEW_SITE.getPath())
                .content(JsonUtils.asJsonString(siteRequest))
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(
            jsonPath(
                "$.error_description",
                is(ErrorCode.SITE_PERMISSION_ACCESS_DENIED.getDescription())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnCannotAddSiteForOpenStudyError() throws Exception {
    // Step 1: Set study type to open
    SiteRequest siteRequest = newSiteRequest();
    studyEntity.setType(OPEN);
    testDataHelper.getStudyRepository().saveAndFlush(studyEntity);

    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    // Step 2: Call API and expect CANNOT_ADD_SITE_FOR_OPEN_STUDY error
    mockMvc
        .perform(
            post(ApiEndpoint.ADD_NEW_SITE.getPath())
                .content(asJsonString(siteRequest))
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(
            jsonPath(
                "$.error_description",
                is(ErrorCode.CANNOT_ADD_SITE_FOR_OPEN_STUDY.getDescription())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldAddNewSite() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());
    SiteRequest siteRequest = newSiteRequest();
    MvcResult result =
        mockMvc
            .perform(
                post(ApiEndpoint.ADD_NEW_SITE.getPath())
                    .content(JsonUtils.asJsonString(siteRequest))
                    .headers(headers)
                    .contextPath(getContextPath()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.siteId", notNullValue()))
            .andExpect(jsonPath("$.message", is(MessageCode.ADD_SITE_SUCCESS.getMessage())))
            .andReturn();

    siteId = JsonPath.read(result.getResponse().getContentAsString(), "$.siteId");

    // verify updated values
    Optional<SiteEntity> optSiteEntity = siteRepository.findById(siteId);
    SiteEntity siteEntity = optSiteEntity.get();
    SitePermissionEntity sitePermissionEntity = siteEntity.getSitePermissions().get(0);
    assertNotNull(siteEntity);
    assertEquals(locationEntity.getId(), siteEntity.getLocation().getId());
    assertEquals(sitePermissionEntity.getCreatedBy(), userRegAdminEntity.getId());

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnSiteNotExistForAddNewParticipant() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            post(ApiEndpoint.ADD_NEW_PARTICIPANT.getPath(), IdGenerator.id())
                .headers(headers)
                .content(asJsonString(newParticipantRequest()))
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.error_description", is(SITE_NOT_EXIST_OR_INACTIVE.getDescription())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnEmailExistForAddNewParticipant() throws Exception {
    // Step 1: set emailId
    siteEntity.setStudy(studyEntity);
    participantRegistrySiteEntity.setEmail(newParticipantRequest().getEmail());
    participantRegistrySiteRepository.saveAndFlush(participantRegistrySiteEntity);

    // Step 2: Call API to return EMAIL_EXISTS error
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            post(ApiEndpoint.ADD_NEW_PARTICIPANT.getPath(), siteEntity.getId())
                .headers(headers)
                .content(asJsonString(newParticipantRequest()))
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error_description", is(EMAIL_EXISTS.getDescription())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnAccessDeniedForAddNewParticipant() throws Exception {
    // Step 1: set manage site permission to view only
    sitePermissionEntity = siteEntity.getSitePermissions().get(0);
    sitePermissionEntity.setCanEdit(Permission.VIEW);
    testDataHelper.getSiteRepository().saveAndFlush(siteEntity);

    // Step 2: Call API to return MANAGE_SITE_PERMISSION_ACCESS_DENIED error
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            post(ApiEndpoint.ADD_NEW_PARTICIPANT.getPath(), siteEntity.getId())
                .headers(headers)
                .content(asJsonString(newParticipantRequest()))
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(
            jsonPath(
                "$.error_description", is(MANAGE_SITE_PERMISSION_ACCESS_DENIED.getDescription())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnOpenStudyForAddNewParticipant() throws Exception {
    // Step 1: set study type to open study
    sitePermissionEntity = siteEntity.getSitePermissions().get(0);
    studyEntity.setType(CommonConstants.OPEN_STUDY);
    siteEntity.setStudy(studyEntity);
    testDataHelper.getSiteRepository().saveAndFlush(siteEntity);

    // Step 2: Call API to return OPEN_STUDY error
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            post(ApiEndpoint.ADD_NEW_PARTICIPANT.getPath(), siteEntity.getId())
                .headers(headers)
                .content(asJsonString(newParticipantRequest()))
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error_description", is(ErrorCode.OPEN_STUDY.getDescription())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldAddNewParticipant() throws Exception {
    // Step 1: Set studyEntity
    siteEntity.setStudy(studyEntity);
    testDataHelper.getSiteRepository().saveAndFlush(siteEntity);
    ParticipantDetailRequest participantRequest = newParticipantRequest();

    // Step 2: Call API to get ADD_PARTICIPANT_SUCCESS
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    MvcResult result =
        mockMvc
            .perform(
                post(ApiEndpoint.ADD_NEW_PARTICIPANT.getPath(), siteEntity.getId())
                    .content(asJsonString(participantRequest))
                    .headers(headers)
                    .contextPath(getContextPath()))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.participantId", notNullValue()))
            .andExpect(jsonPath("$.message", is(MessageCode.ADD_PARTICIPANT_SUCCESS.getMessage())))
            .andReturn();

    String participantId =
        JsonPath.read(result.getResponse().getContentAsString(), "$.participantId");

    // Step 3: verify saved values
    Optional<ParticipantRegistrySiteEntity> optParticipantRegistrySite =
        participantRegistrySiteRepository.findById(participantId);
    assertNotNull(optParticipantRegistrySite.get().getSite());
    assertEquals(siteEntity.getId(), optParticipantRegistrySite.get().getSite().getId());
    assertEquals(participantRequest.getEmail(), optParticipantRegistrySite.get().getEmail());

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setAppId(appEntity.getId());
    auditRequest.setStudyId(studyEntity.getId());
    auditRequest.setUserId(userRegAdminEntity.getId());
    auditRequest.setSiteId(siteEntity.getId());
    auditRequest.setParticipantId(participantId);

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(PARTICIPANT_EMAIL_ADDED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, PARTICIPANT_EMAIL_ADDED);
    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnSiteNotFoundError() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            get(ApiEndpoint.GET_SITE_PARTICIPANTS.getPath(), IdGenerator.id())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error_description", is(SITE_NOT_FOUND.getDescription())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnSitePermissionAccessDeniedError() throws Exception {
    // Site 1: set manage site permission to no permission
    sitePermissionEntity = siteEntity.getSitePermissions().get(0);
    sitePermissionEntity.setCanEdit(Permission.NO_PERMISSION);
    testDataHelper.getSiteRepository().saveAndFlush(siteEntity);

    // Step 2: Call API and expect MANAGE_SITE_PERMISSION_ACCESS_DENIED error
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            get(ApiEndpoint.GET_SITE_PARTICIPANTS.getPath(), siteEntity.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(
            jsonPath(
                "$.error_description", is(MANAGE_SITE_PERMISSION_ACCESS_DENIED.getDescription())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnInvalidOnboardingStatusError() throws Exception {
    // Step 1: set study entity
    siteEntity.setStudy(studyEntity);
    testDataHelper.getSiteRepository().saveAndFlush(siteEntity);
    testDataHelper
        .getParticipantRegistrySiteRepository()
        .saveAndFlush(participantRegistrySiteEntity);

    // Step 2: Call API and expect INVALID_ONBOARDING_STATUS error
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            get(ApiEndpoint.GET_SITE_PARTICIPANTS.getPath(), siteEntity.getId())
                .headers(headers)
                .param("onboardingStatus", "NEW")
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error_description", is(INVALID_ONBOARDING_STATUS.getDescription())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnSiteParticipantsRegistry() throws Exception {
    // Step 1: set onboarding status to 'N'
    siteEntity.setStudy(studyEntity);
    //  testDataHelper.getSiteRepository().saveAndFlush(siteEntity);
    participantRegistrySiteEntity.setOnboardingStatus(OnboardingStatus.NEW.getCode());
    participantRegistrySiteEntity.setSite(siteEntity);
    participantRegistrySiteEntity.setEmail(TestConstants.EMAIL_VALUE);
    testDataHelper
        .getParticipantRegistrySiteRepository()
        .saveAndFlush(participantRegistrySiteEntity);

    // Step 2: Call API and expect  GET_PARTICIPANT_REGISTRY_SUCCESS
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            get(ApiEndpoint.GET_SITE_PARTICIPANTS.getPath(), siteEntity.getId())
                .headers(headers)
                .param("onboardingStatus", OnboardingStatus.NEW.getCode())
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.participantRegistryDetail", notNullValue()))
        .andExpect(jsonPath("$.participantRegistryDetail.studyId", is(studyEntity.getId())))
        .andExpect(jsonPath("$.participantRegistryDetail.siteStatus", is(siteEntity.getStatus())))
        .andExpect(jsonPath("$.participantRegistryDetail.registryParticipants").isArray())
        .andExpect(
            (jsonPath("$.participantRegistryDetail.registryParticipants[0].onboardingStatus")
                .value(OnboardingStatus.NEW.getStatus())))
        .andExpect(jsonPath("$.participantRegistryDetail.countByStatus.N", is(1)))
        .andExpect(
            jsonPath("$.message", is(MessageCode.GET_PARTICIPANT_REGISTRY_SUCCESS.getMessage())));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setSiteId(siteEntity.getId());
    auditRequest.setStudyId(studyEntity.getId());
    auditRequest.setAppId(appEntity.getId());
    auditRequest.setUserId(userRegAdminEntity.getId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(SITE_PARTICIPANT_REGISTRY_VIEWED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, SITE_PARTICIPANT_REGISTRY_VIEWED);
    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnSiteParticipantsRegistryForPagination() throws Exception {
    studyEntity.setType(OPEN);
    siteEntity.setTargetEnrollment(0);
    siteEntity.setStudy(studyEntity);
    locationEntity.setName("Location1");
    locationEntity.setCustomId("Location");
    locationEntity.setStatus(0);
    siteEntity.setLocation(locationEntity);
    testDataHelper.getSiteRepository().saveAndFlush(siteEntity);
    participantRegistrySiteEntity.setOnboardingStatus(OnboardingStatus.NEW.getCode());
    participantRegistrySiteEntity.setEmail(testDataHelper.EMAIL_VALUE);
    testDataHelper
        .getParticipantRegistrySiteRepository()
        .saveAndFlush(participantRegistrySiteEntity);

    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, userRegAdminEntity.getId());

    /*Step 1: 1 participantRegistrySite already added in @BeforeEach, add 20 new participantRegistrySites*/

    for (int i = 1; i <= 20; i++) {
      participantRegistrySiteEntity =
          testDataHelper.createParticipantRegistrySite(siteEntity, studyEntity);
      participantRegistrySiteEntity.setEmail(String.valueOf(i) + TestConstants.EMAIL_ID);
      participantRegistrySiteRepository.saveAndFlush(participantRegistrySiteEntity);
    }

    /*Step 2: Call API and expect GET_PARTICIPANT_REGISTRY_SUCCESS message and fetch only 5 data out of 21*/
    mockMvc
        .perform(
            get(ApiEndpoint.GET_SITE_PARTICIPANTS.getPath(), siteEntity.getId())
                .headers(headers)
                .queryParam("page", PAGE_NO)
                .queryParam("limit", NO_OF_RECORDS)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.participantRegistryDetail", notNullValue()))
        .andExpect(jsonPath("$.participantRegistryDetail.studyId", is(studyEntity.getId())))
        .andExpect(jsonPath("$.participantRegistryDetail.siteStatus", is(siteEntity.getStatus())))
        .andExpect(jsonPath("$.participantRegistryDetail.registryParticipants").isArray())
        .andExpect(jsonPath("$.participantRegistryDetail.registryParticipants", hasSize(5)))
        .andExpect(jsonPath("$.totalParticipantCount", is(21)))
        .andExpect(
            jsonPath(
                "$.participantRegistryDetail.registryParticipants[0].email",
                is(String.valueOf(20) + TestConstants.EMAIL_ID)))
        .andExpect(
            jsonPath("$.message", is(MessageCode.GET_PARTICIPANT_REGISTRY_SUCCESS.getMessage())));

    verifyTokenIntrospectRequest(1);

    // page index starts with 0, get site participants for 3rd page
    mockMvc
        .perform(
            get(ApiEndpoint.GET_SITE_PARTICIPANTS.getPath(), siteEntity.getId())
                .headers(headers)
                .queryParam("page", "2")
                .queryParam("limit", "9")
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.participantRegistryDetail", notNullValue()))
        .andExpect(jsonPath("$.participantRegistryDetail.studyId", is(studyEntity.getId())))
        .andExpect(jsonPath("$.participantRegistryDetail.siteStatus", is(siteEntity.getStatus())))
        .andExpect(jsonPath("$.participantRegistryDetail.registryParticipants").isArray())
        .andExpect(jsonPath("$.participantRegistryDetail.registryParticipants", hasSize(3)))
        .andExpect(jsonPath("$.totalParticipantCount", is(21)))
        .andExpect(
            jsonPath(
                "$.participantRegistryDetail.registryParticipants[0].email",
                is(String.valueOf(2) + TestConstants.EMAIL_ID)))
        .andExpect(
            jsonPath("$.message", is(MessageCode.GET_PARTICIPANT_REGISTRY_SUCCESS.getMessage())));

    verifyTokenIntrospectRequest(2);

    /*  get participantregistrySites for default page (0), limit (10) and sort by created timestamp
    in descending
    order*/

    mockMvc
        .perform(
            get(ApiEndpoint.GET_SITE_PARTICIPANTS.getPath(), siteEntity.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.participantRegistryDetail", notNullValue()))
        .andExpect(jsonPath("$.participantRegistryDetail.studyId", is(studyEntity.getId())))
        .andExpect(jsonPath("$.participantRegistryDetail.siteStatus", is(siteEntity.getStatus())))
        .andExpect(jsonPath("$.participantRegistryDetail.registryParticipants").isArray())
        .andExpect(jsonPath("$.participantRegistryDetail.registryParticipants", hasSize(10)))
        .andExpect(jsonPath("$.totalParticipantCount", is(21)))
        .andExpect(
            jsonPath(
                "$.participantRegistryDetail.registryParticipants[0].email",
                is(String.valueOf(20) + TestConstants.EMAIL_ID)))
        .andExpect(
            jsonPath("$.message", is(MessageCode.GET_PARTICIPANT_REGISTRY_SUCCESS.getMessage())));

    verifyTokenIntrospectRequest(3);
  }

  @Test
  public void shouldReturnNotFoundForDecomissionSite() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    // Call API and expect SITE_NOT_FOUND error
    mockMvc
        .perform(
            put(ApiEndpoint.DECOMISSION_SITE.getPath(), IdGenerator.id())
                .headers(headers)
                .contextPath(getContextPath()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error_description", is(ErrorCode.SITE_NOT_FOUND.getDescription())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldRecomissionSite() throws Exception {
    // Step 1: Set the status to DEACTIVE
    siteEntity.setStatus(SiteStatus.DEACTIVE.value());
    siteEntity = testDataHelper.getSiteRepository().saveAndFlush(siteEntity);

    // Step 2: call API and expect RECOMMISSION_SITE_SUCCESS message
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());
    result =
        mockMvc
            .perform(
                put(ApiEndpoint.DECOMISSION_SITE.getPath(), siteEntity.getId())
                    .headers(headers)
                    .contextPath(getContextPath()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.siteId", notNullValue()))
            .andExpect(
                jsonPath("$.message", is(MessageCode.RECOMMISSION_SITE_SUCCESS.getMessage())))
            .andExpect(jsonPath("$.siteStatus", is(SiteStatus.ACTIVE.value())))
            .andReturn();

    String siteId = JsonPath.read(result.getResponse().getContentAsString(), "$.siteId");

    // Step 3: verify updated values
    Optional<SiteEntity> optSiteEntity = siteRepository.findById(siteId);
    SiteEntity siteEntity = optSiteEntity.get();
    assertNotNull(siteEntity);
    assertEquals(DECOMMISSION_SITE_NAME, siteEntity.getName());

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setSiteId(siteId);
    auditRequest.setUserId(userRegAdminEntity.getId());
    auditRequest.setStudyId(siteEntity.getStudyId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(SITE_ACTIVATED_FOR_STUDY.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, SITE_ACTIVATED_FOR_STUDY);
    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldDecomissionSite() throws Exception {
    // Step 1: set status to ACTIVE
    siteEntity.setStatus(SiteStatus.ACTIVE.value());
    siteEntity = testDataHelper.getSiteRepository().saveAndFlush(siteEntity);

    // Step 2: Call API and expect DECOMMISSION_SITE_SUCCESS message
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());
    result =
        mockMvc
            .perform(
                put(ApiEndpoint.DECOMISSION_SITE.getPath(), siteEntity.getId())
                    .headers(headers)
                    .contextPath(getContextPath()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.siteId", notNullValue()))
            .andExpect(jsonPath("$.siteStatus", is(SiteStatus.DEACTIVE.value())))
            .andExpect(
                jsonPath("$.message", is(MessageCode.DECOMMISSION_SITE_SUCCESS.getMessage())))
            .andReturn();

    String siteId = JsonPath.read(result.getResponse().getContentAsString(), "$.siteId");

    // Step 3: verify updated values
    Optional<SiteEntity> optSiteEntity = siteRepository.findById(siteId);
    SiteEntity siteEntity = optSiteEntity.get();
    assertNotNull(siteEntity);
    assertEquals(DECOMMISSION_SITE_NAME, siteEntity.getName());

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setSiteId(siteId);
    auditRequest.setUserId(userRegAdminEntity.getId());
    auditRequest.setStudyId(siteEntity.getStudyId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(SITE_DECOMMISSIONED_FOR_STUDY.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, SITE_DECOMMISSIONED_FOR_STUDY);
    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnCannotDecommissionSiteForOpenStudyError() throws Exception {
    // Step 1: set studyType to open
    studyEntity.setType(OPEN);
    studyEntity = testDataHelper.getStudyRepository().saveAndFlush(studyEntity);

    // Step 2: call API and expect CANNOT_DECOMMISSION_SITE_FOR_OPEN_STUDY error
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            put(ApiEndpoint.DECOMISSION_SITE.getPath(), siteEntity.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath(
                "$.error_description",
                is(ErrorCode.CANNOT_DECOMMISSION_SITE_FOR_OPEN_STUDY.getDescription())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnCannotDecomissionSiteForEnrolledAndActiveStatus() throws Exception {
    // Step 1: Set status to enrolled
    participantStudyEntity.setStatus(ENROLLED_STATUS);
    testDataHelper.getParticipantStudyRepository().saveAndFlush(participantStudyEntity);

    // Step 2: call API and expect CANNOT_DECOMMISSION_SITE_FOR_ENROLLED_ACTIVE_STATUS error
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            put(ApiEndpoint.DECOMISSION_SITE.getPath(), siteEntity.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath(
                "$.error_description",
                is(
                    ErrorCode.CANNOT_DECOMMISSION_SITE_FOR_ENROLLED_ACTIVE_STATUS
                        .getDescription())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnSitePermissionAccessDeniedForDecommissionSite() throws Exception {
    // Step 1: Set permission to read only
    StudyPermissionEntity studyPermissionEntity = studyEntity.getStudyPermissions().get(0);
    studyPermissionEntity.setEdit(Permission.VIEW);
    studyEntity = testDataHelper.getStudyRepository().saveAndFlush(studyEntity);

    AppPermissionEntity appPermissionEntity = appEntity.getAppPermissions().get(0);
    appPermissionEntity.setEdit(Permission.VIEW);
    appEntity = testDataHelper.getAppRepository().saveAndFlush(appEntity);

    // Step 2: call API and expect SITE_PERMISSION_ACCESS_DENIED error
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());
    mockMvc
        .perform(
            put(ApiEndpoint.DECOMISSION_SITE.getPath(), siteEntity.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andExpect(status().isForbidden())
        .andExpect(
            jsonPath(
                "$.error_description",
                is(ErrorCode.SITE_PERMISSION_ACCESS_DENIED.getDescription())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnParticipantDetails() throws Exception {
    // Step 1: Set data needed to get Participant details
    participantRegistrySiteEntity.getStudy().setApp(appEntity);
    participantRegistrySiteEntity.setOnboardingStatus(OnboardingStatus.NEW.getCode());
    testDataHelper
        .getParticipantRegistrySiteRepository()
        .saveAndFlush(participantRegistrySiteEntity);
    siteEntity.setLocation(locationEntity);
    testDataHelper.getSiteRepository().saveAndFlush(siteEntity);

    // Step 2: Call API and expect GET_PARTICIPANT_DETAILS_SUCCESS message
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());
    mockMvc
        .perform(
            get(
                    ApiEndpoint.GET_PARTICIPANT_DETAILS.getPath(),
                    participantRegistrySiteEntity.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.participantDetails", notNullValue()))
        .andExpect(
            jsonPath(
                "$.participantDetails.participantRegistrySiteid",
                is(participantRegistrySiteEntity.getId())))
        .andExpect(jsonPath("$.participantDetails.enrollments").isArray())
        .andExpect(jsonPath("$.participantDetails.enrollments", hasSize(1)))
        .andExpect(jsonPath("$.participantDetails.consentHistory").isArray())
        .andExpect(jsonPath("$.participantDetails.consentHistory", hasSize(1)))
        .andExpect(
            jsonPath("$.participantDetails.consentHistory[0].consentVersion", is(CONSENT_VERSION)))
        .andExpect(
            jsonPath("$.message", is(MessageCode.GET_PARTICIPANT_DETAILS_SUCCESS.getMessage())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnParticipantDetailsForPagination() throws Exception {
    // Step 1: Set data needed to get Participant details
    participantRegistrySiteEntity.getStudy().setApp(appEntity);
    participantRegistrySiteEntity.setOnboardingStatus(OnboardingStatus.NEW.getCode());
    testDataHelper
        .getParticipantRegistrySiteRepository()
        .saveAndFlush(participantRegistrySiteEntity);
    siteEntity.setLocation(locationEntity);
    testDataHelper.getSiteRepository().saveAndFlush(siteEntity);

    // Step 2: 1 Participant for Consent already added in @BeforeEach, add 20 new Participant for
    // Consent
    for (int i = 1; i <= 20; i++) {
      studyConsentEntity = testDataHelper.createStudyConsentEntity(participantStudyEntity);
      studyConsentEntity.setVersion(CONSENT_VERSION + String.valueOf(i));
      studyConsentRepository.saveAndFlush(studyConsentEntity);
    }

    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, userRegAdminEntity.getId());

    // Step 3: Call API and expect GET_PARTICIPANT_DETAILS message and fetch only 3 data out of 21
    mockMvc
        .perform(
            get(
                    ApiEndpoint.GET_PARTICIPANT_DETAILS.getPath(),
                    participantRegistrySiteEntity.getId())
                .headers(headers)
                .queryParam("page", "0")
                .queryParam("limit", "3")
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.participantDetails", notNullValue()))
        .andExpect(
            jsonPath(
                "$.participantDetails.participantRegistrySiteid",
                is(participantRegistrySiteEntity.getId())))
        .andExpect(jsonPath("$.participantDetails.enrollments").isArray())
        .andExpect(jsonPath("$.participantDetails.enrollments", hasSize(1)))
        .andExpect(jsonPath("$.participantDetails.consentHistory").isArray())
        .andExpect(jsonPath("$.participantDetails.consentHistory", hasSize(3)))
        .andExpect(jsonPath("$.totalConsentHistoryCount", is(21)))
        .andExpect(
            jsonPath(
                "$.participantDetails.consentHistory[0].consentVersion",
                is(CONSENT_VERSION + String.valueOf(20))))
        .andExpect(
            jsonPath("$.message", is(MessageCode.GET_PARTICIPANT_DETAILS_SUCCESS.getMessage())));
    verifyTokenIntrospectRequest(1);

    // page index starts with 0, get consent history for 6th page
    mockMvc
        .perform(
            get(
                    ApiEndpoint.GET_PARTICIPANT_DETAILS.getPath(),
                    participantRegistrySiteEntity.getId())
                .headers(headers)
                .queryParam("page", "5")
                .queryParam("limit", "4")
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.participantDetails", notNullValue()))
        .andExpect(
            jsonPath(
                "$.participantDetails.participantRegistrySiteid",
                is(participantRegistrySiteEntity.getId())))
        .andExpect(jsonPath("$.participantDetails.enrollments").isArray())
        .andExpect(jsonPath("$.participantDetails.enrollments", hasSize(1)))
        .andExpect(jsonPath("$.participantDetails.consentHistory").isArray())
        .andExpect(jsonPath("$.participantDetails.consentHistory", hasSize(1)))
        .andExpect(jsonPath("$.totalConsentHistoryCount", is(21)))
        .andExpect(
            jsonPath("$.participantDetails.consentHistory[0].consentVersion", is(CONSENT_VERSION)))
        .andExpect(
            jsonPath("$.message", is(MessageCode.GET_PARTICIPANT_DETAILS_SUCCESS.getMessage())));
    verifyTokenIntrospectRequest(2);

    // get consent history for default page (0), limit (5) and sort by created timestamp in
    // descending order
    mockMvc
        .perform(
            get(
                    ApiEndpoint.GET_PARTICIPANT_DETAILS.getPath(),
                    participantRegistrySiteEntity.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.participantDetails", notNullValue()))
        .andExpect(
            jsonPath(
                "$.participantDetails.participantRegistrySiteid",
                is(participantRegistrySiteEntity.getId())))
        .andExpect(jsonPath("$.participantDetails.enrollments").isArray())
        .andExpect(jsonPath("$.participantDetails.enrollments", hasSize(1)))
        .andExpect(jsonPath("$.participantDetails.consentHistory").isArray())
        .andExpect(jsonPath("$.participantDetails.consentHistory", hasSize(5)))
        .andExpect(jsonPath("$.totalConsentHistoryCount", is(21)))
        .andExpect(
            jsonPath(
                "$.participantDetails.consentHistory[0].consentVersion",
                is(CONSENT_VERSION + String.valueOf(20))))
        .andExpect(
            jsonPath("$.message", is(MessageCode.GET_PARTICIPANT_DETAILS_SUCCESS.getMessage())));
    verifyTokenIntrospectRequest(3);
  }

  @Test
  public void shouldReturnParticipantRegistryNotFoundError() throws Exception {
    // Call API and expect PARTICIPANT_REGISTRY_SITE_NOT_FOUND error
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            get(ApiEndpoint.GET_PARTICIPANT_DETAILS.getPath(), IdGenerator.id())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath(
                "$.error_description",
                is(ErrorCode.PARTICIPANT_REGISTRY_SITE_NOT_FOUND.getDescription())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnAccessDeniedForGetParticipantDetails() throws Exception {
    // Step 1: Set userId to invalid
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, IdGenerator.id());

    // Step 2: Call API to return MANAGE_SITE_PERMISSION_ACCESS_DENIED error
    mockMvc
        .perform(
            get(
                    ApiEndpoint.GET_PARTICIPANT_DETAILS.getPath(),
                    participantRegistrySiteEntity.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(
            jsonPath(
                "$.error_description",
                is(ErrorCode.MANAGE_SITE_PERMISSION_ACCESS_DENIED.getDescription())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnSiteNotFoundForInviteParticipant() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    InviteParticipantRequest inviteParticipantRequest = new InviteParticipantRequest();
    inviteParticipantRequest.setIds(Arrays.asList(participantRegistrySiteEntity.getId()));
    mockMvc
        .perform(
            post(ApiEndpoint.INVITE_PARTICIPANTS.getPath(), IdGenerator.id())
                .content(asJsonString(inviteParticipantRequest))
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath(
                "$.error_description", is(ErrorCode.SITE_NOT_EXIST_OR_INACTIVE.getDescription())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnFailedInvitationForDisabledParticipant() throws Exception {
    studyEntity.setApp(appEntity);
    siteEntity.setStudy(studyEntity);
    participantRegistrySiteEntity.setEmail(TestDataHelper.EMAIL_VALUE);
    testDataHelper.getSiteRepository().save(siteEntity);
    testDataHelper.getParticipantRegistrySiteRepository().save(participantRegistrySiteEntity);

    // Step 1: participant invite
    participantRegistrySiteEntity.setOnboardingStatus(OnboardingStatus.INVITED.getCode());
    participantRegistrySiteRepository.saveAndFlush(participantRegistrySiteEntity);

    // Step 2: Disabled participant invite
    ParticipantRegistrySiteEntity participantRegistrySiteEntity1 =
        testDataHelper.createParticipantRegistrySite(siteEntity, studyEntity);
    participantRegistrySiteEntity.setEmail(TestDataHelper.EMAIL_VALUE);
    testDataHelper.getParticipantRegistrySiteRepository().save(participantRegistrySiteEntity1);
    participantRegistrySiteEntity1.setOnboardingStatus(OnboardingStatus.DISABLED.getCode());
    participantRegistrySiteRepository.saveAndFlush(participantRegistrySiteEntity1);

    InviteParticipantRequest inviteParticipantRequest = new InviteParticipantRequest();
    inviteParticipantRequest.setIds(
        Arrays.asList(
            participantRegistrySiteEntity.getId(), participantRegistrySiteEntity1.getId()));
    // Step 3: call the API and assert the error description
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());
    result =
        mockMvc
            .perform(
                post(ApiEndpoint.INVITE_PARTICIPANTS.getPath(), siteEntity.getId())
                    .content(asJsonString(inviteParticipantRequest))
                    .headers(headers)
                    .contextPath(getContextPath()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.invitedParticipantIds").isArray())
            .andExpect(jsonPath("$.invitedParticipantIds", hasSize(1)))
            .andExpect(jsonPath("$.failedParticipantIds").isArray())
            .andExpect(jsonPath("$.failedParticipantIds", hasSize(1)))
            .andExpect(
                jsonPath("$.message", is(MessageCode.PARTICIPANTS_INVITED_SUCCESS.getMessage())))
            .andReturn();

    verify(emailSender, atLeastOnce()).send(isA(MimeMessage.class));

    // Step 4: verify updated values
    String id =
        JsonPath.read(result.getResponse().getContentAsString(), "$.invitedParticipantIds[0]");
    Optional<ParticipantRegistrySiteEntity> optParticipantRegistrySite =
        participantRegistrySiteRepository.findById(id);

    assertNotNull(optParticipantRegistrySite);
    assertEquals(
        OnboardingStatus.INVITED.getCode(), optParticipantRegistrySite.get().getOnboardingStatus());

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userRegAdminEntity.getId());
    auditRequest.setSiteId(siteEntity.getId());
    auditRequest.setStudyId(siteEntity.getStudyId());
    auditRequest.setAppId(siteEntity.getStudy().getAppId());
    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(PARTICIPANT_INVITATION_EMAIL_RESENT.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, PARTICIPANT_INVITATION_EMAIL_RESENT);
    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldInviteParticipant() throws Exception {
    studyEntity.setApp(appEntity);
    siteEntity.setStudy(studyEntity);
    participantRegistrySiteEntity.setEmail(TestDataHelper.EMAIL_VALUE);
    testDataHelper.getSiteRepository().save(siteEntity);
    testDataHelper.getParticipantRegistrySiteRepository().save(participantRegistrySiteEntity);

    // Step 1: New participant invite
    participantRegistrySiteEntity.setOnboardingStatus(OnboardingStatus.NEW.getCode());
    participantRegistrySiteRepository.saveAndFlush(participantRegistrySiteEntity);

    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    InviteParticipantRequest inviteParticipantRequest = new InviteParticipantRequest();
    inviteParticipantRequest.setIds(Arrays.asList(participantRegistrySiteEntity.getId()));
    // Step 2: call the API and expect PARTICIPANTS_INVITED_SUCCESS message
    MvcResult result =
        mockMvc
            .perform(
                post(ApiEndpoint.INVITE_PARTICIPANTS.getPath(), siteEntity.getId())
                    .content(asJsonString(inviteParticipantRequest))
                    .headers(headers)
                    .contextPath(getContextPath()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.invitedParticipantIds").isArray())
            .andExpect(jsonPath("$.invitedParticipantIds", hasSize(1)))
            .andExpect(jsonPath("$.failedParticipantIds").isArray())
            .andExpect(jsonPath("$.failedParticipantIds", hasSize(0)))
            .andExpect(
                jsonPath("$.message", is(MessageCode.PARTICIPANTS_INVITED_SUCCESS.getMessage())))
            .andReturn();

    verify(emailSender, atLeastOnce()).send(isA(MimeMessage.class));
    // Step 3: verify updated values
    String id =
        JsonPath.read(result.getResponse().getContentAsString(), "$.invitedParticipantIds[0]");
    Optional<ParticipantRegistrySiteEntity> optParticipantRegistrySite =
        participantRegistrySiteRepository.findById(id);

    assertNotNull(optParticipantRegistrySite);
    assertEquals(
        OnboardingStatus.INVITED.getCode(), optParticipantRegistrySite.get().getOnboardingStatus());

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userRegAdminEntity.getId());
    auditRequest.setSiteId(siteEntity.getId());
    auditRequest.setStudyId(siteEntity.getStudyId());
    auditRequest.setAppId(siteEntity.getStudy().getAppId());
    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(INVITATION_EMAIL_SENT.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, INVITATION_EMAIL_SENT);
    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnAccessDeniedForImportNewParticipant() throws Exception {
    // Step 1: set manage site permission to view only
    sitePermissionEntity = siteEntity.getSitePermissions().get(0);
    sitePermissionEntity.setCanEdit(Permission.VIEW);
    testDataHelper.getSiteRepository().saveAndFlush(siteEntity);

    // Step 2: Call API to return MANAGE_SITE_PERMISSION_ACCESS_DENIED error
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    MockMultipartFile file = getMultipartFile("classpath:Email_Import_Template.xlsx");
    mockMvc
        .perform(
            multipart(ApiEndpoint.IMPORT_PARTICIPANT.getPath(), siteEntity.getId())
                .file(file)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(
            jsonPath(
                "$.error_description", is(MANAGE_SITE_PERMISSION_ACCESS_DENIED.getDescription())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnOpenStudyForImportNewParticipant() throws Exception {
    // Step 1: set study type to open study
    sitePermissionEntity = siteEntity.getSitePermissions().get(0);
    studyEntity.setType(CommonConstants.OPEN_STUDY);
    siteEntity.setStudy(studyEntity);
    testDataHelper.getSiteRepository().saveAndFlush(siteEntity);

    // Step 2: Call API to return OPEN_STUDY error
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    MockMultipartFile file = getMultipartFile("classpath:Email_Import_Template.xlsx");
    mockMvc
        .perform(
            multipart(ApiEndpoint.IMPORT_PARTICIPANT.getPath(), siteEntity.getId())
                .file(file)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error_description", is(OPEN_STUDY.getDescription())));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userRegAdminEntity.getId());
    auditRequest.setSiteId(siteEntity.getId());
    auditRequest.setStudyId(siteEntity.getStudyId());
    auditRequest.setAppId(siteEntity.getStudy().getAppId());
    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(PARTICIPANTS_EMAIL_LIST_IMPORT_FAILED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, PARTICIPANTS_EMAIL_LIST_IMPORT_FAILED);
    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnSiteNotExistForImportNewParticipant() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    MockMultipartFile file = getMultipartFile("classpath:Email_Import_Template.xlsx");
    mockMvc
        .perform(
            multipart(ApiEndpoint.IMPORT_PARTICIPANT.getPath(), IdGenerator.id())
                .file(file)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.error_description", is(SITE_NOT_EXIST_OR_INACTIVE.getDescription())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnWithBadHeaders() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    MockMultipartFile file = getMultipartFile("classpath:Email_Import_Template_bad_header.xlsx");
    mockMvc
        .perform(
            multipart(ApiEndpoint.IMPORT_PARTICIPANT.getPath(), siteEntity.getId())
                .file(file)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath(
                "$.error_description",
                is(ErrorCode.DOCUMENT_NOT_IN_PRESCRIBED_FORMAT.getDescription())));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userRegAdminEntity.getId());
    auditRequest.setSiteId(siteEntity.getId());
    auditRequest.setStudyId(siteEntity.getStudyId());
    auditRequest.setAppId(siteEntity.getStudy().getAppId());
    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(PARTICIPANTS_EMAIL_LIST_IMPORT_FAILED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, PARTICIPANTS_EMAIL_LIST_IMPORT_FAILED);
    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnImportNewParticipantAndInvalidEmail() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    // Step 1: Call API to import new participants
    MockMultipartFile file =
        getMultipartFile("classpath:Email_Import_Template_Invalid_Emails.xlsx");
    MvcResult result =
        mockMvc
            .perform(
                multipart(ApiEndpoint.IMPORT_PARTICIPANT.getPath(), siteEntity.getId())
                    .file(file)
                    .headers(headers)
                    .contextPath(getContextPath()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(
                jsonPath("$.message", is(MessageCode.IMPORT_PARTICIPANT_SUCCESS.getMessage())))
            .andExpect(jsonPath("$.participants").isArray())
            .andExpect(jsonPath("$.participants", hasSize(1)))
            .andExpect(jsonPath("$.participants[0].email", is(IMPORT_EMAIL_2)))
            .andExpect(jsonPath("$.invalidEmails", hasSize(1)))
            .andExpect(jsonPath("$.invalidEmails[0]", is(INVALID_TEST_EMAIL)))
            .andReturn();

    String participantId =
        JsonPath.read(result.getResponse().getContentAsString(), "$.participants[0].id");

    // Step 2: verify saved values
    Optional<ParticipantRegistrySiteEntity> optParticipantRegistrySite =
        participantRegistrySiteRepository.findById(participantId);
    assertNotNull(optParticipantRegistrySite.get().getSite());
    assertEquals(siteEntity.getId(), optParticipantRegistrySite.get().getSite().getId());
    assertEquals(IMPORT_EMAIL_2, optParticipantRegistrySite.get().getEmail());

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userRegAdminEntity.getId());
    auditRequest.setSiteId(siteEntity.getId());
    auditRequest.setStudyId(siteEntity.getStudyId());
    auditRequest.setAppId(siteEntity.getStudy().getAppId());
    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(PARTICIPANTS_EMAIL_LIST_IMPORT_PARTIAL_FAILED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, PARTICIPANTS_EMAIL_LIST_IMPORT_PARTIAL_FAILED);
    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnImportNewParticipant() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    // Step 1: Call API to import new participants
    MockMultipartFile file = getMultipartFile("classpath:Email_Import_Template.xlsx");
    MvcResult result =
        mockMvc
            .perform(
                multipart(ApiEndpoint.IMPORT_PARTICIPANT.getPath(), siteEntity.getId())
                    .file(file)
                    .headers(headers)
                    .contextPath(getContextPath()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(
                jsonPath("$.message", is(MessageCode.IMPORT_PARTICIPANT_SUCCESS.getMessage())))
            .andExpect(jsonPath("$.participants").isArray())
            .andExpect(jsonPath("$.participants", hasSize(2)))
            .andExpect(jsonPath("$.participants[0].email", is(IMPORT_EMAIL_1)))
            .andExpect(jsonPath("$.participants[1].email", is(IMPORT_EMAIL_2)))
            .andReturn();

    String participantId =
        JsonPath.read(result.getResponse().getContentAsString(), "$.participants[0].id");

    // Step 2: verify saved values
    Optional<ParticipantRegistrySiteEntity> optParticipantRegistrySite =
        participantRegistrySiteRepository.findById(participantId);
    assertNotNull(optParticipantRegistrySite.get().getSite());
    assertEquals(siteEntity.getId(), optParticipantRegistrySite.get().getSite().getId());
    assertEquals(IMPORT_EMAIL_1, optParticipantRegistrySite.get().getEmail());

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userRegAdminEntity.getId());
    auditRequest.setSiteId(siteEntity.getId());
    auditRequest.setStudyId(siteEntity.getStudyId());
    auditRequest.setAppId(siteEntity.getStudy().getAppId());
    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(PARTICIPANTS_EMAIL_LIST_IMPORTED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, PARTICIPANTS_EMAIL_LIST_IMPORTED);
    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnSiteNotExistOrInactiveError() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            patch(ApiEndpoint.UPDATE_ONBOARDING_STATUS.getPath(), IdGenerator.id())
                .headers(headers)
                .content(asJsonString(newParticipantStatusRequest()))
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.error_description", is(SITE_NOT_EXIST_OR_INACTIVE.getDescription())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnAccessDeniedError() throws Exception {
    // Step 1: set manage site permission to view only
    sitePermissionEntity = siteEntity.getSitePermissions().get(0);
    sitePermissionEntity.setCanEdit(Permission.VIEW);
    testDataHelper.getSiteRepository().saveAndFlush(siteEntity);

    // Step 2: Call API to return MANAGE_SITE_PERMISSION_ACCESS_DENIED error
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            patch(ApiEndpoint.UPDATE_ONBOARDING_STATUS.getPath(), siteEntity.getId())
                .headers(headers)
                .content(asJsonString(newParticipantStatusRequest()))
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(
            jsonPath(
                "$.error_description", is(MANAGE_SITE_PERMISSION_ACCESS_DENIED.getDescription())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnInvalidStatus() throws Exception {
    // Step 1:set request body
    ParticipantStatusRequest participantStatusRequest = newParticipantStatusRequest();
    participantStatusRequest.setStatus("Z");

    // Step 2: Call API to INVALID_ONBOARDING_STATUS
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            patch(ApiEndpoint.UPDATE_ONBOARDING_STATUS.getPath(), siteEntity.getId())
                .headers(headers)
                .content(asJsonString(participantStatusRequest))
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error_description", is(INVALID_ONBOARDING_STATUS.getDescription())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldUpdateParticipantStatus() throws Exception {
    // Step 1:set request body
    ParticipantStatusRequest participantStatusRequest = newParticipantStatusRequest();

    // Step 2: Call API to UPDATE_STATUS_SUCCESS
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            patch(ApiEndpoint.UPDATE_ONBOARDING_STATUS.getPath(), siteEntity.getId())
                .headers(headers)
                .content(asJsonString(participantStatusRequest))
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message", is(MessageCode.UPDATE_STATUS_SUCCESS.getMessage())));

    // Step 3: verify updated values
    List<ParticipantRegistrySiteEntity> optParticipantRegistrySiteEntity =
        participantRegistrySiteRepository.findByIds(participantStatusRequest.getIds());
    ParticipantRegistrySiteEntity participantRegistrySiteEntity =
        optParticipantRegistrySiteEntity.get(0);
    assertNotNull(participantRegistrySiteEntity);
    assertEquals(
        participantStatusRequest.getStatus(), participantRegistrySiteEntity.getOnboardingStatus());

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setSiteId(siteEntity.getId());
    auditRequest.setUserId(userRegAdminEntity.getId());
    auditRequest.setStudyId(studyEntity.getId());
    auditRequest.setAppId(appEntity.getId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(PARTICIPANT_INVITATION_ENABLED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, PARTICIPANT_INVITATION_ENABLED);
  }

  @Test
  public void shouldUpdateParticipantStatusToDisabled() throws Exception {
    // Step 1:set request body
    ParticipantStatusRequest participantStatusRequest = newParticipantStatusRequest();
    participantStatusRequest.setStatus("D");

    // Step 2: Call API to UPDATE_ONBOARDING_STATUS
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            patch(ApiEndpoint.UPDATE_ONBOARDING_STATUS.getPath(), siteEntity.getId())
                .headers(headers)
                .content(asJsonString(participantStatusRequest))
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message", is(MessageCode.UPDATE_STATUS_SUCCESS.getMessage())));

    // Step 3: verify updated values
    List<ParticipantRegistrySiteEntity> optParticipantRegistrySiteEntity =
        participantRegistrySiteRepository.findByIds(participantStatusRequest.getIds());
    ParticipantRegistrySiteEntity participantRegistrySiteEntity =
        optParticipantRegistrySiteEntity.get(0);
    assertNotNull(participantRegistrySiteEntity);
    assertEquals(
        participantStatusRequest.getStatus(), participantRegistrySiteEntity.getOnboardingStatus());

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setSiteId(siteEntity.getId());
    auditRequest.setUserId(userRegAdminEntity.getId());
    auditRequest.setStudyId(studyEntity.getId());
    auditRequest.setAppId(appEntity.getId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(PARTICIPANT_INVITATION_DISABLED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, PARTICIPANT_INVITATION_DISABLED);
    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnSites() throws Exception {
    // Step 1: set the data needed to get studies with sites
    studyEntity.setApp(appEntity);
    siteEntity.setLocation(locationEntity);
    participantRegistrySiteEntity.setEmail(TestDataHelper.EMAIL_VALUE);
    testDataHelper.getSiteRepository().save(siteEntity);
    testDataHelper.getParticipantRegistrySiteRepository().save(participantRegistrySiteEntity);

    // Step 2: call API and expect GET_SITES_SUCCESS message
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());
    mockMvc
        .perform(
            get(ApiEndpoint.GET_SITES.getPath()).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.studies").isArray())
        .andExpect(jsonPath("$.studies", hasSize(1)))
        .andExpect(jsonPath("$.studies[0].id").isNotEmpty())
        .andExpect(jsonPath("$.studies[0].sites").isArray())
        .andExpect(jsonPath("$.studies[0].sites[0].id").value(siteEntity.getId()))
        .andExpect(jsonPath("$.message", is(MessageCode.GET_SITES_SUCCESS.getMessage())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnNotFoundForGetSites() throws Exception {
    // Step 1: set the userId to invalid
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, IdGenerator.id());

    // Step 2: Call API and expect SITE_NOT_FOUND error
    mockMvc
        .perform(
            get(ApiEndpoint.GET_SITES.getPath()).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error_description", is(ErrorCode.SITE_NOT_FOUND.getDescription())));

    verifyTokenIntrospectRequest();
  }

  @AfterEach
  public void clean() {
    testDataHelper.cleanUp();
  }

  private SiteRequest newSiteRequest() {
    SiteRequest siteRequest = new SiteRequest();
    siteRequest.setStudyId(studyEntity.getId());
    siteRequest.setLocationId(locationEntity.getId());
    return siteRequest;
  }

  private ParticipantDetailRequest newParticipantRequest() {
    ParticipantDetailRequest participantRequest = new ParticipantDetailRequest();
    participantRequest.setEmail(TestConstants.EMAIL_VALUE);
    return participantRequest;
  }

  private ParticipantStatusRequest newParticipantStatusRequest() {
    ParticipantStatusRequest request = new ParticipantStatusRequest();
    request.setIds(Arrays.asList(participantRegistrySiteEntity.getId()));
    request.setStatus(OnboardingStatus.NEW.getCode());
    return request;
  }

  private MockMultipartFile getMultipartFile(String fileName) throws IOException {
    File file = ResourceUtils.getFile(fileName);
    MockMultipartFile multipart =
        new MockMultipartFile(
            "file",
            file.getName(),
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            Files.readAllBytes(file.toPath()));
    return multipart;
  }
}
