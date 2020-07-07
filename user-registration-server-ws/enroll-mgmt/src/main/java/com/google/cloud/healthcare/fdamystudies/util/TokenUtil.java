/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.util;

import java.security.SecureRandom;

public class TokenUtil {

  private static final String SOURCE = "0123456789ABCDEFGHIJKLMNOPQRSWXYZ";
  private static SecureRandom secureRnd = new SecureRandom();

  private TokenUtil() {}

  public static String randomString(int length) {
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      sb.append(SOURCE.charAt(secureRnd.nextInt(SOURCE.length())));
    }
    return sb.toString();
  }
}
