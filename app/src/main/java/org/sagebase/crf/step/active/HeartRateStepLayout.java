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

package org.sagebase.crf.step.active;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.recorder.Recorder;
import org.researchstack.backbone.step.active.recorder.RecorderConfig;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.layout.ActiveStepLayout;

import org.sagebase.crf.camera.CameraSourcePreview;
import org.sagebionetworks.research.crf.R;

/**
 * Created by TheMDP on 10/19/17.
 */

public class HeartRateStepLayout extends ActiveStepLayout implements HeartRateCameraRecorder.BpmUpdateListener {

    private CameraSourcePreview cameraSourcePreview;

    public HeartRateStepLayout(Context context) {
        super(context);
    }

    public HeartRateStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeartRateStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public HeartRateStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initialize(Step step, StepResult result) {
        super.initialize(step, result);
    }

    @Override
    public void start() {
        if (cameraSourcePreview == null) {
            cameraSourcePreview = new CameraSourcePreview(getContext(), null);
            getActiveStepLayout().addView(cameraSourcePreview, new LinearLayout.LayoutParams(500, 500));
        }
        titleTextview.setText("BPM --");
        titleTextview.setVisibility(View.VISIBLE);

        super.start();

        // If the camera was not set up properly,
        if (!cameraSourcePreview.isCameraSetup()) {
            showOkAlertDialog("Error opening camera interface", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    callbacks.onSaveStep(StepCallbacks.ACTION_PREV, activeStep, null);
                }
            });
        }
    }

    @Override
    public Recorder createCustomRecorder(RecorderConfig config) {
        if (config instanceof HeartRateCameraRecorderConfig) {
            HeartRateCameraRecorderConfig heartRateConfig = (HeartRateCameraRecorderConfig)config;
            HeartRateCameraRecorder recorder = (HeartRateCameraRecorder)heartRateConfig.recorderForStep(
                    cameraSourcePreview, activeStep, getOutputDirectory());
            recorder.setBpmUpdateListener(this);
            return recorder;
        }
        return null;
    }

    @Override
    public void bpmUpdate(int bpm) {
        titleTextview.setText("BPM " + bpm);
    }
}
