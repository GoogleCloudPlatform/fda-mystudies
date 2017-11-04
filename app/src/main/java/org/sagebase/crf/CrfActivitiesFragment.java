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

import android.support.annotation.VisibleForTesting;
import android.widget.Toast;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import org.researchstack.backbone.model.SchedulesAndTasksModel;
import org.researchstack.backbone.task.Task;
import org.researchstack.skin.ui.fragment.ActivitiesFragment;
import org.sagebionetworks.bridge.researchstack.CrfTaskFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by TheMDP on 10/19/17
 */
public class CrfActivitiesFragment extends ActivitiesFragment {
    // Mapping from task ID to resource name. Visible to enable unit tests.
    @VisibleForTesting
    static final Map<String, String> TASK_ID_TO_RESOURCE_NAME =
            ImmutableMap.<String, String>builder()
                    .put("HeartRate Measurement", "heart_rate_measurement")
                    .put("Cardio 12MT", "12_minute_walk")
                    .put("Cardio Stair Step", "stair_step")
                    .build();

    private CrfTaskFactory taskFactory = new CrfTaskFactory();

    // To allow unit tests to mock.
    @VisibleForTesting
    void setTaskFactory(CrfTaskFactory taskFactory) {
        this.taskFactory = taskFactory;
    }

    @Override
    protected void startCustomTask(SchedulesAndTasksModel.TaskScheduleModel task) {
        if (TASK_ID_TO_RESOURCE_NAME.containsKey(task.taskID)) {
            Task testTask = taskFactory.createTask(getActivity(), TASK_ID_TO_RESOURCE_NAME.get(task
                    .taskID));
            startActivityForResult(getIntentFactory().newTaskIntent(getActivity(),
                    CrfActiveTaskActivity.class, testTask), REQUEST_TASK);
        } else {
            Toast.makeText(getActivity(),
                    org.researchstack.skin.R.string.rss_local_error_load_task,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public List<Object> processResults(SchedulesAndTasksModel model) {
        if (model == null || model.schedules == null) {
            return Lists.newArrayList();
        }
        List<Object> tasks = new ArrayList<>();

        for (SchedulesAndTasksModel.ScheduleModel scheduleModel : model.schedules) {
            tasks.addAll(scheduleModel.tasks);
        }

        return tasks;
    }
}
