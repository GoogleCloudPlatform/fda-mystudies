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
  public static final String ORGANIZATION_ID = "orgId";
  public static final String KEY_USERID = "userId";
  public static final String STUDY_ID = "studyId";
  public static final String EMAIL = "email";
  public static final String CUSTOM_STUDY_ID = "customId";
  public static final String STUDY_TYPE = "studyType";
  public static final String ENROLLED = "Enrolled";
  public static final String WITHDRAWN = "Withdrawn";
  public static final String SUCCESS = "SUCCESS";

  public static final String OPEN_STUDY = "OPEN";
  public static final String CLOSE_STUDY = "CLOSE";

  public static final String AUDIT_EVENT_UPDATE_STUDY_STATE_NAME =
      "Study State saved/updated for participant";
  public static final String AUDIT_EVENT_UPDATE_STUDY_STATE_DESC =
      "Study State \"%s\" saved or updated for participant in Participant Datastore. ";

  public static final String AUDIT_EVENT_UPDATE_STUDY_STATE_FAILED_NAME =
      "Study State save/update operation failure";
  public static final String AUDIT_EVENT_UPDATE_STUDY_STATE_FAILED_DESC =
      "Study State \"%s\" failed to get saved/updated for participant in Participant Datastore. ";

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
  public static final String FALSE_STR = "false";

  public static final String CODE = "code";
  public static final String USER_MESSAGE = "userMessage";
  public static final String TYPE = "type";
  public static final String DETAIL_MESSAGE = "detailMessage";
  public static final String PARTICIPANT_ID = "participantId";
  public static final String DELETE_RESPONSES = "deleteResponses";
  public static final String RESP_SERVER_APPLICATION_ID = "applicationId";
  public static final String RESP_SERVER_CLIENT_ID = "clientId";
  public static final String RESP_SERVER_CLIENT_SECRET_KEY = "clientSecret";

  public static final String PARTICIPANT_LEVEL_ACCESS = "Participant";
  public static final String APP_LEVEL_ACCESS = "App User";
  public static final String NOT_APPLICABLE = "NA";
  public static final String AUDIT_LOG_MOBILE_APP_CLIENT_ID = "FMSGCMOBAPP";
  public static final String AUDIT_LOG_PARTICIPANT_DATASTORE_CLIENT_ID = "FMSGCPARDTST";

  public static final String AUDIT_EVENT_ENROLL_SUCCESS_NAME = "Enrolment into study successful";
  public static final String AUDIT_EVENT_ENROLL_SUCCESS_DESC =
      "App user was enrolled into study successfully as a Participant. Enrolment Status updated to \"%s\", Participant ID: %s";

  public static final String AUDIT_EVENT_APP_USER_ELIGIBLE_NAME =
      "App user found eligible for study";
  public static final String AUDIT_EVENT_APP_USER_ELIGIBLE_DESC =
      "App user found eligible for study.  Study Type: %s, Token used/generated: %s";

  public static final String AUDIT_EVENT_APP_USER_INELIGIBLE_NAME =
      "App user found ineligible for study";
  public static final String AUDIT_EVENT_APP_USER_INELIGIBLE_DESC =
      "App user found in-eligible for study.";
  public static final String AUDIT_EVENT_INVALID_ENROLLMENT_TOKEN_NAME =
      "Enrollment Token found invalid";
  public static final String AUDIT_EVENT_INVALID_ENROLLMENT_TOKEN_DESC =
      "Enrolment Token entered by app user found invalid. Study Type: Closed, Token used: %s.";

  public static final String AUDIT_EVENT_ENROLL_FAIL_NAME = "Enrolment into study failed";
  public static final String AUDIT_EVENT_ENROLL_FAIL_DESC =
      "App user could not be enrolled into study as a Participant.";

  public static final String AUDIT_EVENT_WITHDRAW_SUCCESS_NAME = "Withdrawal from study successful";
  public static final String AUDIT_EVENT_WITHDRAW_SUCCESS_DESC =
      "Participant withdrawn from study. Enrolment Status: \"%s\"";

  public static final String AUDIT_EVENT_WITHDRAW_FAIL_NAME = "Withdrawal from study: failure";
  public static final String AUDIT_EVENT_WITHDRAW_FAIL_DESC =
      "Participant withdrawal from study failed.";
  public static final String NOT_ELIGIBLE = "notEligible";
}
