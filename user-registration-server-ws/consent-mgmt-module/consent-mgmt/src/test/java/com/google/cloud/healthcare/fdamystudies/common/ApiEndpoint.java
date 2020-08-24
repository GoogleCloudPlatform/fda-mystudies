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
  UPDATE_ELIGIBILITY_CONSENT(
      "http://localhost:8080/myStudiesConsentMgmtWS/updateEligibilityConsentStatus"),

  CONSENT_DOCUMENT("http://localhost:8080/myStudiesConsentMgmtWS/consentDocument");

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
