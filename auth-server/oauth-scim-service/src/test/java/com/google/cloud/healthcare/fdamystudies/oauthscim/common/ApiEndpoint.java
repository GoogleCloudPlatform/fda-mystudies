/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.common;

import java.net.MalformedURLException;
import java.net.URL;

public enum ApiEndpoint {
  HEALTH("http://localhost:8002/auth-server/healthCheck"),

  TOKEN("http://localhost:8002/auth-server/oauth2/token"),

  USERS("http://localhost:8002/auth-server/users"),

  USER("http://localhost:8002/auth-server/users/{userId}"),

  RESET_PASSWORD("http://localhost:8002/auth-server/user/reset_password"),

  LOGIN_PAGE("http://localhost:8002/auth-server/login"),

  CONSENT_PAGE("http://localhost:8002/auth-server/consent"),

  LOGOUT("http://localhost:8002/auth-server/users/{userId}/logout"),

  CHANGE_PASSWORD("http://localhost:8002/auth-server/users/{userId}/change_password"),

  UPDATE_EMAIL_STATUS("http://localhost:8002/auth-server/users/{userId}"),

  DELETE_USER("http://localhost:8002/auth-server/users/{userId}"),

  CALLBACK("http://localhost:8002/auth-server/callback");

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
