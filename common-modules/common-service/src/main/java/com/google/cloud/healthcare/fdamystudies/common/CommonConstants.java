/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

public final class CommonConstants {
  private CommonConstants() {}

  public static final String USER_ID_HEADER = "userId";

  public static final String YES = "Y";

  public static final String NO = "N";

  public static final Integer ACTIVE_STATUS = 1;

  public static final Integer INACTIVE_STATUS = 0;

  public static final Integer INVITED_STATUS = 2;

  public static final String DEACTIVATED = "Deactivated";

  public static final String NOT_APPLICABLE = "NA";

  public static final String OPEN_STUDY = "OPEN";

  public static final String CLOSE_STUDY = "CLOSE";

  public static final Integer READ_PERMISSION = 1;

  public static final Integer READ_AND_EDIT_PERMISSION = 2;

  public static final int VIEW_VALUE = 0;

  public static final int EDIT_VALUE = 1;

  public static final String OPEN = "Open";

  public static final String STATUS_ACTIVE = "Active";

  public static final boolean SELECTED = true;

  public static final boolean UNSELECTED = false;

  public static final String STATUS_LOG = "status=%d";

  public static final String ERROR_CODE_LOG = "error code=%s";

  public static final String MESSAGE_CODE_LOG = "message code=%s";

  public static final String EMAIL_REGEX =
      "^[A-Za-z0-9_+]+([\\.-]?[A-Za-z0-9_+]+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$";

  public static final String APPLICATION_X_WWW_FORM_URLENCODED_CHARSET_UTF_8 =
      "application/x-www-form-urlencoded;charset=UTF-8";

  public static final String PASSWORD_REGEX =
      "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9])(?=\\S+$).{8,64}$";

  public static final String PASSWORD_REGEX_MESSAGE =
      "Your password does not meet the required criteria.";

  public static final int EMAIL_LENGTH = 320;

  public static final String SUPER_ADMIN = "Super Admin";

  public static final String ADMIN = "Admin";

  public static final String EDITED_USER_ID = "edited_user_id";

  public static final String NEW_USER_ID = "new_user_id";

  public static final double DEFAULT_PERCENTAGE = 100.00;

  public static final String DELETE = "delete";

  public static final String RETAIN = "retain";

  public static final String NO_OF_RECORDS = "5";

  public static final String RUN_ID = "run_id";

  public static final String ACTIVITY_VERSION = "activity_version";

  public static final String ACTIVITY_ID = "activity_id";

  public static final String ACTIVITY_TYPE = "activity_type";

  public static final String ACTIVITY_STATE = "activity_state";

  public static final String PAGE_NO = "0";

  public static final String UNAUTHORIZED_MESSAGE = "Unauthorized or Invalid token";

  public static final String APPLICATION_ERROR_MESSAGE =
      "Sorry, an error has occurred and your request could not be processed. Please try again later.";

  public static final String BAD_REQUEST_MESSAGE =
      "Invalid entries found in the submitted form. Please try again.";
}
