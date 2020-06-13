/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.ERROR_CODE;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.ERROR_MESSAGE;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.STATUS;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
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
import com.google.cloud.healthcare.fdamystudies.common.ErrorResponse;
import com.google.cloud.healthcare.fdamystudies.utils.ErrorCode;

abstract class BaseServiceImpl {

  private static final Logger LOG = LoggerFactory.getLogger(BaseServiceImpl.class);

  @Autowired private ObjectMapper objectMapper;

  @Autowired private RestTemplate restTemplate;

  @Autowired private OAuthServiceFactory oauthServiceFactory;

  private Random rand = new Random();

  public ResponseEntity<JsonNode> exchangeForJson(
      String url, HttpHeaders headers, Object request, HttpMethod httpMethod) {
    try {
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      HttpEntity<Object> requestEntity = new HttpEntity<>(request, headers);
      return restTemplate.exchange(url, httpMethod, requestEntity, JsonNode.class);
    } catch (HttpClientErrorException | HttpServerErrorException e) {
      LOG.error(String.format("%s failed with exception", url), e);
      ErrorResponse err = new ErrorResponse(url, e);
      return ResponseEntity.status(e.getRawStatusCode()).body(err.toJson());
    } catch (Exception e) {
      LOG.error(String.format("%s failed with exception", url), e);
      return ResponseEntity.status(ErrorCode.EC_500.statusCode())
          .body(getErrorResponse(ErrorCode.EC_500));
    }
  }

  protected String getTextValue(JsonNode node, String fieldname) {
    return node.hasNonNull(fieldname) ? node.get(fieldname).textValue() : "";
  }

  protected String getEncodedAuthorization(String clientId, String clientSecret) {
    String credentials = clientId + ":" + clientSecret;
    return Base64.getEncoder().encodeToString(credentials.getBytes());
  }

  public OAuthService getOAuthService() {
    return oauthServiceFactory.getOAuthService();
  }

  protected ObjectNode getObjectNode() {
    return objectMapper.createObjectNode();
  }

  protected ObjectMapper getObjectMapper() {
    return objectMapper;
  }

  protected RestTemplate getRestTemplate() {
    return restTemplate;
  }

  protected ObjectNode toJson(String value) throws IOException {
    return objectMapper.readValue(value, ObjectNode.class);
  }

  protected String getUUID(String random) {
    StringBuilder builder = new StringBuilder(getUUID());

    if (StringUtils.isNotBlank(random)) {

      for (int i = 0; i < random.length(); i++) {
        int index = rand.nextInt(builder.length() - 1);
        builder.insert(index, random.charAt(i));
      }
    }
    return builder.toString();
  }

  protected String getUUID() {
    return UUID.randomUUID().toString();
  }

  protected String encrypt(String input, String rawSalt) {
    StringBuilder sb = new StringBuilder();
    try {
      byte[] salt = rawSalt.getBytes();
      MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
      // Pass salt to the digest
      messageDigest.update(salt);
      messageDigest.update(input.getBytes(StandardCharsets.UTF_8));
      byte[] digestBytes = messageDigest.digest();

      for (int i = 0; i < digestBytes.length; i++) {
        String hex = Integer.toHexString(0xff & digestBytes[i]);
        if (hex.length() < 2) {
          sb.append('0');
        }
        sb.append(hex);
      }
    } catch (Exception e) {
      LOG.error("exception in encrypt()", e);
    }
    return sb.toString();
  }

  protected JsonNode getErrorResponse(ErrorCode error) {
    ObjectNode response = getObjectNode();
    response.put(STATUS, error.statusCode());
    response.put(ERROR_MESSAGE, error.errorMessage());
    response.put(ERROR_CODE, error.code());
    return response;
  }

  protected void copyAll(JsonNode source, ObjectNode dest, String... excludeFields) {
    dest.setAll((ObjectNode) source);
    if (excludeFields != null) {
      for (String fieldname : excludeFields) {
        dest.remove(fieldname);
      }
    }
  }
}
