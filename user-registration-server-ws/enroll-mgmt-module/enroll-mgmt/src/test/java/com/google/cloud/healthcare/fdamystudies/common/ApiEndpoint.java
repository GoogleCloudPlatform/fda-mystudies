/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import java.net.MalformedURLException;
import java.net.URL;

public enum ApiEndpoint {

  UPDATE_STUDY_STATE_PATH("http://localhost:8080/myStudiesEnrollmentMgmt/updateStudyState"),

  STUDY_STATE_PATH("http://localhost:8080/myStudiesEnrollmentMgmt/studyState"),

  WITHDRAW_FROM_STUDY_PATH("http://localhost:8080/myStudiesEnrollmentMgmt/withdrawfromstudy"),

  PARTICIPANT_INFO("http://localhost:8080/myStudiesEnrollmentMgmt/participantInfo"),

  VALIDATE_ENROLLMENT_TOKEN_PATH(
      "http://localhost:8080/myStudiesEnrollmentMgmt/validateEnrollmentToken"),

  ENROLL_PATH("http://localhost:8080/myStudiesEnrollmentMgmt/enroll");

  private String url;

  private ApiEndpoint(String url) {
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

  public String getPath() throws MalformedURLException {
    return new URL(url).getPath();
  }
}
