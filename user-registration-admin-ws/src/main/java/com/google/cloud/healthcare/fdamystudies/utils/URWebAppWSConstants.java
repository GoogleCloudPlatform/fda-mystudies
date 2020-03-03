/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.utils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

public interface URWebAppWSConstants {

  public final SimpleDateFormat UI_SDF_DATE = new SimpleDateFormat("MM-dd-yyyy");
  public final SimpleDateFormat UI_SDF_DATE_TIME = new SimpleDateFormat("MM-dd-yyyy HH:mm");
  public final SimpleDateFormat DB_SDF_DATE_TIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  public final SimpleDateFormat DB_SDF_DATE = new SimpleDateFormat("yyyy-MM-dd");
  public final SimpleDateFormat UI_DISPLAY_DATE = new SimpleDateFormat("EEE, MMM dd, yyyy");
  public final SimpleDateFormat UI_SDF_TIME = new SimpleDateFormat("HH:mm");
  public final SimpleDateFormat HR_SDF_TIME = new SimpleDateFormat("HH");
  public final String UI_SDF_DATE_TIME_AMPM = "MM-dd-yyyy h:mm a";
  public final SimpleDateFormat SDF_TIME = new SimpleDateFormat("h:mm a");
  public final SimpleDateFormat DB_SDF_DATE_TIME_ZONE =
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
  public final SimpleDateFormat UI_SDF_DATE_TIME_ZONE = new SimpleDateFormat("MM-dd-yyyy HH:mm");
  public final SimpleDateFormat UI_SDF_DATE_TIME_ZONE_AMPM =
      new SimpleDateFormat("MM-dd-yyyy HH:mm a");

  public SimpleDateFormat SDF_DATE_TIME = new SimpleDateFormat("MM/dd/yyyy");
  public final String ERROR = "error";

  public static final String GET_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
  public static final String GET_DATE = "yyyy-MM-dd";
  public static final String ACTUAL_DATE = "yyyy-MM-dd";
  public static final String UI_DATE = "MM/dd/yyyy";
  public static final String UI_DATE_TIME = "MM/dd/yyyy HH:mm";
  public static final String UI_DATE_TIME_FOR_DATE_DIFF = "MM/dd/yyyy HH:mm:ss";
  public static final String INPUT_TIME = "HH:mm:ss";
  public static final String REQUIRED_TIME = "HH:mm";
  public static final String REQUIRED_DATE_TIME_AMPM = "MM/dd/yyyy h:mm a";
  public static final String REQUIRED_TIME_AMPM = "h:mm a";
  public static final String CONVERT_DATE_TIME_FOR_DATE_DIFF = "MM-dd-yyyy HH:mm:ss";

  public static final String FORMAT_UI_SDF_DATE_TIME = "MM-dd-yyyy HH:mm:ss";
  public static final String FORMAT_DB_SDF_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
  public static final String FORMAT_REQUIRED_DATE_TIME_FOR_DATE_DIFF = "MM/dd/yyyy HH:mm:ss";
  public static final String FORMAT_UI_SDF_DATE = "MM-dd-yyyy";
  public static final String FORMAT_DB_SDF_DATE = "yyyy-MM-dd";
  public static final String FORMAT_PDF_SDF_DATE = "MM/dd/yyyy";

  public static final String OPEN_STUDY = "OPEN";
  public static final String CLOSE_STUDY = "CLOSE";

  public static final Integer READ_PERMISSION = 1;
  public static final Integer READ_AND_EDIT_PERMISSION = 2;

  public static final String ONBOARDING_STATUS_NEW = "new";
  public static final String ONBOARDING_STATUS_INVITED = "invited";
  public static final String ONBOARDING_STATUS_DISABLED = "disabled";
  public static final String ONBOARDING_STATUS_ALL = "all";

  public static final String ONBOARDING_STATUS_NEW_CAPS = "New";
  public static final String ONBOARDING_STATUS_INVITED_CAPS = "Invited";
  public static final String ONBOARDING_STATUS_DISABLED_CAPS = "Disabled";

  public static final List<String> ONBOARDING_STATUS_VALUES = Arrays.asList("N", "I", "D");
}
