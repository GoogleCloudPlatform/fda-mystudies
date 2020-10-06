/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import android.support.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FdaApplicationTest {
  private static final String FAILED_MESSAGE = "Incorrect CodeChallenge";
  private static final String CODE_VERIFIER = "NOMDR6CJ7E2EEJ7KFNR75Y3SXQNIT0QGBY6E8DJ43M5Z2HBXS6";
  private static final String CODE_CHALLENGE = "pXY2qFqROdI0q1eUzqjHQQJJOc2LCSsArNXAnPopCiQ\n";

  @Test
  public void getCodeChallengeTest() {
    assertThat(
        FAILED_MESSAGE,
        FdaApplication.getCodeChallenge(CODE_VERIFIER),
        is(CODE_CHALLENGE));
  }
}
