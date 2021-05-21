/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style license that can be found in the LICENSE file
 * or at https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.util;

import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;

public final class IdGenerator {

  private IdGenerator() {}

  /** Generate a random Id using UUID and replace hyphen (-) with random alpha numeric letter */
  public static String id() {
    String uuid = UUID.randomUUID().toString();
    StringBuilder builder = new StringBuilder();

    // replace hyphen (-) with random alpha numeric letter
    for (int i = 0; i < uuid.length(); i++) {
      if (uuid.charAt(i) == '-') {
        builder.append(RandomStringUtils.randomAlphanumeric(1));
      } else {
        builder.append(uuid.charAt(i));
      }
    }

    return builder.toString().toLowerCase();
  }
}
