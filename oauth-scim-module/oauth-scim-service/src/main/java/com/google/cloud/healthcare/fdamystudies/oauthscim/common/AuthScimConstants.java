/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.common;

public final class AuthScimConstants {

  private AuthScimConstants() {}

  public static final String GRANT_TYPE = "grant_type";

  public static final String SCOPE = "scope";

  public static final String REDIRECT_URI = "redirect_uri";

  public static final String CODE = "code";

  public static final String CODE_VERIFIER = "code_verifier";

  public static final String REFRESH_TOKEN = "refresh_token";

  public static final String CONTENT_TYPE = "Content-Type";

  public static final String AUTHORIZATION = "Authorization";

  public static final String CLIENT_CREDENTIALS = "client_credentials";

  public static final String CLIENT_ID = "client_id";

  public static final String AUTHORIZATION_CODE = "authorization_code";

  /** to revoke previous session tokens. */
  public static final String USER_ID = "user_id";
}
