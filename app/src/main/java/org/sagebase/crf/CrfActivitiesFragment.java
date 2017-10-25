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

import android.widget.Toast;

import com.google.common.collect.Lists;

import org.researchstack.backbone.DataProvider;
import org.researchstack.backbone.model.SchedulesAndTasksModel;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.PermissionsStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.ActiveTaskActivity;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.researchstack.skin.ui.fragment.ActivitiesFragment;
import org.sagebase.crf.step.active.HeartRateCameraStep;
import org.sagebionetworks.bridge.researchstack.CrfTaskFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TheMDP on 10/19/17
 */

public class CrfActivitiesFragment extends ActivitiesFragment {

    public static final String TASK_ID_HEART_RATE_MEASUREMENT = "HeartRate Measurement";
    public static final String TASK_ID_CARDIO_12MT = "Cardio 12MT";

    @Override
    public void taskSelected(SchedulesAndTasksModel.TaskScheduleModel task) {
        Task newTask = DataProvider.getInstance().loadTask(getContext(), task);
        if (newTask == null) {

            if (task.taskID.equals(TASK_ID_HEART_RATE_MEASUREMENT)) {
                List<Step> stepList = new ArrayList<>();

                stepList.add(new PermissionsStep("permission", "Heart rate camera permission", ""));
                stepList.add(new InstructionStep("Instruction", "Heartrate camera test", "Place your finger over your camera without contacting the lens. Your data will be recorded for 60 seconds and then uploaded."));
                stepList.add(new HeartRateCameraStep("camera"));
                OrderedTask heartrateTask = new OrderedTask("HeartRate Measurement", stepList);
                startActivityForResult(ActiveTaskActivity.newIntent(getActivity(), heartrateTask), REQUEST_TASK);
            } else if (task.taskID.equals(TASK_ID_CARDIO_12MT)) {
                CrfTaskFactory taskFactory = new CrfTaskFactory();
                Task testTask = taskFactory.createTask(getActivity(), "instruction_test");
                startActivity(CrfActiveTaskActivity.newIntent(getActivity(), testTask));
//                Toast.makeText(getActivity(),
//                        "TODO: implement 12 minute cardio task",
//                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(),
                        org.researchstack.skin.R.string.rss_local_error_load_task,
                        Toast.LENGTH_SHORT).show();
            }

            return;
        }

        startActivityForResult(ViewTaskActivity.newIntent(getContext(), newTask), REQUEST_TASK);
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
