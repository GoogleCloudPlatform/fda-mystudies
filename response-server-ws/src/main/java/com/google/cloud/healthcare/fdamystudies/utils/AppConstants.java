/*******************************************************************************
 * Copyright 2020 Google LLC
 * 
 * Use of this source code is governed by an MIT-style license that can be found in the LICENSE file
 * or at https://opensource.org/licenses/MIT.
 ******************************************************************************/
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
  public static final String RESPONSE_DATA_QUERY_NAME_LEGACY = "response_query";
  public static final String TRUE_STR = "true";
  public static final String VALUE_KEY_STR = "value";
  public static final String PARTICIPANT_ID_KEY = "participantId";
  public static final String PARTICIPANT_ID_RESPONSE = "ParticipantId";
  public static final String CREATED_TS_KEY = "createdTimestamp";
  public static final String DATE_FORMAT_RESPONSE = "YYYY-MM-dd HH:mm:ss:SSS";
  public static final String CREATED_RESPONSE = "Created";
  public static final String RESULT_TYPE_KEY = "resultType";
  public static final String QUESTION_ID_KEY = "key";
  public static final String FIELD_PATH_ACTIVITY_ID = "metadata.activityId";
  public static final String FIELD_PATH_SITE_ID = "siteId";
  public static final String PARTICIPANT_TOKEN_IDENTIFIER_KEY = "tokenIdentifier";
  public static final String PARTICIPANT_IDENTIFIER_KEY = "participantIdentifier";
  public static final String GROUPED_FIELD_KEY = "grouped";
  public static final Object DATA_FIELD_KEY = "data";
  public static final String RESULTS_FIELD_KEY = "results";
  public static final String RAW_RESPONSE_FIELD_KEY = "rawResponseData";
  public static final Object PROPERTY_NAME_CLASS = "class";
  public static final String CLIENT_TOKEN_KEY = "clientToken";
  public static final String USER_ID_KEY = "userId";
  public static final String ACCESS_TOKEN_KEY = "accessToken";
  public static final String ORG_ID_HEADER = "orgId";
  public static final String APPLICATION_ID_HEADER = "applicationId";
  public static final String AUTHORIZATION_HEADER = "Authorization";
  public static final String STUDY_ID_PARAM = "studyId";
  public static final String ACTIVITY_ID_PARAM = "activityId";
  public static final String ACTIVITY_VERSION_PARAM = "activityVersion";
  public static final String ERROR_STR = AppConstants.ERROR_STR;
  public static final String OPTIONS_METHOD = "OPTIONS";
  public static final String CLIENT_ID_PARAM = "clientId";
  public static final String CLIENT_SECRET_PARAM = "clientSecret";
  public static final String SITE_ID_KEY = "siteId";
  public static final String STUDY_VERSION_KEY = "studyVersion";
  public static final String SHARING_CONSENT_KEY = "sharingConsent";
  public static final String PARTICIPANTS_NAME = "Participants";
  public static final String ACTIVITIES_NAME = "Activities";
}
