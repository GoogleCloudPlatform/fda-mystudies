/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.testutils;

public class Constants {

  public static final String VERSION_1_0 = "1.0";
  public static final String VERSION_1_2 = "1.2";
  public static final String VERSION_1_3 = "1.3";

  // A very long version to cause db exception while saving study_consent
  public static final String VERSION_VERY_LONG =
      "very-long-content-very-long-content-very-long-content-very-long-"
          + "content-very-long-content-very-long-content-very-long-content-"
          + "very-long-content-very-long-content-very-long-content-very-long-"
          + "content-very-long-content-very-long-content-very-long-content-"
          + "very-long-content-very-long-content-very-long-content-very-long-"
          + "content-very-long-content-very-long-content-very-long-content-very-"
          + "long-content-very-long-content-very-long-content-very-long-content-"
          + "very-long-content-very-long-content-very-long-content-";
  public static final String CONTENT_1_0 = "text pdf content";
  public static final String CONTENT_1_0_UPDATED = CONTENT_1_0 + " updated";
  public static final String CONTENT_1_2 = "text pdf content 1.2";
  public static final String ENCODED_CONTENT_1_0 = "dGV4dCBwZGYgY29udGVudA==";
  public static final String ENCODED_CONTENT_1_0_UPDATED = "dGV4dCBwZGYgY29udGVudCB1cGRhdGVk";
  public static final String ENCODED_CONTENT_1_2 = "dGV4dCBwZGYgY29udGVudCAxLjI=";

  public static final String ACCESS_TOKEN_VALUE = "1";
  public static final String CLIENT_TOKEN_VALUE = "1";
  public static final String ACCESS_TOKEN_HEADER = "accessToken";
  public static final String CLIENT_TOKEN_HEADER = "clientToken";
  public static final String USER_ID_HEADER = "userId";
  public static final String VALID_USER_ID = "kR2g5m2pJPP0P31-WNFYK8Al7jBP0mJ-cTSFJJHJ4DewuCg";
  public static final String FIELD_VERSION = "version";
  public static final String FIELD_STATUS = "status";
  public static final String FIELD_PDF = "pdf";
  public static final String FIELD_CONSENT = "consent";
  public static final String FIELD_ELIGIBILITY = "eligibility";
  public static final String FIELD_STUDY_ID = "studyId";
  public static final String FIELD_MESSAGE = "message";
  public static final String FIELD_TYPE = "type";
  public static final String FIELD_CONTENT = "content";

  public static final String UPDATE_CONSENT_SUCCESS_MSG =
      "Eligibility consent has been updated successfully";
  public static final String STUDYOF_HEALTH = "StudyofHealth";
  public static final String STATUS_COMPLETE = "completed";
  public static final String SHARING_VALUE = "Provided";
  public static final String INVALID_USER_ID = "invalid userId";
  public static final String INVALID_STUDY_ID = "invalid studyId";
  public static final String INVALID_CONSENT_VERSION = "1.5";
}
