/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.auditlog.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.cloud.healthcare.fdamystudies.service.BaseServiceImpl;

@Service
class OAuthServiceImpl extends BaseServiceImpl implements OAuthService {

  @Value("${security.oauth2.health_endpoint}")
  private String healthEndpoint;

  @Override
  public ResponseEntity<JsonNode> health() {
    return getForJson(healthEndpoint);
  }
}
