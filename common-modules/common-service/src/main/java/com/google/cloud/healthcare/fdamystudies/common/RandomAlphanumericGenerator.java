/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import java.util.Random;

public final class RandomAlphanumericGenerator {

  private RandomAlphanumericGenerator() {}

  public static String generateRandomAlphanumeric(int targetStringLength) {
    int leftLimit = 49; // numeral '1'
    int rightLimit = 122; // letter 'z'

    String generatedString =
        new Random()
            .ints(leftLimit, rightLimit + 1)
            .filter(
                i ->
                    (i <= 57 || i >= 65)
                        && (i <= 90 || i >= 97)
                        && (i != 73 && i != 79 && i != 108 && i != 111))
            .limit(targetStringLength)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
    return generatedString;
  }
}
