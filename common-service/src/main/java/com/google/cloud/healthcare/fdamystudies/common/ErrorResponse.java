/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import java.io.IOException;
import java.time.Instant;
import org.springframework.web.client.RestClientResponseException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonSerialize(using = ErrorResponse.ErrorResponseSerializer.class)
public class ErrorResponse {

  private String requestUri;

  private RestClientResponseException restClientResponseException;

  public ErrorResponse(String requestUri, RestClientResponseException restClientResponseException) {
    this.requestUri = requestUri;
    this.restClientResponseException = restClientResponseException;
  }

  public JsonNode toJson() {
    return new ObjectMapper().convertValue(this, JsonNode.class);
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
      jsonGenerator.writeNumberField(
          "status", errorResponse.getRestClientResponseException().getRawStatusCode());
      jsonGenerator.writeStringField(
          "error_description", errorResponse.getRestClientResponseException().getMessage());
      jsonGenerator.writeNumberField("timestamp", Instant.now().toEpochMilli());
      jsonGenerator.writeStringField("path", errorResponse.getRequestUri());
      jsonGenerator.writeEndObject();
    }
  }
}
