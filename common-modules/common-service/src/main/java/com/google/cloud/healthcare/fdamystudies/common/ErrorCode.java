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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
@JsonSerialize(using = ErrorCode.ErrorCodeSerializer.class)
public enum ErrorCode {
  BAD_REQUEST(
      400,
      "EC-400",
      Constants.BAD_REQUEST,
      "Malformed request syntax or invalid request message framing."),

  UNAUTHORIZED(401, "EC-401", "Unauthorized", "Invalid token"),
  
  ACCOUNT_LOCKED(
      400,
      "EC-107",
      Constants.BAD_REQUEST,
      "Due to consecutive failed sign-in attempts with incorrect password, your account has been locked for a period of 15 minutes. Please check your registered email inbox for assistance to reset your password in this period or wait until the lock period is over to sign in again."),

  TEMP_PASSWORD_EXPIRED(
      401,
      "EC-110",
      Constants.BAD_REQUEST,
      "Your temporary password is expired. Please use the Forgot Your Login/Reset Password link to reset your password"),

  PASSWORD_EXPIRED(
      401,
      "EC-111",
      Constants.BAD_REQUEST,
      "Your password is expired. Please use the Forgot Your Login/Reset Password link to reset your password"),

  USER_NOT_FOUND(404, "EC-114", Constants.BAD_REQUEST, "User not found"),

  ACCOUNT_DEACTIVATED(403, "EC-116", Constants.BAD_REQUEST, "Your account has been deactivated."),

  PENDING_CONFIRMATION(
      403,
      "EC-117",
      Constants.BAD_REQUEST,
      "Your account has not been activated yet. Account need to be activated by an activation link that arrives via email to the address you provided."),

  CURRENT_PASSWORD_INVALID(400, "EC-119", Constants.BAD_REQUEST, "Current password is invalid"),

  INVALID_LOGIN_CREDENTIALS(400, "EC-120", Constants.BAD_REQUEST, "Invalid email or password."),

  ENFORCE_PASSWORD_HISTORY(
      400,
      "EC-105",
      Constants.BAD_REQUEST,
      "Your new password cannot repeat any of your previous 10 passwords;"),

  INVALID_UPDATE_USER_REQUEST(400, "EC-120", Constants.BAD_REQUEST, "Email or Status is required."),

  EMAIL_EXISTS(
      409,
      "EC-101",
      Constants.BAD_REQUEST,
      "This email has already been used. Please try with a different email address."),

  EMAIL_SEND_FAILED_EXCEPTION(
      500,
      "EC-500",
      "Email Server Error",
      "Your email was unable to send because the connection to mail server was interrupted. Please check your inbox for mail delivery failure notice."),

  APPLICATION_ERROR(
      500,
      "EC-500",
      "Internal Server Error",
      "Sorry, an error has occurred and your request could not be processed. Please try again later.");

  private final int status;
  private final String code;
  private final String errorType;
  private final String description;

  private static class Constants {

    private static final String BAD_REQUEST = "Bad Request";
  }

  static class ErrorCodeSerializer extends StdSerializer<ErrorCode> {

    private static final long serialVersionUID = 1L;

    public ErrorCodeSerializer() {
      super(ErrorCode.class);
    }

    @Override
    public void serialize(
        ErrorCode error, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
        throws IOException {
      jsonGenerator.writeStartObject();
      jsonGenerator.writeNumberField("status", error.getStatus());
      jsonGenerator.writeStringField("error_type", error.getErrorType());
      jsonGenerator.writeStringField("error_code", error.getCode());
      jsonGenerator.writeStringField("error_description", error.getDescription());
      jsonGenerator.writeNumberField("timestamp", Instant.now().toEpochMilli());
      jsonGenerator.writeEndObject();
    }
  }
}
