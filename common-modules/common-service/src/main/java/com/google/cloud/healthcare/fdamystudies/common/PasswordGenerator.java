/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import org.apache.commons.text.RandomStringGenerator;
import org.apache.syncope.common.lib.SecureTextRandomProvider;

public final class PasswordGenerator {

  private PasswordGenerator() {}

  public static String generate(int length) {
    if (length < 8) {
      throw new IllegalArgumentException("Password should be 8 characters long");
    }

    String password =
        new RandomStringGenerator.Builder()
            .usingRandom(new SecureTextRandomProvider())
            .filteredBy(
                codePoint ->
                    (codePoint >= 'a' && codePoint <= 'z')
                        || (codePoint >= '0' && codePoint <= '9')
                        || (codePoint >= 'A' && codePoint <= 'Z')
                        || (codePoint >= '#' && codePoint <= '%')
                        || (codePoint >= '(' && codePoint <= '+')
                        || (codePoint >= '?' && codePoint <= '@')
                        || (codePoint >= '{' && codePoint <= '}'))
            .build()
            .generate(length);

    return password;
  }
}
