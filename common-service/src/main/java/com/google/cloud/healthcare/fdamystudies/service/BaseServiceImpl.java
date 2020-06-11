/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.cloud.healthcare.fdamystudies.common.ErrorResponse;

public abstract class BaseServiceImpl {

  private static final String REQUEST_FAILED_WITH_AN_EXCEPTION =
      "%s request failed with an exception";

  private static final Logger LOG = LoggerFactory.getLogger(BaseServiceImpl.class);

  @Autowired private ObjectMapper objectMapper;

  @Autowired private RestTemplate restTemplate;

  protected ResponseEntity<JsonNode> exchangeForJson(
      String url,
      HttpHeaders headers,
      JsonNode request,
      HttpMethod method,
      Object... uriVariables) {
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    HttpEntity<JsonNode> requestEntity = new HttpEntity<>(request, headers);
    try {
      return restTemplate.exchange(url, method, requestEntity, JsonNode.class, uriVariables);
    } catch (HttpClientErrorException | HttpServerErrorException e) {
      LOG.error(String.format(REQUEST_FAILED_WITH_AN_EXCEPTION, url), e);
      ErrorResponse err = new ErrorResponse(url, e);
      return ResponseEntity.status(e.getRawStatusCode()).body(err.toJson());
    }
  }

  protected ResponseEntity<JsonNode> exchangeForJson(
      String url,
      HttpHeaders headers,
      MultiValueMap<String, String> request,
      HttpMethod method,
      Object... uriVariables) {
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(request, headers);
    try {
      return restTemplate.exchange(url, method, requestEntity, JsonNode.class, uriVariables);
    } catch (HttpClientErrorException | HttpServerErrorException e) {
      LOG.error(String.format(REQUEST_FAILED_WITH_AN_EXCEPTION, url), e);
      ErrorResponse err = new ErrorResponse(url, e);
      return ResponseEntity.status(e.getRawStatusCode()).body(err.toJson());
    }
  }

  protected ResponseEntity<JsonNode> getForJson(String url, Object... uriVariables) {
    try {
      return restTemplate.getForEntity(url, JsonNode.class, uriVariables);
    } catch (HttpClientErrorException | HttpServerErrorException e) {
      LOG.error(String.format(REQUEST_FAILED_WITH_AN_EXCEPTION, url), e);
      ErrorResponse err = new ErrorResponse(url, e);
      return ResponseEntity.status(e.getRawStatusCode()).body(err.toJson());
    }
  }

  protected ObjectNode getObjectNode() {
    return objectMapper.createObjectNode();
  }

  protected RestTemplate getRestTemplate() {
    return restTemplate;
  }
}
