/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020-2021 Google LLC
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
 * HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.fdahpstudydesigner.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import java.util.Base64;
import java.util.Collections;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class OAuthServiceImpl implements OAuthService {

  private XLogger logger = XLoggerFactory.getXLogger(OAuthServiceImpl.class.getName());

  private static final String SCOPE = "scope";

  private static final String AUTHORIZATION = "Authorization";

  private static final String CONTENT_TYPE = "Content-Type";

  private static final String GRANT_TYPE = "grant_type";

  private static final String REDIRECT_URI = "redirect_uri";

  private static final String ACCESS_TOKEN = "access_token";

  private static final String APPLICATION_X_WWW_FORM_URLENCODED_CHARSET_UTF_8 =
      "application/x-www-form-urlencoded;charset=UTF-8";

  private String accessToken;

  private String encodedAuthorization;

  @Autowired private RestTemplate restTemplate;

  @PostConstruct
  public void init() {
    String clientId = getPropertyValue("security.oauth2.client.client-id");
    String clientSecret = getPropertyValue("security.oauth2.client.client-secret");
    String credentials = clientId + ":" + clientSecret;
    encodedAuthorization = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
  }

  @Override
  public String getAccessToken() {
    if (StringUtils.isEmpty(accessToken)) {
      this.accessToken = getNewAccessToken();
    }
    return this.accessToken;
  }

  @Override
  public String getNewAccessToken() {
    logger.entry("begin getNewAccessToken()");
    ResponseEntity<JsonNode> response = getToken();
    if (response.getStatusCode() == HttpStatus.OK) {
      this.accessToken = response.getBody().get(ACCESS_TOKEN).textValue();
      logger.exit(String.format("status=%d", response.getStatusCode().value()));
    } else {
      logger.error(
          String.format(
              "Get new access token from oauth scim service failed with status=%d and response=%s",
              response.getStatusCode().value(), response.getBody().toString()));
    }
    return this.accessToken;
  }

  private ResponseEntity<JsonNode> getToken() {
    String tokenEndpoint = getPropertyValue("security.oauth2.token_endpoint");
    String clientRedirectUri = getPropertyValue("security.oauth2.client.redirect-uri");

    HttpHeaders headers = new HttpHeaders();
    headers.add(CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED_CHARSET_UTF_8);
    headers.add(AUTHORIZATION, encodedAuthorization);

    MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
    requestBody.add(GRANT_TYPE, "client_credentials");
    requestBody.add(SCOPE, "openid");
    requestBody.add(REDIRECT_URI, clientRedirectUri);

    ResponseEntity<JsonNode> response =
        exchangeForJson(tokenEndpoint, headers, requestBody, HttpMethod.POST);

    if (response.getStatusCode() != HttpStatus.OK) {
      logger.error(
          String.format(
              "get token failed with status %d and response %s",
              response.getStatusCode().value(), response.getBody().toString()));
    }

    return response;
  }

  private ResponseEntity<JsonNode> exchangeForJson(
      String url,
      HttpHeaders headers,
      MultiValueMap<String, String> request,
      HttpMethod method,
      Object... uriVariables) {
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(request, headers);
    return restTemplate.exchange(url, method, requestEntity, JsonNode.class, uriVariables);
  }

  private String getPropertyValue(String name) {
    return FdahpStudyDesignerUtil.getAppProperties().get(name);
  }
}
