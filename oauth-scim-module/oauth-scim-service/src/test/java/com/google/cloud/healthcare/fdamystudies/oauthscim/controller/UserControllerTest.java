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
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.PASSWORD_REGEX_MESSAGE;
import static com.google.cloud.healthcare.fdamystudies.common.HashUtils.hash;
import static com.google.cloud.healthcare.fdamystudies.common.ErrorCode.ACCOUNT_LOCKED;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.asJsonString;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.getObjectNode;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.getTextValue;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.readJsonFile;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.ACCOUNT_LOCKED_PASSWORD;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.CORRELATION_ID;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.EXPIRE_TIMESTAMP;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.HASH;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.PASSWORD;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.PASSWORD_HISTORY;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.REFRESH_TOKEN;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.SALT;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.PASSWORD_CHANGE_FAILED;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.PASSWORD_CHANGE_SUCCEEDED;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.PASSWORD_HELP_EMAIL_SENT;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.PASSWORD_HELP_REQUESTED;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.PASSWORD_HELP_REQUESTED_FOR_UNREGISTERED_USERNAME;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.PASSWORD_RESET_EMAIL_SENT_FOR_LOCKED_ACCOUNT;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.PASSWORD_RESET_SUCCEEDED;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.USER_SIGNOUT_SUCCEEDED;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.ContainsPattern;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ChangePasswordRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ResetPasswordRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateEmailStatusRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UserResponse;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.common.CommonAuditEvent;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.IdGenerator;
import com.google.cloud.healthcare.fdamystudies.common.JsonUtils;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.common.PlaceholderReplacer;
import com.google.cloud.healthcare.fdamystudies.common.TextEncryptor;
import com.google.cloud.healthcare.fdamystudies.common.UserAccountStatus;
import com.google.cloud.healthcare.fdamystudies.oauthscim.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.oauthscim.config.AppPropertyConfig;
import com.google.cloud.healthcare.fdamystudies.oauthscim.model.UserEntity;
import com.google.cloud.healthcare.fdamystudies.oauthscim.repository.UserRepository;
import com.google.cloud.healthcare.fdamystudies.oauthscim.service.UserService;
import com.jayway.jsonpath.JsonPath;
import java.net.MalformedURLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.mail.internet.MimeMessage;
import org.apache.commons.collections4.map.HashedMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

public class UserControllerTest extends BaseMockIT {

  private static final String APP_ID_VALUE = "MyStudies";

  protected static final String VALID_CORRELATION_ID = "8a56d20c-d755-4487-b80d-22d5fa383046";

  private static final String EMAIL_VALUE = "mockit_oauth_scim_user@grr.la";

  private static final String CURRENT_PASSWORD_VALUE = "M0ck!tPassword";

  private static final String NEW_PASSWORD_VALUE = "M0ck!tPassword2";

  @Autowired private UserRepository repository;

  @Autowired private TextEncryptor encryptor;

  @Autowired private UserService userService;

  private static UserEntity userEntity;

  @Autowired private UserRepository userRepository;

  @Autowired private AppPropertyConfig appPropertyConfig;

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

    verifyAuditEventCall(CommonAuditEvent.ACCESS_TOKEN_INVALID_OR_EXPIRED);
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

  @ParameterizedTest
  @ValueSource(
      strings = {
        "test@b0ston",
        "TEST@B0STON",
        "TEST@BoSTON",
        "TEST@0a",
        "T3stB0ston",
        "Test @b0ston"
      })
  public void shouldReturnBadRequestForCreateUserAccount(String password)
      throws MalformedURLException, JsonProcessingException, Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);

    UserRequest request = newUserRequest();
    request.setPassword(password);

    mockMvc
        .perform(
            post(ApiEndpoint.USERS.getPath())
                .contextPath(getContextPath())
                .content(asJsonString(request))
                .headers(headers))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.violations").isArray())
        .andExpect(jsonPath("$.violations[0].path").value("password"))
        .andExpect(jsonPath("$.violations[0].message").value(PASSWORD_REGEX_MESSAGE));

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
            .andExpect(jsonPath("$.tempRegId").doesNotExist())
            .andExpect(jsonPath("$.userId").isNotEmpty())
            .andReturn();

    // Step-2 Find UserEntity by userId and compare with UserRequest object
    String userId = JsonPath.read(result.getResponse().getContentAsString(), "$.userId");
    UserEntity userEntity = repository.findByUserId(userId).get();
    assertNotNull(userEntity);
    assertEquals(request.getEmail(), userEntity.getEmail());

    // Step 2A- assert password and password_history fields
    JsonNode userInfo = userEntity.getUserInfo();
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
    headers.add("correlationId", IdGenerator.id());

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

  @ParameterizedTest
  @ValueSource(
      strings = {
        "test@b0ston",
        "TEST@B0STON",
        "TEST@BoSTON",
        "TEST@0a",
        "T3stB0ston",
        "Test @b0ston"
      })
  public void shouldReturnPasswordInvalidForChangePassword(String password)
      throws MalformedURLException, JsonProcessingException, Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);

    ChangePasswordRequest userRequest = new ChangePasswordRequest();
    userRequest.setNewPassword(password);
    userRequest.setCurrentPassword(password);

    MvcResult result =
        mockMvc
            .perform(
                put(ApiEndpoint.CHANGE_PASSWORD.getPath(), IdGenerator.id())
                    .contextPath(getContextPath())
                    .content(asJsonString(userRequest))
                    .headers(headers))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.violations").isArray())
            .andReturn();

    String actualResponse = result.getResponse().getContentAsString();
    String expectedResponse = readJsonFile("/response/password_regex_match_failed.json");
    JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.NON_EXTENSIBLE);

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

    ChangePasswordRequest userRequest = new ChangePasswordRequest();
    userRequest.setNewPassword("new_password");
    userRequest.setCurrentPassword("example_current_password");

    MvcResult result =
        mockMvc
            .perform(
                put(ApiEndpoint.CHANGE_PASSWORD.getPath(), IdGenerator.id())
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
  }

  @Test
  public void shouldReturnCurrentPasswordInvalidErrroCodeForChangePasswordAction()
      throws MalformedURLException, JsonProcessingException, Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);
    headers.add("correlationId", IdGenerator.id());

    ChangePasswordRequest request = new ChangePasswordRequest();
    request.setCurrentPassword("CurrentM0ck!tPassword");
    request.setNewPassword("NewM0ck!tPassword");

    mockMvc
        .perform(
            put(ApiEndpoint.CHANGE_PASSWORD.getPath(), userEntity.getUserId())
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

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userEntity.getUserId());
    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(PASSWORD_CHANGE_FAILED.getEventCode(), auditRequest);
    verifyAuditEventCall(auditEventMap, PASSWORD_CHANGE_FAILED);
  }

  @Test
  public void shouldReturnUserNotFoundErrroCodeForChangePasswordAction()
      throws MalformedURLException, JsonProcessingException, Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);
    headers.add("correlation_id", VALID_CORRELATION_ID);

    ChangePasswordRequest request = new ChangePasswordRequest();
    request.setCurrentPassword(CURRENT_PASSWORD_VALUE);
    request.setNewPassword(NEW_PASSWORD_VALUE);

    mockMvc
        .perform(
            put(ApiEndpoint.CHANGE_PASSWORD.getPath(), IdGenerator.id())
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
    // set the status to PASSWORD_RESET(3)
    userEntity.setStatus(UserAccountStatus.PASSWORD_RESET.getStatus());
    userEntity = repository.saveAndFlush(userEntity);

    // Step-1 Call PUT method to change the password
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);
    headers.add("correlationId", IdGenerator.id());

    ChangePasswordRequest request = new ChangePasswordRequest();
    request.setCurrentPassword(CURRENT_PASSWORD_VALUE);
    request.setNewPassword(NEW_PASSWORD_VALUE);

    mockMvc
        .perform(
            put(ApiEndpoint.CHANGE_PASSWORD.getPath(), userEntity.getUserId())
                .contextPath(getContextPath())
                .content(asJsonString(request))
                .headers(headers))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Your password has been changed successfully"));

    // Step-2 Find UserEntity by userId and then compare the password hash values
    userEntity = repository.findByUserId(userEntity.getUserId()).get();
    assertNotNull(userEntity);
    assertTrue(userEntity.getStatus() == UserAccountStatus.ACTIVE.getStatus());

    // Step 2A- assert password hash value and password_history size
    JsonNode userInfoNode = userEntity.getUserInfo();
    JsonNode passwordNode = userInfoNode.get(PASSWORD);
    String salt = getTextValue(passwordNode, SALT);
    String actualPasswordHash = getTextValue(passwordNode, HASH);
    String expectedPasswordHash = hash(NEW_PASSWORD_VALUE, salt);

    assertEquals(expectedPasswordHash, actualPasswordHash);
    assertTrue(userInfoNode.get(PASSWORD_HISTORY).isArray());
    assertTrue(userInfoNode.get(PASSWORD_HISTORY).size() == 2);

    verify(
        1,
        postRequestedFor(urlEqualTo("/oauth-scim-service/oauth2/introspect"))
            .withRequestBody(new ContainsPattern(VALID_TOKEN)));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userEntity.getUserId());
    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(PASSWORD_CHANGE_SUCCEEDED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, PASSWORD_CHANGE_SUCCEEDED);
  }

  @Test
  public void shouldReturnEnforcePasswordHistoryErrroCodeForChangePasswordAction()
      throws MalformedURLException, JsonProcessingException, Exception {

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userEntity.getUserId());
    // Step-0 change the password
    ChangePasswordRequest userRequest = new ChangePasswordRequest();
    userRequest.setCurrentPassword(CURRENT_PASSWORD_VALUE);
    userRequest.setNewPassword(NEW_PASSWORD_VALUE);
    userRequest.setUserId(userEntity.getUserId());
    userService.changePassword(userRequest, auditRequest);

    // Step-1 call API to change the password
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);

    ChangePasswordRequest request = new ChangePasswordRequest();
    request.setCurrentPassword(NEW_PASSWORD_VALUE);
    request.setNewPassword(CURRENT_PASSWORD_VALUE);

    mockMvc
        .perform(
            put(ApiEndpoint.CHANGE_PASSWORD.getPath(), userEntity.getUserId())
                .contextPath(getContextPath())
                .content(asJsonString(request))
                .headers(headers))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.error_description")
                .value(ErrorCode.ENFORCE_PASSWORD_HISTORY.getDescription()));

    verify(
        1,
        postRequestedFor(urlEqualTo("/oauth-scim-service/oauth2/introspect"))
            .withRequestBody(new ContainsPattern(VALID_TOKEN)));

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(PASSWORD_CHANGE_FAILED.getEventCode(), auditRequest);
    verifyAuditEventCall(auditEventMap, PASSWORD_CHANGE_FAILED);
  }

  @Test
  public void shouldReturnBadRequestForForgotPasswordAction()
      throws MalformedURLException, JsonProcessingException, Exception {
    HttpHeaders headers = getCommonHeaders();

    ResetPasswordRequest userRequest = new ResetPasswordRequest();

    MvcResult result =
        mockMvc
            .perform(
                post(ApiEndpoint.RESET_PASSWORD.getPath())
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
  }

  @Test
  public void shouldReturnAccountLockedError() throws Exception {

    userEntity.setStatus(UserAccountStatus.ACCOUNT_LOCKED.getStatus());
    JsonNode userInfo = userEntity.getUserInfo();
    String rawSalt = salt();
    String encrypted = encrypt(CURRENT_PASSWORD_VALUE, rawSalt);
    ObjectNode passwordNode = getObjectNode();
    passwordNode.put(HASH, hash(encrypted));
    passwordNode.put(SALT, rawSalt);
    passwordNode.put(EXPIRE_TIMESTAMP, Instant.now().plus(Duration.ofMinutes(15)).toEpochMilli());
    ((ObjectNode) userInfo).set(ACCOUNT_LOCKED_PASSWORD, passwordNode);
    userEntity.setUserInfo(userInfo);
    userEntity = userRepository.saveAndFlush(userEntity);

    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);
    headers.add("correlationId", "CorrelationIdValue_For_5XX_failure");

    ResetPasswordRequest userRequest = new ResetPasswordRequest();
    userRequest.setEmail(EMAIL_VALUE);
    userRequest.setAppId(APP_ID_VALUE);

    mockMvc
        .perform(
            post(ApiEndpoint.RESET_PASSWORD.getPath())
                .contextPath(getContextPath())
                .content(asJsonString(userRequest))
                .headers(headers))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(content().string(containsString(ACCOUNT_LOCKED.getDescription())));
  }

  @Test
  public void shouldReturnAccountNotVerifiedForForgotPasswordAction()
      throws MalformedURLException, JsonProcessingException, Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);

    ResetPasswordRequest userRequest = new ResetPasswordRequest();
    userRequest.setEmail(EMAIL_VALUE);
    userRequest.setAppId(APP_ID_VALUE);

    mockMvc
        .perform(
            post(ApiEndpoint.RESET_PASSWORD.getPath())
                .contextPath(getContextPath())
                .content(asJsonString(userRequest))
                .headers(headers))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(
            jsonPath("$.error_description").value(ErrorCode.ACCOUNT_NOT_VERIFIED.getDescription()));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userEntity.getUserId());
    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(PASSWORD_HELP_REQUESTED.getEventCode(), auditRequest);
    verifyAuditEventCall(auditEventMap, PASSWORD_HELP_REQUESTED);
  }

  @Test
  public void shouldReturnDeactivatedForForgotPasswordAction()
      throws MalformedURLException, JsonProcessingException, Exception {
    HttpHeaders headers = getCommonHeaders();
    // headers.add("Authorization", VALID_BEARER_TOKEN);

    userEntity.setStatus(UserAccountStatus.DEACTIVATED.getStatus());
    userRepository.saveAndFlush(userEntity);
    ResetPasswordRequest userRequest = new ResetPasswordRequest();
    userRequest.setEmail(EMAIL_VALUE);
    userRequest.setAppId(APP_ID_VALUE);

    mockMvc
        .perform(
            post(ApiEndpoint.RESET_PASSWORD.getPath())
                .contextPath(getContextPath())
                .content(asJsonString(userRequest))
                .headers(headers))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(
            jsonPath("$.error_description").value(ErrorCode.ACCOUNT_DEACTIVATED.getDescription()));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userEntity.getUserId());
    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(PASSWORD_HELP_REQUESTED.getEventCode(), auditRequest);
    verifyAuditEventCall(auditEventMap, PASSWORD_HELP_REQUESTED);
  }

  @Test
  public void shouldSendPasswordResetEmailAndUpdateThePassword()
      throws MalformedURLException, JsonProcessingException, Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);

    ResetPasswordRequest userRequest = new ResetPasswordRequest();
    userRequest.setEmail(EMAIL_VALUE);
    userRequest.setAppId(APP_ID_VALUE);
    userEntity.setStatus(UserAccountStatus.ACCOUNT_LOCKED.getStatus());
    userRepository.saveAndFlush(userEntity);

    mockMvc
        .perform(
            post(ApiEndpoint.RESET_PASSWORD.getPath())
                .contextPath(getContextPath())
                .content(asJsonString(userRequest))
                .headers(headers))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value(MessageCode.PASSWORD_RESET_SUCCESS.getMessage()));

    String subject = getMailResetSubject();
    String body = "Thank you for reaching out for password help for your account";

    MimeMessage mail =
        verifyMimeMessage(EMAIL_VALUE, appPropertyConfig.getFromEmail(), subject, body);
    verifyDoesNotContain(mail.getContent().toString(), "@tempPassword@");
    // Step-2 Find UserEntity by userId and then compare the password hash values
    userEntity = repository.findByUserId(userEntity.getUserId()).get();
    assertNotNull(userEntity);
    assertEquals(EMAIL_VALUE, userEntity.getEmail());
    assertEquals(APP_ID_VALUE, userEntity.getAppId());

    // Step 2A- assert password hash value and password_history size
    JsonNode userInfoNode = userEntity.getUserInfo();
    JsonNode passwordNode = userInfoNode.get(PASSWORD);
    String salt = getTextValue(passwordNode, SALT);
    String actualPasswordHash = getTextValue(passwordNode, HASH);
    String expectedPasswordHash = hash(NEW_PASSWORD_VALUE, salt);

    assertNotEquals(expectedPasswordHash, actualPasswordHash);

    assertTrue(userInfoNode.get(PASSWORD_HISTORY).size() == 2);

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userEntity.getUserId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(PASSWORD_HELP_REQUESTED.getEventCode(), auditRequest);
    auditEventMap.put(PASSWORD_RESET_EMAIL_SENT_FOR_LOCKED_ACCOUNT.getEventCode(), auditRequest);
    auditEventMap.put(PASSWORD_RESET_SUCCEEDED.getEventCode(), auditRequest);

    verifyAuditEventCall(
        auditEventMap,
        PASSWORD_HELP_REQUESTED,
        PASSWORD_RESET_SUCCEEDED,
        PASSWORD_RESET_EMAIL_SENT_FOR_LOCKED_ACCOUNT);
  }

  @Test
  public void shouldSendPasswordResetEmail()
      throws MalformedURLException, JsonProcessingException, Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);

    ResetPasswordRequest userRequest = new ResetPasswordRequest();
    userRequest.setEmail(EMAIL_VALUE);
    userRequest.setAppId(APP_ID_VALUE);
    userEntity.setStatus(UserAccountStatus.ACTIVE.getStatus());
    userRepository.saveAndFlush(userEntity);

    mockMvc
        .perform(
            post(ApiEndpoint.RESET_PASSWORD.getPath())
                .contextPath(getContextPath())
                .content(asJsonString(userRequest))
                .headers(headers))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value(MessageCode.PASSWORD_RESET_SUCCESS.getMessage()));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userEntity.getUserId());
    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(PASSWORD_HELP_EMAIL_SENT.getEventCode(), auditRequest);
    verifyAuditEventCall(auditEventMap, PASSWORD_HELP_EMAIL_SENT);
  }

  private String getMailResetSubject() {
    Map<String, String> templateArgs = new HashMap<>();
    return PlaceholderReplacer.replaceNamedPlaceholders(
        appPropertyConfig.getMailResetPasswordSubject(), templateArgs);
  }

  @Test
  public void testPasswordHelpRequestFailedforUnregUser()
      throws MalformedURLException, JsonProcessingException, Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);
    headers.add("correlationId", "CorrelationIdValue_For_5XX_failure");

    ResetPasswordRequest userRequest = new ResetPasswordRequest();
    userRequest.setEmail(EMAIL_VALUE);
    userRequest.setAppId("Invalid");

    mockMvc
        .perform(
            post(ApiEndpoint.RESET_PASSWORD.getPath())
                .contextPath(getContextPath())
                .content(asJsonString(userRequest))
                .headers(headers))
        .andDo(print())
        .andExpect(status().is4xxClientError());

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userEntity.getUserId());
    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(PASSWORD_HELP_REQUESTED.getEventCode(), auditRequest);
    auditEventMap.put(
        PASSWORD_HELP_REQUESTED_FOR_UNREGISTERED_USERNAME.getEventCode(), auditRequest);
    verifyAuditEventCall(
        auditEventMap, PASSWORD_HELP_REQUESTED, PASSWORD_HELP_REQUESTED_FOR_UNREGISTERED_USERNAME);
  }

  @Test
  public void shouldReturnInvalidUpdateUserRequestError()
      throws MalformedURLException, JsonProcessingException, Exception {
    // Step-1 create user
    UserEntity userEntity = newUserEntity();

    userEntity = repository.saveAndFlush(userEntity);

    // Step-2 call the API and expect INVALID_UPDATE_USER_REQUEST error
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);

    UpdateEmailStatusRequest userRequest = new UpdateEmailStatusRequest();

    mockMvc
        .perform(
            put(ApiEndpoint.UPDATE_EMAIL_STATUS.getPath(), userEntity.getUserId())
                .contextPath(getContextPath())
                .content(asJsonString(userRequest))
                .headers(headers))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.error_description")
                .value(ErrorCode.INVALID_UPDATE_USER_REQUEST.getDescription()));
  }

  @Test
  public void shouldUpdateEmail() throws MalformedURLException, JsonProcessingException, Exception {
    // Step-1 create user
    UserEntity userEntity = newUserEntity();
    userEntity = repository.saveAndFlush(userEntity);

    // Step-2 call the API and expect email is updated
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);

    final String EMAIL_2 = "mockit_oauth_scim_user_2@grr.la";
    UpdateEmailStatusRequest userRequest = new UpdateEmailStatusRequest();
    userRequest.setEmail(EMAIL_2);

    mockMvc
        .perform(
            put(ApiEndpoint.UPDATE_EMAIL_STATUS.getPath(), userEntity.getUserId())
                .contextPath(getContextPath())
                .content(asJsonString(userRequest))
                .headers(headers))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.tempRegId").doesNotExist())
        .andExpect(
            jsonPath("$.message").value(MessageCode.UPDATE_USER_DETAILS_SUCCESS.getMessage()));

    verify(
        1,
        postRequestedFor(urlEqualTo("/oauth-scim-service/oauth2/introspect"))
            .withRequestBody(new ContainsPattern(VALID_TOKEN)));

    // Step-3 verify updated email
    userEntity = repository.findByUserId(userEntity.getUserId()).get();
    assertEquals(EMAIL_2, userEntity.getEmail());
    assertEquals(UserAccountStatus.PENDING_CONFIRMATION.getStatus(), userEntity.getStatus());

    repository.deleteByUserId(userEntity.getUserId());
  }

  @Test
  public void shouldActivateUserAccount()
      throws MalformedURLException, JsonProcessingException, Exception {
    // Step-1 create user
    UserEntity userEntity = newUserEntity();
    userEntity = repository.saveAndFlush(userEntity);

    // Step-2 call the API and expect account status is ACTIVE
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);

    UpdateEmailStatusRequest userRequest = new UpdateEmailStatusRequest();
    userRequest.setStatus(UserAccountStatus.ACTIVE.getStatus());
    mockMvc
        .perform(
            put(ApiEndpoint.UPDATE_EMAIL_STATUS.getPath(), userEntity.getUserId())
                .contextPath(getContextPath())
                .content(asJsonString(userRequest))
                .headers(headers))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.message").value(MessageCode.UPDATE_USER_DETAILS_SUCCESS.getMessage()))
        .andExpect(jsonPath("$.tempRegId").isNotEmpty());
  }

  @Test
  public void shouldLogout() throws MalformedURLException, JsonProcessingException, Exception {
    // Step-1 set the refresh token for the user
    ObjectNode userInfo = (ObjectNode) userEntity.getUserInfo();
    userInfo.put(REFRESH_TOKEN, encryptor.encrypt(VALID_TOKEN));
    userEntity.setUserInfo(userInfo);
    userEntity = repository.saveAndFlush(userEntity);

    // Step-2 call logout api
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);
    headers.add("correlationId", IdGenerator.id());

    mockMvc
        .perform(
            post(ApiEndpoint.LOGOUT.getPath(), userEntity.getUserId())
                .contextPath(getContextPath())
                .headers(headers))
        .andDo(print())
        .andExpect(status().isOk());

    // Step-3 check the refresh token removed from database
    userEntity = repository.findByUserId(userEntity.getUserId()).get();
    userInfo = (ObjectNode) userEntity.getUserInfo();
    assertFalse(userInfo.hasNonNull(REFRESH_TOKEN));
    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userEntity.getUserId());
    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(USER_SIGNOUT_SUCCEEDED.getEventCode(), auditRequest);
    verifyAuditEventCall(auditEventMap, USER_SIGNOUT_SUCCEEDED);
  }

  @Test
  public void shouldReturnUserNotFoundForDeleteUserAccount()
      throws MalformedURLException, JsonProcessingException, Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);
    headers.add("correlationId", IdGenerator.id());

    mockMvc
        .perform(
            delete(ApiEndpoint.DELETE_USER.getPath(), IdGenerator.id())
                .contextPath(getContextPath())
                .headers(headers))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath("$.error_description").value(ErrorCode.USER_NOT_FOUND.getDescription()));
  }

  @Test
  public void shouldDeleteUserAccount()
      throws MalformedURLException, JsonProcessingException, Exception {
    // Step-1 call delete api
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);
    headers.add("correlationId", IdGenerator.id());

    mockMvc
        .perform(
            delete(ApiEndpoint.DELETE_USER.getPath(), userEntity.getUserId())
                .contextPath(getContextPath())
                .headers(headers))
        .andDo(print())
        .andExpect(status().isOk());

    // Step-2 find the user by userId and assert for null
    Optional<UserEntity> optUserEntity = repository.findByUserId(userEntity.getUserId());
    assertFalse(optUserEntity.isPresent());
  }

  @AfterEach
  public void cleanUp() {
    userRepository.deleteAll();
  }

  private HttpHeaders getCommonHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add(CORRELATION_ID, IdGenerator.id());
    headers.add("appVersion", "1.0");
    headers.add("appId", "SCIM AUTH SERVER");
    headers.add("userId", userEntity.getUserId());
    headers.add("studyId", "MyStudies");
    headers.add("source", "SCIM AUTH SERVER");
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

  private UserEntity newUserEntity() {
    UserEntity userEntity = new UserEntity();
    userEntity.setAppId(APP_ID_VALUE);
    userEntity.setEmail(EMAIL_VALUE);
    userEntity.setStatus(UserAccountStatus.PENDING_CONFIRMATION.getStatus());
    userEntity.setUserId(IdGenerator.id());

    ObjectNode userInfoNode = JsonUtils.getObjectNode();
    userEntity.setUserInfo(userInfoNode);
    return userEntity;
  }
}
