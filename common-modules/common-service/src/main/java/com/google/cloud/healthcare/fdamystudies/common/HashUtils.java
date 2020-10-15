/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public final class HashUtils {

  private static XLogger logger = XLoggerFactory.getXLogger(HashUtils.class.getName());

  private HashUtils() {}

  public static String salt() {
    return DigestUtils.sha512Hex(IdGenerator.id());
  }

  public static String hash(String input, String rawSalt) {
    StringBuilder sb = new StringBuilder();
    try {
      byte[] salt = rawSalt.getBytes();
      MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
      // Pass salt to the digest
      messageDigest.update(salt);
      messageDigest.update(input.getBytes(StandardCharsets.UTF_8));
      byte[] digestBytes = messageDigest.digest();

      for (int i = 0; i < digestBytes.length; i++) {
        String hex = Integer.toHexString(0xff & digestBytes[i]);
        if (hex.length() < 2) {
          sb.append('0');
        }
        sb.append(hex);
      }
    } catch (Exception e) {
      logger.error("encrypt() failed with an exception", e);
    }
    return sb.toString();
  }
}
