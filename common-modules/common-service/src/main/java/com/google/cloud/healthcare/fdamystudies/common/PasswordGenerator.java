/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.apache.syncope.common.lib.SecureTextRandomProvider;

public final class PasswordGenerator {

  private static final String UPPERCASE_KEY = "uppercase";

  private static final String LOWERCASE_KEY = "lowercase";

  private static final String DIGITS_KEY = "digits";

  private static SecureRandom secureRandom = new SecureRandom();

  public static final String SPECIAL_CHARS = "!#$%&()*?@{}.;:,-_'\"/\\|=+^";

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
                        || (codePoint >= 'A' && codePoint <= 'Z'))
            .build()
            .generate(length - 1);

    StringBuilder builder = new StringBuilder(password);
    char specialChar = SPECIAL_CHARS.charAt(secureRandom.nextInt(SPECIAL_CHARS.length()));
    int position = secureRandom.nextInt(password.length());
    builder.insert(position, specialChar);

    return convertToValidPassword(builder);
  }

  private static String convertToValidPassword(StringBuilder password) {
    char[] passwordChars = password.toString().toCharArray();

    Map<String, Integer> countMap = new HashMap<>();
    for (char c : passwordChars) {
      if (Character.isDigit(c)) {
        incrementCount(countMap, DIGITS_KEY);
      } else if (Character.isLowerCase(c)) {
        incrementCount(countMap, LOWERCASE_KEY);
      } else if (Character.isUpperCase(c)) {
        incrementCount(countMap, UPPERCASE_KEY);
      }
    }

    if (isValidPassword(countMap)) {
      return String.valueOf(passwordChars);
    }

    while (!isValidPassword(countMap)) {
      String missingCharKey = findMissingChar(countMap);
      insertNewChar(passwordChars, countMap, missingCharKey);
    }
    return String.valueOf(passwordChars);
  }

  private static boolean isValidPassword(Map<String, Integer> countMap) {
    return countMap.containsKey(DIGITS_KEY)
        && countMap.containsKey(LOWERCASE_KEY)
        && countMap.containsKey(UPPERCASE_KEY);
  }

  private static String findMissingChar(Map<String, Integer> countMap) {
    if (!countMap.containsKey(DIGITS_KEY)) {
      return DIGITS_KEY;
    } else if (!countMap.containsKey(LOWERCASE_KEY)) {
      return LOWERCASE_KEY;
    }
    return UPPERCASE_KEY;
  }

  private static void insertNewChar(
      char[] passwordChars, Map<String, Integer> countMap, String key) {
    int position = secureRandom.nextInt(passwordChars.length);
    char oldChar = passwordChars[position];
    char newChar = getRandomChar(key);
    if (Character.isDigit(oldChar) && countMap.get(DIGITS_KEY) > 1) {
      passwordChars[position] = newChar;
      incrementCount(countMap, key);
      decrementCount(countMap, DIGITS_KEY);
    } else if (Character.isLowerCase(oldChar) && countMap.get(LOWERCASE_KEY) > 1) {
      passwordChars[position] = newChar;
      incrementCount(countMap, key);
      decrementCount(countMap, LOWERCASE_KEY);
    } else if (Character.isUpperCase(oldChar) && countMap.get(UPPERCASE_KEY) > 1) {
      passwordChars[position] = newChar;
      incrementCount(countMap, key);
      decrementCount(countMap, UPPERCASE_KEY);
    }
  }

  private static char getRandomChar(String key) {
    char value = 0;
    if (DIGITS_KEY.equals(key)) {
      int digit = secureRandom.nextInt(9);
      value = Character.forDigit(digit, 10);
    } else if (LOWERCASE_KEY.equals(key)) {
      value = RandomStringUtils.randomAlphabetic(1).toLowerCase().charAt(0);
    } else if (UPPERCASE_KEY.equals(key)) {
      value = RandomStringUtils.randomAlphabetic(1).toUpperCase().charAt(0);
    }
    return value;
  }

  private static void incrementCount(Map<String, Integer> countMap, String key) {
    if (countMap.containsKey(key)) {
      countMap.put(key, countMap.get(key) + 1);
    } else {
      countMap.put(key, 1);
    }
  }

  private static void decrementCount(Map<String, Integer> countMap, String key) {
    if (countMap.containsKey(key)) {
      countMap.put(key, countMap.get(key) - 1);
    }
  }
}
