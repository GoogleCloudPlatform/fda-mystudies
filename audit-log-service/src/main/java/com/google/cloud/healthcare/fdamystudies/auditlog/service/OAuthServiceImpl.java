/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.auditlog.service;

import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;

@Service
class OAuthServiceImpl extends BaseServiceImpl implements OAuthService {

  @Value("${security.oauth2.health_endpoint}")
  private String healthEndpoint;

  @Override
  public ResponseEntity<JsonNode> health() {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);

    return exchangeForJson(healthEndpoint, headers, null, HttpMethod.GET);
  }
}
