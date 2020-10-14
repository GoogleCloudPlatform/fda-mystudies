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
import org.apache.commons.codec.digest.DigestUtils;

public final class IdGenerator {

  private static SecureRandom secureRandom = new SecureRandom();

  private IdGenerator() {}

  /**
   * Generates a random Id using UUID and current time (milliseconds). Returns Sha256 Hex value of
   * random id (length=64).
   */
  public static String id() {
    String millis = String.valueOf(System.currentTimeMillis());
    StringBuilder builder = new StringBuilder(UUID.randomUUID().toString());
    for (int i = 0; i < millis.length(); i++) {
      int index = secureRandom.nextInt(builder.length() - 1);
      builder.insert(index, millis.charAt(i));
    }
    return DigestUtils.sha256Hex(builder.toString());
  }
}
