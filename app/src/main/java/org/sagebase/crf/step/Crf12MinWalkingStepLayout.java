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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Path;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.RecorderService;
import org.researchstack.backbone.step.active.recorder.AudioRecorder;
import org.researchstack.backbone.step.active.recorder.LocationRecorder;
import org.researchstack.backbone.ui.step.layout.ActiveStepLayout;
import org.researchstack.backbone.ui.views.ArcDrawable;
import org.sagebase.crf.view.CrfTaskStatusBarManipulator;
import org.sagebionetworks.research.crf.R;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Created by TheMDP on 10/31/17.
 */

public class Crf12MinWalkingStepLayout extends ActiveStepLayout implements CrfTaskStatusBarManipulator {

    private static final String CRF_12_RUN_IDENTIFIER_SUFFIX = ".runDistance";

    private TextView crfCountdownText;

    protected TextView distanceNumber;
    protected int lastDistanceMeasurement;
    protected BroadcastReceiver locationReceiver;

    protected View arcDrawableContainer;
    protected ArcDrawable arcDrawable;

    public Crf12MinWalkingStepLayout(Context context) {
        super(context);
    }

    public Crf12MinWalkingStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Crf12MinWalkingStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Crf12MinWalkingStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public int getContentResourceId() {
        return R.layout.crf_step_layout_12_min_walk;
    }

    @Override
    public int getContentContainerLayoutId() {
        return R.id.crf_step_layout_container;
    }

    @Override
    public int getFixedSubmitBarLayoutId() {
        return R.layout.crf_step_layout_container;
    }

    @Override
    public void doUIAnimationPerSecond() {
        long min = secondsLeft / 60;
        long sec = secondsLeft % 60;
        crfCountdownText.setText(String.format(Locale.getDefault(), "%02d:%02d", min, sec));

        float progress = 1.0f - ((float)secondsLeft - (float)activeStep.getStepDuration());
        arcDrawable.setSweepAngle(ArcDrawable.FULL_SWEEPING_ANGLE * progress);
    }

    @Override
    public void start() {
        super.start();

        distanceNumber.setText("0");
        setDistanceResultForCompletionStep("0");
        lastDistanceMeasurement = 0;
    }

    @Override
    public void stop() {
        super.stop();
        setFinalDistanceResult();
    }

    protected void stepLayoutWasResumedInFinishedState(RecorderService.ResultHolder resultHolder) {
        updateLastRecordedDistance(LocationRecorder.getLastRecordedTotalDistance(getContext()));
        super.stepLayoutWasResumedInFinishedState(resultHolder);
    }

    protected void stepLayoutWasResumedInRecordingState(long recordingStartTime) {
        updateLastRecordedDistance(LocationRecorder.getLastRecordedTotalDistance(getContext()));
        super.stepLayoutWasResumedInRecordingState(recordingStartTime);
    }

    @Override
    protected void registerRecorderBroadcastReceivers(Context appContext) {
        super.registerRecorderBroadcastReceivers(appContext);
        locationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null || intent.getAction() == null) {
                    return;
                }
                if (LocationRecorder.BROADCAST_LOCATION_UPDATE_ACTION.equals(intent.getAction())) {
                    LocationRecorder.LocationUpdateHolder dataHolder =
                            LocationRecorder.getLocationUpdateHolder(intent);
                    if (dataHolder != null) {
                        updateLastRecordedDistance(dataHolder.getTotalDistance());
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(AudioRecorder.BROADCAST_SAMPLE_ACTION);
        LocalBroadcastManager.getInstance(appContext)
                .registerReceiver(locationReceiver, intentFilter);
    }

    protected void updateLastRecordedDistance(double totalDistance) {
        int distanceInFeet = (int)(3.28084 * totalDistance);
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        String distanceString = formatter.format(distanceInFeet);
        distanceNumber.setText(distanceString);
        setDistanceResultForCompletionStep(distanceString);
        lastDistanceMeasurement = distanceInFeet;
    }

    @Override
    protected void unregisterRecorderBroadcastReceivers() {
        super.unregisterRecorderBroadcastReceivers();
        Context appContext = getContext().getApplicationContext();
        LocalBroadcastManager.getInstance(appContext).unregisterReceiver(locationReceiver);
    }

    private void setFinalDistanceResult() {
        String stepId = activeStep.getIdentifier() + CRF_12_RUN_IDENTIFIER_SUFFIX;
        StepResult<Double> result = new StepResult<>(new Step(stepId));
        double lastRecordedDistance = LocationRecorder.getLastRecordedTotalDistance(getContext());
        result.setResult(lastRecordedDistance);
        stepResult.setResultForIdentifier(stepId, result);
    }

    private void setDistanceResultForCompletionStep(String resultStr) {
        String distanceStepId = CrfCompletionStepLayout.COMPLETION_DISTANCE_VALUE_RESULT;
        StepResult<String> distanceResult = new StepResult<>(new Step(distanceStepId));
        distanceResult.setResult(resultStr);
        stepResult.setResultForIdentifier(distanceStepId, distanceResult);
    }

    @Override
    public void setupActiveViews() {
        super.setupActiveViews();

        crfCountdownText = findViewById(R.id.crf_12_min_walk_countdown_text);

        distanceNumber = findViewById(R.id.crf_distance_number);

        arcDrawableContainer = findViewById(R.id.crf_arc_drawable_container);
        arcDrawable = new ArcDrawable();
        arcDrawable.setColor(ResourcesCompat.getColor(getResources(), R.color.greenyBlue, null));
        arcDrawable.setArchWidth(getResources().getDimensionPixelOffset(R.dimen.crf_ard_drawable_width));
        arcDrawable.setDirection(Path.Direction.CW);
        arcDrawableContainer.setBackground(arcDrawable);
    }

    @Override
    public int crfStatusBarColor() {
        return R.color.sky_blur;
    }
}
