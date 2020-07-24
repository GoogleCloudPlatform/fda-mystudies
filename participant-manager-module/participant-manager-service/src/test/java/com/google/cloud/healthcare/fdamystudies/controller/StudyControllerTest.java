/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.google.cloud.healthcare.fdamystudies.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.IdGenerator;
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
import com.google.cloud.healthcare.fdamystudies.service.StudyService;

public class StudyControllerTest extends BaseMockIT {

  @Autowired private StudyService studyService;

  @Autowired private StudyController controller;

  @Autowired private TestDataHelper testDataHelper;

  private UserRegAdminEntity userRegAdminEntity;

  private SiteEntity siteEntity;

  private StudyEntity studyEntity;

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
  public void shouldReturnStudies() throws Exception {
    HttpHeaders headers = newCommonHeaders();
    headers.add(TestConstants.USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            get(ApiEndpoint.GET_STUDIES.getPath()).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.studies").isArray())
        .andExpect(jsonPath("$.studies[0].id").isNotEmpty())
        .andExpect(jsonPath("$.studies[0].type").value(studyEntity.getType()))
        .andExpect(jsonPath("$.sitePermissionCount").value(1));
  }

  @Test
  public void shouldReturnBadRequestForGetStudies() throws Exception {
    HttpHeaders headers = newCommonHeaders();

    mockMvc
        .perform(
            get(ApiEndpoint.GET_STUDIES.getPath()).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.violations").isArray())
        .andExpect(jsonPath("$.violations[0].path").value("userId"))
        .andExpect(jsonPath("$.violations[0].message").value("header is required"));
  }

  @Test
  public void shouldNotReturnStudies() throws Exception {
    HttpHeaders headers = newCommonHeaders();
    headers.add(TestConstants.USER_ID_HEADER, IdGenerator.id());

    mockMvc
        .perform(
            get(ApiEndpoint.GET_STUDIES.getPath()).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath("$.error_description").value(ErrorCode.STUDY_NOT_FOUND.getDescription()));
  }

  @Test
  public void shouldReturnStudyNotFoundForStudyParticipants() throws Exception {
    HttpHeaders headers = newCommonHeaders();
    headers.add(TestConstants.USER_ID_HEADER, userRegAdminEntity.getId());
    mockMvc
        .perform(
            get(ApiEndpoint.GET_STUDY_PARTICIPANT.getPath(), IdGenerator.id())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath("$.error_description").value(ErrorCode.STUDY_NOT_FOUND.getDescription()));
  }

  @Test
  public void shouldReturnAppNotFoundForStudyParticipants() throws Exception {
    HttpHeaders headers = newCommonHeaders();
    headers.add(TestConstants.USER_ID_HEADER, userRegAdminEntity.getId());

    StudyPermissionEntity studyPermission = studyEntity.getStudyPermissions().get(0);
    studyPermission.setAppInfo(null);
    studyEntity = testDataHelper.getStudyRepository().saveAndFlush(studyEntity);
    mockMvc
        .perform(
            get(ApiEndpoint.GET_STUDY_PARTICIPANT.getPath(), studyEntity.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error_description").value(ErrorCode.APP_NOT_FOUND.getDescription()));
  }

  @Test
  public void shouldReturnAccessDeniedForStudyParticipants() throws Exception {
    HttpHeaders headers = newCommonHeaders();
    headers.add(TestConstants.USER_ID_HEADER, userRegAdminEntity.getId());

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
  }

  @Test
  public void shouldReturnStudyParticipants() throws Exception {
    HttpHeaders headers = newCommonHeaders();
    headers.add(TestConstants.USER_ID_HEADER, userRegAdminEntity.getId());
    locationEntity = testDataHelper.createLocation();
    siteEntity.setLocation(locationEntity);
    testDataHelper.getSiteRepository().saveAndFlush(siteEntity);

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
                .value(locationEntity.getName()));
  }

  @Test
  public void shouldReturnUserNotFound() throws Exception {
    HttpHeaders headers = newCommonHeaders();

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
  }

  public HttpHeaders newCommonHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  @AfterEach
  public void cleanUp() {
    testDataHelper.getParticipantStudyRepository().delete(participantStudyEntity);
    testDataHelper.getParticipantRegistrySiteRepository().delete(participantRegistrySiteEntity);
    testDataHelper.getSiteRepository().delete(siteEntity);
    testDataHelper.getStudyRepository().delete(studyEntity);
    testDataHelper.getAppRepository().delete(appEntity);
    testDataHelper.getUserRegAdminRepository().delete(userRegAdminEntity);
  }
}
