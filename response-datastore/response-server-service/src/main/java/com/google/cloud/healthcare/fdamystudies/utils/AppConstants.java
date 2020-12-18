/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.utils;

public class AppConstants {
  public static final String HYPHEN = "-";
  public static final String RESPONSES = "RESPONSES";
  public static final String EMPTY_STR = "";
  public static final String FAILURE = "FAILURE";
  public static final int RESPONSE_DATA_SAVE_RETRY_ATTEMPTS = 3;
  public static final String JSON_FILE_EXTENSION = ".json";
  public static final String FILE_SEPARATOR = "/";
  public static final String RESPONSE_DATA_SCHEMA_NAME_LEGACY = "MobileAppResponse";
  public static final String RESPONSE_DATA_QUERY_NAME_LEGACY = "firestore_response_query";
  public static final String TRUE_STR = "true";
  public static final String VALUE_KEY_STR = "value";
  public static final String PARTICIPANT_ID_KEY = "participantId";
  public static final String PARTICIPANT_ID_RESPONSE = "ParticipantId";
  public static final String CREATED_TS_KEY = "createdTimestamp";
  public static final String ISO_DATE_FORMAT_RESPONSE = "yyyy-MM-dd'T'HH:mm:ss:SSSZZZZZ";
  public static final String CREATED_RESPONSE = "Created";
  public static final String RESULT_TYPE_KEY = "resultType";
  public static final String QUESTION_ID_KEY = "key";
  // TODO: The score sum feature should be properly implemented. This dummy question
  // approach is a short-term workaround.
  public static final String DUMMY_SUM_QUESTION_KEY = "_SUM";
  public static final String FIELD_PATH_ACTIVITY_ID = "metadata.activityId";
  public static final String PARTICIPANT_TOKEN_IDENTIFIER_KEY = "tokenId";
  public static final String PARTICIPANT_IDENTIFIER_KEY = "participantId";
  public static final String GROUPED_FIELD_KEY = "grouped";
  public static final Object DATA_FIELD_KEY = "data";
  public static final String RESULTS_FIELD_KEY = "results";
  public static final String RAW_RESPONSE_FIELD_KEY = "rawResponseData";
  public static final Object PROPERTY_NAME_CLASS = "class";
  public static final String USER_ID_KEY = "userId";
  public static final String APPLICATION_ID_HEADER_WCP = "applicationId";
  public static final String APPLICATION_ID_HEADER = "appId";
  public static final String AUTHORIZATION_HEADER = "Authorization";
  public static final String STUDY_ID_PARAM = "studyId";
  public static final String ACTIVITY_ID_KEY = "activityId";
  public static final String ACTIVITY_VERSION_PARAM = "activityVersion";
  public static final String ERROR_STR = "Error";
  public static final String OPTIONS_METHOD = "OPTIONS";
  public static final String SITE_ID_KEY = "siteId";
  public static final String STUDY_VERSION_KEY = "studyVersion";
  public static final String SHARING_CONSENT_KEY = "sharingConsent";
  public static final String ACTIVITY_TYPE_KEY = "activityType";
  public static final String ACTIVITY_TYPE_TASK = "task";
  public static final String PARTICIPANT_METADATA_KEY = "Participants";
  public static final String ACTIVITIES_COLLECTION_NAME = "Activities";
  public static final String SUCCESS_MSG = "SUCCESS";
  public static final String COMPLETED = "Completed";
  public static final String WITHDRAWAL_STATUS_KEY = "withdrawalStatus";
  public static final int FS_BATCH_COMMIT_LIMIT = 500;

  public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
  public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
  public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
  public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
  public static final String HTTP_METHODS = "GET, POST, PUT, DELETE, OPTIONS, HEAD";
  public static final String CODE = "code";
  public static final String USER_MESSAGE = "userMessage";
  public static final String TYPE = "type";
  public static final String DETAIL_MESSAGE = "detailMessage";
  public static final String BASIC_PREFIX = "Basic ";
  public static final String COMMA_STR = ",";
}
