/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import java.time.Instant;
import org.springframework.web.client.RestClientResponseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ErrorResponse {

  public static final String PATH = "path";

  public static final String STATUS = "status";

  public static final String ERROR_DESCRIPTION = "error_description";

  private String requestUri;

  private RestClientResponseException restClientResponseException;

  public ErrorResponse(String requestUri, RestClientResponseException restClientResponseException) {
    this.requestUri = requestUri;
    this.restClientResponseException = restClientResponseException;
  }

  public JsonNode toJson() {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode errorResponse = mapper.createObjectNode();

    if (restClientResponseException != null) {
      errorResponse.put(STATUS, restClientResponseException.getRawStatusCode());
      errorResponse.put(ERROR_DESCRIPTION, restClientResponseException.getMessage());
    }

    errorResponse.put(PATH, requestUri);
    errorResponse.put("timestamp", Instant.now().toEpochMilli());

    return errorResponse;
  }
}
