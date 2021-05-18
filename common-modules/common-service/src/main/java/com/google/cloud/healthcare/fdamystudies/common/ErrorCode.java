/*
 * Copyright 2020-2021 Google LLC
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
      400,
      "EC_0002",
      HttpStatus.BAD_REQUEST.toString(),
      "The temporary password entered is either invalid or expired. Please use the Forgot Password link to get password help."),

  ACCOUNT_DEACTIVATED(
      403, "EC_0003", HttpStatus.FORBIDDEN.toString(), "Your account has been deactivated"),

  SITE_NOT_FOUND(404, "EC_0004", HttpStatus.NOT_FOUND.toString(), "Site(s) not found"),

  INVALID_LOGIN_CREDENTIALS(
      400,
      "EC_0005",
      Constants.BAD_REQUEST,
      "Wrong email or password. Try again or click Forgot Password"),

  PASSWORD_EXPIRED(
      400,
      "EC_0006",
      HttpStatus.BAD_REQUEST.toString(),
      "Your password has expired. Please use the Forgot Password link to set up a new password."),

  EMAIL_EXISTS(
      409,
      "EC_0007",
      HttpStatus.CONFLICT.toString(),
      "This email has already been used. Please try with a different email address."),

  EMAIL_SEND_FAILED_EXCEPTION(
      500,
      "EC_0008",
      HttpStatus.INTERNAL_SERVER_ERROR.toString(),
      "Sorry, an error occurred and we could not send you the email. Please try again later."),

  APPLICATION_ERROR(
      500,
      "EC_0009",
      HttpStatus.INTERNAL_SERVER_ERROR.toString(),
      "Sorry, an error has occurred and your request could not be processed. Please try again later."),

  CURRENT_PASSWORD_INVALID(
      400, "EC_0010", Constants.BAD_REQUEST, "The current password entered is incorrect"),

  ENFORCE_PASSWORD_HISTORY(
      400,
      "EC_0011",
      Constants.BAD_REQUEST,
      "Your new password should not match any of your previous 10 passwords"),

  USER_NOT_ACTIVE(
      400, "EC_0012", Constants.BAD_REQUEST, "This admin does not have an active account"),

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

  UNAUTHORIZED(401, "EC_0019", HttpStatus.UNAUTHORIZED.toString(), "Unauthorized or Invalid token"),

  INVALID_UPDATE_USER_REQUEST(
      400, "EC_0020", Constants.BAD_REQUEST, "Email ID or status to be provided"),

  SITE_PERMISSION_ACCESS_DENIED(
      403,
      "EC_0021",
      HttpStatus.FORBIDDEN.toString(),
      "You do not have permission to access this site"),

  SITE_EXISTS(
      400,
      "EC_0022",
      Constants.BAD_REQUEST,
      "Site already exists for this combination of location ID and study ID"),

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
      "This email either does not exist or has not been sent an invitation yet."),

  CANNOT_REACTIVATE(
      400, "EC_0027", Constants.BAD_REQUEST, "This location already has 'Active' status"),

  DEFAULT_SITE_MODIFY_DENIED(400, "EC_0028", Constants.BAD_REQUEST, "This site cannot be modified"),

  ALREADY_DECOMMISSIONED(
      400, "EC_0029", Constants.BAD_REQUEST, "This location is already decommissioned"),

  LOCATION_UPDATE_DENIED(
      403,
      "EC_0030",
      HttpStatus.FORBIDDEN.toString(),
      "You do not have permission to update the location"),

  OPEN_STUDY(
      403,
      "EC_0031",
      HttpStatus.FORBIDDEN.toString(),
      "Participant(s) cannot be added to the registry of an open study"),

  PERMISSION_MISSING(
      400,
      "EC_0032",
      Constants.BAD_REQUEST,
      "The admin should have atleast one permission in the system to access this resource"),

  SECURITY_CODE_EXPIRED(
      410,
      "EC_0034",
      HttpStatus.GONE.toString(),
      "This link is no longer valid to be used. Please contact the system admin for assistance with your account or sign in if already registered."),

  PARTICIPANT_REGISTRY_SITE_NOT_FOUND(
      400, "EC_0035", Constants.BAD_REQUEST, "Error in getting participants details"),

  DOCUMENT_NOT_IN_PRESCRIBED_FORMAT(
      400, "EC_0036", Constants.BAD_REQUEST, "The uploaded file is not in the prescribed format"),

  FAILED_TO_IMPORT_PARTICIPANTS(
      500,
      "EC_0037",
      HttpStatus.INTERNAL_SERVER_ERROR.toString(),
      "The uploaded file does not adhere to the given template"),

  CANNOT_UPDATE_ENROLLMENT_TARGET_FOR_CLOSE_STUDY(
      400,
      "EC_0038",
      Constants.BAD_REQUEST,
      "Enrollment target update failed (the study is a closed study)"),

  CANNOT_UPDATE_ENROLLMENT_TARGET_FOR_DECOMMISSIONED_SITE(
      400,
      "EC_0039",
      Constants.BAD_REQUEST,
      "Enrollment target failed to be updated (the site is decommissionned)"),

  CONSENT_DATA_NOT_AVAILABLE(
      400, "EC_0040", Constants.BAD_REQUEST, "Error in getting consent data"),

  INVALID_APPS_FIELDS_VALUES(
      400, "EC_0041", Constants.BAD_REQUEST, "Allowed values for 'fields' are studies, sites"),

  ADMIN_NOT_FOUND(404, "EC_0042", HttpStatus.NOT_FOUND.toString(), "Admin user not found"),

  PENDING_CONFIRMATION(
      403,
      "EC_0043",
      HttpStatus.BAD_REQUEST.toString(),
      "Your account is pending activation. Please check your email for details and sign in to complete activation."),

  ACCOUNT_NOT_VERIFIED(
      403,
      "EC_0044",
      HttpStatus.BAD_REQUEST.toString(),
      "Your account is pending activation. Please check your email for details."),

  USER_ALREADY_EXISTS(
      409,
      "EC_0045",
      HttpStatus.CONFLICT.toString(),
      "An account with this email is already registered. Please sign in."),

  USER_NOT_EXISTS(401, "EC_0046", HttpStatus.UNAUTHORIZED.toString(), "User does not exist"),

  STUDY_PERMISSION_ACCESS_DENIED(
      403,
      "EC_0047",
      HttpStatus.FORBIDDEN.toString(),
      "You do not have permission to view/edit this study"),

  MANAGE_SITE_PERMISSION_ACCESS_DENIED(
      403,
      "EC_0048",
      HttpStatus.FORBIDDEN.toString(),
      "You do not have permission to manage this site"),

  SITE_NOT_EXIST_OR_INACTIVE(
      400, "EC_0049", Constants.BAD_REQUEST, "Site doesn't exist or is inactive"),

  INVALID_ONBOARDING_STATUS(
      400, "EC_0050", HttpStatus.BAD_REQUEST.toString(), "Allowed values are: N, D, I and E"),

  CANNOT_DECOMMISSION_SITE_FOR_OPEN_STUDY(
      400,
      "EC_0051",
      Constants.BAD_REQUEST,
      "The decommission action cannot be taken with this site as it belongs to an open study"),

  INVALID_USER_STATUS(400, "EC-0052", Constants.BAD_REQUEST, "Invalid admin user status"),

  CANNOT_ADD_SITE_FOR_OPEN_STUDY(
      403, "EC_0053", HttpStatus.FORBIDDEN.toString(), "Cannot add site to an open study"),

  USER_ID_REQUIRED(400, "EC_0054", Constants.BAD_REQUEST, "User ID is required"),

  LOCATION_ID_UNIQUE(
      400,
      "EC_0058",
      Constants.BAD_REQUEST,
      "Location ID must be unique across the location directory"),

  EMAIL_ID_OR_PASSWORD_NULL(
      400, "EC_0128", Constants.BAD_REQUEST, "EmailId or password is blank in request"),

  APPLICATION_ID_MISSING(
      400, "EC_0129", Constants.BAD_REQUEST, "ApplicationId is missing in request header"),

  INVALID_FILE_UPLOAD(400, "EC_0057", Constants.BAD_REQUEST, "Please upload a .xls or .xlsx file"),

  INVALID_DATA_SHARING_STATUS(400, "EC-130", Constants.BAD_REQUEST, "Invalid data sharing status."),

  INVALID_SOURCE_NAME(400, "EC_0121", Constants.BAD_REQUEST, "Invalid 'source' value"),

  APP_PERMISSION_ACCESS_DENIED(
      403,
      "EC_0123",
      HttpStatus.FORBIDDEN.toString(),
      "You do not have permission to access this app."),

  CANNOT_ADD_SITE_FOR_DECOMMISSIONED_LOCATION(
      400, "EC_0122", Constants.BAD_REQUEST, "Cannot add site using a decommissioned location."),

  CANNOT_ADD_SITE_FOR_DEACTIVATED_STUDY(
      403,
      "EC_0124",
      HttpStatus.FORBIDDEN.toString(),
      "This study is deactivated. Sites cannot be added to deactivated studies."),

  LOCATION_DECOMMISSIONED(
      400,
      "EC_0069",
      Constants.BAD_REQUEST,
      "This site cannot be activated as the associated location is decommissioned"),

  CANNOT_ACTIVATE_SITE_FOR_DEACTIVATED_STUDY(
      403,
      "EC_0127",
      HttpStatus.FORBIDDEN.toString(),
      "This study is deactivated. Sites cannot be re-activated for deactivated studies."),

  CANNOT_ENABLE_PARTICIPANT(
      403,
      "EC_0125",
      HttpStatus.FORBIDDEN.toString(),
      "1 or more participant record(s) could not be enabled. This could happen if the emails exist in enabled state in another site of the same study."),

  CANNOT_DELETE_INVITATION(
      403,
      "EC_0065",
      HttpStatus.FORBIDDEN.toString(),
      "This admin's account is already active. Please try deactivating instead if you wish to revoke access to the Participant Manager."),

  TOKEN_EXPIRED(
      410,
      "EC_0066",
      HttpStatus.GONE.toString(),
      "The token entered is no longer valid. Please contact the site coordinator for assistance."),

  LOCATION_NAME_EXISTS(
      400, "EC_0068", Constants.BAD_REQUEST, "Sorry, a location with this name already exists"),

  NO_SITES_FOUND(404, "EC_0070", HttpStatus.NOT_FOUND.toString(), "No sites found"),

  NO_STUDIES_FOUND(
      404,
      "EC_0071",
      HttpStatus.NOT_FOUND.toString(),
      "This view displays study-wise enrollment if you manage multiple sites."),

  NO_APPS_FOUND(
      404,
      "EC_0072",
      HttpStatus.NOT_FOUND.toString(),
      "This view displays app-wise enrollment if you manage multiple studies."),

  USER_EMAIL_EXIST(400, "EC_0064", Constants.BAD_REQUEST, "The email already exists"),

  REGISTRATION_EMAIL_SEND_FAILED(
      500,
      "EC_0075",
      "Internal Server Error",
      "Sorry, an error occurred and we could not send you the email required to complete account activation. Please try again."),

  UNSUPPORTED_SORTBY_VALUE(
      400, "EC_0076", HttpStatus.BAD_REQUEST.toString(), "Invalid sortBy value"),

  UNSUPPORTED_SORT_DIRECTION_VALUE(
      400, "EC_0077", HttpStatus.BAD_REQUEST.toString(), "Invalid sorting direction"),

  FEEDBACK_ERROR_MESSAGE(
      500,
      "EC_0073",
      "Internal Server Error",
      "Sorry, an error occurred and your feedback could not be sent to the organization. Please retry in some time."),

  CONTACT_US_ERROR_MESSAGE(
      500,
      "EC_0074",
      "Internal Server Error",
      "Sorry, an error occurred and your inquiry could not be sent to the organization. Please retry in some time."),

  TEMP_PASSWORD_INCORRECT(
      400, "EC_0078", Constants.BAD_REQUEST, "The temporary password entered is incorrect."),

  ACTIVE_STUDY_ENROLLED_PARTICIPANT(
      400,
      "EC_0079",
      Constants.BAD_REQUEST,
      "This site belongs to an active study that has one or more actively enrolled participants, and cannot be decommissioned.");

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
