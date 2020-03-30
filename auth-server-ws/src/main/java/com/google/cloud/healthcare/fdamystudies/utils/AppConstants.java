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

  public static final String AUDIT_EVENT_SIGN_IN_NAME = "SIGN IN";
  public static final String AUDIT_EVENT_SIGN_IN_DESC = "User %s signed in";

  public static final String AUDIT_EVENT_FAILED_SIGN_IN_NAME = "FAILED SIGN IN";
  public static final String AUDIT_EVENT_FAILED_SIGN_IN_DESC = "User %s sign in failed";
  public static final String AUDIT_EVENT_FAILED_SIGN_IN_WRONG_EMAIL_DESC =
      "Email %s does not exist";

  public static final String AUDIT_EVENT_SIGN_OUT_NAME = "SIGN OUT SUCCESSFULL";
  public static final String AUDIT_EVENT_SIGN_OUT_DESC = "User %s signed out successfully";

  public static final String AUDIT_EVENT_SIGN_OUT_UNSUCCESSFUL_NAME = "SIGN OUT UNSUCCESSFULL";
  public static final String AUDIT_EVENT_SIGN_OUT_UNSUCCESSFUL_DESC =
      "User %s signed out unsuccessfull";

  public static final String AUDIT_EVENT_CHANGE_PASSWORD_NAME = "CHANGE PASSWORD SUCCESSFUL";
  public static final String AUDIT_EVENT_CHANGE_PASSWORD_DESC =
      "Password changed successfully for %s";

  public static final String AUDIT_EVENT_CHANGE_PASSWORD_UNSUCCESSFUL_NAME =
      "CHANGE PASSWORD UNSUCCESSFUl";
  public static final String AUDIT_EVENT_CHANGE_PASSWORD_UNSUCCESSFUL_DESC =
      "Password changed unsuccessfull for %s";

  public static final String AUDIT_EVENT_FAILED_REFRESH_TOKEN_NAME = "FAILED RefreshToken";
  public static final String AUDIT_EVENT_FAILED_REFRESH_TOKEN_DESC =
      "Refresh token failed as token does not exist";

  public static final String AUDIT_EVENT_PASSWORD_HELP_NAME = "PASSWORD HELP";
  public static final String AUDIT_EVENT_PASSWORD_HELP_DESC =
      "Password Help sent to user. (User ID = %s)";

  public static final String AUDIT_EVENT_SIGN_IN_WITH_TMP_PASSD_NAME = "SIGN IN WITH TEMP PASSWORD";
  public static final String AUDIT_EVENT_SIGN_IN_WITH_TMP_PASSD_DESC =
      "User  %s signed in with temp password";
}
