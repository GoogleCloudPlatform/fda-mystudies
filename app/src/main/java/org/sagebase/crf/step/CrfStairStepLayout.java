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

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
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

    @SuppressLint("MissingSuperCall")
    @Override
    public void setupActiveViews() {
        super.setupActiveViews();

        crfCountdownText = findViewById(R.id.crf_stair_countdown_text);
        crfImageView = findViewById(R.id.crf_stair_image);
        crfInstructionText = findViewById(R.id.crf_stair_instruction_text);
    }

    protected void startAnimation() {
        super.startAnimation();
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.crf_stair_step_animation, null);
        if (drawable != null && drawable instanceof AnimationDrawable) {
            AnimationDrawable animationDrawable = (AnimationDrawable)drawable;
            crfImageView.setImageDrawable(animationDrawable);
            animationDrawable.start();
        }
    }

    @Override
    public int crfStatusBarColor() {
        return R.color.perrywinkleStatus;
    }
}
