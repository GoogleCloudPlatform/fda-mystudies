/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import com.google.cloud.healthcare.fdamystudies.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.IdGenerator;
import com.google.cloud.healthcare.fdamystudies.common.TestConstants;
import com.google.cloud.healthcare.fdamystudies.helper.TestDataHelper;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import com.google.cloud.healthcare.fdamystudies.service.AppService;

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
  public void shouldReturnAppsRegisteredByUser() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(TestConstants.USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(get(ApiEndpoint.GET_APPS.getPath()).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.apps").isArray())
        .andExpect(jsonPath("$.apps[0].customId").value(appEntity.getAppId()))
        .andExpect(jsonPath("$.apps[0].name").value(appEntity.getAppName()))
        .andExpect(jsonPath("$.studyPermissionCount").value(1));
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
  }

  @Test
  public void shouldNotReturnApp() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(TestConstants.USER_ID_HEADER, IdGenerator.id());

    mockMvc
        .perform(get(ApiEndpoint.GET_APPS.getPath()).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error_description").value(ErrorCode.APP_NOT_FOUND.getDescription()));
  }

  @AfterEach
  public void cleanUp() {
    testDataHelper.getParticipantStudyRepository().delete(participantStudyEntity);
    testDataHelper.getParticipantRegistrySiteRepository().delete(participantRegistrySiteEntity);
    testDataHelper.getUserDetailsRepository().delete(userDetailsEntity);
    testDataHelper.getSiteRepository().delete(siteEntity);
    testDataHelper.getStudyRepository().delete(studyEntity);
    testDataHelper.getAppRepository().delete(appEntity);
    testDataHelper.getUserRegAdminRepository().delete(userRegAdminEntity);
  }
}
