/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import java.security.SecureRandom;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.apache.syncope.common.lib.SecureTextRandomProvider;

public final class PasswordGenerator {

  private static final String UPPERCASE_KEY = "uppercase";

  private static final String LOWERCASE_KEY = "lowercase";

  private static final String DIGITS_KEY = "digits";

  private static SecureRandom secureRandom = new SecureRandom();

  public static final String SPECIAL_CHARS = "!#$%&()*?@{}";

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

    return convertToValidPassword(builder.toString());
  }

  private static String convertToValidPassword(String password) {
    char[] passwordChars = password.toCharArray();
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

    if (countMap.containsKey(DIGITS_KEY)
        && countMap.containsKey(LOWERCASE_KEY)
        && countMap.containsKey(UPPERCASE_KEY)) {
      return password;
    }

    addMissingChar(passwordChars, countMap, DIGITS_KEY);
    addMissingChar(passwordChars, countMap, LOWERCASE_KEY);
    addMissingChar(passwordChars, countMap, UPPERCASE_KEY);
    return String.valueOf(passwordChars);
  }

  private static void addMissingChar(
      char[] passwordChars, Map<String, Integer> countMap, String key) {
    char randomChar = getRandomChar(key);
    if (!countMap.containsKey(key)) {
      countMap = sortMapByValue(countMap);
      String firstKey = countMap.entrySet().iterator().next().getKey();
      int position = findFirstIndexOf(passwordChars, firstKey);
      passwordChars[position] = randomChar;
      countMap.put(key, 1);
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

  private static int findFirstIndexOf(char[] letters, String key) {
    for (int i = 0; i < letters.length; i++) {
      if (Character.isDigit(letters[i]) && DIGITS_KEY.equals(key)) {
        return i;
      } else if (Character.isLowerCase(letters[i]) && LOWERCASE_KEY.equals(key)) {
        return i;
      } else if (Character.isUpperCase(letters[i]) && UPPERCASE_KEY.equals(key)) {
        return i;
      }
    }
    return 0;
  }

  private static Map<String, Integer> sortMapByValue(Map<String, Integer> countMap) {
    return countMap
        .entrySet()
        .stream()
        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
        .collect(
            Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (oldValue, newValue) -> oldValue,
                LinkedHashMap::new));
  }
}
