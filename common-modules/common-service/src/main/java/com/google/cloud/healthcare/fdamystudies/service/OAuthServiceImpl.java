/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.addTextFields;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.getTextValue;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class OAuthServiceImpl extends BaseServiceImpl implements OAuthService {

  private XLogger logger = XLoggerFactory.getXLogger(OAuthServiceImpl.class.getName());

  private static final String TOKEN = "token";

  private static final String SCOPE = "scope";

  private static final String AUTHORIZATION = "Authorization";

  private static final String CONTENT_TYPE = "Content-Type";

  @Value("${security.oauth2.health_endpoint}")
  private String healthEndpoint;

  @Value("${security.oauth2.introspection_endpoint}")
  private String introspectEndpoint;

  @Override
  public ResponseEntity<JsonNode> health() {
    return getForJson(healthEndpoint);
  }

  @Override
  public ResponseEntity<JsonNode> introspectToken(JsonNode params) {
    logger.entry("begin introspectToken()");
    HttpHeaders headers = new HttpHeaders();
    headers.add(CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    headers.add(AUTHORIZATION, "Bearer " + getTextValue(params, TOKEN));

    MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
    addTextFields(params, requestBody, TOKEN, SCOPE);

    ResponseEntity<JsonNode> response =
        exchangeForJson(introspectEndpoint, headers, requestBody, HttpMethod.POST);
    logger.exit(
        String.format("status=%d, response=%s", response.getStatusCodeValue(), response.getBody()));
    return response;
  }
}
