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

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.researchstack.backbone.ui.step.layout.ActiveStepLayout;
import org.sagebionetworks.research.crf.R;

import java.util.Locale;

/**
 * Created by TheMDP on 10/31/17.
 */

public class CrfCountdownStepLayout extends ActiveStepLayout implements CrfTaskToolbarTintManipulator {

    protected TextView crfTitleTextView;
    protected TextView crfCountdownTextView;
    protected Button pauseResumeButton;
    private boolean isPaused = false;

    public CrfCountdownStepLayout(Context context) {
        super(context);
    }

    public CrfCountdownStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CrfCountdownStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CrfCountdownStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setupActiveViews() {
        super.setupActiveViews();

        crfTitleTextView = findViewById(R.id.crf_countdown_title);
        if (activeStep.getTitle() != null) {
            crfTitleTextView.setText(activeStep.getTitle());
        }

        crfCountdownTextView = findViewById(R.id.crf_countdown_text);

        pauseResumeButton = findViewById(R.id.crf_pause_resume_button);
        pauseResumeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPaused) {
                    pauseResumeButton.setText(R.string.crf_underlined_pause);
                    resumeCountdown();
                } else {
                    pauseResumeButton.setText(R.string.crf_underlined_resume);
                    pauseCountdown();
                }
                isPaused = !isPaused;
            }
        });
    }

    @Override
    public void doUIAnimationPerSecond() {
        crfCountdownTextView.setText(String.format(Locale.getDefault(), "%d", secondsLeft));
    }

    private void pauseCountdown() {
        mainHandler.removeCallbacks(animationRunnable);
    }

    private void resumeCountdown() {
        startTime = System.currentTimeMillis() - (1000 * (activeStep.getStepDuration() - secondsLeft));
        mainHandler.post(animationRunnable);
    }

    @Override
    public int getContentResourceId() {
        return R.layout.crf_step_layout_countdown;
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
    public int crfToolbarTintColor() {
        return R.color.azure;
    }
}
