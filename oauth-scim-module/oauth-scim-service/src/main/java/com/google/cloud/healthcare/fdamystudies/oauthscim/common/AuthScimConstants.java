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

  public static final String AUTHORIZATION = "Authorization";

  public static final String CLIENT_CREDENTIALS = "client_credentials";

  public static final String CLIENT_ID = "client_id";

  public static final String AUTHORIZATION_CODE = "authorization_code";

  /** to revoke previous session tokens. */
  public static final String USER_ID = "userId";

  public static final String PASSWORD_HISTORY = "password_history";

  public static final String PASSWORD = "password";

  public static final String EXPIRES_AT = "expires_at";

  public static final String SALT = "salt";

  public static final String HASH = "hash";

  public static final String CHANGE_PASSWORD = "change_password";

  public static final String FORGOT_PASSWORD = "forgot_password";

  public static final int TEMP_PASSWORD_LENGTH = 12;

  public static final String TOKEN = "token";

  public static final String CORRELATION_ID = "correlation_id";

  public static final String LOGIN_CHALLENGE = "login_challenge";

  public static final String SKIP = "skip";

  public static final String APP_ID = "appId";

  public static final String DEVICE_TYPE = "deviceType";

  public static final String DEVICE_PLATFORM = "devicePlatform";

  public static final String CLIENT_APP_VERSION = "clientAppVersion";

  public static final String TEMP_REG_ID = "temp_reg_id";

  public static final String EMAIL = "email";

  public static final String ERROR_DESCRIPTION = "error_description";

  public static final String REDIRECT_TO = "redirect_to";

  public static final String EXPIRE_TIMESTAMP = "expire_timestamp";

  public static final String LOGIN_ATTEMPTS = "login_attempts";

  public static final String LOGIN_TIMESTAMP = "login_timestamp";

  public static final String OTP_USED = "otp_used";

  public static final String ACCOUNT_LOCK_EMAIL_TIMESTAMP = "account_lock_email_timestamp";

  public static final String CONSENT_CHALLENGE = "consent_challenge";

  public static final String FORGOT_PASSWORD_LINK = "forgot_password_link";

  public static final String SIGNUP_LINK = "signup_link";

  public static final String TERMS_LINK = "terms_link";

  public static final String PRIVACY_POLICY_LINK = "privacy_policy_link";

  public static final String ABOUT_LINK = "about_link";

  public static final String AUTO_LOGIN = "autoLogin";
}
