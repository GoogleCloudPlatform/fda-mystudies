/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public final class DateTimeUtils {

  public static final SimpleDateFormat SDF_DATE_TIME = new SimpleDateFormat("MM/dd/yyyy");

  private DateTimeUtils() {}

  public static long getSystemDateTimestamp(long days, long hours, long minutes) {
    Instant instant = Instant.now();
    instant = instant.plus(days, ChronoUnit.DAYS);
    instant = instant.plus(hours, ChronoUnit.HOURS);
    instant = instant.plus(minutes, ChronoUnit.MINUTES);
    return instant.toEpochMilli();
  }

  public static String format(Timestamp timestamp) {
    if (timestamp != null) {
      return SDF_DATE_TIME.format(timestamp);
    }
    return null;
  }
}
