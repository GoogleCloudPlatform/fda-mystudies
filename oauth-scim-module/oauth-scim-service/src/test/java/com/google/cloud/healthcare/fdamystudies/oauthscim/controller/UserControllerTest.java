/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.google.cloud.healthcare.fdamystudies.common.EncryptionUtils.encrypt;
import static com.google.cloud.healthcare.fdamystudies.common.EncryptionUtils.hash;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.asJsonString;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.getTextValue;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.readJsonFile;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.toJsonNode;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.CHANGE_PASSWORD;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.EXPIRE_TIMESTAMP;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.FORGOT_PASSWORD;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.HASH;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.PASSWORD;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.PASSWORD_HISTORY;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.REFRESH_TOKEN;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.SALT;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.ContainsPattern;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateUserRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UserResponse;
import com.google.cloud.healthcare.fdamystudies.common.AuditLogEventStatus;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.IdGenerator;
import com.google.cloud.healthcare.fdamystudies.common.TextEncryptor;
import com.google.cloud.healthcare.fdamystudies.common.UserAccountStatus;
import com.google.cloud.healthcare.fdamystudies.model.AuditEventEntity;
import com.google.cloud.healthcare.fdamystudies.oauthscim.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent;
import com.google.cloud.healthcare.fdamystudies.oauthscim.model.UserEntity;
import com.google.cloud.healthcare.fdamystudies.oauthscim.repository.UserRepository;
import com.google.cloud.healthcare.fdamystudies.oauthscim.service.UserService;
import com.google.cloud.healthcare.fdamystudies.repository.AuditEventRepository;
import com.jayway.jsonpath.JsonPath;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;
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

public class UserControllerTest extends BaseMockIT {

  private static final String APP_ID_VALUE = "MyStudies";

  private static final String EMAIL_VALUE = "mockit_oauth_scim_user@grr.la";

  private static final String CURRENT_PASSWORD_VALUE = "M0ck!tPassword";

  private static final String NEW_PASSWORD_VALUE = "M0ck!tPassword2";

  @Autowired private UserRepository repository;

  @Autowired private AuditEventRepository auditEventRepository;

  @Autowired private TextEncryptor encryptor;

  @Autowired private UserService userService;

  private static UserEntity userEntity;

  @Autowired private UserRepository userRepository;

  @BeforeEach
  public void setUp() {
    WireMock.resetAllRequests();

    // create a user
    UserResponse userResponse = userService.createUser(newUserRequest());
    userEntity = userRepository.findByUserId(userResponse.getUserId()).get();
  }

  @Test
  public void shouldReturnUnauthorized() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", INVALID_BEARER_TOKEN);

    performPost(
        ApiEndpoint.USERS.getPath(),
        asJsonString(newUserRequest()),
        headers,
        "Invalid token",
        UNAUTHORIZED);

    verify(
        1,
        postRequestedFor(urlEqualTo("/oauth-scim-service/oauth2/introspect"))
            .withRequestBody(new ContainsPattern(INVALID_TOKEN)));
  }

  @Test
  public void shouldReturnBadRequestForInvalidContent() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);

    UserRequest userRequest = new UserRequest();
    userRequest.setPassword("example_password");
    userRequest.setEmail("example.com");
    MvcResult result =
        mockMvc
            .perform(
                post(ApiEndpoint.USERS.getPath())
                    .contextPath(getContextPath())
                    .content(asJsonString(userRequest))
                    .headers(headers))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.violations").isArray())
            .andReturn();

    String actualResponse = result.getResponse().getContentAsString();
    String expectedResponse = readJsonFile("/response/create_user_bad_request.json");
    JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.NON_EXTENSIBLE);

    verify(
        1,
        postRequestedFor(urlEqualTo("/oauth-scim-service/oauth2/introspect"))
            .withRequestBody(new ContainsPattern(VALID_TOKEN)));
  }

  @Test
  public void shouldCreateANewUser() throws Exception {
    // Step-0: remove any users created in setUp()
    userRepository.deleteAll();

    // Step-1 call API to create an user account in oauth scim database
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);

    UserRequest request = newUserRequest();

    MvcResult result =
        mockMvc
            .perform(
                post(ApiEndpoint.USERS.getPath())
                    .contextPath(getContextPath())
                    .content(asJsonString(request))
                    .headers(headers))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.tempRegId").isNotEmpty())
            .andExpect(jsonPath("$.userId").isNotEmpty())
            .andReturn();

    // Step-2 Find UserEntity by userId and compare with UserRequest object
    String userId = JsonPath.read(result.getResponse().getContentAsString(), "$.userId");
    UserEntity userEntity = repository.findByUserId(userId).get();
    assertNotNull(userEntity);
    assertEquals(request.getEmail(), userEntity.getEmail());

    // Step 2A- assert password and password_history fields
    JsonNode userInfo = toJsonNode(userEntity.getUserInfo());
    assertTrue(userInfo.get(PASSWORD).get(EXPIRE_TIMESTAMP).isLong());
    assertTrue(userInfo.get(PASSWORD_HISTORY).isArray());

    verify(
        1,
        postRequestedFor(urlEqualTo("/oauth-scim-service/oauth2/introspect"))
            .withRequestBody(new ContainsPattern(VALID_TOKEN)));
  }

  @Test
  public void shouldReturnEmailExistsErrorCode() throws Exception {
    // Step-1 call API to create an user account in oauth scim database
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);

    UserRequest request = newUserRequest();

    mockMvc
        .perform(
            post(ApiEndpoint.USERS.getPath())
                .contextPath(getContextPath())
                .content(asJsonString(request))
                .headers(headers))
        .andDo(print())
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.userId").doesNotExist())
        .andExpect(jsonPath("$.error_description").value(ErrorCode.EMAIL_EXISTS.getDescription()));

    verify(
        1,
        postRequestedFor(urlEqualTo("/oauth-scim-service/oauth2/introspect"))
            .withRequestBody(new ContainsPattern(VALID_TOKEN)));
  }

  @Test
  public void shouldReturnBadRequestForChangePasswordAction()
      throws MalformedURLException, JsonProcessingException, Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);

    // Step-1 assert validation errors from validation annotations
    UpdateUserRequest userRequest = new UpdateUserRequest();
    userRequest.setNewPassword("new_password");
    userRequest.setCurrentPassword("example_current_password");
    userRequest.setAction("password_reset");

    MvcResult result =
        mockMvc
            .perform(
                put(ApiEndpoint.USER.getPath(), IdGenerator.id())
                    .contextPath(getContextPath())
                    .content(asJsonString(userRequest))
                    .headers(headers))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.violations").isArray())
            .andReturn();

    String actualResponse = result.getResponse().getContentAsString();
    String expectedResponse =
        readJsonFile("/response/change_password_bad_request_response_from_annotations.json");
    JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.NON_EXTENSIBLE);

    verify(
        1,
        postRequestedFor(urlEqualTo("/oauth-scim-service/oauth2/introspect"))
            .withRequestBody(new ContainsPattern(VALID_TOKEN)));

    // Step-2 assert validation errors from UserRequestValidator
    userRequest = new UpdateUserRequest();
    userRequest.setAction(CHANGE_PASSWORD);

    result =
        mockMvc
            .perform(
                put(ApiEndpoint.USER.getPath(), IdGenerator.id())
                    .contextPath(getContextPath())
                    .content(asJsonString(userRequest))
                    .headers(headers))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.violations").isArray())
            .andReturn();

    actualResponse = result.getResponse().getContentAsString();
    expectedResponse = readJsonFile("/response/change_password_bad_request_from_validator.json");
    JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.NON_EXTENSIBLE);

    verify(
        2,
        postRequestedFor(urlEqualTo("/oauth-scim-service/oauth2/introspect"))
            .withRequestBody(new ContainsPattern(VALID_TOKEN)));
  }

  @Test
  public void shouldReturnCurrentPasswordInvalidErrroCodeForChangePasswordAction()
      throws MalformedURLException, JsonProcessingException, Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);

    UpdateUserRequest request = new UpdateUserRequest();
    request.setAction(CHANGE_PASSWORD);
    request.setCurrentPassword("CurrentM0ck!tPassword");
    request.setNewPassword("NewM0ck!tPassword");

    mockMvc
        .perform(
            put(ApiEndpoint.USER.getPath(), userEntity.getUserId())
                .contextPath(getContextPath())
                .content(asJsonString(request))
                .headers(headers))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.error_description")
                .value(ErrorCode.CURRENT_PASSWORD_INVALID.getDescription()));

    verify(
        1,
        postRequestedFor(urlEqualTo("/oauth-scim-service/oauth2/introspect"))
            .withRequestBody(new ContainsPattern(VALID_TOKEN)));
  }

  @Test
  public void shouldReturnUserNotFoundErrroCodeForChangePasswordAction()
      throws MalformedURLException, JsonProcessingException, Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);

    UpdateUserRequest request = new UpdateUserRequest();
    request.setAction(CHANGE_PASSWORD);
    request.setCurrentPassword(CURRENT_PASSWORD_VALUE);
    request.setNewPassword(NEW_PASSWORD_VALUE);

    mockMvc
        .perform(
            put(ApiEndpoint.USER.getPath(), IdGenerator.id())
                .contextPath(getContextPath())
                .content(asJsonString(request))
                .headers(headers))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath("$.error_description").value(ErrorCode.USER_NOT_FOUND.getDescription()));

    verify(
        1,
        postRequestedFor(urlEqualTo("/oauth-scim-service/oauth2/introspect"))
            .withRequestBody(new ContainsPattern(VALID_TOKEN)));
  }

  @Test
  public void shouldChangeThePassword()
      throws MalformedURLException, JsonProcessingException, Exception {
    // Step-1 Call PUT method to change the password
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);

    UpdateUserRequest request = new UpdateUserRequest();
    request.setAction(CHANGE_PASSWORD);
    request.setCurrentPassword(CURRENT_PASSWORD_VALUE);
    request.setNewPassword(NEW_PASSWORD_VALUE);

    mockMvc
        .perform(
            put(ApiEndpoint.USER.getPath(), userEntity.getUserId())
                .contextPath(getContextPath())
                .content(asJsonString(request))
                .headers(headers))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Your password has been changed successfully!"));

    // Step-2 Find UserEntity by userId and then compare the password hash values
    userEntity = repository.findByUserId(userEntity.getUserId()).get();
    assertNotNull(userEntity);

    // Step 2A- assert password hash value and password_history size
    JsonNode userInfoNode = toJsonNode(userEntity.getUserInfo());
    JsonNode passwordNode = userInfoNode.get(PASSWORD);
    String salt = getTextValue(passwordNode, SALT);
    String actualPasswordHash = getTextValue(passwordNode, HASH);
    String expectedPasswordHash = hash(encrypt(NEW_PASSWORD_VALUE, salt));

    assertEquals(expectedPasswordHash, actualPasswordHash);
    assertTrue(userInfoNode.get(PASSWORD_HISTORY).isArray());
    assertTrue(userInfoNode.get(PASSWORD_HISTORY).size() == 2);

    verify(
        1,
        postRequestedFor(urlEqualTo("/oauth-scim-service/oauth2/introspect"))
            .withRequestBody(new ContainsPattern(VALID_TOKEN)));
  }

  @Test
  public void shouldReturnEnforcePasswordHistoryErrroCodeForChangePasswordAction()
      throws MalformedURLException, JsonProcessingException, Exception {
    // Step-1 call API to change the password
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);

    UpdateUserRequest request = new UpdateUserRequest();
    request.setAction(CHANGE_PASSWORD);
    request.setCurrentPassword(CURRENT_PASSWORD_VALUE);
    request.setNewPassword(NEW_PASSWORD_VALUE);

    mockMvc
        .perform(
            put(ApiEndpoint.USER.getPath(), userEntity.getUserId())
                .contextPath(getContextPath())
                .content(asJsonString(request))
                .headers(headers))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Your password has been changed successfully!"));

    // Step-2 call change password with previously used password and expect ENFORCE_PASSWORD_HISTORY
    request = new UpdateUserRequest();
    request.setAction(CHANGE_PASSWORD);
    request.setCurrentPassword(NEW_PASSWORD_VALUE);
    request.setNewPassword(CURRENT_PASSWORD_VALUE);

    mockMvc
        .perform(
            put(ApiEndpoint.USER.getPath(), userEntity.getUserId())
                .contextPath(getContextPath())
                .content(asJsonString(request))
                .headers(headers))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.error_description")
                .value(ErrorCode.ENFORCE_PASSWORD_HISTORY.getDescription()));

    verify(
        2,
        postRequestedFor(urlEqualTo("/oauth-scim-service/oauth2/introspect"))
            .withRequestBody(new ContainsPattern(VALID_TOKEN)));
  }

  @Test
  public void shouldReturnBadRequestForForgotPasswordAction()
      throws MalformedURLException, JsonProcessingException, Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);

    UpdateUserRequest userRequest = new UpdateUserRequest();
    userRequest.setAction(FORGOT_PASSWORD);

    MvcResult result =
        mockMvc
            .perform(
                put(ApiEndpoint.RESET_PASSWORD.getPath())
                    .contextPath(getContextPath())
                    .content(asJsonString(userRequest))
                    .headers(headers))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.violations").isArray())
            .andReturn();

    String actualResponse = result.getResponse().getContentAsString();
    String expectedResponse = readJsonFile("/response/forgot_password_bad_request.json");

    JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.NON_EXTENSIBLE);

    verify(
        1,
        postRequestedFor(urlEqualTo("/oauth-scim-service/oauth2/introspect"))
            .withRequestBody(new ContainsPattern(VALID_TOKEN)));
  }

  @Test
  public void shouldSendPasswordResetEmailAndUpdateThePassword()
      throws MalformedURLException, JsonProcessingException, Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);
    headers.add("correlationId", "CorrelationIdValue_For_2XX_Success");

    UpdateUserRequest userRequest = new UpdateUserRequest();
    userRequest.setAction(FORGOT_PASSWORD);
    userRequest.setEmail(EMAIL_VALUE);
    userRequest.setAppId(APP_ID_VALUE);

    mockMvc
        .perform(
            put(ApiEndpoint.RESET_PASSWORD.getPath())
                .contextPath(getContextPath())
                .content(asJsonString(userRequest))
                .headers(headers))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Password reset successful"));

    // Step-2 Find UserEntity by userId and then compare the password hash values
    userEntity = repository.findByUserId(userEntity.getUserId()).get();
    assertNotNull(userEntity);
    assertEquals(EMAIL_VALUE, userEntity.getEmail());
    assertEquals(APP_ID_VALUE, userEntity.getAppId());

    // Step 2A- assert password hash value and password_history size
    JsonNode userInfoNode = toJsonNode(userEntity.getUserInfo());
    JsonNode passwordNode = userInfoNode.get(PASSWORD);
    String salt = getTextValue(passwordNode, SALT);
    String actualPasswordHash = getTextValue(passwordNode, HASH);
    String expectedPasswordHash = hash(encrypt(NEW_PASSWORD_VALUE, salt));

    assertNotEquals(expectedPasswordHash, actualPasswordHash);
    assertTrue(userInfoNode.get(PASSWORD_HISTORY).isArray());
    assertTrue(userInfoNode.get(PASSWORD_HISTORY).size() == 2);

    verify(
        1,
        postRequestedFor(urlEqualTo("/oauth-scim-service/oauth2/introspect"))
            .withRequestBody(new ContainsPattern(VALID_TOKEN)));

    verify(
        1,
        postRequestedFor(urlEqualTo("/audit-log-service/events"))
            .withRequestBody(new ContainsPattern("CorrelationIdValue_For_2XX_Success")));
  }

  @Test
  public void shouldSaveTheAuditEventInDatabase()
      throws MalformedURLException, JsonProcessingException, Exception {
    // Step-1 call API to create an user account in oauth scim database
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);
    headers.add("correlationId", "CorrelationIdValue_For_5XX_ERROR");

    // Step-2 reset the password
    UpdateUserRequest userRequest = new UpdateUserRequest();
    userRequest.setAction(FORGOT_PASSWORD);
    userRequest.setEmail(EMAIL_VALUE);
    userRequest.setAppId(APP_ID_VALUE);

    mockMvc
        .perform(
            put(ApiEndpoint.RESET_PASSWORD.getPath())
                .contextPath(getContextPath())
                .content(asJsonString(userRequest))
                .headers(headers))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Password reset successful"));

    verify(
        1,
        postRequestedFor(urlEqualTo("/audit-log-service/events"))
            .withRequestBody(new ContainsPattern("CorrelationIdValue_For_5XX_ERROR")));

    verify(
        1,
        postRequestedFor(urlEqualTo("/oauth-scim-service/oauth2/introspect"))
            .withRequestBody(new ContainsPattern(VALID_TOKEN)));

    // Step-3 verify PASSWORD_RESET_SUCCESS event saved in database
    List<AuditEventEntity> events =
        auditEventRepository.findByStatus(
            AuditLogEventStatus.NOT_RECORDED_AT_CENTRAL_AUDIT_LOG.getStatus());

    AuditEventEntity eventEntity =
        events
            .stream()
            .filter(
                event ->
                    StringUtils.contains(event.getEventRequest(), userEntity.getUserId())
                        && StringUtils.contains(
                            event.getEventRequest(),
                            AuthScimEvent.PASSWORD_RESET_SUCCESS.getEventName()))
            .findAny()
            .orElse(null);

    assertNotNull(eventEntity);
  }

  @Test
  public void shouldLogout() throws MalformedURLException, JsonProcessingException, Exception {
    // Step-1 set the refresh token for the user
    ObjectNode userInfo = (ObjectNode) toJsonNode(userEntity.getUserInfo());
    userInfo.put(REFRESH_TOKEN, encryptor.encrypt(VALID_TOKEN));
    userEntity.setUserInfo(userInfo.toString());
    userEntity = repository.saveAndFlush(userEntity);

    // Step-2 call logout api
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);
    headers.add("correlationId", IdGenerator.id());

    // Step-2 call logout api
    UpdateUserRequest userRequest = new UpdateUserRequest();
    userRequest.setAction(FORGOT_PASSWORD);
    userRequest.setEmail(EMAIL_VALUE);
    userRequest.setAppId(APP_ID_VALUE);

    mockMvc
        .perform(
            post(ApiEndpoint.LOGOUT.getPath(), userEntity.getUserId())
                .contextPath(getContextPath())
                .headers(headers))
        .andDo(print())
        .andExpect(status().isOk());

    // Step-3 check the refresh token removed from database
    userEntity = repository.findByUserId(userEntity.getUserId()).get();
    userInfo = (ObjectNode) toJsonNode(userEntity.getUserInfo());
    assertFalse(userInfo.hasNonNull(REFRESH_TOKEN));
  }

  @AfterEach
  public void cleanUp() {
    userRepository.deleteAll();
  }

  private HttpHeaders getCommonHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  private UserRequest newUserRequest() {
    UserRequest userRequest = new UserRequest();
    userRequest.setAppId(APP_ID_VALUE);
    userRequest.setEmail(EMAIL_VALUE);
    userRequest.setPassword(CURRENT_PASSWORD_VALUE);
    userRequest.setStatus(UserAccountStatus.PENDING_CONFIRMATION.getStatus());
    return userRequest;
  }
}
