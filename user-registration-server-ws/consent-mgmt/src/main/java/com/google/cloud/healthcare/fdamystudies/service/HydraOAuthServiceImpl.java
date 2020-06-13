/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.ACCESS_TOKEN;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.CODE;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.CODE_VERIFIER;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.GRANT_TYPE;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.REDIRECT_URI;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.REFRESH_TOKEN;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.SCOPE;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.TOKEN;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
class HydraOAuthServiceImpl extends BaseServiceImpl implements OAuthService {

  private static final String APPLICATION_X_WWW_FORM_URLENCODED_CHARSET_UTF_8 =
      "application/x-www-form-urlencoded;charset=UTF-8";

  private static final String BASIC = "Basic ";

  private static final String CONTENT_TYPE = "Content-Type";

  private static final String AUTHORIZATION = "Authorization";

  private static final Logger LOG = LoggerFactory.getLogger(HydraOAuthServiceImpl.class);

  @Value("${security.oauth2.hydra.token_endpoint}")
  private String tokenEndpoint;

  @Value("${security.oauth2.hydra.client.client-id}")
  private String clientId;

  @Value("${security.oauth2.hydra.client.client-secret}")
  private String clientSecret;

  @Value("${security.oauth2.hydra.token_revoke_endpoint}")
  private String revokeTokenEndpoint;

  @Value("${security.oauth2.hydra.introspection_endpoint}")
  private String introspectEndpoint;

  @Value("${security.oauth2.hydra.client.redirect-uri}")
  private String clientRedirectUri;

  private String accessToken;

  public ResponseEntity<JsonNode> getToken(JsonNode params, JsonNode auditLogEventParams) {
    HttpHeaders headers = new HttpHeaders();
    headers.add(CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED_CHARSET_UTF_8);
    headers.add(AUTHORIZATION, BASIC + getEncodedAuthorization(clientId, clientSecret));

    MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
    requestBody.add(GRANT_TYPE, getTextValue(params, GRANT_TYPE));
    requestBody.add(SCOPE, getTextValue(params, SCOPE));
    requestBody.add(REDIRECT_URI, getTextValue(params, REDIRECT_URI));

    if (params.hasNonNull(CODE)) {
      requestBody.add(CODE, getTextValue(params, CODE));
    }

    if (params.hasNonNull(CODE_VERIFIER)) {
      requestBody.add(CODE_VERIFIER, getTextValue(params, CODE_VERIFIER));
    }

    if (params.hasNonNull(REFRESH_TOKEN)) {
      requestBody.add(REFRESH_TOKEN, getTextValue(params, REFRESH_TOKEN));
    }

    return exchangeForJson(tokenEndpoint, headers, requestBody, HttpMethod.POST);
  }

  @Override
  public ResponseEntity<JsonNode> revokeToken(JsonNode params, JsonNode auditLogEventParams) {
    HttpHeaders headers = new HttpHeaders();
    headers.add(CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED_CHARSET_UTF_8);
    headers.add(AUTHORIZATION, BASIC + getEncodedAuthorization(clientId, clientSecret));

    MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
    requestBody.add(TOKEN, getTextValue(params, TOKEN));

    return exchangeForJson(revokeTokenEndpoint, headers, requestBody, HttpMethod.POST);
  }

  @Override
  public ResponseEntity<JsonNode> introspectToken(JsonNode params, JsonNode auditLogEventParams) {
    HttpHeaders headers = new HttpHeaders();
    headers.add(CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED_CHARSET_UTF_8);
    headers.add(AUTHORIZATION, BASIC + getEncodedAuthorization(clientId, clientSecret));

    MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
    requestBody.add(TOKEN, getTextValue(params, TOKEN));
    if (params.hasNonNull(SCOPE)) {
      requestBody.add(SCOPE, getTextValue(params, SCOPE));
    }

    return exchangeForJson(introspectEndpoint, headers, requestBody, HttpMethod.POST);
  }

  @Override
  public String getNewAccessToken(JsonNode auditLogEventParams) {
    ObjectNode requestBody = getObjectNode();
    requestBody.put(GRANT_TYPE, "client_credentials");
    requestBody.put(SCOPE, "openid");
    requestBody.put(REDIRECT_URI, clientRedirectUri);
    ResponseEntity<JsonNode> response =
        getOAuthService().getToken(requestBody, auditLogEventParams);
    if (response.getStatusCode().is2xxSuccessful()) {
      accessToken = response.getBody().get(ACCESS_TOKEN).textValue();
    } else {
      if (LOG.isErrorEnabled()) {
        LOG.error(
            String.format(
                "get new access token from oauth server failed with status %d and response %s",
                response.getStatusCodeValue(), response.getBody().toString()));
      }
    }
    return accessToken;
  }

  @Override
  public String getAccessToken(JsonNode auditLogEventParams) {
    if (StringUtils.isEmpty(accessToken)) {
      accessToken = getNewAccessToken(auditLogEventParams);
    }
    return accessToken;
  }
}
