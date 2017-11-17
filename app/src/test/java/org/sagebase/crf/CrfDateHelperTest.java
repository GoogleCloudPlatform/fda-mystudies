/*
 *    Copyright 2017 Sage Bionetworks
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package org.sagebase.crf;

import org.joda.time.DateTime;
import org.junit.Test;
import org.sagebase.crf.helper.CrfDateHelper;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CrfDateHelperTest {

    private static final DateTime PAST_DAY      = DateTime.parse("2017-11-11T12:56:37.361Z");
    private static final DateTime PAST_DAY_SAME = DateTime.parse("2017-11-11T13:56:37.361Z");
    private static final DateTime PAST_DAY_DIFF = DateTime.parse("2017-11-09T12:56:37.361Z");

    @Test
    public void isToday_test1() {
        assertTrue(CrfDateHelper.isToday(DateTime.now().toDate()));
    }

    @Test
    public void isNotToday_test1() {
        assertFalse(CrfDateHelper.isToday(PAST_DAY.toDate()));
    }

    @Test
    public void isNotToday_test2() {
        assertFalse(CrfDateHelper.isToday(PAST_DAY_DIFF.toDate()));
    }

    @Test
    public void isSameDay_test1() {
        assertTrue(CrfDateHelper.isSameDay(PAST_DAY.toDate(), PAST_DAY_SAME.toDate()));
    }

    @Test
    public void isNotSameDay_test1() {
        assertFalse(CrfDateHelper.isSameDay(PAST_DAY.toDate(), PAST_DAY_DIFF.toDate()));
    }
}
