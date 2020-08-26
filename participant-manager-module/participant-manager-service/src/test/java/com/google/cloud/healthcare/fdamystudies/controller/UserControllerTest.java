/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.USER_ID_HEADER;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.asJsonString;
import static com.google.cloud.healthcare.fdamystudies.helper.TestDataHelper.EMAIL_VALUE;
import static com.google.cloud.healthcare.fdamystudies.helper.TestDataHelper.NON_SUPER_ADMIN_EMAIL_ID;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequest;
import com.google.cloud.healthcare.fdamystudies.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.common.CommonConstants;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.IdGenerator;
import com.google.cloud.healthcare.fdamystudies.common.JsonUtils;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.common.Permission;
import com.google.cloud.healthcare.fdamystudies.common.TestConstants;
import com.google.cloud.healthcare.fdamystudies.helper.TestDataHelper;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.AppPermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.SitePermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyPermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import com.google.cloud.healthcare.fdamystudies.repository.AppPermissionRepository;
import com.google.cloud.healthcare.fdamystudies.repository.SitePermissionRepository;
import com.google.cloud.healthcare.fdamystudies.repository.StudyPermissionRepository;
import com.google.cloud.healthcare.fdamystudies.repository.UserRegAdminRepository;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;

public class UserControllerTest extends BaseMockIT {

  private static String adminUserRequestJson;

  private static String updateAdminUserRequestJson;

  private UserRegAdminEntity userRegAdminEntity;

  private UserRegAdminEntity adminforUpdate;

  @Autowired private TestDataHelper testDataHelper;

  @Autowired private UserRegAdminRepository userRegAdminRepository;

  @Autowired private StudyPermissionRepository studyPermissionRepository;

  @Autowired private SitePermissionRepository sitePermissionRepository;

  @Autowired private AppPermissionRepository appPermissionRepository;

  private AppEntity appEntity;

  private StudyEntity studyEntity;

  private SiteEntity siteEntity;

  @BeforeEach
  public void setUp() throws JsonParseException, JsonMappingException, IOException {
    userRegAdminEntity = testDataHelper.createUserRegAdmin();
    appEntity = testDataHelper.createAppEntity(userRegAdminEntity);
    studyEntity = testDataHelper.createStudyEntity(userRegAdminEntity, appEntity);
    siteEntity =
        testDataHelper.createSiteEntityForManageUsers(studyEntity, userRegAdminEntity, appEntity);
    adminUserRequestJson = JsonUtils.readJsonFile(TestConstants.ADMIN_USER_REQUEST_JSON_FILE);
    updateAdminUserRequestJson =
        JsonUtils.readJsonFile(TestConstants.UPDATE_USER_REQUEST_JSON_FILE);
  }

  @Test
  public void shouldReturnPermissionMissingError() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(CommonConstants.USER_ID_HEADER, userRegAdminEntity.getId());
    mockMvc
        .perform(
            post(ApiEndpoint.ADD_NEW_USER.getPath())
                .content(adminUserRequestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.error_description").value(ErrorCode.PERMISSION_MISSING.getDescription()));
  }

  @Test
  public void shouldReturnEmailExistsError() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(CommonConstants.USER_ID_HEADER, userRegAdminEntity.getId());
    UserRequest userRequest = newUserRequest();
    userRequest.setEmail(EMAIL_VALUE);
    mockMvc
        .perform(
            post(ApiEndpoint.ADD_NEW_USER.getPath())
                .content(asJsonString(userRequest))
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error_description").value(ErrorCode.EMAIL_EXISTS.getDescription()));
  }

  @Test
  public void shouldReturnUserNotFoundError() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(CommonConstants.USER_ID_HEADER, IdGenerator.id());
    UserRequest userRequest = newUserRequest();
    userRequest.setSuperAdmin(false);

    mockMvc
        .perform(
            post(ApiEndpoint.ADD_NEW_USER.getPath())
                .content(asJsonString(userRequest))
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath("$.error_description").value(ErrorCode.USER_NOT_FOUND.getDescription()));
  }

  @Test
  public void shouldReturnNotSuperAdminAccessError() throws Exception {
    // Step 1: Creating non super admin
    userRegAdminEntity = testDataHelper.createNonSuperAdmin();
    UserRequest userRequest = newUserRequest();
    userRequest.setSuperAdmin(false);

    // Step 2: Call the API and expect NOT_SUPER_ADMIN_ACCESS error
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(CommonConstants.USER_ID_HEADER, userRegAdminEntity.getId());
    mockMvc
        .perform(
            post(ApiEndpoint.ADD_NEW_USER.getPath())
                .content(asJsonString(userRequest))
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(
            jsonPath("$.error_description")
                .value(ErrorCode.NOT_SUPER_ADMIN_ACCESS.getDescription()));
  }

  @Test
  public void shouldReturnBadRequestForFirstNameMissing() throws Exception {
    // Step 1: Setting first name as empty
    UserRequest userRequest = newUserRequest();
    userRequest.setFirstName("");

    // Step 2: Call the API and expect must not be blank
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(CommonConstants.USER_ID_HEADER, userRegAdminEntity.getId());
    mockMvc
        .perform(
            post(ApiEndpoint.ADD_NEW_USER.getPath())
                .content(asJsonString(userRequest))
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.violations").isArray())
        .andExpect(jsonPath("$.violations[0].message").value("must not be blank"));
  }

  @Test
  public void shouldCreateSuperAdminUser() throws Exception {
    // Step 1: Setting up the request for super admin
    UserRequest userRequest = newUserRequest();

    // Step 2: Call the API and expect ADD_NEW_USER_SUCCESS message
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(CommonConstants.USER_ID_HEADER, userRegAdminEntity.getId());
    MvcResult result =
        mockMvc
            .perform(
                post(ApiEndpoint.ADD_NEW_USER.getPath())
                    .content(asJsonString(userRequest))
                    .headers(headers)
                    .contextPath(getContextPath()))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.message").value(MessageCode.ADD_NEW_USER_SUCCESS.getMessage()))
            .andExpect(jsonPath("$.userId", notNullValue()))
            .andReturn();

    String userId = JsonPath.read(result.getResponse().getContentAsString(), "$.userId");

    // Step 3: verify saved values
    assertAdminUser(userId, true);
    assertAppPermissionDetails(userId);
    assertStudyPermissionDetails(userId);
    assertSitePermissionDetails(userId);
  }

  @Test
  public void shouldCreateAdminUserForSitePermission() throws Exception {
    // Step 1: Setting up the request for site permission
    DocumentContext json = JsonPath.parse(adminUserRequestJson);
    adminUserRequestJson =
        json.set("$.apps[0].studies[0].sites[0].siteId", siteEntity.getId())
            .set("$.apps[0].studies[0].sites[0].selected", true)
            .jsonString();

    // Step 2: Call the API and expect ADD_NEW_USER_SUCCESS message
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(CommonConstants.USER_ID_HEADER, userRegAdminEntity.getId());
    MvcResult result =
        mockMvc
            .perform(
                post(ApiEndpoint.ADD_NEW_USER.getPath())
                    .content(adminUserRequestJson)
                    .headers(headers)
                    .contextPath(getContextPath()))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.message").value(MessageCode.ADD_NEW_USER_SUCCESS.getMessage()))
            .andExpect(jsonPath("$.userId", notNullValue()))
            .andReturn();

    String userId = JsonPath.read(result.getResponse().getContentAsString(), "$.userId");

    // Step 3: verify saved values
    assertAdminUser(userId, false);
    assertSitePermissionDetails(userId);
  }

  @Test
  public void shouldCreateAdminUserForStudyPermission() throws Exception {
    // Step 1: Setting up the request for site permission
    DocumentContext json = JsonPath.parse(adminUserRequestJson);
    adminUserRequestJson =
        json.set("$.apps[0].studies[0].studyId", studyEntity.getId())
            .set("$.apps[0].studies[0].selected", true)
            .jsonString();

    // Step 2: Call the API and expect ADD_NEW_USER_SUCCESS message
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(CommonConstants.USER_ID_HEADER, userRegAdminEntity.getId());
    MvcResult result =
        mockMvc
            .perform(
                post(ApiEndpoint.ADD_NEW_USER.getPath())
                    .content(adminUserRequestJson)
                    .headers(headers)
                    .contextPath(getContextPath()))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.message").value(MessageCode.ADD_NEW_USER_SUCCESS.getMessage()))
            .andExpect(jsonPath("$.userId", notNullValue()))
            .andReturn();

    String userId = JsonPath.read(result.getResponse().getContentAsString(), "$.userId");

    // Step 3: verify saved values
    assertAdminUser(userId, false);
    assertStudyPermissionDetails(userId);
    assertSitePermissionDetails(userId);
  }

  @Test
  public void shouldCreateAdminUserForAppPermission() throws Exception {
    // Step 1: Setting up the request for site permission
    DocumentContext json = JsonPath.parse(adminUserRequestJson);
    adminUserRequestJson =
        json.set("$.apps[0].id", appEntity.getId()).set("$.apps[0].selected", true).jsonString();

    // Step 2: Call the API and expect ADD_NEW_USER_SUCCESS message
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(CommonConstants.USER_ID_HEADER, userRegAdminEntity.getId());
    MvcResult result =
        mockMvc
            .perform(
                post(ApiEndpoint.ADD_NEW_USER.getPath())
                    .content(adminUserRequestJson)
                    .headers(headers)
                    .contextPath(getContextPath()))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.message").value(MessageCode.ADD_NEW_USER_SUCCESS.getMessage()))
            .andExpect(jsonPath("$.userId", notNullValue()))
            .andReturn();

    String userId = JsonPath.read(result.getResponse().getContentAsString(), "$.userId");

    // Step 3: verify saved values
    assertAdminUser(userId, false);
    assertAppPermissionDetails(userId);
    assertStudyPermissionDetails(userId);
    assertSitePermissionDetails(userId);
  }

  @Test
  public void shouldUpdateSuperAdminUser() throws Exception {
    // Step 1: Creating a non super admin
    adminforUpdate = testDataHelper.createNonSuperAdmin();

    // Step 2: Call the API and expect UPDATE_USER_SUCCESS message
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    UserRequest userRequest = newUserRequestForUpdate();
    userRequest.setUserId(adminforUpdate.getId());
    mockMvc
        .perform(
            put(ApiEndpoint.UPDATE_USER.getPath(), userRegAdminEntity.getId())
                .content(asJsonString(userRequest))
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value(MessageCode.UPDATE_USER_SUCCESS.getMessage()))
        .andExpect(jsonPath("$.userId", notNullValue()));

    // Step 3: verify updated values
    assertAdminDetails(adminforUpdate.getId(), true);
    assertAppPermissionDetails(adminforUpdate.getId());
    assertStudyPermissionDetails(adminforUpdate.getId());
    assertSitePermissionDetails(adminforUpdate.getId());
  }

  @Test
  public void shouldUpdateAdminUserForSitePermission() throws Exception {
    // Step 1: Creating a super admin
    adminforUpdate = testDataHelper.createSuperAdmin();

    // Step 2: Call the API and expect UPDATE_USER_SUCCESS message
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    DocumentContext json = JsonPath.parse(updateAdminUserRequestJson);
    updateAdminUserRequestJson =
        json.set("$.apps[0].studies[0].sites[0].siteId", siteEntity.getId())
            .set("$.apps[0].studies[0].sites[0].selected", true)
            .set("$.userId", adminforUpdate.getId())
            .jsonString();
    mockMvc
        .perform(
            put(ApiEndpoint.UPDATE_USER.getPath(), userRegAdminEntity.getId())
                .content(updateAdminUserRequestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value(MessageCode.UPDATE_USER_SUCCESS.getMessage()))
        .andExpect(jsonPath("$.userId", notNullValue()));

    // Step 3: verify updated values
    assertAdminDetails(adminforUpdate.getId(), false);
    assertSitePermissionDetails(adminforUpdate.getId());
  }

  @Test
  public void shouldUpdateAdminUserForStudyPermission() throws Exception {
    // Step 1: Creating a super admin
    adminforUpdate = testDataHelper.createSuperAdmin();

    // Step 2: Call the API and expect UPDATE_USER_SUCCESS message
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    DocumentContext json = JsonPath.parse(updateAdminUserRequestJson);
    updateAdminUserRequestJson =
        json.set("$.apps[0].studies[0].studyId", studyEntity.getId())
            .set("$.apps[0].studies[0].selected", true)
            .set("$.userId", adminforUpdate.getId())
            .jsonString();
    mockMvc
        .perform(
            put(ApiEndpoint.UPDATE_USER.getPath(), userRegAdminEntity.getId())
                .content(updateAdminUserRequestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value(MessageCode.UPDATE_USER_SUCCESS.getMessage()))
        .andExpect(jsonPath("$.userId", notNullValue()));

    // Step 3: verify updated values
    assertAdminDetails(adminforUpdate.getId(), false);
    assertSitePermissionDetails(adminforUpdate.getId());
    assertStudyPermissionDetails(adminforUpdate.getId());
  }

  @Test
  public void shouldUpdateAdminUserForAppPermission() throws Exception {
    // Step 1: Creating a super admin
    adminforUpdate = testDataHelper.createSuperAdmin();

    // Step 2: Call the API and expect UPDATE_USER_SUCCESS message
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    DocumentContext json = JsonPath.parse(updateAdminUserRequestJson);
    updateAdminUserRequestJson =
        json.set("$.apps[0].id", appEntity.getId())
            .set("$.apps[0].selected", true)
            .set("$.userId", adminforUpdate.getId())
            .jsonString();
    mockMvc
        .perform(
            put(ApiEndpoint.UPDATE_USER.getPath(), userRegAdminEntity.getId())
                .content(updateAdminUserRequestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value(MessageCode.UPDATE_USER_SUCCESS.getMessage()))
        .andExpect(jsonPath("$.userId", notNullValue()));

    // Step 3: verify updated values
    assertAdminDetails(adminforUpdate.getId(), false);
    assertSitePermissionDetails(adminforUpdate.getId());
    assertStudyPermissionDetails(adminforUpdate.getId());
    assertAppPermissionDetails(adminforUpdate.getId());
  }

  @Test
  public void shouldReturnUserNotFoundErrorForUpdateUser() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    UserRequest userRequest = newUserRequestForUpdate();
    userRequest.setSuperAdmin(false);
    mockMvc
        .perform(
            put(ApiEndpoint.UPDATE_USER.getPath(), userRegAdminEntity.getId())
                .content(asJsonString(userRequest))
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath("$.error_description").value(ErrorCode.USER_NOT_FOUND.getDescription()));
  }

  @Test
  public void shouldReturnNotSuperAdminAccessErrorForUpdateUser() throws Exception {
    userRegAdminEntity = testDataHelper.createNonSuperAdmin();
    adminforUpdate = testDataHelper.createSuperAdmin();
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    UserRequest userRequest = newUserRequestForUpdate();
    userRequest.setUserId(adminforUpdate.getId());
    mockMvc
        .perform(
            put(ApiEndpoint.UPDATE_USER.getPath(), userRegAdminEntity.getId())
                .content(asJsonString(userRequest))
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(
            jsonPath("$.error_description")
                .value(ErrorCode.NOT_SUPER_ADMIN_ACCESS.getDescription()));
  }

  @Test
  public void shouldReturnBadRequestForMissingParameter() throws Exception {
    // Step 1: Setting last name as empty
    UserRequest userRequest = newUserRequestForUpdate();
    userRequest.setLastName("");

    // Step 2: Call the API and expect must not be blank
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    mockMvc
        .perform(
            put(ApiEndpoint.UPDATE_USER.getPath(), userRegAdminEntity.getId())
                .content(asJsonString(userRequest))
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.violations").isArray())
        .andExpect(jsonPath("$.violations[0].message").value("must not be blank"));
  }

  @Test
  public void shouldReturnPermissionMissingErrorForUpdateUser() throws Exception {
    adminforUpdate = testDataHelper.createSuperAdmin();
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    mockMvc
        .perform(
            put(ApiEndpoint.UPDATE_USER.getPath(), userRegAdminEntity.getId())
                .content(updateAdminUserRequestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.error_description").value(ErrorCode.PERMISSION_MISSING.getDescription()));
  }

  @Test
  public void shouldReturnAdminRecordsWithoutAppStudySitePermissionForGetAdminDetailsAndApps()
      throws Exception {
    // Step 1: Set few admins
    UserRegAdminEntity superAdmin = testDataHelper.createSuperAdmin();

    // Step 2: Call API and expect MANAGE_USERS_SUCCESS message
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, userRegAdminEntity.getId());
    mockMvc
        .perform(
            get(ApiEndpoint.GET_ADMIN_DETAILS_AND_APPS.getPath(), superAdmin.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.user.id", is(superAdmin.getId())))
        .andExpect(jsonPath("$.user.apps").isArray())
        .andExpect(jsonPath("$.user.apps").isNotEmpty())
        .andExpect(jsonPath("$.user.apps[0].totalStudiesCount", is(1)))
        .andExpect(jsonPath("$.user.apps[0].selectedStudiesCount", is(0)))
        .andExpect(jsonPath("$.user.apps[0].totalSitesCount", is(1)))
        .andExpect(jsonPath("$.user.apps[0].selectedSitesCount", is(0)))
        .andExpect(jsonPath("$.message", is(MessageCode.GET_ADMIN_DETAILS_SUCCESS.getMessage())));
  }

  @Test
  public void shouldReturnAdminRecordsWithAppStudySiteForGetAdminDetailsAndApps() throws Exception {
    // Step 1: Set one admin
    UserRegAdminEntity superAdmin = testDataHelper.createSuperAdmin();
    testDataHelper.createAppPermission(superAdmin, appEntity, userRegAdminEntity.getId());
    testDataHelper.createStudyPermission(
        superAdmin, appEntity, studyEntity, userRegAdminEntity.getId());
    testDataHelper.createSitePermission(
        superAdmin, appEntity, studyEntity, siteEntity, userRegAdminEntity.getId());

    // Step 2: Call API and expect MANAGE_USERS_SUCCESS message
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, userRegAdminEntity.getId());
    mockMvc
        .perform(
            get(ApiEndpoint.GET_ADMIN_DETAILS_AND_APPS.getPath(), superAdmin.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.user.id", is(superAdmin.getId())))
        .andExpect(jsonPath("$.user.apps").isArray())
        .andExpect(jsonPath("$.user.apps").isNotEmpty())
        .andExpect(jsonPath("$.user.apps[0].totalStudiesCount", is(1)))
        .andExpect(jsonPath("$.user.apps[0].selectedStudiesCount", is(1)))
        .andExpect(jsonPath("$.user.apps[0].totalSitesCount", is(1)))
        .andExpect(jsonPath("$.user.apps[0].selectedSitesCount", is(1)))
        .andExpect(jsonPath("$.message", is(MessageCode.GET_ADMIN_DETAILS_SUCCESS.getMessage())));
  }

  @Test
  public void shouldReturnUserNotFoundErrorForGetAdminDetailsAndApps() throws Exception {
    // Step 1: Set a super admin
    UserRegAdminEntity superAdmin = testDataHelper.createSuperAdmin();

    // Step 2: Call API and expect USER_NOT_FOUND error
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, IdGenerator.id());
    mockMvc
        .perform(
            get(ApiEndpoint.GET_ADMIN_DETAILS_AND_APPS.getPath(), superAdmin.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath("$.error_description").value(ErrorCode.USER_NOT_FOUND.getDescription()));
  }

  @Test
  public void shouldReturnNotSuperAdminAccessForGetAdminDetailsAndApps() throws Exception {
    // Step 1: Set a super admin and a non super admin
    UserRegAdminEntity superAdmin = testDataHelper.createSuperAdmin();
    UserRegAdminEntity nonSuperAdmin = testDataHelper.createNonSuperAdmin();

    // Step 1: Call API and expect NOT_SUPER_ADMIN_ACCESS error
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, nonSuperAdmin.getId());
    mockMvc
        .perform(
            get(ApiEndpoint.GET_ADMIN_DETAILS_AND_APPS.getPath(), superAdmin.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(
            jsonPath("$.error_description")
                .value(ErrorCode.NOT_SUPER_ADMIN_ACCESS.getDescription()));
  }

  @Test
  public void shouldReturnAdminNotFoundErrorForGetAdminDetailsAndApps() throws Exception {
    // Step 1: Set one admin
    UserRegAdminEntity superAdmin = testDataHelper.createSuperAdmin();
    testDataHelper.createAppPermission(superAdmin, appEntity, userRegAdminEntity.getId());
    testDataHelper.createStudyPermission(
        superAdmin, appEntity, studyEntity, userRegAdminEntity.getId());
    testDataHelper.createSitePermission(
        superAdmin, appEntity, studyEntity, siteEntity, userRegAdminEntity.getId());

    // Step 2: Call API and expect USER_NOT_FOUND error
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, userRegAdminEntity.getId());
    mockMvc
        .perform(
            get(ApiEndpoint.GET_ADMIN_DETAILS_AND_APPS.getPath(), IdGenerator.id())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath("$.error_description").value(ErrorCode.ADMIN_NOT_FOUND.getDescription()));
  }

  @Test
  public void shouldReturnAdminsForGetAdmins() throws Exception {
    // Step 1: Set few admins
    testDataHelper.createSuperAdmin();
    testDataHelper.createNonSuperAdmin();

    // Step 2: Call API and expect GET_ADMINS_SUCCESS message
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, userRegAdminEntity.getId());
    mockMvc
        .perform(
            get(ApiEndpoint.GET_USERS.getPath()).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.users").isArray())
        .andExpect(jsonPath("$.users", hasSize(3)))
        .andExpect(jsonPath("$.users[0].apps").isArray())
        .andExpect(jsonPath("$.users[0].apps").isEmpty())
        .andExpect(jsonPath("$.message", is(MessageCode.GET_USERS_SUCCESS.getMessage())))
        .andExpect(jsonPath("$.users..email", hasItem(TestDataHelper.SUPER_ADMIN_EMAIL_ID)))
        .andExpect(jsonPath("$.users..email", hasItem(TestDataHelper.NON_SUPER_ADMIN_EMAIL_ID)))
        .andExpect(jsonPath("$.users..email", hasItem(TestDataHelper.EMAIL_VALUE)));
  }

  @Test
  public void shouldReturnUserNotFoundErrorForGetAdmins() throws Exception {
    // Step 1: Call API and expect USER_NOT_FOUND error
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, IdGenerator.id());
    mockMvc
        .perform(
            get(ApiEndpoint.GET_USERS.getPath()).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath("$.error_description").value(ErrorCode.USER_NOT_FOUND.getDescription()));
  }

  @Test
  public void shouldReturnNotSuperAdminAccessForGetAdmins() throws Exception {
    UserRegAdminEntity nonSuperAdmin = testDataHelper.createNonSuperAdmin();
    // Step 1: Call API and expect NOT_SUPER_ADMIN_ACCESS error
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, nonSuperAdmin.getId());
    mockMvc
        .perform(
            get(ApiEndpoint.GET_USERS.getPath()).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(
            jsonPath("$.error_description")
                .value(ErrorCode.NOT_SUPER_ADMIN_ACCESS.getDescription()));
  }

  private UserRequest newUserRequestForUpdate() {
    UserRequest userRequest = new UserRequest();
    userRequest.setEmail(NON_SUPER_ADMIN_EMAIL_ID);
    userRequest.setFirstName(TestConstants.UPDATED_FIRST_NAME);
    userRequest.setLastName(TestConstants.UPDATED_LAST_NAME);
    userRequest.setManageLocations(Permission.EDIT.value());
    userRequest.setSuperAdmin(true);
    return userRequest;
  }

  private UserRequest newUserRequest() {
    UserRequest userRequest = new UserRequest();
    userRequest.setEmail(TestConstants.USER_EMAIL_VALUE);
    userRequest.setFirstName(TestConstants.FIRST_NAME);
    userRequest.setLastName(TestConstants.LAST_NAME);
    userRequest.setManageLocations(Permission.EDIT.value());
    userRequest.setSuperAdmin(true);
    return userRequest;
  }

  private void assertSitePermissionDetails(String userId) {
    List<SitePermissionEntity> sitePermissions =
        sitePermissionRepository.findSitePermissionByUserId(userId);
    assertNotNull(sitePermissions);
  }

  private void assertStudyPermissionDetails(String userId) {
    List<StudyPermissionEntity> studyPermissions =
        studyPermissionRepository.findByAdminUserId(userId);
    assertNotNull(studyPermissions);
  }

  private void assertAppPermissionDetails(String userId) {
    List<AppPermissionEntity> appPermissions = appPermissionRepository.findByAdminUserId(userId);
    assertNotNull(appPermissions);
  }

  private void assertAdminDetails(String userId, boolean isSuperAdmin) {
    Optional<UserRegAdminEntity> optAdminUserEntity = userRegAdminRepository.findById(userId);
    UserRegAdminEntity adminUserEntity = optAdminUserEntity.get();
    assertNotNull(adminUserEntity);
    assertEquals(TestConstants.UPDATED_FIRST_NAME, adminUserEntity.getFirstName());
    assertEquals(TestConstants.UPDATED_LAST_NAME, adminUserEntity.getLastName());
    assertEquals(isSuperAdmin, adminUserEntity.isSuperAdmin());
  }

  private void assertAdminUser(String userId, boolean isSuperAdmin) {
    Optional<UserRegAdminEntity> optAdminUserEntity = userRegAdminRepository.findById(userId);
    UserRegAdminEntity adminUserEntity = optAdminUserEntity.get();
    assertNotNull(adminUserEntity);
    assertEquals(TestConstants.EMAIL_ID, adminUserEntity.getEmail());
    assertEquals(TestConstants.FIRST_NAME, adminUserEntity.getFirstName());
    assertEquals(TestConstants.LAST_NAME, adminUserEntity.getLastName());
    assertEquals(isSuperAdmin, adminUserEntity.isSuperAdmin());
  }

  @AfterEach
  public void clean() {
    testDataHelper.cleanUp();
  }
}
