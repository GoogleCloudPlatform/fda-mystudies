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

package org.sagebase.crf.step;

import android.content.Context;

import com.google.common.collect.Sets;

import org.junit.Test;
import org.sagebionetworks.bridge.researchstack.CrfDataProvider;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Created by liujoshua on 12/8/2017.
 */
public class CrfFitBitStepLayoutTest {
    @Test
    public void shouldAllowSkip_testUser() throws Exception {
         boolean shouldAllowSkip = CrfFitBitStepLayout.shouldAllowSkip(
                Sets.newHashSet(CrfDataProvider.TEST_USER));

        assertTrue(shouldAllowSkip);
    }

    @Test
    public void shouldAllowSkip_uxTester() throws Exception {
        boolean shouldAllowSkip = CrfFitBitStepLayout.shouldAllowSkip(
                Sets.newHashSet(CrfDataProvider.UX_TESTER, CrfDataProvider.CLINIC1));

        assertTrue(shouldAllowSkip);
    }

    @Test
    public void shouldAllowSkip_clinic1() throws Exception {
        boolean shouldAllowSkip = CrfFitBitStepLayout.shouldAllowSkip(
                Sets.newHashSet(CrfDataProvider.CLINIC1));

        assertFalse(shouldAllowSkip);
    }


    @Test
    public void shouldAllowSkip_clinic2() throws Exception {
        boolean shouldAllowSkip = CrfFitBitStepLayout.shouldAllowSkip(
                Sets.newHashSet(CrfDataProvider.CLINIC2));

        assertFalse(shouldAllowSkip);
    }
}