/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.auditlog.common;

import static com.google.cloud.healthcare.fdamystudies.auditlog.common.FieldNames.ERROR_TYPE;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
@JsonSerialize(using = ErrorMessages.ErrorMessageSerializer.class)
public enum ErrorMessages {
  BAD_REQUEST(
      400, "EC-400", "Bad Request", "Malformed request syntax or invalid request message framing."),

  UNAUTHORIZED(401, "EC-401", "Unauthorized", "Invalid token"),

  APPLICATION_ERROR(
      500,
      "EC-500",
      "Internal Server Error",
      "Sorry, an error has occurred and your request could not be processed. Please try again later.");

  private final int statusCode;
  private final String description;
  private final String errorCode;
  private final String errorType;

  static class ErrorMessageSerializer extends StdSerializer<ErrorMessages> {

    public ErrorMessageSerializer() {
       super(ErrorMessages.class);
     }

    @Override
    public void serialize(ErrorMessages error, JsonGenerator jsonGenerator,
        SerializerProvider serializerProvider) throws IOException {
      jsonGenerator.writeStartObject();
      jsonGenerator.writeNumberField("status", error.getStatusCode());
      jsonGenerator.writeStringField("type", error.getErrorType());
      jsonGenerator.writeStringField("description", error.getDescription());
      jsonGenerator.writeStringField("code", error.getErrorCode());
      jsonGenerator.writeEndObject();
    }
  }
}
