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
  ACCOUNT_LOCKED(
      400,
      "EC_0001",
      Constants.BAD_REQUEST,
      "Due to consecutive failed sign-in attempts with incorrect password, your account has been locked for a period of 15 minutes. Please check your registered email inbox for assistance to reset your password in this period or wait until the lock period is over to sign in again."),

  TEMP_PASSWORD_EXPIRED(
      401,
      "EC_0002",
      HttpStatus.UNAUTHORIZED.toString(),
      "Your temporary password is expired. Please use the forgot password link to reset your password."),

  ACCOUNT_DEACTIVATED(403, "EC_0003", Constants.BAD_REQUEST, "Your account has been deactivated"),

  SITE_NOT_FOUND(404, "EC_0004", HttpStatus.NOT_FOUND.toString(), "Site not found"),

  INVALID_LOGIN_CREDENTIALS(
      400,
      "EC_0005",
      Constants.BAD_REQUEST,
      "Wrong email or password. Try again or click forgot password."),

  PASSWORD_EXPIRED(
      401,
      "EC_0006",
      HttpStatus.UNAUTHORIZED.toString(),
      "Your password is expired. Please use the forgot password link to reset your password."),

  EMAIL_EXISTS(
      409,
      "EC_0007",
      HttpStatus.CONFLICT.toString(),
      "This email has already been used. Please try with a different email address."),

  EMAIL_SEND_FAILED_EXCEPTION(
      500,
      "EC_0008",
      "Email Server Error",
      "Your verification email was unable to send because the connection to mail server was interrupted"),

  APPLICATION_ERROR(
      500,
      "EC_0009",
      "Internal Server Error",
      "Sorry, an error has occurred and your request could not be processed. Please try again later."),

  CURRENT_PASSWORD_INVALID(
      400, "EC_0010", Constants.BAD_REQUEST, "Current password entered is invalid"),

  ENFORCE_PASSWORD_HISTORY(
      400,
      "EC_0011",
      Constants.BAD_REQUEST,
      "Your new password cannot repeat any of your previous 10 passwords"),

  USER_NOT_ACTIVE(400, "EC_0012", Constants.BAD_REQUEST, "User is not active"),

  APP_NOT_FOUND(404, "EC_0013", HttpStatus.NOT_FOUND.toString(), "App not found"),

  STUDY_NOT_FOUND(404, "EC_0014", HttpStatus.NOT_FOUND.toString(), "Study not found"),

  LOCATION_NOT_FOUND(404, "EC_0015", HttpStatus.NOT_FOUND.toString(), "Location not found"),

  CANNOT_DECOMMISSION_SITE_FOR_ENROLLED_ACTIVE_STATUS(
      400,
      "EC_0016",
      Constants.BAD_REQUEST,
      "This location is being used as an active site in one or more studies and cannot be decomissioned"),

  NOT_SUPER_ADMIN_ACCESS(
      403,
      "EC_0017",
      HttpStatus.FORBIDDEN.toString(),
      "You are not authorized to access this information"),

  BAD_REQUEST(
      400, "EC_0018", Constants.BAD_REQUEST, "The request cannot be fulfilled due to bad syntax"),

  UNAUTHORIZED(401, "EC_0019", "Unauthorized", "Unauthorized or Invalid token"),

  INVALID_UPDATE_USER_REQUEST(
      400, "EC_0020", Constants.BAD_REQUEST, "Email ID or status to be provided"),

  SITE_PERMISSION_ACCESS_DENIED(
      403,
      "EC_0021",
      HttpStatus.FORBIDDEN.toString(),
      "You do not have permission to access this site"),

  SITE_EXISTS(
      400, "EC_0022", Constants.BAD_REQUEST, "Site exists with the given location ID and study ID"),

  LOCATION_ACCESS_DENIED(
      403,
      "EC_0023",
      HttpStatus.FORBIDDEN.toString(),
      "You do not have permission to view or add or update locations"),

  USER_NOT_FOUND(404, "EC_0024", HttpStatus.NOT_FOUND.toString(), "User not found"),

  CUSTOM_ID_EXISTS(400, "EC_0025", Constants.BAD_REQUEST, "Location ID already exists"),

  USER_NOT_INVITED(
      400,
      "EC_0026",
      Constants.BAD_REQUEST,
      "Provided email ID does not exists or user is not invited"),

  CANNOT_REACTIVATE(
      400, "EC_0027", Constants.BAD_REQUEST, "Can't activate an already actived location"),

  DEFAULT_SITE_MODIFY_DENIED(
      400, "EC_0028", Constants.BAD_REQUEST, "Default site can't be modified"),

  ALREADY_DECOMMISSIONED(
      400,
      "EC_0029",
      Constants.BAD_REQUEST,
      "Can't decommision an already decommissioned location"),

  LOCATION_UPDATE_DENIED(
      403, "EC_0030", "Forbidden", "You do not have permission to update the location"),

  OPEN_STUDY(
      403, "EC_0031", HttpStatus.FORBIDDEN.toString(), "Participant cannot be added to open study"),

  PERMISSION_MISSING(
      400, "EC_0032", Constants.BAD_REQUEST, "User should have atleast one permission"),

  SECURITY_CODE_EXPIRED(
      410,
      "EC_0034",
      HttpStatus.GONE.toString(),
      "This link is no longer valid to be used. Please contact the system admin for assistance with your account."),

  PARTICIPANT_REGISTRY_SITE_NOT_FOUND(
      400, "EC_0035", Constants.BAD_REQUEST, "Error in getting participants details"),

  DOCUMENT_NOT_IN_PRESCRIBED_FORMAT(
      400, "EC_0036", Constants.BAD_REQUEST, "Import document not in prescribed format"),

  FAILED_TO_IMPORT_PARTICIPANTS(
      500,
      "EC_0037",
      HttpStatus.INTERNAL_SERVER_ERROR.toString(),
      "Unable to import the document due to invalid format in the document content"),

  CANNOT_UPDATE_ENROLLMENT_TARGET_FOR_CLOSE_STUDY(
      400, "EC_0038", Constants.BAD_REQUEST, "Enrollment target update failed for closed study"),

  CANNOT_UPDATE_ENROLLMENT_TARGET_FOR_DECOMMISSIONED_SITE(
      400,
      "EC_0039",
      Constants.BAD_REQUEST,
      "Enrollment target failed to be updated decommissionned site"),

  CONSENT_DATA_NOT_AVAILABLE(
      400, "EC_0040", Constants.BAD_REQUEST, "Error in getting consent data"),

  INVALID_APPS_FIELDS_VALUES(
      400, "EC_0041", Constants.BAD_REQUEST, "Allowed values for 'fields' are studies, sites"),

  ADMIN_NOT_FOUND(404, "EC_0042", HttpStatus.NOT_FOUND.toString(), "Admin user not found"),

  PENDING_CONFIRMATION(
      403,
      "EC_0043",
      HttpStatus.NOT_FOUND.toString(),
      "Your account verification is pending.Please check your email for the activation link."),

  ACCOUNT_NOT_VERIFIED(
      403,
      "EC_0044",
      HttpStatus.NOT_FOUND.toString(),
      "Your account is not verified. Please verify your account by clicking on the link which has been sent to your registered email. if not received, would you like to resend verification link?"),

  USER_ALREADY_EXISTS(
      409,
      "EC_0045",
      HttpStatus.CONFLICT.toString(),
      "User with same email has already been registered. Please log in."),

  USER_NOT_EXISTS(401, "EC_0046", "Unauthorized", "User does not exist"),

  STUDY_PERMISSION_ACCESS_DENIED(
      403,
      "EC_0047",
      HttpStatus.FORBIDDEN.toString(),
      "You do not have permission to view/add study"),

  MANAGE_SITE_PERMISSION_ACCESS_DENIED(
      403, "EC_0048", HttpStatus.FORBIDDEN.toString(), "You do not have permission to manage site"),

  SITE_NOT_EXIST_OR_INACTIVE(
      400, "EC_0049", Constants.BAD_REQUEST, "Site doesn't exists or is inactive"),

  INVALID_ONBOARDING_STATUS(
      400, "EC_0050", HttpStatus.BAD_REQUEST.toString(), "Allowed values are: N, D, I and E"),

  CANNOT_DECOMMISSION_SITE_FOR_OPEN_STUDY(
      400, "EC_0051", Constants.BAD_REQUEST, "Cannot decomission site as study type is open"),

  INVALID_USER_STATUS(400, "EC-114", Constants.BAD_REQUEST, "Invalid user status"),

  CANNOT_ADD_SITE_FOR_OPEN_STUDY(
      403, "EC_0053", HttpStatus.FORBIDDEN.toString(), "Cannot add site to open study"),

  USER_ID_REQUIRED(400, "EC_0054", Constants.BAD_REQUEST, "User Id is required"),

  EMAIL_ID_OR_PASSWORD_NULL(
      400, "EC_0055", Constants.BAD_REQUEST, "emailId or password is blank in request"),

  APPLICATION_ID_MISSING(
      400, "EC_0056", Constants.BAD_REQUEST, "applicationId is missing in request header"),

  INVALID_DATA_SHARING_STATUS(400, "EC-120", Constants.BAD_REQUEST, "Invalid data sharing status."),

  INVALID_SOURCE_NAME(400, "EC_0121", Constants.BAD_REQUEST, "Invalid 'source' value"),

  APP_PERMISSION_ACCESS_DENIED(
      403,
      "EC_0123",
      HttpStatus.FORBIDDEN.toString(),
      "You do not have permission to access this app"),

  CANNOT_ADD_SITE_FOR_DECOMMISSIONED_LOCATION(
      400, "EC_0122", Constants.BAD_REQUEST, "Cannot add site for decommissioned location."),

  TEMP_PASSWORD_INVALID(400, "EC_0122", Constants.BAD_REQUEST, "Temporary password is invalid"),

  CANNOT_ADD_SITE_FOR_DEACTIVATED_STUDY(
      403, "EC_0124", HttpStatus.FORBIDDEN.toString(), "Cannot add site to Deactivated study"),

  CANNOT_ACTIVATE_SITE_FOR_DEACTIVATED_LOCATION(
      403,
      "EC_0126",
      HttpStatus.FORBIDDEN.toString(),
      "Cannot activate the site as the location is decommissioned"),

  CANNOT_ACTIVATE_SITE_FOR_DEACTIVATED_STUDY(
      403,
      "EC_0127",
      HttpStatus.FORBIDDEN.toString(),
      "Cannot activate the site as the study is deactivated"),

  CANNOT_ENABLE_PARTICIPANT(
      403,
      "EC_0125",
      HttpStatus.FORBIDDEN.toString(),
      "Invitation cannot be enabled as participant record is enabled in another site within the same study."),

  LOCATION_NAME_EXISTS(
      400, "EC_0068", Constants.BAD_REQUEST, "Sorry, a location with this name already exists");

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
