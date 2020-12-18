/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.common;

import static org.junit.Assert.assertTrue;

import com.google.cloud.healthcare.fdamystudies.common.PasswordGenerator;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class PasswordGeneratorTest {

  @ParameterizedTest
  @ValueSource(ints = {8, 10, 12})
  public void genaratePassword(int passwordLength) {
    Set<String> passwords = new HashSet<>();
    for (int i = 0; i < 100; i++) {
      String password = genarateUniqueValidPassword(passwordLength);
      assertTrue(passwords.add(password));
    }
  }

  public String genarateUniqueValidPassword(int passwordLength) {
    String password = PasswordGenerator.generate(passwordLength);

    assertTrue("Password length should not be zero", password.length() != 0);
    assertTrue("Password length mismatched", password.length() == passwordLength);

    return password;
  }
}
