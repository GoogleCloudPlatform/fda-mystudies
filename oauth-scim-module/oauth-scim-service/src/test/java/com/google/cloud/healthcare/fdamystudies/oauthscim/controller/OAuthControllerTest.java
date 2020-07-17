/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.controller;

import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.readJsonFile;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.AUTHORIZATION_CODE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.CLIENT_CREDENTIALS;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.CLIENT_ID;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.CODE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.CODE_VERIFIER;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.GRANT_TYPE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.REDIRECT_URI;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.REFRESH_TOKEN;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.SCOPE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.USER_ID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.oauthscim.common.ApiEndpoint;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class OAuthControllerTest extends BaseMockIT {

  @Value("${security.oauth2.hydra.client.client-id}")
  private String clientId;

  @Value("${security.oauth2.hydra.client.client-secret}")
  private String clientSecret;

  @Value("${security.oauth2.hydra.client.redirect-uri}")
  private String redirectUri;

  @Test
  public void shouldReturnBadRequestForInvalidClientCredentialsGrantRequest() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
    headers.set("Authorization", getEncodedAuthorization(clientId, clientSecret));

    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();

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
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add(GRANT_TYPE, AUTHORIZATION_CODE);

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
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

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
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
    headers.set("Authorization", getEncodedAuthorization(clientId, clientSecret));

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
  }

  @Test
  public void shouldReturnRefreshTokenForAuthorizationCodeGrant() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add(GRANT_TYPE, AUTHORIZATION_CODE);
    requestParams.add(SCOPE, "openid");
    requestParams.add(REDIRECT_URI, redirectUri);
    requestParams.add(CODE, UUID.randomUUID().toString());
    requestParams.add(USER_ID, UUID.randomUUID().toString());
    requestParams.add(CODE_VERIFIER, UUID.randomUUID().toString());

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
  }

  @Test
  public void shouldReturnNewTokensForRefreshTokenGrant() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add(GRANT_TYPE, REFRESH_TOKEN);
    requestParams.add(REDIRECT_URI, redirectUri);
    requestParams.add(REFRESH_TOKEN, UUID.randomUUID().toString());
    requestParams.add(CLIENT_ID, clientId);

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
  }
}
