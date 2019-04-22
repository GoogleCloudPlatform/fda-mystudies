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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.sagebase.crf.CrfTaskIntentFactory;
import org.sagebase.crf.CrfTaskResultFactory;
import org.sagebase.crf.result.CrfTaskResult;
import org.sagebase.crf.step.active.CsvUtils;

import static org.researchstack.backbone.ui.fragment.ActivitiesFragment.REQUEST_TASK;

public class CrfSample extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;

    private Intent taskToStartIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_crf_sample);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LinearLayout taskContainer = findViewById(R.id.crf_task_container);

        final Intent trainingTaskIntent = CrfTaskIntentFactory.getHeartRateTrainingTaskIntent(this);
        final String trainingTaskTitle = "Heart Rate Training";
        addTask(taskContainer, trainingTaskIntent, trainingTaskTitle);

        final Intent restingHrTaskIntent = CrfTaskIntentFactory.getHeartRateMeasurementTaskIntent(this);
        final String restingHrTaskTitle = "Heart Rate Measurement";
        addTask(taskContainer, restingHrTaskIntent, restingHrTaskTitle);

        final Intent stepHrTaskIntent = CrfTaskIntentFactory.getStairStepTaskIntent(this);
        final String stepHrTaskTitle = "Cardio Stair Step";
        addTask(taskContainer, stepHrTaskIntent, stepHrTaskTitle);

        // is this needed?
        CsvUtils.getHighPassFilterParams(this);
    }

    private void addTask(final ViewGroup taskContainer, final Intent taskIntent,
                         final String taskTitle) {
        View taskView = LayoutInflater.from(this).inflate(R.layout.crf_task, taskContainer, false);
        taskContainer.addView(taskView);
        Button taskButton = taskView.findViewById(R.id.button_start_task);
        TextView taskTitleTextView = taskView.findViewById(R.id.task_name);
        taskTitleTextView.setText(taskTitle);
        taskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (hasCameraPermission(activeTask)) {
                startTask(taskIntent);
                //}
            }
        });
    }

    private boolean hasCameraPermission(Intent activeTaskIntent) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            taskToStartIntent = activeTaskIntent;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    startTask(taskToStartIntent);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void startTask(final Intent taskIntent) {
        if (taskIntent != null) {
            startActivityForResult(taskIntent, REQUEST_TASK);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_TASK) {
            TaskResult taskResult = (TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT);
            CrfTaskResult crfTaskResult = CrfTaskResultFactory.create(taskResult);
            Log.d("CrfSample", String.valueOf(crfTaskResult));
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_crf_sample, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
