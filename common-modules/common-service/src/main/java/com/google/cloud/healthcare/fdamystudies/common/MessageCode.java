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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
@RequiredArgsConstructor
@JsonSerialize(using = MessageCode.MessageCodeSerializer.class)
public enum MessageCode {
  ADD_SITE_SUCCESS(HttpStatus.CREATED, "MSG-0001", "Site added successfully"),

  ADD_LOCATION_SUCCESS(HttpStatus.CREATED, "MSG-0002", "New location added successfully");

  PASSWORD_RESET_SUCCESS(HttpStatus.OK, "MSG-0001", "Your password has been reset successfully!"),

  CHANGE_PASSWORD_SUCCESS(
      HttpStatus.OK, "MSG-0002", "Your password has been changed successfully!"),

  EMAIL_ACCEPTED_BY_MAIL_SERVER(
      HttpStatus.ACCEPTED, "MSG-0003", "The email is accepted by the receiving mail server."),

  UPDATE_USER_DETAILS_SUCCESS(HttpStatus.OK, "MSG-0004", "User details successfully updated.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  static class MessageCodeSerializer extends StdSerializer<MessageCode> {

    private static final long serialVersionUID = 1L;

    public MessageCodeSerializer() {
      super(MessageCode.class);
    }

    @Override
    public void serialize(
        MessageCode msgCode, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
        throws IOException {
      jsonGenerator.writeStartObject();
      jsonGenerator.writeNumberField("status", msgCode.getHttpStatus().value());
      jsonGenerator.writeStringField("code", msgCode.getCode());
      jsonGenerator.writeStringField("message", msgCode.getMessage());
      jsonGenerator.writeEndObject();
    }
  }
}
