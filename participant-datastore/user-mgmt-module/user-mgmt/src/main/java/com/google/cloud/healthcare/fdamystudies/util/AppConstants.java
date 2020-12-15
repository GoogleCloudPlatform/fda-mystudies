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
  public static final String APPLICATION_ID = "id";
  public static final String APP_ID = "appId";
  public static final String TITLE = "title";

  public static final String KEY_USERID = "userId";
  public static final String STUDY_INFO_ID = "studyInfoId";
  public static final String EMAIL = "email";
  public static final String CUSTOM_APPLICATION_ID = "appId";
  public static final String USER_DETAILS_ID = "id";
  public static final String STUDY_LEVEL = "ST";
  public static final String GATEWAY_LEVEL = "GT";
  public static final String USER_REGISTRATION_CONTROLLER_ENDS_MESSAGE =
      "UserRegistrationController registerUser() - ends";
  public static final boolean FALSE = false;
  public static final String USER_REGD_FAILURE = "User Registration Failure";
  public static final String USER_REGD_FAILURE_DESC = "User Registration failed for email ";
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

  public static final String MISSING_REQUIRED_PARAMETER = "MissingRequiredParameter";
  public static final String INVALID_REQUEST_EXCEPTION = "InvalidRequestException";
  public static final String INVALID_EMAIL_CODE_EXCEPTION = "InvalidEmailCodeException";
  public static final String INVALID_USERID_EXCEPTION = "InvalidUserIdException";
  public static final String SYSTEM_EXCEPTION = "SystemException";
  public static final Integer EMAILID_VERIFIED_STATUS = 1;
  public static final String EMAIL_NOT_EXISTS = "EMAIL_NOT_EXISTS";
}
