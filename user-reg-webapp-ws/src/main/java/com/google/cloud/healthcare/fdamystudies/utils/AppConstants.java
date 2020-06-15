/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.utils;

public class AppConstants {

  private AppConstants() {}

  public static final String KEY_USER_EMAILID = "email";

  public static final String KEY_USER_PASWORD = "userPassword";

  public static final String KEY_USER_STATUS = "enabled";

  public static final String KEY_USER_TYPE = "userType";

  public static final String KEY_USERID = "userId";

  public static final String KEY_ACC_NON_EXPIRED = "accountNonExpired";

  public static final String KEY_CREDEN_NON_EXPIRED = "credentialsNonExpired";

  public static final String KEY_ACC_NON_LOCKED = "accountNonLocked";

  public static final int FLAG_ONE = 1;

  public static final int FLAG_TWO = 2;

  public static final String KEY_AUTH_TOKEN = "authKey";

  public static final String SUCCESS = "SUCCESS";

  public static final String FAILURE = "FAILURE";

  public static final String ZERO = "0";

  public static final String KEY_NEW_YORK_TIME_ZONE = "America/New_York";

  public static final String KEY_NOTAVALILABLE = "NA";

  public static final String USER = "user";

  public static final String KEY_INACTIVE = "Inactive";

  public static final String KEY_ACTIVE = "active";

  public static final String NO_FEEDBACK = "noFeedback";

  public static final String DB_SDF_DATE_TIME = "yyyy-MM-dd HH:mm:ss";

  public static final String UI_SDF_DATE = "MM/dd/yyyy";

  public static final String FB_ONE_RATING = "Very Poor";

  public static final String FB_TWO_RATING = "Poor";

  public static final String FB_THREE_RATING = "Needs Improvement";

  public static final String FB_FOUR_RATING = "Good";

  public static final String FB_FIVE_RATING = "Excellent";

  public static final String MAIL_SMTP_HOST = "mail.smtp.host";
  public static final String MAIL_SMTP_PORT = "mail.smtp.port";
  public static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
  public static final String MAIL_SMTP_SOCKETFACTROY_PORT = "mail.smtp.socketFactory.port";
  public static final String MAIL_SMTP_SOCKETFACTROY_CLASS = "mail.smtp.socketFactory.class";
  public static final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
  public static final String APP_ENV = "appEnv";
  public static final String APP_ENV_LOCAL = "local";

  public static final String AUTH_USERID = "urAdminAuthId";

  public static final String ID = "id";

  public static final String KEY_FIRSTNAME = "firstName";

  public static final String KEY_LASTNAME = "lastName";

  public static final String KEY_STATUS = "status";

  public static final String ALPHA_NUMERIC_REGEX_MAX15 = "^[0-9a-zA-Z]{1,15}$";

  public static final Integer LOCATION_NAME_MAX_LENGTH = 200;

  public static final Integer LOCATION_DESCRIPTION_MAX_LENGTH = 500;

  public static final Integer TOKEN_SIZE = 8;

  public static final String EMAIL_REGEX =
      "^[A-Za-z0-9_+]+([\\.-]?[A-Za-z0-9_+]+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$";

  public static final String SITE_ALREADY_DEACTIVATE = "SITE_ALREADY_DEACTIVATE";

  public static final String OPEN = "OPEN";

  public static final String CLOSE = "CLOSE";

  public static final String ACTIVE = "Active";

  public static final String DEACTIVATED = "Deactivated";

  public static final String INVITED = "Invited";

  public static final String STATUS_ACTIVE = "1";

  public static final String STATUS_DEACTIVE = "0";

  public static final String ERROR = "error";
}
