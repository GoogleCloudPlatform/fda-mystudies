/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import java.security.SecureRandom;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;

public final class IdGenerator {

  private static SecureRandom secureRandom = new SecureRandom();

  private IdGenerator() {}

  /** Generates a random Id using UUID and current time (milliseconds). */
  public static String id() {
    String millis = String.valueOf(System.currentTimeMillis());
    String uuid = UUID.randomUUID().toString();
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < uuid.length(); i++) {
      if (uuid.charAt(i) == '-') {
        builder.append(RandomStringUtils.randomAlphabetic(1));
      } else {
        builder.append(uuid.charAt(i));
      }
    }

    for (int i = 0; i < millis.length(); i++) {
      int index = secureRandom.nextInt(builder.length() - 1);
      builder.insert(index, millis.charAt(i));
    }
    return builder.toString();
  }
}
