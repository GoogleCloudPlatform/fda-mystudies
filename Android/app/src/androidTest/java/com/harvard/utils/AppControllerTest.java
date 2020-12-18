/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard.utils;

import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Test;

public class AppControllerTest {
  private static final String TEST_ACTIVITYESWS_START_TIME = "2020-10-08T12:10:00.000+0000";
  private static final String TEST_ACTIVITYESWS_END_TIME = "2020-10-12T23:59:59.000+0000";
  private static final String TEST_REGEX = "\\.";
  private static final boolean TEST_TRUE = true;
  private static final boolean TEST_FALSE = false;
  private static final String INVALID_DATE_FORMAT = "Invalid date format";

  @Test
  public void isDateWithinRangeTest() {
    SimpleDateFormat simpleDateFormat = AppController.getDateFormatUtcNoZone();
    Date startDate = null;
    Date endDate = null;
    try {
      startDate = simpleDateFormat.parse(TEST_ACTIVITYESWS_START_TIME.split(TEST_REGEX)[0]);
      endDate = simpleDateFormat.parse(TEST_ACTIVITYESWS_END_TIME.split(TEST_REGEX)[0]);
    } catch (ParseException e) {
      fail(INVALID_DATE_FORMAT);
    }
    boolean isWithRange = AppController.isWithinRange(startDate, endDate);
    assertThat(isWithRange, equalTo(TEST_FALSE));
    boolean notEndDate = AppController.isWithinRange(startDate, null);
    assertThat(notEndDate, equalTo(TEST_TRUE));
  }

  @Test
  public void checkafterTest() {
    SimpleDateFormat simpleDateFormat = AppController.getDateFormatUtcNoZone();
    Date startDate = null;
    try {
      startDate = simpleDateFormat.parse(TEST_ACTIVITYESWS_START_TIME.split(TEST_REGEX)[0]);
    } catch (ParseException e) {
      fail(INVALID_DATE_FORMAT);
    }
    boolean checkAfter = AppController.checkafter(startDate);
    assertThat(checkAfter, equalTo(TEST_FALSE));
  }
}
