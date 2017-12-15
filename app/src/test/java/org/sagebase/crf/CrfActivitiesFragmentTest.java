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
import org.junit.Before;
import org.junit.Test;
import org.researchstack.backbone.factory.IntentFactory;
import org.researchstack.backbone.model.SchedulesAndTasksModel;
import org.sagebionetworks.bridge.researchstack.CrfTaskFactory;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;


public class CrfActivitiesFragmentTest {
    private static final String SURVEY_GUID = "my-survey-guid";
    private static final DateTime SURVEY_CREATED_ON = DateTime.parse("2016-11-23T12:56:37.361Z");
    private static final String TASK_ID = "my-task-id";

    private CrfActivitiesFragment fragment;
    private CrfTaskFactory mockTaskFactory;
    private IntentFactory mockIntentFactory;

    @Before
    public void setup() {
        mockIntentFactory = mock(IntentFactory.class);
        mockTaskFactory = mock(CrfTaskFactory.class);

        fragment = spy(new CrfActivitiesFragment());
        fragment.setIntentFactory(mockIntentFactory);
        fragment.setTaskFactory(mockTaskFactory);
    }

    @Test
    public void processResults_NullModel() {
        List<Object> resultList = fragment.processResults(null);
        assertTrue(resultList.isEmpty());
    }

    @Test
    public void processResults_NullSchedules() {
        List<Object> resultList = fragment.processResults(new SchedulesAndTasksModel());
        assertTrue(resultList.isEmpty());
    }
}
