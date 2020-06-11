/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.auditlog.common;

public enum ApiEndpoint {
  HEALTH("http://localhost:8001/audit-log-service/v1/health"),

  EVENTS("http://localhost:8001/audit-log-service/v1/events");

  private String url;

  private ApiEndpoint(String url) {
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

  public String getPath() {
    return url.substring(url.indexOf("/audit-log-service/"));
  }
}
