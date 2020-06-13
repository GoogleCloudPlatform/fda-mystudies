/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.ERROR;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.ERROR_MESSAGE;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.ERROR_TYPE;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.MESSAGE;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.PATH;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.STATUS;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.TIMESTAMP;
import java.time.Instant;
import java.util.Map;
import org.springframework.web.client.RestClientResponseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ErrorResponse {

  private String requestUri = null;

  private Map<String, Object> errorAttributes = null;

  private RestClientResponseException restClientResponseException = null;

  public ErrorResponse(String requestUri, RestClientResponseException restClientResponseException) {
    this.requestUri = requestUri;
    this.restClientResponseException = restClientResponseException;
  }

  public ErrorResponse(Map<String, Object> errorAttributes) {
    this.errorAttributes = errorAttributes;
  }

  public JsonNode toJson() {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode errorResponse = mapper.createObjectNode();
    errorResponse.put(PATH, requestUri);
    errorResponse.put(TIMESTAMP, Instant.now().toEpochMilli());
    if (errorAttributes != null) {
      errorResponse.put(PATH, errorAttributes.get(PATH).toString());
      errorResponse.put(STATUS, errorAttributes.get(STATUS).toString());
      errorResponse.put(ERROR_TYPE, errorAttributes.get(ERROR).toString());
      errorResponse.put(ERROR_MESSAGE, errorAttributes.get(MESSAGE).toString());
    } else if (restClientResponseException != null) {
      errorResponse.put(STATUS, restClientResponseException.getRawStatusCode());
      String errorMessage = restClientResponseException.getMessage();
      errorResponse.put(ERROR_MESSAGE, errorMessage);
    }
    return errorResponse;
  }
}
