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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class PasswordGeneratorTest {

  @ParameterizedTest
  @ValueSource(ints = {8, 10, 12})
  public void isValidPassword(int passwordLength) {
    String password = PasswordGenerator.generate(passwordLength);

    boolean lowerCase = false;
    boolean upperCase = false;
    boolean numeric = false;
    boolean specialChar = false;

    for (char c : password.toCharArray()) {
      if (Character.isDigit(c)) {
        numeric = true;
      } else if (Character.isLowerCase(c)) {
        lowerCase = true;
      } else if (Character.isUpperCase(c)) {
        upperCase = true;
      } else if (PasswordGenerator.SPECIAL_CHARS.contains(String.valueOf(c))) {
        specialChar = true;
      }
    }

    if (passwordLength == 0) {
      assertTrue("should return empty value", passwordLength == 0);
    } else {
      assertTrue("Password should contain alteast one digit", numeric);
      assertTrue("Password should contain alteast one lowercase letter", lowerCase);
      assertTrue("Password should contain alteast one uppercase letter", upperCase);
      assertTrue("Password should contain alteast one special character", specialChar);
      assertTrue("Password length mismatched", password.length() == passwordLength);
    }
  }
}
