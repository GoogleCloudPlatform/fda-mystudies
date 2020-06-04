/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.auditlog.service;

import static com.google.cloud.healthcare.fdamystudies.auditlog.common.FieldNames.ERROR_CODE;
import static com.google.cloud.healthcare.fdamystudies.auditlog.common.FieldNames.ERROR_DESCRIPTION;
import static com.google.cloud.healthcare.fdamystudies.auditlog.common.FieldNames.ERROR_TYPE;
import static com.google.cloud.healthcare.fdamystudies.auditlog.common.FieldNames.STATUS;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.cloud.healthcare.fdamystudies.auditlog.common.ErrorMessages;
import com.google.cloud.healthcare.fdamystudies.auditlog.common.ErrorResponse;

abstract class BaseServiceImpl {

  private static final Logger LOG = LoggerFactory.getLogger(BaseServiceImpl.class);

  @Autowired private ObjectMapper objectMapper;

  @Autowired private RestTemplate restTemplate;

  public ResponseEntity<JsonNode> exchangeForJson(
      String url, HttpHeaders headers, Object request, HttpMethod httpMethod) {
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    HttpEntity<Object> requestEntity = new HttpEntity<>(request, headers);
    try {
      return restTemplate.exchange(url, httpMethod, requestEntity, JsonNode.class);
    } catch (HttpClientErrorException | HttpServerErrorException e) {
      LOG.error(String.format("%s request failed with an exception", url), e);
      ErrorResponse err = new ErrorResponse(url, e);
      return ResponseEntity.status(e.getRawStatusCode()).body(err.toJson());
    } catch (Exception e) {
      LOG.error(String.format("%s request failed with an exception", url), e);
      return ResponseEntity.status(ErrorMessages.APPLICATION_ERROR.getStatusCode())
          .body(getErrorResponse(ErrorMessages.APPLICATION_ERROR));
    }
  }

  protected ObjectNode getObjectNode() {
    return objectMapper.createObjectNode();
  }

  protected JsonNode getErrorResponse(ErrorMessages error) {
    ObjectNode response = getObjectNode();
    response.put(STATUS, error.getStatusCode());
    response.put(ERROR_TYPE, error.getErrorType());
    response.put(ERROR_DESCRIPTION, error.getDescription());
    response.put(ERROR_CODE, error.getErrorCode());
    return response;
  }
}
