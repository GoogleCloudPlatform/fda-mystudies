/*
 *    Copyright 2018 Sage Bionetworks
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

import org.junit.Test;
import org.researchstack.backbone.step.active.recorder.LocationRecorder;
import org.researchstack.backbone.step.active.recorder.LocationRecorderConfig;
import org.researchstack.backbone.step.active.recorder.RecorderConfig;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class Crf12MinWalkingStepTest {
    private static final String TEST_IDENTIFIER = "test-identifier";
    private static final File MOCK_OUTPUT_DIRECTORY = mock(File.class);

    @Test
    public void testInit() {
        Crf12MinWalkingStep step = new Crf12MinWalkingStep(TEST_IDENTIFIER);
        assertEquals(TEST_IDENTIFIER, step.getIdentifier());

        // The easiest way to check the recorder config is to make a recorder out of it.
        List<RecorderConfig> recorderConfigList = step.getRecorderConfigurationList();
        assertEquals(1, recorderConfigList.size());

        LocationRecorderConfig config = (LocationRecorderConfig) recorderConfigList.get(0);
        LocationRecorder recorder = (LocationRecorder) config.recorderForStep(step,
                MOCK_OUTPUT_DIRECTORY);
        assertEquals(Crf12MinWalkingStep.LOCATION_RECORDER_ID, recorder.getIdentifier());
        assertSame(MOCK_OUTPUT_DIRECTORY, recorder.getOutputDirectory());
        assertTrue(recorder.getUsesRelativeCoordinates());
        assertSame(step, recorder.getStep());
    }
}