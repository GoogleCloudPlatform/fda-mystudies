/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.text.RandomStringGenerator;
import org.apache.commons.text.RandomStringGenerator.Builder;
import org.apache.syncope.common.lib.SecureTextRandomProvider;

public final class PasswordGenerator {

  public static final String SPECIAL_CHARS = "!#$%&()*?@{}";

  private PasswordGenerator() {}

  public static String generate(int length) {
    int count = length / 4;
    int extra = length % 4;

    Builder builder =
        new RandomStringGenerator.Builder().usingRandom(new SecureTextRandomProvider());

    StringBuilder password = new StringBuilder();
    password
        .append(builder.withinRange('0', '9').build().generate(count))
        .append(builder.withinRange('A', 'Z').build().generate(count + extra))
        .append(builder.withinRange('a', 'z').build().generate(count))
        .append(builder.selectFrom(SPECIAL_CHARS.toCharArray()).build().generate(count));

    List<Character> passwordChars =
        password.chars().mapToObj(data -> (char) data).collect(Collectors.toList());
    Collections.shuffle(passwordChars);

    return passwordChars
        .stream()
        .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
        .toString();
  }
}
