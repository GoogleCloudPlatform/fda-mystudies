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
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.speech.tts.TextToSpeech;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.View;
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

    enum StairState {
        START,
        UP1,
        UP2,
        DOWN1,
        DOWN2
    }
    protected StairState stairState = StairState.START;
    protected int stairCounter = 0;

    public static long STAIR_UPDATE_DOING_WELL_IN_MS = 60 * 1000; // 60 seconds
    public static long STAIR_UPDATE_GOOD_JOB_IN_MS = 120 * 1000; // 120 seconds
    public static long STAIR_UPDATE_ALMOST_DONE_IN_MS = 170 * 1000; // 170 seconds
    public static long TIME_IN_MS_TO_SPEAK_DONE_TEXT = 2000; // 2 seconds

    protected Runnable stairStepRunnable;
    public static long STAIR_UPDATE_IN_MS = 625;

    protected int metronomeCounter;
    protected Runnable metronomeRunnable;
    public static long METRONOME_UPDATE_IN_MS = 625;

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
    public void start() {
        // Do not start here, wait for TTS to start
    }

    @Override
    public void setupActiveViews() {
        super.setupActiveViews();

        crfCountdownText = findViewById(R.id.crf_stair_countdown_text);
        crfImageView = findViewById(R.id.crf_stair_image);
        crfInstructionText = findViewById(R.id.crf_stair_instruction_text);
    }

    @Override
    public void onInit(int i) {
        super.onInit(i);
        if (i == TextToSpeech.SUCCESS) {
            super.start();

            startSpokenTextTimers();
        }
    }

    protected void startSpokenTextTimers() {
        startStairStepTimer();
        startMetronomeTimer();

        mainHandler.postDelayed(() ->
                speakText(getContext().getString(R.string.crf_spoken_doing_well)),
                calculateTimeUntilDoingWellStairStepIteration());

        mainHandler.postDelayed(() ->
                speakText(getContext().getString(R.string.crf_spoken_good_job)),
                calculateTimeUntilGoodJobStairStepIteration());

        mainHandler.postDelayed(() ->
                speakText(getContext().getString(R.string.crf_spoken_almost_done)),
                calculateTimeUntilAlmostDoneStairStepIteration());

        mainHandler.postDelayed(() ->
                speakText(getContext().getString(R.string.crf_spoken_done)),
                calculateTimeUntilDoneStairStepIteration());
    }

    protected void startStairStepTimer() {
        stairCounter = 0;
        stairStepRunnable = () -> {

            String instructionText = null;
            @DrawableRes int instructionImage = R.drawable.crf_stair_step_start_1;
            switch (stairState) {
                case START:
                    instructionText = getContext().getString(R.string.crf_up);
                    instructionImage = R.drawable.crf_stair_step_start_2;
                    stairState = StairState.UP1;
                    break;
                case UP1:
                    instructionText = getContext().getString(R.string.crf_up);
                    instructionImage = R.drawable.crf_stair_step_start_3;
                    stairState = StairState.UP2;
                    break;
                case UP2:
                    instructionText = getContext().getString(R.string.crf_down);
                    instructionImage = R.drawable.crf_stair_step_start_4;
                    stairState = StairState.DOWN1;
                    break;
                case DOWN1:
                    instructionText = getContext().getString(R.string.crf_down);
                    instructionImage = R.drawable.crf_stair_step_start_1;
                    stairState = StairState.DOWN2;
                    break;
                case DOWN2:
                    instructionText = getContext().getString(R.string.crf_up);
                    instructionImage = R.drawable.crf_stair_step_start_2;
                    stairState = StairState.UP1;
                    break;
            }

            if ((stairCounter % StairState.DOWN2.ordinal() == 0 ||
                 stairCounter % StairState.UP2.ordinal() == 0) &&
                    stairCounter < 16) {
                speakText(instructionText);
            }
            if (stairCounter >= 16) {
                crfInstructionText.setVisibility(View.INVISIBLE);
            }
            crfImageView.setImageResource(instructionImage);
            crfInstructionText.setText(instructionText);

            stairCounter++;

            if (secondsLeft > 0) {
                mainHandler.postDelayed(stairStepRunnable, calculateTimeUntilNextStairStepIteration());
            }
        };
        mainHandler.removeCallbacks(stairStepRunnable);
        mainHandler.post(stairStepRunnable);
    }

    protected void startMetronomeTimer() {
        final ToneGenerator tockSound = new ToneGenerator(AudioManager.STREAM_MUSIC, 60);
        metronomeCounter = 0;
        metronomeRunnable = () -> {

            tockSound.startTone(ToneGenerator.TONE_CDMA_PIP, 100);
            metronomeCounter++;

            if (secondsLeft > 0) {
                mainHandler.postDelayed(metronomeRunnable, calculateTimeUntilNextMetronomeIteration());
            }
        };
        mainHandler.removeCallbacks(metronomeRunnable);
        mainHandler.post(metronomeRunnable);
    }

    protected long calculateTimeUntilNextStairStepIteration() {
        return (startTime + (stairCounter * STAIR_UPDATE_IN_MS)) - System.currentTimeMillis();
    }

    protected long calculateTimeUntilNextMetronomeIteration() {
        return (startTime + (metronomeCounter * METRONOME_UPDATE_IN_MS)) - System.currentTimeMillis();
    }

    protected long calculateTimeUntilAlmostDoneStairStepIteration() {
        return (startTime + STAIR_UPDATE_ALMOST_DONE_IN_MS) - System.currentTimeMillis();
    }

    protected long calculateTimeUntilDoingWellStairStepIteration() {
        return (startTime + STAIR_UPDATE_DOING_WELL_IN_MS) - System.currentTimeMillis();
    }

    protected long calculateTimeUntilGoodJobStairStepIteration() {
        return (startTime + STAIR_UPDATE_GOOD_JOB_IN_MS) - System.currentTimeMillis();
    }

    protected long calculateTimeUntilDoneStairStepIteration() {
        return (startTime +
                ((1000 * activeStep.getStepDuration()) - TIME_IN_MS_TO_SPEAK_DONE_TEXT)) -
                System.currentTimeMillis();
    }

    @Override
    public int crfStatusBarColor() {
        return R.color.perrywinkleStatus;
    }
}
