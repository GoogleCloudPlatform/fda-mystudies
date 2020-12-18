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
  public static final String APPLICATION_ID = "appId";
  public static final String USER_ID = "userId";
  public static final String STUDY_ID = "studyId";
  public static final String EMAIL = "email";
  public static final String CUSTOM_STUDY_ID = "customId";
  public static final String STUDY_TYPE = "studyType";
  public static final String SUCCESS = "SUCCESS";

  public static final String OPEN_STUDY = "OPEN";
  public static final String CLOSE_STUDY = "CLOSE";

  public static final String AUDIT_EVENT_UPDATE_STUDY_STATE_NAME =
      "Save or update of an user study info";
  public static final String AUDIT_EVENT_UPDATE_STUDY_STATE_DESC =
      "Study state has been updated for custom study id :%s";

  public static final String AUDIT_EVENT_UPDATE_STUDY_STATE_FAILED_NAME =
      "Save or update of an user study info failed";
  public static final String AUDIT_EVENT_UPDATE_STUDY_STATE_FAILED_DESC =
      "Study state has been unscuccessful for custom study id :%s";

  public static final String CLIENT_ID = "clientId";
  public static final String SECRET_KEY = "secretKey";

  public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
  public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
  public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
  public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
  public static final String HTTP_METHODS = "GET, POST, PUT, DELETE, OPTIONS, HEAD";

  public static final String CLIENT_TOKEN_KEY = "clientToken";
  public static final String ACCESS_TOKEN_KEY = "accessToken";
  public static final String OPTIONS_METHOD = "OPTIONS";
  public static final String TRUE_STR = "true";
  public static final String ERROR_STR = "Error";

  public static final String CODE = "code";
  public static final String USER_MESSAGE = "userMessage";
  public static final String TYPE = "type";
  public static final String DETAIL_MESSAGE = "detailMessage";
  public static final String PARTICIPANT_ID = "participantId";
  public static final String DELETE_RESPONSES = "deleteResponses";
  public static final String RESP_SERVER_APPLICATION_ID = "applicationId";
  public static final String RESP_SERVER_CLIENT_ID = "clientId";
  public static final String RESP_SERVER_CLIENT_SECRET_KEY = "clientSecret";
}
