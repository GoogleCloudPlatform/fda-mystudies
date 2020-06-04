/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.auditlog.common;

import static com.google.cloud.healthcare.fdamystudies.auditlog.common.FieldNames.ERROR_DESCRIPTION;
import static com.google.cloud.healthcare.fdamystudies.auditlog.common.FieldNames.PATH;
import static com.google.cloud.healthcare.fdamystudies.auditlog.common.FieldNames.STATUS;
import java.time.Instant;
import org.springframework.web.client.RestClientResponseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ErrorResponse {

  private String requestUri = null;

  private RestClientResponseException restClientResponseException = null;

  public ErrorResponse(String requestUri, RestClientResponseException restClientResponseException) {
    this.requestUri = requestUri;
    this.restClientResponseException = restClientResponseException;
  }

  public JsonNode toJson() {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode errorResponse = mapper.createObjectNode();
    errorResponse.put(PATH, requestUri);
    errorResponse.put("datetime", Instant.now().toEpochMilli());
    if (restClientResponseException != null) {
      errorResponse.put(STATUS, restClientResponseException.getRawStatusCode());
      String errorMessage = restClientResponseException.getMessage();
      errorResponse.put(ERROR_DESCRIPTION, errorMessage);
    }
    return errorResponse;
  }
}
