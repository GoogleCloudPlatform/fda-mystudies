/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.util;

public class AppConstants {

  private AppConstants() {}

  public static final String SERVER_TIMEZONE = "America/New_York";
  public static final String USER_EMAILID = "userEmail";
  public static final String APPLICATION_ID = "appInfoId";
  public static final String APP_ID = "appId";
  public static final String CLIENT_ID = "clientId";
  public static final String SECRET_KEY = "secretKey";
  public static final String TITLE = "title";

  public static final String ORGANIZATION_ID = "orgId";
  public static final String KEY_USERID = "userId";
  public static final String STUDY_INFO_ID = "studyInfoId";
  public static final String EMAIL = "email";
  public static final String CUSTOM_APPLICATION_ID = "appId";
  public static final String USER_DETAILS_ID = "userDetailsId";
  public static final String STUDY_LEVEL = "ST";
  public static final String GATEWAY_LEVEL = "GT";
  public static final String USER_REGISTRATION_CONTROLLER_ENDS_MESSAGE =
      "UserRegistrationController registerUser() - ends";
  public static final boolean FALSE = false;
  public static final String USER_REGD_FAILURE = "User Registration Failure";
  public static final String USER_REGD_FAILURE_DESC =
      "Request for account registration received from user with username ";

  public static final String AUDIT_LOG_APP_USER_CREATION_PART_DATASTORE_FAILURE_NAME =
      "App user creation failed on Participant Datastore";
  public static final String AUDIT_LOG_APP_USER_CREATION_PART_DATASTORE_FAILURE_DESC =
      "User creation failed on Participant Datastore for email %s after successful registration on Auth Server. Auth Server deletes user in such cases.";

  public static final String AUDIT_LOG_APP_USER_CREATION_AUTH_SERVER_FAILURE_NAME =
      "App user not created after failed registration on Auth Server";
  public static final String AUDIT_LOG_APP_USER_CREATION_AUTH_SERVER_FAILURE_DESC =
      "User creation failed on Participant Datastore for email %s after failed user registration on Auth Server. ";

  public static final String AUDIT_LOG_APP_USER_ACTIVATION_FAILURE_NAME =
      "App user account activation failed";
  public static final String AUDIT_LOG_APP_USER_ACTIVATION_FAILURE_WITH_USERID_DESC =
      "Account activation failed for app user account associated with User ID %s .";
  public static final String AUDIT_LOG_APP_USER_ACTIVATION_FAILURE_WITHOUT_USERID_DESC =
      "Account activation failed for app user account associated with username: %s from App ID: %s having Org ID: %s .";
  public static final String VERIFY_EMAILID_CONTROLLER_ENDS_MESSAGE =
      "VerifyEmailIdController verifyEmailId() - ends";
  public static final String CLIENT_TOKEN = "clientToken";
  public static final String USER_ID = "userId";
  public static final String ACCESS_TOKEN = "accessToken";

  public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
  public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
  public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
  public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
  public static final String HTTP_METHODS = "GET, POST, PUT, DELETE, OPTIONS, HEAD";
  public static final String OPTIONS_METHOD = "OPTIONS";

  public static final String TRUE_STR = "true";
  public static final String ERROR_STR = "Error";
  public static final String FALSE_STR = "false";

  public static final String CODE = "code";
  public static final String USER_MESSAGE = "userMessage";
  public static final String TYPE = "type";
  public static final String DETAIL_MESSAGE = "detailMessage";

  public static final String STATUS = "status";
  public static final String STATUS_MESSAGE = "StatusMessage";
  public static final String SUCCESS = "SUCCESS";
  public static final String FAILURE = "FAILURE";
  public static final String INVALID_CLIENTID_SECRETKEY = "Invalid clientId or secretKey";

  public static final String OPEN_STUDY = "OPEN";
  public static final String CLOSE_STUDY = "CLOSE";

  public static final String DEVICE_ANDROID = "android";
  public static final String DEVICE_IOS = "ios";

  public static final String STUDY = "Study";
  public static final String GATEWAY = "Gateway";

  public static final String WITHDRAWN = "Withdrawn";
  public static final String PARTICIPANT_LEVEL_ACCESS = "Participant";
  public static final String APP_LEVEL_ACCESS = "App User";
  public static final String NOT_APPLICABLE = "NA";
  public static final String EMAIL_EXIST_ERROR_FROM_AUTH_SERVER =
      "This email has already been used. Please try with different email address.";
  public static final String AUDIT_LOG_MOBILE_APP_CLIENT_ID = "FMSGCMOBAPP";
  public static final String AUDIT_LOG_AUTH_SERVER_CLIENT_ID = "FMSGCAUTHSVR";
  public static final String AUDIT_LOG_PART_DATASTORE_CLIENT_ID = "FMSGCPARDTST";
  public static final String USER_NOT_CREATED = "(Not created yet)";
  public static final String AUDIT_LOG_USER_REG_ATTEMPT_FAIL =
      "App user registration attempt failure";
}
