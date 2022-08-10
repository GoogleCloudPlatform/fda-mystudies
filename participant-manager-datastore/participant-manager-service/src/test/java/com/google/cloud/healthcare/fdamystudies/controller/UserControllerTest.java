/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.USER_ID_HEADER;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.asJsonString;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.ADMIN_USER_RECORD_UPDATED;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.NEW_ADMIN_ADDED;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.RESEND_INVITATION;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.USER_REGISTRY_VIEWED;
import static com.google.cloud.healthcare.fdamystudies.helper.TestDataHelper.EMAIL_VALUE;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
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
import com.google.cloud.healthcare.fdamystudies.common.UserStatus;
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
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.collections4.map.HashedMap;
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

  public static final String NON_SUPER_ADMIN_EMAIL_ID = "mockit_non_super_admin_email@grr.la";

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

    verifyTokenIntrospectRequest();
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

    verifyTokenIntrospectRequest();
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

    verifyTokenIntrospectRequest();
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

    verifyTokenIntrospectRequest();
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

    verifyTokenIntrospectRequest();
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

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userRegAdminEntity.getId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(NEW_ADMIN_ADDED.getEventCode(), auditRequest);
    verifyAuditEventCall(auditEventMap, NEW_ADMIN_ADDED);

    String userId = JsonPath.read(result.getResponse().getContentAsString(), "$.userId");

    // Step 3: verify saved values
    assertAdminUser(userId, true);
    assertAppPermissionDetailsForSuperAdmin(userId);
    assertStudyPermissionDetailsForSuperAdmin(userId);
    assertSitePermissionDetailsForSuperAdmin(userId);

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldCreateAdminHavingLocationAsPermissionRole() throws Exception {
    // Step 1: Setting up the request for super admin
    UserRequest userRequest = newUserRequest();
    userRequest.setSuperAdmin(false);
    userRequest.setManageLocations(Permission.VIEW.value());

    // Step 2: Call the API and expect ADD_NEW_USER_SUCCESS message
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(CommonConstants.USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            post(ApiEndpoint.ADD_NEW_USER.getPath())
                .content(asJsonString(userRequest))
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.message").value(MessageCode.ADD_NEW_USER_SUCCESS.getMessage()))
        .andExpect(jsonPath("$.userId", notNullValue()));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userRegAdminEntity.getId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(NEW_ADMIN_ADDED.getEventCode(), auditRequest);
    verifyAuditEventCall(auditEventMap, NEW_ADMIN_ADDED);

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldNotCreateAdminWithoutAnyPermission() throws Exception {
    // Step 1: Setting up the request for super admin
    UserRequest userRequest = newUserRequest();
    userRequest.setSuperAdmin(false);
    userRequest.setManageLocations(Permission.NO_PERMISSION.value());

    // Step 2: Call the API and expect ADD_NEW_USER_SUCCESS message
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
        .andExpect(
            jsonPath("$.error_description").value(ErrorCode.PERMISSION_MISSING.getDescription()));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldCreateAdminUserForSitePermission() throws Exception {
    // Step 1: Setting up the request for site permission
    DocumentContext json = JsonPath.parse(adminUserRequestJson);
    adminUserRequestJson =
        json.set("$.apps[0].id", appEntity.getId())
            .set("$.apps[0].studies[0].studyId", studyEntity.getId())
            .set("$.apps[0].studies[0].sites[0].siteId", siteEntity.getId())
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

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userRegAdminEntity.getId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(NEW_ADMIN_ADDED.getEventCode(), auditRequest);
    verifyAuditEventCall(auditEventMap, NEW_ADMIN_ADDED);

    String userId = JsonPath.read(result.getResponse().getContentAsString(), "$.userId");

    // Step 3: verify saved values
    assertAdminUser(userId, false);
    assertSitePermissionDetails(userId);

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldCreateAdminUserForStudyPermission() throws Exception {
    // Step 1: Setting up the request for site permission
    DocumentContext json = JsonPath.parse(adminUserRequestJson);
    adminUserRequestJson =
        json.set("$.apps[0].id", appEntity.getId())
            .set("$.apps[0].studies[0].studyId", studyEntity.getId())
            .set("$.apps[0].studies[0].selected", true)
            .set("$.apps[0].studies[0].sites[0].siteId", siteEntity.getId())
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

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userRegAdminEntity.getId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(NEW_ADMIN_ADDED.getEventCode(), auditRequest);
    verifyAuditEventCall(auditEventMap, NEW_ADMIN_ADDED);

    String userId = JsonPath.read(result.getResponse().getContentAsString(), "$.userId");

    // Step 3: verify saved values
    assertAdminUser(userId, false);
    assertStudyPermissionDetails(userId);
    assertSitePermissionDetails(userId);

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldCreateAdminUserForAppPermission() throws Exception {
    // Step 1: Setting up the request for site permission
    DocumentContext json = JsonPath.parse(adminUserRequestJson);
    adminUserRequestJson =
        json.set("$.apps[0].id", appEntity.getId())
            .set("$.apps[0].selected", true)
            .set("$.apps[0].studies[0].studyId", studyEntity.getId())
            .set("$.apps[0].studies[0].selected", true)
            .set("$.apps[0].studies[0].sites[0].siteId", siteEntity.getId())
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

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userRegAdminEntity.getId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(NEW_ADMIN_ADDED.getEventCode(), auditRequest);
    verifyAuditEventCall(auditEventMap, NEW_ADMIN_ADDED);

    String userId = JsonPath.read(result.getResponse().getContentAsString(), "$.userId");

    // Step 3: verify saved values
    assertAdminUser(userId, false);
    assertAppPermissionDetails(userId);
    assertStudyPermissionDetails(userId);
    assertSitePermissionDetails(userId);

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldUpdateAndLogoutAdminUser() throws Exception {
    // Step 1: Creating a non super admin
    adminforUpdate = testDataHelper.createNonSuperAdmin();

    // Step 2: Call the API and expect UPDATE_USER_SUCCESS message
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, userRegAdminEntity.getId());

    UserRequest userRequest = newUserRequestForUpdate();
    userRequest.setId(adminforUpdate.getId());
    mockMvc
        .perform(
            put(ApiEndpoint.UPDATE_USER.getPath(), adminforUpdate.getId())
                .content(asJsonString(userRequest))
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value(MessageCode.UPDATE_USER_SUCCESS.getMessage()))
        .andExpect(jsonPath("$.userId", notNullValue()));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userRegAdminEntity.getId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(ADMIN_USER_RECORD_UPDATED.getEventCode(), auditRequest);
    verifyAuditEventCall(auditEventMap, ADMIN_USER_RECORD_UPDATED);

    // Step 3: verify updated values
    assertAdminDetails(adminforUpdate.getId(), true);
    assertAppPermissionDetailsForSuperAdmin(adminforUpdate.getId());
    assertStudyPermissionDetailsForSuperAdmin(adminforUpdate.getId());
    assertSitePermissionDetailsForSuperAdmin(adminforUpdate.getId());

    // verify external API call
    verify(
        1,
        postRequestedFor(
            urlEqualTo(
                String.format(
                    "/auth-server/users/%s",
                    "TuKUeFdyWz4E2A1-LqQcoYKBpMsfLnl-KjiuRFuxWcM3sQh" + "/logout"))));
    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldUpdateAdminHavingLocationAsPermissionRole() throws Exception {
    // Step 1: Creating a super admin
    adminforUpdate = testDataHelper.createSuperAdmin();

    // Step 2: Call the API and expect UPDATE_USER_SUCCESS message
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, adminforUpdate.getId());

    UserRequest userRequest = newUserRequestForUpdate();
    userRequest.setSuperAdmin(false);
    userRequest.setManageLocations(Permission.EDIT.value());
    userRequest.setId(adminforUpdate.getId());
    mockMvc
        .perform(
            put(ApiEndpoint.UPDATE_USER.getPath(), adminforUpdate.getId())
                .content(asJsonString(userRequest))
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value(MessageCode.UPDATE_USER_SUCCESS.getMessage()))
        .andExpect(jsonPath("$.userId", notNullValue()));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(adminforUpdate.getId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(ADMIN_USER_RECORD_UPDATED.getEventCode(), auditRequest);
    verifyAuditEventCall(auditEventMap, ADMIN_USER_RECORD_UPDATED);

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldNotUpdateAdminWithoutAnyPermission() throws Exception {
    // Step 1: Creating a non super admin
    adminforUpdate = testDataHelper.createNonSuperAdmin();

    // Step 2: Call the API and expect PERMISSION_MISSING message
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, userRegAdminEntity.getId());

    UserRequest userRequest = newUserRequestForUpdate();
    userRequest.setSuperAdmin(false);
    userRequest.setManageLocations(Permission.NO_PERMISSION.value());
    userRequest.setId(adminforUpdate.getId());
    mockMvc
        .perform(
            put(ApiEndpoint.UPDATE_USER.getPath(), userRegAdminEntity.getId())
                .content(asJsonString(userRequest))
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.error_description").value(ErrorCode.PERMISSION_MISSING.getDescription()));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldUpdateAdminUserForSitePermission() throws Exception {
    // Step 1: Creating a super admin
    adminforUpdate = testDataHelper.createSuperAdmin();

    // Step 2: Call the API and expect UPDATE_USER_SUCCESS message
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, userRegAdminEntity.getId());
    DocumentContext json = JsonPath.parse(updateAdminUserRequestJson);
    updateAdminUserRequestJson =
        json.set("$.apps[0].id", appEntity.getId())
            .set("$.apps[0].studies[0].studyId", studyEntity.getId())
            .set("$.apps[0].studies[0].sites[0].siteId", siteEntity.getId())
            .set("$.apps[0].studies[0].sites[0].selected", true)
            .set("$.id", adminforUpdate.getId())
            .jsonString();
    mockMvc
        .perform(
            put(ApiEndpoint.UPDATE_USER.getPath(), adminforUpdate.getId())
                .content(updateAdminUserRequestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value(MessageCode.UPDATE_USER_SUCCESS.getMessage()))
        .andExpect(jsonPath("$.userId", notNullValue()));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userRegAdminEntity.getId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(ADMIN_USER_RECORD_UPDATED.getEventCode(), auditRequest);
    verifyAuditEventCall(auditEventMap, ADMIN_USER_RECORD_UPDATED);

    // Step 3: verify updated values
    assertAdminDetails(adminforUpdate.getId(), false);
    assertSitePermissionDetails(adminforUpdate.getId());

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldUpdateAdminUserForStudyPermission() throws Exception {
    // Step 1: Creating a super admin
    adminforUpdate = testDataHelper.createSuperAdmin();

    // Step 2: Call the API and expect UPDATE_USER_SUCCESS message
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, userRegAdminEntity.getId());
    DocumentContext json = JsonPath.parse(updateAdminUserRequestJson);
    updateAdminUserRequestJson =
        json.set("$.apps[0].id", appEntity.getId())
            .set("$.apps[0].studies[0].studyId", studyEntity.getId())
            .set("$.apps[0].studies[0].selected", true)
            .set("$.apps[0].studies[0].sites[0].siteId", siteEntity.getId())
            .set("$.apps[0].studies[0].sites[0].selected", true)
            .set("$.id", adminforUpdate.getId())
            .jsonString();
    mockMvc
        .perform(
            put(ApiEndpoint.UPDATE_USER.getPath(), adminforUpdate.getId())
                .content(updateAdminUserRequestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value(MessageCode.UPDATE_USER_SUCCESS.getMessage()))
        .andExpect(jsonPath("$.userId", notNullValue()));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userRegAdminEntity.getId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(ADMIN_USER_RECORD_UPDATED.getEventCode(), auditRequest);
    verifyAuditEventCall(auditEventMap, ADMIN_USER_RECORD_UPDATED);

    // Step 3: verify updated values
    assertAdminDetails(adminforUpdate.getId(), false);
    assertSitePermissionDetails(adminforUpdate.getId());
    assertStudyPermissionDetails(adminforUpdate.getId());

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldUpdateAdminUserForAppPermission() throws Exception {
    // Step 1: Creating a super admin
    adminforUpdate = testDataHelper.createSuperAdmin();

    // Step 2: Call the API and expect UPDATE_USER_SUCCESS message
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, userRegAdminEntity.getId());
    DocumentContext json = JsonPath.parse(updateAdminUserRequestJson);
    updateAdminUserRequestJson =
        json.set("$.apps[0].id", appEntity.getId())
            .set("$.apps[0].selected", true)
            .set("$.apps[0].studies[0].studyId", studyEntity.getId())
            .set("$.apps[0].studies[0].selected", true)
            .set("$.apps[0].studies[0].sites[0].siteId", siteEntity.getId())
            .set("$.apps[0].studies[0].sites[0].selected", true)
            .set("$.id", adminforUpdate.getId())
            .jsonString();
    mockMvc
        .perform(
            put(ApiEndpoint.UPDATE_USER.getPath(), adminforUpdate.getId())
                .content(updateAdminUserRequestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value(MessageCode.UPDATE_USER_SUCCESS.getMessage()))
        .andExpect(jsonPath("$.userId", notNullValue()));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userRegAdminEntity.getId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(ADMIN_USER_RECORD_UPDATED.getEventCode(), auditRequest);
    verifyAuditEventCall(auditEventMap, ADMIN_USER_RECORD_UPDATED);

    // Step 3: verify updated values
    assertAdminDetails(adminforUpdate.getId(), false);
    assertSitePermissionDetails(adminforUpdate.getId());
    assertStudyPermissionDetails(adminforUpdate.getId());
    assertAppPermissionDetails(adminforUpdate.getId());

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnUserNotFoundErrorForUpdateUser() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, userRegAdminEntity.getId());
    UserRequest userRequest = newUserRequestForUpdate();
    userRequest.setSuperAdmin(false);
    mockMvc
        .perform(
            put(ApiEndpoint.UPDATE_USER.getPath(), IdGenerator.id())
                .content(asJsonString(userRequest))
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath("$.error_description").value(ErrorCode.USER_NOT_FOUND.getDescription()));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnNotSuperAdminAccessErrorForUpdateUser() throws Exception {
    userRegAdminEntity = testDataHelper.createNonSuperAdmin();
    adminforUpdate = testDataHelper.createSuperAdmin();
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, userRegAdminEntity.getId());
    UserRequest userRequest = newUserRequestForUpdate();
    userRequest.setId(adminforUpdate.getId());
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

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnBadRequestForMissingParameter() throws Exception {
    // Step 1: Setting last name as empty
    UserRequest userRequest = newUserRequestForUpdate();
    userRequest.setLastName("");

    // Step 2: Call the API and expect must not be blank
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, userRegAdminEntity.getId());
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

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnPermissionMissingErrorForUpdateUser() throws Exception {
    adminforUpdate = testDataHelper.createSuperAdmin();
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, userRegAdminEntity.getId());
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

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnAdminRecordsWithoutAppStudySitePermissionForGetAdminDetailsAndApps()
      throws Exception {
    // Step 1: Set few admins
    UserRegAdminEntity admin = testDataHelper.createNonSuperAdmin();
    testDataHelper.createAppPermission(admin, appEntity, userRegAdminEntity.getId());
    testDataHelper.createStudyPermission(admin, appEntity, studyEntity, userRegAdminEntity.getId());
    testDataHelper.createSitePermission(
        admin, appEntity, studyEntity, siteEntity, userRegAdminEntity.getId());

    // Step 2: Call API and expect MANAGE_USERS_SUCCESS message
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, userRegAdminEntity.getId());
    mockMvc
        .perform(
            get(ApiEndpoint.GET_ADMIN_DETAILS_AND_APPS.getPath(), admin.getId())
                .headers(headers)
                .queryParam("includeUnselected", String.valueOf(false))
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.user.id", is(admin.getId())))
        .andExpect(jsonPath("$.user.apps").isArray())
        .andExpect(jsonPath("$.user.apps").isNotEmpty())
        .andExpect(jsonPath("$.user.apps[0].totalStudiesCount", is(1)))
        .andExpect(jsonPath("$.user.apps[0].selectedStudiesCount", is(1)))
        .andExpect(jsonPath("$.user.apps[0].totalSitesCount", is(1)))
        .andExpect(jsonPath("$.user.apps[0].selectedSitesCount", is(1)))
        .andExpect(jsonPath("$.message", is(MessageCode.GET_ADMIN_DETAILS_SUCCESS.getMessage())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnAdminRecordsWithAppStudySiteForGetAdminDetailsAndApps() throws Exception {
    // Step 1: Set one admin
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
        .andExpect(jsonPath("$.user.email", is(superAdmin.getEmail())))
        .andExpect(jsonPath("$.user.firstName", is(superAdmin.getFirstName())))
        .andExpect(jsonPath("$.user.lastName", is(superAdmin.getLastName())))
        .andExpect(jsonPath("$.user.superAdmin", is(true)))
        .andExpect(jsonPath("$.user.status", is(UserStatus.ACTIVE.getDescription())))
        .andExpect(jsonPath("$.message", is(MessageCode.GET_ADMIN_DETAILS_SUCCESS.getMessage())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnAdminRecordsWithAppStudyForGetAdminDetailsAndApps() throws Exception {
    // Step 1: Set one admin
    UserRegAdminEntity admin = testDataHelper.createNonSuperAdmin();

    List<StudyEntity> studyList = testDataHelper.createMultipleStudyEntity(appEntity);
    // delete all permissions
    testDataHelper.getSitePermissionRepository().deleteAll();
    testDataHelper.getStudyPermissionRepository().deleteAll();
    testDataHelper.getAppPermissionRepository().deleteAll();

    // assign only study permission which has no sites added
    testDataHelper.createStudyPermission(
        admin, appEntity, studyList.get(0), userRegAdminEntity.getId());

    // Step 2: Call API and expect MANAGE_USERS_SUCCESS message
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, userRegAdminEntity.getId());
    mockMvc
        .perform(
            get(ApiEndpoint.GET_ADMIN_DETAILS_AND_APPS.getPath(), admin.getId())
                .headers(headers)
                .queryParam("includeUnselected", String.valueOf(true))
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.user.id", is(admin.getId())))
        .andExpect(jsonPath("$.user.apps").isArray())
        .andExpect(jsonPath("$.user.apps").isNotEmpty())
        .andExpect(jsonPath("$.user.apps[0].totalStudiesCount", is(3)))
        .andExpect(jsonPath("$.user.apps[0].selectedStudiesCount", is(1)))
        .andExpect(jsonPath("$.user.apps[0].totalSitesCount", is(1)))
        .andExpect(jsonPath("$.user.apps[0].selectedSitesCount", is(0)))
        .andExpect(jsonPath("$.message", is(MessageCode.GET_ADMIN_DETAILS_SUCCESS.getMessage())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldNotReturnAnyAppForGetAdminDetailsAndApps() throws Exception {
    // Step 1: Set one admin without assigning any permission
    UserRegAdminEntity admin = testDataHelper.createNonSuperAdmin();

    testDataHelper.getSitePermissionRepository().deleteAll();
    testDataHelper.getStudyPermissionRepository().deleteAll();
    testDataHelper.getAppPermissionRepository().deleteAll();

    // Step 2: Call API and expect MANAGE_USERS_SUCCESS message
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, userRegAdminEntity.getId());
    mockMvc
        .perform(
            get(ApiEndpoint.GET_ADMIN_DETAILS_AND_APPS.getPath(), admin.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.user.id", is(admin.getId())))
        .andExpect(jsonPath("$.user.apps").isArray())
        .andExpect(jsonPath("$.user.apps").isEmpty())
        .andExpect(jsonPath("$.message", is(MessageCode.GET_ADMIN_DETAILS_SUCCESS.getMessage())));

    verifyTokenIntrospectRequest();
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
            jsonPath("$.error_description").value(ErrorCode.ADMIN_NOT_FOUND.getDescription()));

    verifyTokenIntrospectRequest();
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

    verifyTokenIntrospectRequest();
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

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnUsersForPagination() throws Exception {
    // Step 1: 1 user already added in @BeforeEach, Add 20 new users
    for (int i = 1; i <= 20; i++) {
      userRegAdminEntity = testDataHelper.newSuperAdminForUpdate();
      userRegAdminEntity.setEmail(String.valueOf(i) + EMAIL_VALUE);
      userRegAdminRepository.saveAndFlush(userRegAdminEntity);
      Thread.sleep(5);
    }

    // Step 2: Call API and expect GET_ADMINS_SUCCESS message and fetch only 1 data out of 21
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            get(ApiEndpoint.GET_USERS.getPath())
                .headers(headers)
                .queryParam("limit", "20")
                .queryParam("offset", "0")
                .param("sortBy", "email")
                .param("sortDirection", "asc")
                .param("searchTerm", "10")
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.users").isArray())
        .andExpect(jsonPath("$.users", hasSize(1)))
        .andExpect(jsonPath("$.totalUsersCount", is(1)))
        .andExpect(jsonPath("$.users[0].apps").isArray())
        .andExpect(jsonPath("$.users[0].apps").isEmpty())
        .andExpect(jsonPath("$.message", is(MessageCode.GET_USERS_SUCCESS.getMessage())))
        .andExpect(jsonPath("$.users[0].email", is(String.valueOf(10) + EMAIL_VALUE)));

    verifyTokenIntrospectRequest();

    // get users for default values
    mockMvc
        .perform(
            get(ApiEndpoint.GET_USERS.getPath()).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.users").isArray())
        .andExpect(jsonPath("$.users", hasSize(10)))
        .andExpect(jsonPath("$.totalUsersCount", is(21)))
        .andExpect(jsonPath("$.users[0].apps").isArray())
        .andExpect(jsonPath("$.users[0].apps").isEmpty())
        .andExpect(jsonPath("$.message", is(MessageCode.GET_USERS_SUCCESS.getMessage())))
        .andExpect(jsonPath("$.users[0].email", is(EMAIL_VALUE)));

    verifyTokenIntrospectRequest(2);
  }

  @Test
  public void shouldReturnAdminsForGetUsers() throws Exception {
    // Step 1: Set few users
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
        .andExpect(jsonPath("$.totalUsersCount", is(3)))
        .andExpect(jsonPath("$.users[0].apps").isArray())
        .andExpect(jsonPath("$.users[0].apps").isEmpty())
        .andExpect(jsonPath("$.message", is(MessageCode.GET_USERS_SUCCESS.getMessage())))
        .andExpect(jsonPath("$.users..email", hasItem(TestDataHelper.SUPER_ADMIN_EMAIL_ID)))
        .andExpect(jsonPath("$.users..email", hasItem(TestDataHelper.NON_SUPER_ADMIN_EMAIL_ID)))
        .andExpect(jsonPath("$.users..email", hasItem(TestDataHelper.EMAIL_VALUE)));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userRegAdminEntity.getId());
    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(USER_REGISTRY_VIEWED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, USER_REGISTRY_VIEWED);

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnUserNotFoundErrorForGetUsers() throws Exception {
    // Step 1: Call API and expect USER_NOT_FOUND error
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, IdGenerator.id());
    mockMvc
        .perform(
            get(ApiEndpoint.GET_USERS.getPath()).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath("$.error_description").value(ErrorCode.ADMIN_NOT_FOUND.getDescription()));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnNotSuperAdminAccessForGetUsers() throws Exception {
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

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldSendInvitationEmail() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(CommonConstants.USER_ID_HEADER, userRegAdminEntity.getId());

    // Step 1: change the security code expire date to before current date
    UserRegAdminEntity user = new UserRegAdminEntity();
    user.setEmail(TestConstants.EMAIL_ID);
    user.setFirstName(TestConstants.FIRST_NAME);
    user.setSecurityCodeExpireDate(
        new Timestamp(Instant.now().minus(1, ChronoUnit.DAYS).toEpochMilli()));
    testDataHelper.getUserRegAdminRepository().save(user);

    // Step 2: Call the API and expect RESEND_INVITATION_SENT_SUCCESSFULLY message
    MvcResult result =
        mockMvc
            .perform(
                post(ApiEndpoint.SEND_INVITATION_EMAIL.getPath(), user.getId())
                    .headers(headers)
                    .contextPath(getContextPath()))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(
                jsonPath("$.message").value(MessageCode.INVITATION_SENT_SUCCESSFULLY.getMessage()))
            .andExpect(jsonPath("$.userId", notNullValue()))
            .andReturn();

    String userId = JsonPath.read(result.getResponse().getContentAsString(), "$.userId");
    Optional<UserRegAdminEntity> optUserRegAdminEntity = userRegAdminRepository.findById(userId);
    UserRegAdminEntity userReg = optUserRegAdminEntity.get();

    // Step 3: verify saved values
    assertEquals(TestConstants.EMAIL_ID, userReg.getEmail());
    assertEquals(TestConstants.FIRST_NAME, userReg.getFirstName());
    assertTrue(
        new Timestamp(Instant.now().toEpochMilli()).before(userReg.getSecurityCodeExpireDate()));
    assertNotNull(userReg.getSecurityCode());

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userReg.getId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(RESEND_INVITATION.getEventCode(), auditRequest);
    verifyAuditEventCall(auditEventMap, RESEND_INVITATION);
  }

  @Test
  public void shouldReturnUserNotFoundForSendInvitationEmail() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(CommonConstants.USER_ID_HEADER, IdGenerator.id());

    // Call the API and expect USER_NOT_FOUND message
    mockMvc
        .perform(
            post(ApiEndpoint.SEND_INVITATION_EMAIL.getPath(), userRegAdminEntity.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath("$.error_description").value(ErrorCode.ADMIN_NOT_FOUND.getDescription()));
  }

  @Test
  public void shouldReturnNotSuperAdminError() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(CommonConstants.USER_ID_HEADER, userRegAdminEntity.getId());

    // Set super admin to false
    userRegAdminEntity.setSuperAdmin(false);
    testDataHelper.getUserRegAdminRepository().saveAndFlush(userRegAdminEntity);

    // Call the API and expect NOT_SUPER_ADMIN_ACCESS message
    mockMvc
        .perform(
            post(ApiEndpoint.SEND_INVITATION_EMAIL.getPath(), IdGenerator.id())
                .headers(headers)
                .contextPath(getContextPath()))
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

  private void assertAppPermissionDetailsForSuperAdmin(String userId) {
    List<AppPermissionEntity> appPermissions = appPermissionRepository.findByAdminUserId(userId);
    assertEquals(0, appPermissions.size());
  }

  private void assertStudyPermissionDetailsForSuperAdmin(String userId) {
    List<StudyPermissionEntity> studyPermissions =
        studyPermissionRepository.findByAdminUserId(userId);
    assertEquals(0, studyPermissions.size());
  }

  private void assertSitePermissionDetailsForSuperAdmin(String userId) {
    List<SitePermissionEntity> sitePermissions =
        sitePermissionRepository.findSitePermissionByUserId(userId);
    assertEquals(0, sitePermissions.size());
  }

  @AfterEach
  public void clean() {
    testDataHelper.cleanUp();
  }
}
