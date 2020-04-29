/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.utils;

public class AppConstants {

  private AppConstants() {}

  public static final String SERVER_TIMEZONE = "America/New_York";
  public static final String USER_EMAILID = "userEmail";
  public static final String APPLICATION_ID = "appId";
  public static final String ORGANIZATION_ID = "orgId";
  public static final String KEY_USERID = "userId";
  public static final String STUDY_ID = "studyId";
  public static final String VERIFIED = "Verified";
  public static final String SDF_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
  public static final String STATUS = "status";
  public static final String TITLE = "title";
  public static final String MESSAGE = "message";

  public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
  public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
  public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
  public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
  public static final String HTTP_METHODS = "GET, POST, PUT, DELETE, OPTIONS, HEAD";
  public static final String ACTIVE = "Active";
  public static final String PENDING = "Pending";
  public static final String MA = "MA";
  public static final String USWS = "USWS";
  public static final String FAILURE = "FAILURE";
  public static final String SUCCESS = "SUCCESS";
  public static final String AUTHENTICATION_FILTER_ENDS = "AuthenticationFilter doFilter() - ends";
  public static final String VERIDIED = "Verified";
  public static final String URS = "URS";
  public static final String RS = "RS";
  public static final String WCP = "WCP";

  public static final String AUDIT_EVENT_SIGN_IN_NAME = "Sign-in successful";
  public static final String AUDIT_EVENT_SIGN_IN_DESC = "User %s signed in successfully";

  public static final String AUDIT_EVENT_FAILED_SIGN_IN_NAME = "Sign-in attempt failure";
  public static final String AUDIT_EVENT_FAILED_SIGN_IN_DESC =
      "Sign-in attempt failed for username %s.";

  public static final String AUDIT_EVENT_FAILED_SIGN_IN_WRONG_EMAIL_NAME =
      "Sign-in attempt failure: unregistered username";
  public static final String AUDIT_EVENT_FAILED_SIGN_IN_WRONG_EMAIL_DESC =
      "A user attempted to sign-in with an unregistered username %s";

  public static final String AUDIT_EVENT_ACCOUNT_LOCK_NAME = "Account locked";
  public static final String AUDIT_EVENT_ACCOUNT_LOCK_DESC =
      "Account locked for %s for User ID %s  due to %s consecutively failed sign-in attempts with incorrect password.";

  public static final String AUDIT_EVENT_SIGN_OUT_NAME = "App user sign out success";
  public static final String AUDIT_EVENT_SIGN_OUT_DESC =
      "User ID %s was successfully signed out of the app.";

  public static final String AUDIT_EVENT_SIGN_OUT_UNSUCCESSFUL_NAME = "App user sign out failure";
  public static final String AUDIT_EVENT_SIGN_OUT_UNSUCCESSFUL_DESC =
      "User ID %s could not be signed out of the app.";

  public static final String AUDIT_EVENT_CHANGE_PASSWORD_SUCCESS_NAME = "Password change success";
  public static final String AUDIT_EVENT_CHANGE_PASSWORD_SUCCESS_DESC =
      "Password change for User ID %s was successful.";

  public static final String AUDIT_EVENT_CHANGE_PASSWORD_UNSUCCESSFUL_NAME =
      "Password change failure";
  public static final String AUDIT_EVENT_CHANGE_PASSWORD_UNSUCCESSFUL_DESC =
      "Password change failed for User ID %s .";

  public static final String AUDIT_EVENT_RESET_PASSWORD_SUCCESS_NAME = "Password reset success";
  public static final String AUDIT_EVENT_RESET_PASSWORD_SUCCESS_DESC =
      "Password reset for User ID %s was successful.";

  public static final String AUDIT_EVENT_RESET_PASSWORD_UNSUCCESSFUL_NAME =
      "Password reset failure";
  public static final String AUDIT_EVENT_RESET_PASSWORD_UNSUCCESSFUL_DESC =
      "Password reset for User ID %s, failed.";

  public static final String AUDIT_EVENT_FAILED_REFRESH_TOKEN_NAME = "FAILED RefreshToken";
  public static final String AUDIT_EVENT_FAILED_REFRESH_TOKEN_DESC =
      "Refresh token failed as token does not exist";

  public static final String AUDIT_EVENT_PASSWORD_HELP_NAME = "Password help requested";
  public static final String AUDIT_EVENT_PASSWORD_HELP_DESC =
      "Password help email requested by email ID %s";

  public static final String AUDIT_EVENT_PASSWORD_HELP_UNREGISTERED_USER_NAME =
      "Password help request failure: unregistered username";
  public static final String AUDIT_EVENT_PASSWORD_HELP_UNREGISTERED_USER_DESC =
      "A user attempted to request password help with an unregistered username %s";

  public static final String AUDIT_EVENT_SIGN_IN_WITH_TMP_PASSD_NAME =
      "Sign-in with temporary password: success";
  public static final String AUDIT_EVENT_SIGN_IN_WITH_TMP_PASSD_DESC =
      "User  %s  signed in with temporary password.";

  public static final String AUDIT_EVENT_SIGN_IN_WITH_TMP_PASSD_FAILURE_NAME =
      "Sign-in with temporary password: failure";
  public static final String AUDIT_EVENT_SIGN_IN_WITH_TMP_PASSD_FAILURE_DESC =
      "Sign-in attempt for username %s failed with temporary password.";

  public static final String AUDIT_EVENT_USER_REGISTRATION_SUCCESS_NAME =
      "App user registration success";
  public static final String AUDIT_EVENT_USER_REGISTRATION_SUCCESS_DESC =
      "App user registration successful for username %s  and user ID %s returned to Resource Server";

  public static final String AUDIT_EVENT_USER_REGISTRATION_FAILURE_NAME =
      "App user registration failure";
  public static final String AUDIT_EVENT_USER_REGISTRATION_FAILURE_DESC =
      "App user registration failed for username %s, no User ID returned to Resource Server";

  public static final String AUDIT_EVENT_DELETE_USER_AFTER_FAILURE_IN_REG_SERVER_NAME =
      "App user deleted: Resource Server user creation error";
  public static final String AUDIT_EVENT_DELETE_USER_AFTER_FAILURE_IN_REG_SERVER_DESC =
      "User creation failed on the Participant Datastore server and User ID %s record was deleted from Auth Server";

  public static final String AUDIT_EVENT_VALIDATION_OF_TOKEN_SUCCESS_NAME =
      "Validation of tokens successful";
  public static final String AUDIT_EVENT_VALIDATION_OF_TOKEN_SUCCESS_DESC =
      "Access Token, Client Credentials found valid in Validate Token request made by Resource Server for User ID %s.";

  public static final String AUDIT_EVENT_ACCESS_TOKEN_NOT_VALID_NAME = "Access Token not valid";
  public static final String AUDIT_EVENT_ACCESS_TOKEN_NOT_VALID_DESC =
      "Access Token found invalid in Validate Token request made by Resource Server for User ID %s.";

  public static final String AUDIT_EVENT_ACCESS_TOKEN_EXPIRED_NAME = "Access Token expired";
  public static final String AUDIT_EVENT_ACCESS_TOKEN_EXPIRED_DESC =
      "Access Token found expired in Validate Token request made by Resource Server for User ID %s.";

  public static final String AUDIT_EVENT_INVALID_CLIENT_APPLICATION_CREDENTIALS_NAME =
      "Invalid client application credentials";
  public static final String AUDIT_EVENT_INVALID_CLIENT_APPLICATION_CREDENTIALS_DESC =
      "Client Application credentials found invalid in Validate Token request made by Resource Server for User ID %s.";

  public static final String AUDIT_EVENT_CLIENT_CREDENTIALS_SUCCESS_NAME =
      "Client credentials validation success";
  public static final String AUDIT_EVENT_CLIENT_CREDENTIALS_SUCCESS_DESC =
      "Client credentials found valid in client validation request made by Resource Server for.";

  public static final String AUDIT_EVENT_INVALID_CLIENT_CREDENTIALS_NAME =
      "Invalid client credentials";
  public static final String AUDIT_EVENT_INVALID_CLIENT_CREDENTIALS_DESC =
      "Client credentials found invalid in client validation request made by Resource Server for.";

  public static final String AUDIT_EVENT_ACCESS_TOKEN_GENERATION_SUCCESS_NAME =
      "New Access Token generated.";
  public static final String AUDIT_EVENT_ACCESS_TOKEN_GENERATION_SUCCESS_DESC =
      "New Access Token generated for user ID %s on receipt of Refresh Token request, and sent to mobile app.";

  public static final String AUDIT_EVENT_ACCESS_TOKEN_GENERATION_FAILURE_NAME =
      "New Access Token generation failure";
  public static final String AUDIT_EVENT_ACCESS_TOKEN_GENERATION_FAILURE_DESC =
      "New Access Token generation failed for user ID %s after receipt of Refresh Token request and no new Access Token was sent to the mobile app.";

  public static final String AUDIT_EVENT_DELETE_USER_NAME = "App user deleted";
  public static final String AUDIT_EVENT_DELETE_USER_DESC =
      "User account deactivated on the Participant Datastore server and User ID %s record was deleted from Auth Server";

  public static final String AUDIT_EVENT_DELETE_USER_FAILURE_NAME = "App user deletion failed";
  public static final String AUDIT_EVENT_DELETE_USER_FAILURE_DESC =
      "User account deactivated on the Participant Datastore server but User ID %s record could not be deleted from Auth Server";

  public static final String PARTICIPANT_LEVEL_ACCESS = "Participant";
  public static final String APP_LEVEL_ACCESS = "App User";
  public static final String NOT_APPLICABLE = "NA";
  public static final String AUDIT_LOG_MOBILE_APP_CLIENT_ID = "FMSGCMOBAPP";
  public static final String AUDIT_LOG_PARTICIPANT_DATASTORE_CLIENT_ID = "FMSGCPARDTST";
  public static final String INVALID_USERNAME_PASSWORD_MSG = "INVALID_USERNAME_PASSWORD_MSG";
}
