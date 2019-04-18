/*
 *    Copyright 2019 Sage Bionetworks
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

package org.sagebase.research.crf;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.researchstack.backbone.factory.IntentFactory;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.sagebase.crf.CrfActiveTaskActivity;
import org.sagebase.crf.researchstack.CrfResourceManager;
import org.sagebase.crf.researchstack.CrfTaskFactory;
import org.sagebase.crf.step.active.CsvUtils;
import org.sagebase.research.crf.R;

import static org.researchstack.backbone.ui.fragment.ActivitiesFragment.REQUEST_TASK;

public class CrfSample extends AppCompatActivity {

    private CrfTaskFactory taskFactory = new CrfTaskFactory();
    private IntentFactory intentFactory = IntentFactory.INSTANCE;

    public final IntentFactory getIntentFactory() {
        return intentFactory;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_crf_sample);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LinearLayout taskContainer = findViewById(R.id.crf_task_container);

        final Task trainingTask = taskFactory.createTask(this, CrfResourceManager.HEART_RATE_TRAINING_TEST_RESOURCE);
        addTask(taskContainer, trainingTask);

        final Task restingHrTask = taskFactory.createTask(this, CrfResourceManager.HEART_RATE_MEASUREMENT_TEST_RESOURCE);
        addTask(taskContainer, restingHrTask);

        final Task stepHrTask = taskFactory.createTask(this, CrfResourceManager.STAIR_STEP_RESOURCE);
        addTask(taskContainer, stepHrTask);

        CsvUtils.getHighPassFilterParams(this);

    }

    private void addTask(ViewGroup taskContainer, final Task activeTask) {
        View taskView = LayoutInflater.from(this).inflate(R.layout.crf_task, taskContainer, false);
        taskContainer.addView(taskView);
        Button taskButton = taskView.findViewById(R.id.button_start_task);
        TextView taskTitleTextView = taskView.findViewById(R.id.task_name);
        taskTitleTextView.setText(activeTask.getIdentifier());
        taskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTask(activeTask);
            }
        });
    }


    private void startTask(Task activeTask) {
        if (activeTask != null) {
            Intent intent = getIntentFactory().newTaskIntent(this, CrfActiveTaskActivity.class, activeTask);
            startActivityForResult(intent, REQUEST_TASK);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_TASK) {
            TaskResult taskResult = (TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT);
            taskResult.getResults();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

}
