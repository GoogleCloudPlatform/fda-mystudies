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
  HEALTH("http://localhost:8004/mystudies-response-server/healthCheck"),
  PARTICIPANTID("http://localhost:8004/mystudies-response-server/participant/add"),
  UPDATE_ACTIVITY_STATE("http://localhost:8004/mystudies-response-server/participant/update-activity-state"),
  GET_ACTIVITY_STATE("http://localhost:8004/mystudies-response-server/participant/get-activity-state");
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
