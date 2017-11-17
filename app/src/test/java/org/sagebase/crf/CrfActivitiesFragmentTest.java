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

import android.content.Intent;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.researchstack.backbone.factory.IntentFactory;
import org.researchstack.backbone.model.SchedulesAndTasksModel;
import org.researchstack.backbone.task.Task;
import org.researchstack.skin.ui.fragment.ActivitiesFragment;
import org.sagebionetworks.bridge.researchstack.CrfDataProvider;
import org.sagebionetworks.bridge.researchstack.CrfTaskFactory;
import org.sagebionetworks.bridge.researchstack.survey.SurveyTaskScheduleModel;
import org.sagebionetworks.bridge.rest.model.ActivityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    public void startCustomTaskTest() {
        for (Map.Entry<String, String> oneTaskIdAndResourceName : CrfActivitiesFragment
                .TASK_ID_TO_RESOURCE_NAME.entrySet()) {
            String taskId = oneTaskIdAndResourceName.getKey();
            String resourceName = oneTaskIdAndResourceName.getValue();

            // Reset mocks and spies, so we don't carry over state from the previous test.
            reset(mockIntentFactory, mockTaskFactory, fragment);

            // Mock dependencies
            Task task = mock(Task.class);
            when(mockTaskFactory.createTask(any(), anyString())).thenReturn(task);

            Intent intent = new Intent();
            when(mockIntentFactory.newTaskIntent(any(), any(), any())).thenReturn(intent);

            doNothing().when(fragment).startActivityForResult(any(), anyInt());

            // Make our task schedule model with our task ID
            SchedulesAndTasksModel.TaskScheduleModel taskScheduleModel =
                    new SchedulesAndTasksModel.TaskScheduleModel();
            taskScheduleModel.taskID = taskId;

            // Execute
            fragment.startCustomTask(taskScheduleModel);

            // Verify dependent calls
            verify(mockTaskFactory).createTask(any(), eq(resourceName));
            verify(mockIntentFactory).newTaskIntent(any(), eq(CrfActiveTaskActivity.class),
                    same(task));
            verify(fragment).startActivityForResult(same(intent), eq(ActivitiesFragment
                    .REQUEST_TASK));
        }
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
