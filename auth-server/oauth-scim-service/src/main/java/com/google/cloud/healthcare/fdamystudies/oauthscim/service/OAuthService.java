/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

public interface OAuthService {

  public ResponseEntity<?> getToken(
      MultiValueMap<String, String> paramMap,
      HttpHeaders headers,
      AuditLogEventRequest auditRequest)
      throws JsonProcessingException;

  public ResponseEntity<JsonNode> revokeToken(
      MultiValueMap<String, String> paramMap, HttpHeaders headers);

  public ResponseEntity<JsonNode> introspectToken(
      MultiValueMap<String, String> paramMap, HttpHeaders headers);

  public ResponseEntity<JsonNode> requestLogin(MultiValueMap<String, String> paramMap);

  public ResponseEntity<JsonNode> loginAccept(String userId, String loginChallenge);

  public ResponseEntity<JsonNode> requestConsent(MultiValueMap<String, String> paramMap);

  public ResponseEntity<JsonNode> consentAccept(MultiValueMap<String, String> paramMap);
}
