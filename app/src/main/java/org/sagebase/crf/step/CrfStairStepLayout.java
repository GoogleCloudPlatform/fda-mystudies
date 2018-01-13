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
import android.support.annotation.DrawableRes;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.RecorderService;
import org.researchstack.backbone.ui.step.layout.ActiveStepLayout;
import org.sagebase.crf.view.CrfTaskStatusBarManipulator;
import org.sagebionetworks.research.crf.R;

import java.util.Locale;

/**
 * Created by TheMDP on 11/1/17.
 */

public class CrfStairStepLayout extends ActiveStepLayout implements CrfTaskStatusBarManipulator {

    protected TextView crfCountdownText;
    protected ImageView crfImageView;
    protected TextView crfInstructionText;

    protected BroadcastReceiver metronomeReceiver;

    protected CrfStairStep crfStairStep;

    public CrfStairStepLayout(Context context) {
        super(context);
    }

    public CrfStairStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CrfStairStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CrfStairStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initialize(Step step, StepResult result) {
        validateAndStartStep(step);
        super.initialize(step, result);
    }

    protected void validateAndStartStep(Step step) {
        if (!(step instanceof CrfStairStep)) {
            throw new IllegalStateException("CrfStairStepLayout must have an CrfStairStep");
        }
        crfStairStep = (CrfStairStep)step;
    }

    @Override
    public int getContentResourceId() {
        return R.layout.crf_step_layout_stair;
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
    }

    @Override
    public void setupActiveViews() {
        super.setupActiveViews();

        crfCountdownText = findViewById(R.id.crf_stair_countdown_text);
        crfImageView = findViewById(R.id.crf_stair_image);
        crfInstructionText = findViewById(R.id.crf_stair_instruction_text);
    }

    @Override
    protected void registerRecorderBroadcastReceivers(Context appContext) {
        super.registerRecorderBroadcastReceivers(appContext);
        metronomeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null || intent.getAction() == null) {
                    return;
                }
                if (RecorderService.BROADCAST_RECORDER_METRONOME.equals(intent.getAction())) {
                    if (intent.hasExtra(RecorderService.BROADCAST_RECORDER_METRONOME_CTR)) {
                        stairStepTransition(intent.getIntExtra(
                                RecorderService.BROADCAST_RECORDER_METRONOME_CTR, 0));
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(RecorderService.BROADCAST_RECORDER_METRONOME);
        LocalBroadcastManager.getInstance(appContext)
                .registerReceiver(metronomeReceiver, intentFilter);
    }

    @Override
    protected void unregisterRecorderBroadcastReceivers() {
        super.unregisterRecorderBroadcastReceivers();
        Context appContext = getContext().getApplicationContext();
        LocalBroadcastManager.getInstance(appContext).unregisterReceiver(metronomeReceiver);
    }

    protected void stairStepTransition(int stairCounter) {
        int stairState = stairCounter % 4;  // up1, up2, down1, down2
        String instructionText = null;
        @DrawableRes int instructionImage = R.drawable.crf_stair_step_start_1;
        switch (stairState) {
            case 0:  // up 1
                instructionText = getContext().getString(R.string.crf_up);
                instructionImage = R.drawable.crf_stair_step_start_3;
                break;
            case 1:  // up 2
                instructionText = getContext().getString(R.string.crf_down);
                instructionImage = R.drawable.crf_stair_step_start_4;
                break;
            case 2:  // down 1
                instructionText = getContext().getString(R.string.crf_down);
                instructionImage = R.drawable.crf_stair_step_start_1;
                break;
            case 3:  // down 2
                instructionText = getContext().getString(R.string.crf_up);
                instructionImage = R.drawable.crf_stair_step_start_2;
                break;
        }
        if (stairCounter >= 16) {  // Hide Up/Down after 16 * 0.625 seconds
            crfInstructionText.setVisibility(View.INVISIBLE);
        }
        crfImageView.setImageResource(instructionImage);
        crfInstructionText.setText(instructionText);
    }

    @Override
    public int crfStatusBarColor() {
        return R.color.perrywinkleStatus;
    }
}
