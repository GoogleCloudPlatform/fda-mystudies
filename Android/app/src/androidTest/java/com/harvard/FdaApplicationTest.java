/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard;

import android.support.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class FdaApplicationTest {
  private static final String FAILED_MESSAGE = "Incorrect CodeChallenge";

  @Test
  public void getCodeChallengeTest() {
    assertThat(
        FAILED_MESSAGE,
        FdaApplication.getCodeChallenge("NOMDR6CJ7E2EEJ7KFNR75Y3SXQNIT0QGBY6E8DJ43M5Z2HBXS6"),
        is("pXY2qFqROdI0q1eUzqjHQQJJOc2LCSsArNXAnPopCiQ\n"));
  }
}
