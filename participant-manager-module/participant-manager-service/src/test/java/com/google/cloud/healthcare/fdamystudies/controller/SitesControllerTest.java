/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import com.google.cloud.healthcare.fdamystudies.beans.ParticipantDetailRequest;
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
import com.google.cloud.healthcare.fdamystudies.common.TestConstants;
import com.google.cloud.healthcare.fdamystudies.helper.TestDataHelper;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.AppPermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.LocationEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.SitePermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyPermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantRegistrySiteRepository;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantStudyRepository;
import com.google.cloud.healthcare.fdamystudies.repository.SiteRepository;
import com.google.cloud.healthcare.fdamystudies.service.SiteService;
import com.jayway.jsonpath.JsonPath;
import java.util.Collections;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.ENROLLED_STATUS;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.USER_ID_HEADER;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.VIEW_VALUE;
import static com.google.cloud.healthcare.fdamystudies.common.ErrorCode.EMAIL_EXISTS;
import static com.google.cloud.healthcare.fdamystudies.common.ErrorCode.ENROLLED_PARTICIPANT;
import static com.google.cloud.healthcare.fdamystudies.common.ErrorCode.INVALID_ONBOARDING_STATUS;
import static com.google.cloud.healthcare.fdamystudies.common.ErrorCode.MANAGE_SITE_PERMISSION_ACCESS_DENIED;
import static com.google.cloud.healthcare.fdamystudies.common.ErrorCode.SITE_NOT_EXIST_OR_INACTIVE;
import static com.google.cloud.healthcare.fdamystudies.common.ErrorCode.SITE_NOT_FOUND;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.asJsonString;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.readJsonFile;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SitesControllerTest extends BaseMockIT {

  private static String siteId;

  @Autowired private SiteController controller;
  @Autowired private SiteService siteService;
  @Autowired private TestDataHelper testDataHelper;
  @Autowired private SiteRepository siteRepository;
  @Autowired private ParticipantStudyRepository participantStudyRepository;
  @Autowired private ParticipantRegistrySiteRepository participantRegistrySiteRepository;

  private UserRegAdminEntity userRegAdminEntity;
  private StudyEntity studyEntity;
  private LocationEntity locationEntity;
  private AppEntity appEntity;
  private SiteEntity siteEntity;
  private ParticipantRegistrySiteEntity participantRegistrySiteEntity;
  private ParticipantStudyEntity participantStudyEntity;
  private SitePermissionEntity sitePermissionEntity;

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
  }

  @Test
  public void contextLoads() {
    assertNotNull(controller);
    assertNotNull(mockMvc);
    assertNotNull(siteService);
  }

  @Test
  public void shouldReturnBadRequestForAddNewSite() throws Exception {
    HttpHeaders headers = newCommonHeaders();
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
  }

  @Test
  public void shouldReturnSitePermissionAccessDeniedForAddNewSite() throws Exception {
    // pre-condition: deny study permission
    StudyPermissionEntity studyPermissionEntity = studyEntity.getStudyPermissions().get(0);
    studyPermissionEntity.setEditPermission(VIEW_VALUE);
    studyEntity = testDataHelper.getStudyRepository().saveAndFlush(studyEntity);

    // pre-condition: deny app permission
    AppPermissionEntity appPermissionEntity = appEntity.getAppPermissions().get(0);
    appPermissionEntity.setEditPermission(VIEW_VALUE);
    appEntity = testDataHelper.getAppRepository().saveAndFlush(appEntity);
    HttpHeaders headers = newCommonHeaders();
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
  }

  @Test
  public void shouldAddNewSite() throws Exception {
    HttpHeaders headers = newCommonHeaders();
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
  }

  @Test
  public void shouldReturnSiteNotExistForAddNewParticipant() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, userRegAdminEntity.getId());

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
  }

  @Test
  public void shouldReturnEnrolledParticipantForAddNewParticipant() throws Exception {
    // Step 1: set participantStudy status to enrolled
    siteEntity.setStudy(studyEntity);
    participantStudyEntity.setStatus(ENROLLED_STATUS);
    participantRegistrySiteEntity.setEmail(newParticipantRequest().getEmail());
    participantStudyRepository.saveAndFlush(participantStudyEntity);

    // Step 2: Call API to return ENROLLED_PARTICIPANT error
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            post(ApiEndpoint.ADD_NEW_PARTICIPANT.getPath(), siteEntity.getId())
                .headers(headers)
                .content(asJsonString(newParticipantRequest()))
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error_description", is(ENROLLED_PARTICIPANT.getDescription())));
  }

  @Test
  public void shouldReturnEmailExistForAddNewParticipant() throws Exception {
    // Step 1: set emailId
    siteEntity.setStudy(studyEntity);
    participantRegistrySiteEntity.setEmail(newParticipantRequest().getEmail());
    participantRegistrySiteRepository.saveAndFlush(participantRegistrySiteEntity);

    // Step 2: Call API to return EMAIL_EXISTS error
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            post(ApiEndpoint.ADD_NEW_PARTICIPANT.getPath(), siteEntity.getId())
                .headers(headers)
                .content(asJsonString(newParticipantRequest()))
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error_description", is(EMAIL_EXISTS.getDescription())));
  }

  @Test
  public void shouldReturnAccessDeniedForAddNewParticipant() throws Exception {
    // Step 1: set manage site permission to view only
    sitePermissionEntity = siteEntity.getSitePermissions().get(0);
    sitePermissionEntity.setEditPermission(Permission.READ_VIEW.value());
    testDataHelper.getSiteRepository().saveAndFlush(siteEntity);

    // Step 2: Call API to return MANAGE_SITE_PERMISSION_ACCESS_DENIED error
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, userRegAdminEntity.getId());

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
    headers.set(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            post(ApiEndpoint.ADD_NEW_PARTICIPANT.getPath(), siteEntity.getId())
                .headers(headers)
                .content(asJsonString(newParticipantRequest()))
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error_description", is(ErrorCode.OPEN_STUDY.getDescription())));
  }

  @Test
  public void shouldAddNewParticipant() throws Exception {
    // Step 1: Set studyEntity
    siteEntity.setStudy(studyEntity);
    testDataHelper.getSiteRepository().saveAndFlush(siteEntity);
    ParticipantDetailRequest participantRequest = newParticipantRequest();

    // Step 2: Call API to get ADD_PARTICIPANT_SUCCESS
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, userRegAdminEntity.getId());

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
  }

  @Test
  public void shouldReturnSiteNotFoundError() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            get(ApiEndpoint.GET_SITE_PARTICIPANTS.getPath(), IdGenerator.id())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error_description", is(SITE_NOT_FOUND.getDescription())));
  }

  @Test
  public void shouldReturnSitePermissionAccessDeniedError() throws Exception {
    // Site 1: set manage site permission to no permission
    sitePermissionEntity = siteEntity.getSitePermissions().get(0);
    sitePermissionEntity.setEditPermission(Permission.NO_PERMISSION.value());
    testDataHelper.getSiteRepository().saveAndFlush(siteEntity);

    // Step 2: Call API and expect MANAGE_SITE_PERMISSION_ACCESS_DENIED error
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, userRegAdminEntity.getId());

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
    headers.set(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            get(ApiEndpoint.GET_SITE_PARTICIPANTS.getPath(), siteEntity.getId())
                .headers(headers)
                .param("onboardingStatus", "NEW")
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error_description", is(INVALID_ONBOARDING_STATUS.getDescription())));
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
    headers.set(USER_ID_HEADER, userRegAdminEntity.getId());

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
  }

  @AfterEach
  public void cleanUp() {
    if (StringUtils.isNotEmpty(siteId)) {
      siteRepository.deleteById(siteId);
      siteId = null;
    }
    testDataHelper.getParticipantStudyRepository().deleteAll();
    testDataHelper.getParticipantRegistrySiteRepository().deleteAll();
    testDataHelper.getSiteRepository().deleteAll();
    testDataHelper.getStudyRepository().deleteAll();
    testDataHelper.getAppRepository().deleteAll();
    testDataHelper.getUserRegAdminRepository().deleteAll();
    testDataHelper.getLocationRepository().deleteAll();
  }

  private HttpHeaders newCommonHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
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
}
