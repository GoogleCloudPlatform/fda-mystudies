/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import java.time.Instant;

public final class DateTimeUtils {

  private DateTimeUtils() {}

  public static long getSystemDateTimestamp() {
    return Instant.now().toEpochMilli();
  }
}
