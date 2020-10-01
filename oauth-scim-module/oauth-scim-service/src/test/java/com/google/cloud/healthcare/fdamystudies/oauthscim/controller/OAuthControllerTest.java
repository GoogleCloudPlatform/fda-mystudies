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
import static com.google.cloud.healthcare.fdamystudies.common.CommonAuditEvent.INVALID_CLIENT_ID_OR_SECRET;
import static com.google.cloud.healthcare.fdamystudies.common.CommonAuditEvent.RESOURCE_ACCESS_FAILED;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.getObjectNode;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.getTextValue;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.readJsonFile;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.AUTHORIZATION;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.AUTHORIZATION_CODE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.CLIENT_CREDENTIALS;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.CLIENT_ID;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.CODE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.CODE_VERIFIER;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.CORRELATION_ID;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.GRANT_TYPE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.REDIRECT_URI;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.REFRESH_TOKEN;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.SCOPE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.TOKEN;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.USER_ID;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.ACCESS_TOKEN_INVALID_OR_EXPIRED;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.NEW_ACCESS_TOKEN_GENERATED;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.ContainsPattern;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.common.CommonAuditEvent;
import com.google.cloud.healthcare.fdamystudies.common.IdGenerator;
import com.google.cloud.healthcare.fdamystudies.common.PasswordGenerator;
import com.google.cloud.healthcare.fdamystudies.common.TextEncryptor;
import com.google.cloud.healthcare.fdamystudies.common.UserAccountStatus;
import com.google.cloud.healthcare.fdamystudies.oauthscim.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.oauthscim.model.UserEntity;
import com.google.cloud.healthcare.fdamystudies.oauthscim.repository.UserRepository;
import com.jayway.jsonpath.JsonPath;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.collections4.map.HashedMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class OAuthControllerTest extends BaseMockIT {

  protected static final String VALID_CORRELATION_ID = "8a56d20c-d755-4487-b80d-22d5fa383046";

  private static final String TEMP_REG_ID_VALUE = "ec2045a1-0cd3-4998-b515-7f9703dff5bf";

  @Value("${security.oauth2.hydra.client.client-id}")
  private String clientId;

  @Value("${security.oauth2.hydra.client.client-secret}")
  private String clientSecret;

  @Value("${security.oauth2.hydra.client.redirect-uri}")
  private String redirectUri;

  @Autowired private UserRepository userRepository;

  @Autowired private TextEncryptor encryptor;

  private UserEntity userEntity;

  @BeforeEach
  public void init() {
    WireMock.resetAllRequests();

    userEntity = newUserEntity();
    userEntity = userRepository.saveAndFlush(userEntity);
  }

  @Test
  public void shouldReturnBadRequestForInvalidClientCredentialsGrantRequest() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.add(AUTHORIZATION, VALID_BEARER_TOKEN);
    headers.add(CORRELATION_ID, VALID_CORRELATION_ID);
    headers.set("Authorization", getEncodedAuthorization(clientId, clientSecret));

    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add(CLIENT_ID, clientId);

    MvcResult result =
        mockMvc
            .perform(
                post(ApiEndpoint.TOKEN.getPath())
                    .contextPath(getContextPath())
                    .params(requestParams)
                    .headers(headers))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.violations").isArray())
            .andReturn();

    String actualResponse = result.getResponse().getContentAsString();
    String expectedResponse = readJsonFile("/response/client_credentials_grant_bad_request.json");
    JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void shouldReturnBadRequestForInvalidAuthorizationCodeGrantRequest() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.add(AUTHORIZATION, VALID_BEARER_TOKEN);
    headers.add(CORRELATION_ID, VALID_CORRELATION_ID);
    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add(GRANT_TYPE, AUTHORIZATION_CODE);
    requestParams.add(CLIENT_ID, clientId);

    MvcResult result =
        mockMvc
            .perform(
                post(ApiEndpoint.TOKEN.getPath())
                    .contextPath(getContextPath())
                    .params(requestParams)
                    .headers(headers))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.violations").isArray())
            .andReturn();

    String actualResponse = result.getResponse().getContentAsString();
    String expectedResponse = readJsonFile("/response/authorization_code_grant_bad_request.json");
    JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void shouldReturnBadRequestForInvalidRefreshTokenGrantRequest() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.add(AUTHORIZATION, VALID_BEARER_TOKEN);
    headers.add(CORRELATION_ID, VALID_CORRELATION_ID);
    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add(GRANT_TYPE, REFRESH_TOKEN);

    MvcResult result =
        mockMvc
            .perform(
                post(ApiEndpoint.TOKEN.getPath())
                    .contextPath(getContextPath())
                    .params(requestParams)
                    .headers(headers))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.violations").isArray())
            .andReturn();

    String actualResponse = result.getResponse().getContentAsString();
    String expectedResponse = readJsonFile("/response/refresh_token_grant_bad_request.json");
    JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void shouldReturnAccessTokenForClientCredentialsGrant() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.set("Authorization", getEncodedAuthorization(clientId, clientSecret));
    headers.add("correlationId", IdGenerator.id());

    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add(GRANT_TYPE, CLIENT_CREDENTIALS);
    requestParams.add(SCOPE, "openid");
    requestParams.add(REDIRECT_URI, redirectUri);

    mockMvc
        .perform(
            post(ApiEndpoint.TOKEN.getPath())
                .contextPath(getContextPath())
                .params(requestParams)
                .headers(headers))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.access_token").isNotEmpty());

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userEntity.getUserId());
    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(NEW_ACCESS_TOKEN_GENERATED.getEventCode(), auditRequest);
    verifyAuditEventCall(auditEventMap, NEW_ACCESS_TOKEN_GENERATED);
  }

  @Test
  public void shouldLogInvalidClientIdOrSecretAuditEvent() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.set("Authorization", "invalid_client_credentials");
    headers.add("correlationId", IdGenerator.id());

    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add(GRANT_TYPE, CLIENT_CREDENTIALS);
    requestParams.add(SCOPE, "openid");
    requestParams.add(REDIRECT_URI, redirectUri);

    mockMvc
        .perform(
            post(ApiEndpoint.TOKEN.getPath())
                .contextPath(getContextPath())
                .params(requestParams)
                .headers(headers))
        .andDo(print())
        .andExpect(status().isInternalServerError());

    verifyAuditEventCall(INVALID_CLIENT_ID_OR_SECRET);
  }

  @Test
  public void shouldLogResourceAccessFailedAuditEvent() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.set("Authorization", "resource_access_failed");
    headers.add("correlationId", IdGenerator.id());

    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add(GRANT_TYPE, CLIENT_CREDENTIALS);
    requestParams.add(SCOPE, "openid");
    requestParams.add(REDIRECT_URI, redirectUri);

    mockMvc
        .perform(
            post(ApiEndpoint.TOKEN.getPath())
                .contextPath(getContextPath())
                .params(requestParams)
                .headers(headers))
        .andDo(print())
        .andExpect(status().isInternalServerError());

    verifyAuditEventCall(RESOURCE_ACCESS_FAILED);
  }

  @Test
  public void shouldLogInvalidGrantOrInvalidRedreshTokenAuditEvent() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.set("Authorization", "invalid_grant_or_invalid_refresh_token");
    headers.add("correlationId", IdGenerator.id());

    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add(GRANT_TYPE, CLIENT_CREDENTIALS);
    requestParams.add(SCOPE, "openid");
    requestParams.add(REDIRECT_URI, redirectUri);

    mockMvc
        .perform(
            post(ApiEndpoint.TOKEN.getPath())
                .contextPath(getContextPath())
                .params(requestParams)
                .headers(headers))
        .andDo(print())
        .andExpect(status().isInternalServerError());

    verifyAuditEventCall(CommonAuditEvent.INVALID_GRANT_OR_INVALID_REFRESH_TOKEN);
  }

  @Test
  public void shouldReturnRefreshTokenForAuthorizationCodeGrant() throws Exception {
    // Step-1 call the API and expect access and refresh tokens in response
    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add(GRANT_TYPE, AUTHORIZATION_CODE);
    requestParams.add(SCOPE, "openid");
    requestParams.add(REDIRECT_URI, redirectUri);
    requestParams.add(CODE, UUID.randomUUID().toString());
    requestParams.add(USER_ID, userEntity.getUserId());
    requestParams.add(CODE_VERIFIER, UUID.randomUUID().toString());

    HttpHeaders headers = getCommonHeaders();
    headers.add(AUTHORIZATION, VALID_BEARER_TOKEN);
    headers.add(CORRELATION_ID, VALID_CORRELATION_ID);
    MvcResult result =
        mockMvc
            .perform(
                post(ApiEndpoint.TOKEN.getPath())
                    .contextPath(getContextPath())
                    .params(requestParams)
                    .headers(headers))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.access_token").isNotEmpty())
            .andExpect(jsonPath("$.refresh_token").isNotEmpty())
            .andReturn();

    String refreshToken =
        JsonPath.read(result.getResponse().getContentAsString(), "$.refresh_token");

    // Step-2 check refresh token saved in database
    userEntity = userRepository.findByUserId(userEntity.getUserId()).get();
    ObjectNode userInfo = (ObjectNode) userEntity.getUserInfo();
    assertEquals(refreshToken, encryptor.decrypt(getTextValue(userInfo, REFRESH_TOKEN)));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userEntity.getUserId());
    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(NEW_ACCESS_TOKEN_GENERATED.getEventCode(), auditRequest);
    verifyAuditEventCall(auditEventMap, NEW_ACCESS_TOKEN_GENERATED);
  }

  @Test
  public void shouldReturnNewTokensForRefreshTokenGrant() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.add(AUTHORIZATION, VALID_BEARER_TOKEN);
    headers.add(CORRELATION_ID, VALID_CORRELATION_ID);

    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add(GRANT_TYPE, REFRESH_TOKEN);
    requestParams.add(REDIRECT_URI, redirectUri);
    requestParams.add(REFRESH_TOKEN, UUID.randomUUID().toString());
    requestParams.add(CLIENT_ID, clientId);
    requestParams.add(USER_ID, userEntity.getUserId());

    mockMvc
        .perform(
            post(ApiEndpoint.TOKEN.getPath())
                .contextPath(getContextPath())
                .params(requestParams)
                .headers(headers))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.access_token").isNotEmpty())
        .andExpect(jsonPath("$.refresh_token").isNotEmpty());

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userEntity.getUserId());
    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(NEW_ACCESS_TOKEN_GENERATED.getEventCode(), auditRequest);
    verifyAuditEventCall(auditEventMap, NEW_ACCESS_TOKEN_GENERATED);
  }

  private UserEntity newUserEntity() {
    UserEntity user = new UserEntity();
    user.setUserId(IdGenerator.id());
    user.setEmail("mockit_email@grr.la");
    user.setAppId("MyStudies");
    user.setStatus(UserAccountStatus.ACTIVE.getStatus());
    user.setTempRegId(TEMP_REG_ID_VALUE);
    // UserInfo JSON contains password hash & salt, password history etc
    ObjectNode userInfo = getObjectNode().put("password", PasswordGenerator.generate(12));
    user.setUserInfo(userInfo);
    return user;
  }

  @Test
  public void shouldReturnTokenIsActive() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.add("correlationId", IdGenerator.id());
    headers.add(AUTHORIZATION, VALID_BEARER_TOKEN);
    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add(TOKEN, VALID_TOKEN);

    mockMvc
        .perform(
            post(ApiEndpoint.TOKEN_INTROSPECT.getPath())
                .contextPath(getContextPath())
                .params(requestParams)
                .headers(headers))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.active").value(true));

    verify(
        1,
        postRequestedFor(urlEqualTo("/hydra/oauth2/introspect"))
            .withRequestBody(new ContainsPattern(VALID_TOKEN)));
  }

  @Test
  public void shouldReturnBadReqForInvalidToken() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.add("correlationId", IdGenerator.id());
    headers.add(AUTHORIZATION, VALID_BEARER_TOKEN);
    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add(USER_ID, userEntity.getUserId());
    requestParams.add(CLIENT_ID, clientId);
    String expectedResponseContains = ("must not be blank");

    mockMvc
        .perform(
            post(ApiEndpoint.TOKEN_INTROSPECT.getPath())
                .contextPath(getContextPath())
                .params(requestParams)
                .headers(headers))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.violations").isArray())
        .andExpect(content().string(containsString(expectedResponseContains)))
        .andReturn();

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userEntity.getUserId());
    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(ACCESS_TOKEN_INVALID_OR_EXPIRED.getEventCode(), auditRequest);
    verifyAuditEventCall(auditEventMap, ACCESS_TOKEN_INVALID_OR_EXPIRED);
  }

  @Test
  public void shouldRevokeTheToken() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.add(AUTHORIZATION, VALID_BEARER_TOKEN);
    headers.add(CORRELATION_ID, VALID_CORRELATION_ID);
    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add(TOKEN, VALID_TOKEN);

    mockMvc
        .perform(
            post(ApiEndpoint.REVOKE_TOKEN.getPath())
                .contextPath(getContextPath())
                .params(requestParams)
                .headers(headers))
        .andDo(print())
        .andExpect(status().isOk());

    verify(
        1,
        postRequestedFor(urlEqualTo("/oauth2/revoke"))
            .withRequestBody(new ContainsPattern(VALID_TOKEN)));
  }

  @Test
  public void shouldReturnBadRequestForRevokeToken() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.add("correlationId", IdGenerator.id());
    headers.add(AUTHORIZATION, VALID_BEARER_TOKEN);

    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();

    mockMvc
        .perform(
            post(ApiEndpoint.REVOKE_TOKEN.getPath())
                .contextPath(getContextPath())
                .params(requestParams)
                .headers(headers))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.violations[0].path").value("token"))
        .andExpect(jsonPath("$.violations[0].message").value("must not be blank"));
  }

  @Test
  public void shouldReturnBadRequestForIntrospectToken() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.add(AUTHORIZATION, VALID_BEARER_TOKEN);
    headers.add(CORRELATION_ID, VALID_CORRELATION_ID);
    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.set(USER_ID, userEntity.getUserId());

    mockMvc
        .perform(
            post(ApiEndpoint.TOKEN_INTROSPECT.getPath())
                .contextPath(getContextPath())
                .params(requestParams)
                .headers(headers))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.violations[0].path").value("token"))
        .andExpect(jsonPath("$.violations[0].message").value("must not be blank"));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userEntity.getUserId());
    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(ACCESS_TOKEN_INVALID_OR_EXPIRED.getEventCode(), auditRequest);
    verifyAuditEventCall(auditEventMap, ACCESS_TOKEN_INVALID_OR_EXPIRED);
  }

  @AfterEach
  public void cleanUp() {
    userRepository.deleteAll();
  }

  private HttpHeaders getCommonHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
    headers.add("userId", userEntity.getUserId());
    headers.add("appVersion", "1.0");
    headers.add("appId", "SCIM AUTH SERVER");
    headers.add("studyId", "MyStudies");
    headers.add("source", "SCIM AUTH SERVER");
    return headers;
  }
}
