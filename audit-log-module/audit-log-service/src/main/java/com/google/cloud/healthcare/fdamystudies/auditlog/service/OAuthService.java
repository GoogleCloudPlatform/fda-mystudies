/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.auditlog.service;

import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;

public interface OAuthService {

  public ResponseEntity<JsonNode> health();
}
