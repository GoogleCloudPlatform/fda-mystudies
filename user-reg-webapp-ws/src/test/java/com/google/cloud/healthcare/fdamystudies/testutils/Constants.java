package com.google.cloud.healthcare.fdamystudies.testutils;

public final class Constants {
  private Constants() {}

  public static final String USER_ID_HEADER = "userId";

  public static final String VALID_USER_ID = "1";

  public static final String NEW_USER_ID = "2";

  public static final String INVALID_USER_ID = "100";

  public static final int LOCATION_ID_PATH_VARIABLE = 2;

  public static final String FORBIDDEN_PERMISSION_MSG =
      "You do not have permission to view or add or update locations";

  public static final String UNAUTHORIZED_MSG = "User does not exist";

  public static final String CUSTOM_ID = "customId";

  public static final String CUSTOM_ID_NOT_MATCHING_REGEX = "-customId130.53";

  public static final String LOCATION_NAME = "name -1-updated000";

  public static final String LOCATION_DESCRIPTION = "location-descp-updated";

  public static final String LOCATION_STATUS = "1";

  public static final String LOCATION_DECOMMISSION_STATUS = "0";

  public static final String LOCATION_INVALID_STATUS = "2";
}
