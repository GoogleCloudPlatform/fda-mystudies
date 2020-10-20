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
  HEALTH("http://localhost:8004/response-datastore/healthCheck"),

  ADD("http://localhost:8004/response-datastore/participant/add"),

  STUDYMETADATA("http://localhost:8004/response-datastore/studymetadata"),

  ADD_PARTICIPANT("http://localhost:8004/response-datastore/participant/add"),

  UPDATE_ACTIVITY_STATE(
      "http://localhost:8004/response-datastore/participant/update-activity-state"),

  GET_ACTIVITY_STATE("http://localhost:8004/response-datastore/participant/get-activity-state"),

  PROCESS_ACTIVITY_RESPONSE(
      "http://localhost:8004/response-datastore/participant/process-response"),

  GET_PROCESS_ACTIVITY_RESPONSE("http://localhost:8004/response-datastore/participant/getresponse"),

  WITHDRAW("http://localhost:8004/response-datastore/participant/withdraw");
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
