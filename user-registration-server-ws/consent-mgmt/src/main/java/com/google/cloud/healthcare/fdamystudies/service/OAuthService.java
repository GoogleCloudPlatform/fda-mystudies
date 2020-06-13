/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;

public interface OAuthService {

  public String getNewAccessToken(JsonNode auditLogEventParams);

  public String getAccessToken(JsonNode auditLogEventParams);

  public ResponseEntity<JsonNode> getToken(JsonNode params, JsonNode auditLogEventParams);

  public ResponseEntity<JsonNode> revokeToken(JsonNode params, JsonNode auditLogEventParams);

  public ResponseEntity<JsonNode> introspectToken(JsonNode params, JsonNode auditLogEventParams);
}
