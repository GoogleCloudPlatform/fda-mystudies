/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.service;

import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.getObjectNode;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.AUTHORIZATION;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.CONSENT_CHALLENGE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.GRANT_TYPE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.LOGIN_CHALLENGE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.REFRESH_TOKEN;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.cloud.healthcare.fdamystudies.service.BaseServiceImpl;
import java.util.Collections;
import javax.annotation.PostConstruct;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
class HydraOAuthServiceImpl extends BaseServiceImpl implements OAuthService {

  private XLogger logger = XLoggerFactory.getXLogger(HydraOAuthServiceImpl.class.getName());

  private static final String APPLICATION_X_WWW_FORM_URLENCODED_CHARSET_UTF_8 =
      "application/x-www-form-urlencoded;charset=UTF-8";

  private static final String CONTENT_TYPE = "Content-Type";

  @Value("${security.oauth2.hydra.token_endpoint}")
  private String tokenEndpoint;

  @Value("${security.oauth2.hydra.token_revoke_endpoint}")
  private String revokeTokenEndpoint;

  @Value("${security.oauth2.hydra.introspection_endpoint}")
  private String introspectEndpoint;

  @Value("${security.oauth2.hydra.client.client-id}")
  private String clientId;

  @Value("${security.oauth2.hydra.client.client-secret}")
  private String clientSecret;

  @Value("${security.oauth2.hydra.login_endpoint}")
  private String loginEndpoint;

  @Value("${security.oauth2.hydra.login_accept_endpoint}")
  private String loginAcceptEndpoint;

  @Value("${security.oauth2.hydra.consent_endpoint}")
  private String consentEndpoint;

  @Value("${security.oauth2.hydra.consent_accept_endpoint}")
  private String consentAcceptEndpoint;

  private String encodedAuthorization;

  @PostConstruct
  public void init() {
    encodedAuthorization = getEncodedAuthorization(clientId, clientSecret);
  }

  public ResponseEntity<JsonNode> getToken(
      MultiValueMap<String, String> paramMap, HttpHeaders headers) {
    headers.add(CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED_CHARSET_UTF_8);

    if (REFRESH_TOKEN.equals(paramMap.getFirst(GRANT_TYPE))) {
      headers.set(AUTHORIZATION, encodedAuthorization);
    }

    HttpEntity<Object> requestEntity = new HttpEntity<>(paramMap, headers);
    return getRestTemplate().postForEntity(tokenEndpoint, requestEntity, JsonNode.class);
  }

  @Override
  public ResponseEntity<JsonNode> revokeToken(
      MultiValueMap<String, String> paramMap, HttpHeaders headers) {
    headers.add(CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED_CHARSET_UTF_8);
    headers.add(AUTHORIZATION, encodedAuthorization);
    HttpEntity<Object> requestEntity = new HttpEntity<>(paramMap, headers);
    return getRestTemplate().postForEntity(revokeTokenEndpoint, requestEntity, JsonNode.class);
  }

  @Override
  public ResponseEntity<JsonNode> introspectToken(
      MultiValueMap<String, String> paramMap, HttpHeaders headers) {
    headers.add(CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED_CHARSET_UTF_8);
    headers.add(AUTHORIZATION, encodedAuthorization);
    HttpEntity<Object> requestEntity = new HttpEntity<>(paramMap, headers);
    return getRestTemplate().postForEntity(introspectEndpoint, requestEntity, JsonNode.class);
  }

  @Override
  public ResponseEntity<JsonNode> requestLogin(MultiValueMap<String, String> paramMap) {
    HttpHeaders headers = new HttpHeaders();
    headers.add(CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED_CHARSET_UTF_8);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

    StringBuilder url = new StringBuilder(loginEndpoint);
    url.append("?login_challenge").append("=").append(paramMap.getFirst(LOGIN_CHALLENGE));

    return getRestTemplate().getForEntity(url.toString(), JsonNode.class);
  }

  @Override
  public ResponseEntity<JsonNode> loginAccept(String userId, String loginChallenge) {

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

    StringBuilder url = new StringBuilder(loginAcceptEndpoint);
    url.append("?").append(LOGIN_CHALLENGE).append("=").append(loginChallenge);

    ObjectNode requestParams = getObjectNode();
    requestParams.put("subject", userId);

    HttpEntity<Object> requestEntity = new HttpEntity<>(requestParams, headers);

    ResponseEntity<JsonNode> response =
        getRestTemplate().exchange(url.toString(), HttpMethod.PUT, requestEntity, JsonNode.class);

    if (!response.getStatusCode().is2xxSuccessful()) {
      logger.error(
          String.format(
              "%s failed with status=%d and response=%s",
              loginAcceptEndpoint, response.getStatusCodeValue(), response.getBody()));
    }

    return response;
  }

  @Override
  public ResponseEntity<JsonNode> requestConsent(MultiValueMap<String, String> paramMap) {
    HttpHeaders headers = new HttpHeaders();
    headers.add(CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED_CHARSET_UTF_8);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

    StringBuilder url = new StringBuilder(consentEndpoint);
    url.append("?consent_challenge=").append(paramMap.getFirst(CONSENT_CHALLENGE));

    return getRestTemplate().getForEntity(url.toString(), JsonNode.class);
  }

  @Override
  public ResponseEntity<JsonNode> consentAccept(MultiValueMap<String, String> paramMap) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

    StringBuilder url = new StringBuilder(consentAcceptEndpoint);
    url.append("?")
        .append(CONSENT_CHALLENGE)
        .append("=")
        .append(paramMap.getFirst(CONSENT_CHALLENGE));

    HttpEntity<Object> requestEntity = new HttpEntity<>(getObjectNode(), headers);
    ResponseEntity<JsonNode> response =
        getRestTemplate().exchange(url.toString(), HttpMethod.PUT, requestEntity, JsonNode.class);

    if (!response.getStatusCode().is2xxSuccessful()) {
      logger.error(
          String.format(
              "consent accept failed with status %d and response=%s",
              response.getStatusCodeValue(), response.getBody()));
    }

    return response;
  }
}
