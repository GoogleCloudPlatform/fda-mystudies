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
  HEALTH("http://localhost:8002/oauth-scim-service/healthCheck"),

  TOKEN("http://localhost:8002/oauth-scim-service/oauth2/token"),

  USERS("http://localhost:8002/oauth-scim-service/users"),

  USER("http://localhost:8002/oauth-scim-service/users/{userId}"),

  RESET_PASSWORD("http://localhost:8002/oauth-scim-service/user/reset_password"),

  REVOKE_TOKEN("http://localhost:8002/oauth-scim-service/oauth2/revoke"),

  LOGIN_PAGE("http://localhost:8002/oauth-scim-service/login"),

  CONSENT_PAGE("http://localhost:8002/oauth-scim-service/consent"),

  TOKEN_INTROSPECT("http://localhost:8002/oauth-scim-service/oauth2/introspect"),

  LOGOUT("http://localhost:8002/oauth-scim-service/users/{userId}/logout"),

  CHANGE_PASSWORD("http://localhost:8002/oauth-scim-service/users/{userId}/change_password"),

  UPDATE_EMAIL_STATUS("http://localhost:8002/oauth-scim-service/users/{userId}"),

  DELETE_USER("http://localhost:8002/oauth-scim-service/users/{userId}"),

  CALLBACK("http://localhost:8002/oauth-scim-service/callback");

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
