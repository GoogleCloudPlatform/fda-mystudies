/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller.tests;

import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.readJsonFile;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.google.cloud.healthcare.fdamystudies.beans.SiteRequest;
import com.google.cloud.healthcare.fdamystudies.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.JsonUtils;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.controller.SiteController;
import com.google.cloud.healthcare.fdamystudies.helper.TestDataHelper;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.AppPermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.LocationEntity;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.SitePermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyPermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import com.google.cloud.healthcare.fdamystudies.repository.SiteRepository;
import com.google.cloud.healthcare.fdamystudies.service.SiteService;
import com.google.cloud.healthcare.fdamystudies.util.Constants;
import com.jayway.jsonpath.JsonPath;

public class SiteControllerTest extends BaseMockIT {

  private static String siteId;

  @Autowired private SiteController controller;
  @Autowired private SiteService siteService;
  @Autowired private TestDataHelper testDataHelper;
  @Autowired private SiteRepository siteRepository;

  private UserRegAdminEntity userRegAdminEntity;
  private StudyEntity studyEntity;
  private LocationEntity locationEntity;
  private AppEntity appEntity;
  private SiteEntity siteEntiy;

  @BeforeEach
  public void setUp() {
    locationEntity = testDataHelper.createLocation();
    userRegAdminEntity = testDataHelper.createUserRegAdminEntity();
    appEntity = testDataHelper.createAppEntity(userRegAdminEntity);
    studyEntity = testDataHelper.createStudyEntity(userRegAdminEntity, appEntity);
    siteEntiy = testDataHelper.createSiteEntity(studyEntity, userRegAdminEntity, appEntity);
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
    studyPermissionEntity.setEdit(Constants.VIEW_VALUE);
    studyEntity = testDataHelper.getStudyRepository().saveAndFlush(studyEntity);

    // pre-condition: deny app permission
    AppPermissionEntity appPermissionEntity = appEntity.getAppPermissions().get(0);
    appPermissionEntity.setEdit(Constants.VIEW_VALUE);
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
                is(ErrorCode.SITE_PERMISSION_ACEESS_DENIED.getDescription())));
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
  public void shouldReturnSiteExistsError() throws Exception {
    // Step 1: create a site using study and location entities
    siteEntiy.setStudy(studyEntity);
    siteEntiy.setLocation(locationEntity);
    siteEntiy = testDataHelper.getSiteRepository().saveAndFlush(siteEntiy);

    // Step 2: create site request using same study and location ids
    SiteRequest siteRequest = new SiteRequest();
    siteRequest.setStudyId(studyEntity.getId());
    siteRequest.setLocationId(locationEntity.getId());

    // Step 3: call the API and expect SITE_EXSTS error
    HttpHeaders headers = newCommonHeaders();
    mockMvc
        .perform(
            post(ApiEndpoint.ADD_NEW_SITE.getPath())
                .content(JsonUtils.asJsonString(siteRequest))
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error_description", is(ErrorCode.SITE_EXISTS.getDescription())));
  }

  @AfterEach
  public void cleanUp() {
    if (StringUtils.isNotEmpty(siteId)) {
      siteRepository.deleteById(siteId);
      siteId = null;
    }
    testDataHelper.getSiteRepository().deleteAll();
    testDataHelper.getStudyRepository().delete(studyEntity);
    testDataHelper.getAppRepository().delete(appEntity);
    testDataHelper.getUserRegAdminRepository().delete(userRegAdminEntity);
    testDataHelper.getLocationRepository().delete(locationEntity);
  }

  private HttpHeaders newCommonHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(Constants.USER_ID_HEADER, userRegAdminEntity.getId());
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
}
