/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.web.client.RestClientResponseException;

@Getter
@ToString
@JsonSerialize(using = ErrorResponse.ErrorResponseSerializer.class)
public class ErrorResponse {

  @ToString.Exclude
  private XLogger logger = XLoggerFactory.getXLogger(ErrorResponse.class.getName());

  private String errorDescription;

  private String errorType;

  private int status;

  private long timestamp = Instant.now().toEpochMilli();

  public ErrorResponse(RestClientResponseException restClientResponseException) {
    populateErrorFields(restClientResponseException);
  }

  public ErrorResponse(Map<String, Object> errorAttributes) {
    status = Integer.parseInt(errorAttributes.get("status").toString());
    errorType = errorAttributes.get("error").toString();
    errorDescription = errorAttributes.get("message").toString();
  }

  private void populateErrorFields(RestClientResponseException restClientResponseException) {
    status = restClientResponseException.getRawStatusCode();
    errorType = restClientResponseException.getClass().getSimpleName();
    errorDescription = restClientResponseException.getMessage();
  }

  static class ErrorResponseSerializer extends StdSerializer<ErrorResponse> {

    private static final long serialVersionUID = 1L;

    public ErrorResponseSerializer() {
      super(ErrorResponse.class);
    }

    @Override
    public void serialize(
        ErrorResponse errorResponse,
        JsonGenerator jsonGenerator,
        SerializerProvider serializerProvider)
        throws IOException {
      jsonGenerator.writeStartObject();
      jsonGenerator.writeNumberField("status", errorResponse.status);
      jsonGenerator.writeStringField("error_type", errorResponse.errorType);
      jsonGenerator.writeNumberField("timestamp", errorResponse.timestamp);
      jsonGenerator.writeStringField("error_description", errorResponse.errorDescription);
      jsonGenerator.writeEndObject();
    }
  }
}
