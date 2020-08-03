/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Base64;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public abstract class BaseServiceImpl {

  @Autowired private RestTemplate restTemplate;

  protected ResponseEntity<JsonNode> exchangeForJson(
      String url,
      HttpHeaders headers,
      JsonNode request,
      HttpMethod method,
      Object... uriVariables) {
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    HttpEntity<JsonNode> requestEntity = new HttpEntity<>(request, headers);
    return restTemplate.exchange(url, method, requestEntity, JsonNode.class, uriVariables);
  }

  protected ResponseEntity<JsonNode> exchangeForJson(
      String url,
      HttpHeaders headers,
      MultiValueMap<String, String> request,
      HttpMethod method,
      Object... uriVariables) {
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(request, headers);
    return restTemplate.exchange(url, method, requestEntity, JsonNode.class, uriVariables);
  }

  protected ResponseEntity<JsonNode> getForJson(String url, Object... uriVariables) {
    return restTemplate.getForEntity(url, JsonNode.class, uriVariables);
  }

  protected RestTemplate getRestTemplate() {
    return restTemplate;
  }

  protected boolean isSuccessful(ResponseEntity<?> response) {
    return response.getStatusCode().is2xxSuccessful();
  }

  protected String getEncodedAuthorization(String clientId, String clientSecret) {
    String credentials = clientId + ":" + clientSecret;
    return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
  }
}
