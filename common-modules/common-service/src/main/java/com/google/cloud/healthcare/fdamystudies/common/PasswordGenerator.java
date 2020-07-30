/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class PasswordGenerator {

  private static final String[] ALLOWED_CHAR_GROUPS =
      new String[] {
        "abcdefghijklmnopqrstuvwxyz", "ABCDEFGHIJKLMNOPQRSTUVWXYZ", "0123456789", "!#$%&"
      };

  private static SecureRandom secureRandom = new SecureRandom();

  private PasswordGenerator() {}

  public static String generate(int length) {
    StringBuilder password = new StringBuilder();

    while (password.length() < length) {
      for (int i = 0; i < ALLOWED_CHAR_GROUPS.length; i++) {
        int randomIndex = secureRandom.nextInt(ALLOWED_CHAR_GROUPS[i].length());
        password.append(ALLOWED_CHAR_GROUPS[i].charAt(randomIndex));
      }
    }

    return shuffle(password.toString());
  }

  private static String shuffle(String value) {
    List<String> letters = Arrays.asList(value.split(""));
    Collections.shuffle(letters);
    StringBuilder builder = new StringBuilder();
    for (String letter : letters) {
      builder.append(letter);
    }
    return builder.toString();
  }
}
