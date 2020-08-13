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
  ADD_NEW_SITE("http://localhost:8080/participant-manager-service/sites"),

  ADD_NEW_LOCATION("http://localhost:8080/participant-manager-service/locations"),

  HEALTH("http://localhost:8080/participant-manager-service/healthCheck"),

  SET_UP_ACCOUNT("http://localhost:8080/participant-manager-service/users/"),

  GET_APPS("http://localhost:8080/participant-manager-service/apps"),

  GET_STUDIES("http://localhost:8080/participant-manager-service/studies"),

  UPDATE_LOCATION("http://localhost:8080/participant-manager-service/locations/{locationId}"),

  GET_STUDY_PARTICIPANT(
      "http://localhost:8080/participant-manager-service/studies/{studyId}/participants");


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
