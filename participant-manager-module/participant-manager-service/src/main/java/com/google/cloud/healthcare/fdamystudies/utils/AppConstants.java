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

  public static final String ID = "id";

  public static final String ALPHA_NUMERIC_REGEX_MAX15 = "^[0-9a-zA-Z]{1,15}$";

  public static final Integer LOCATION_NAME_MAX_LENGTH = 200;

  public static final Integer LOCATION_DESCRIPTION_MAX_LENGTH = 500;
}
