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
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

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
      "Sorry, an error has occurred and your request could not be processed. Please try again later."),

  SITE_PERMISSION_ACCESS_DENIED(
      403, "EC-105", HttpStatus.FORBIDDEN.toString(), "Does not have permission to add site"),

  SITE_EXISTS(
      400, "EC-106", Constants.BAD_REQUEST, "Site exists with the given locationId and studyId"),

  LOCATION_ACCESS_DENIED(
      403, "EC-882", "Forbidden", "You do not have permission to view or add or update locations"),

  INVALID_ARGUMENTS(400, "EC_813", Constants.BAD_REQUEST, "Provided argument value is invalid"),

  USER_NOT_EXISTS(401, "EC_861", "Unauthorized", "User does not exist"),

  MISSING_REQUIRED_ARGUMENTS(400, "EC_812", Constants.BAD_REQUEST, "Missing required argument"),

  CUSTOM_ID_EXISTS(400, "EC_883", Constants.BAD_REQUEST, "customId already exists"),

  USER_NOT_ACTIVE(400, "EC_93", Constants.BAD_REQUEST, "User not Active"),

  USER_NOT_INVITED(
      400, "EC-869", Constants.BAD_REQUEST, "Provided email not exists or user not invited"),
  APP_NOT_FOUND(404, "EC-817", Constants.BAD_REQUEST, "App not found."),

  STUDY_NOT_FOUND(404, "EC-816", Constants.BAD_REQUEST, "Study not found");

  private final int status;
  private final String code;
  private final String errorType;
  private final String description;

  public static ErrorCode fromCodeAndDescription(String code, String description) {
    for (ErrorCode e : ErrorCode.values()) {
      if (StringUtils.equalsIgnoreCase(e.code, code)
          && StringUtils.equalsIgnoreCase(e.description, description)) {
        return e;
      }
    }
    return null; // not found
  }

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
