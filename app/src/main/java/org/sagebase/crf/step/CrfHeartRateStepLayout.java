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
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Path;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.researchstack.backbone.answerformat.DecimalAnswerFormat;
import org.researchstack.backbone.result.Result;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.recorder.JsonArrayDataRecorder;
import org.researchstack.backbone.step.active.recorder.Recorder;
import org.researchstack.backbone.step.active.recorder.RecorderListener;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.layout.ActiveStepLayout;
import org.researchstack.backbone.ui.views.ArcDrawable;
import org.researchstack.backbone.utils.StepResultHelper;
import org.sagebase.crf.camera.CameraSourcePreview;
import org.sagebase.crf.step.active.BpmRecorder;
import org.sagebase.crf.step.active.HeartRateCamera2Recorder;
import org.sagebase.crf.step.active.HeartRateCameraRecorder;
import org.sagebase.crf.step.active.HeartRateCameraRecorderConfig;
import org.sagebase.crf.view.CrfTaskStatusBarManipulator;
import org.sagebase.crf.view.CrfTaskToolbarTintManipulator;
import org.sagebionetworks.research.crf.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by TheMDP on 10/31/17.
 */

public class CrfHeartRateStepLayout extends ActiveStepLayout implements
        BpmRecorder.BpmUpdateListener,
        BpmRecorder.IntelligentStartUpdateListener,
        RecorderListener,
        CrfTaskToolbarTintManipulator,
        CrfTaskStatusBarManipulator,
        CrfResultListener {
    private static final Logger LOG = LoggerFactory.getLogger(CrfHeartRateStepLayout.class);

    private static final String AVERAGE_BPM_IDENTIFIER = "AVERAGE_BPM_IDENTIFIER";
    private static final String BPM_START_IDENTIFIER_SUFFIX = ".heartRate_start";
    private static final String BPM_END_IDENTIFIER_SUFFIX = ".heartRate_end";

    private CameraSourcePreview cameraSourcePreview;
    private TextureView cameraPreview;
    public TextureView getCameraPreview() {
        return cameraPreview;
    }
    protected TextView crfMessageTextView;

    protected View heartRateTextContainer;
    protected TextView heartRateNumber;

    protected View arcDrawableContainer;
    protected View arcDrawableView;
    protected ArcDrawable arcDrawable;

    protected Button nextButton;
    protected ImageView heartImageView;
    protected HeartBeatAnimation heartBeatAnimation;

    // The previousBpm comes from the TaskResult of an unrelated CrfHeartRateStepLayout
    private int previousBpm = -1;

    private boolean hasDetectedStart = false;
    private List<BpmHolder> bpmList;

    protected  Recorder cameraRecorder;
    protected boolean shouldContinueOnStop = false;
    protected boolean isFinished = false;

    public CrfHeartRateStepLayout(Context context) {
        super(context);
    }

    public CrfHeartRateStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CrfHeartRateStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CrfHeartRateStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public int getContentResourceId() {
        return R.layout.crf_step_layout_heart_rate;
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
    public void initialize(Step step, StepResult result) {
        super.initialize(step, result);
    }

    @Override
    public void setupActiveViews() {
        super.setupActiveViews();

        cameraPreview = findViewById(R.id.crf_camera_texture_view);

        crfMessageTextView = findViewById(R.id.crf_heart_rate_title);
        speakText(getContext().getString(R.string.crf_camera_cover));
        crfMessageTextView.setText(R.string.crf_camera_cover);

        cameraSourcePreview = findViewById(R.id.crf_camera_source);
        cameraSourcePreview.setSurfaceMask(true);
        if (cameraRecorder instanceof HeartRateCameraRecorder) {
            cameraSourcePreview.setCameraSizeListener((width, height) -> {
                ViewGroup.LayoutParams params = arcDrawableContainer.getLayoutParams();
                int size = Math.min(width, height);
                params.width = size;
                params.height = size;
                arcDrawableContainer.setLayoutParams(params);
                arcDrawableContainer.requestLayout();
            });
        }

        heartRateTextContainer = findViewById(R.id.crf_bpm_text_container);
        heartRateTextContainer.setVisibility(View.GONE);
        heartRateNumber = findViewById(R.id.crf_heart_rate_number);

        arcDrawableContainer = findViewById(R.id.crf_arc_drawable_container);
        arcDrawableView = findViewById(R.id.crf_arc_drawable);
        arcDrawable = new ArcDrawable();
        arcDrawable.setColor(ResourcesCompat.getColor(getResources(), R.color.greenyBlue, null));
        arcDrawable.setArchWidth(getResources().getDimensionPixelOffset(R.dimen.crf_ard_drawable_width));
        arcDrawable.setDirection(Path.Direction.CW);
        arcDrawable.setIncludeFullCirclePreview(true);
        arcDrawable.setFullCirclePreviewColor(ResourcesCompat.getColor(getResources(), R.color.silver, null));
        arcDrawableView.setBackground(arcDrawable);

        nextButton = findViewById(R.id.crf_next_button);
        nextButton.setVisibility(View.GONE);
        nextButton.setOnClickListener(view -> onNextButtonClicked());

        heartImageView = findViewById(R.id.crf_heart_icon);
        heartImageView.setVisibility(View.GONE);
    }

    // Wait for intelligent start to call super.start()
    // super.start();
    @SuppressLint("MissingSuperCall")
    @Override
    public void start() {
        // Wait for intelligent start to
        hasDetectedStart = false;
        bpmList = new ArrayList<>();

        HeartRateCameraRecorderConfig config =
                new HeartRateCameraRecorderConfig("HeartRateCamera");
        cameraRecorder = config.recorderForStep(
                cameraSourcePreview, activeStep, this, getOutputDirectory(getContext()));
        cameraRecorder.setRecorderListener(this);
        
        // camera1
        // If the camera was not set up properly,
        if (cameraRecorder instanceof HeartRateCameraRecorder) {
            cameraRecorder.start(getContext().getApplicationContext());
            if(!cameraSourcePreview.isCameraSetup()) {
                showOkAlertDialog("Error opening camera interface", (dialogInterface, i) ->
                        callbacks.onSaveStep(StepCallbacks.ACTION_PREV, activeStep, null));
            }
        }
        
        // camera2
        if((cameraRecorder instanceof HeartRateCamera2Recorder)) {
            cameraSourcePreview.setVisibility(GONE);
            startRecorderForTextureView();
        }
    }
    
    private void startRecorderForTextureView() {
        if (cameraPreview.isAvailable()) {
            cameraRecorder.start(getContext().getApplicationContext());
            return;
        }
        cameraPreview.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                cameraRecorder.start(getContext().getApplicationContext());
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
    }

    @Override
    public void pauseActiveStepLayout() {
        super.pauseActiveStepLayout();
        if (!isFinished) { // pause happens when we've finished too. forceStop deletes the .mp4
            forceStop();  // we do not allow this step to run in the background
            callbacks.onSaveStep(StepCallbacks.ACTION_PREV, activeStep, null);
        }
    }

    @Override
    public void forceStop() {
        super.forceStop();
        if (cameraRecorder != null && cameraRecorder.isRecording()) {
            cameraRecorder.cancel();
        }
    }

    // BPM and heart rate is ready to go, switch the UI
    private void intelligentStartDetected() {
        heartImageView.setVisibility(View.VISIBLE);
        arcDrawableContainer.setVisibility(View.VISIBLE);
        arcDrawable.setSweepAngle(0.0f);
        cameraPreview.setVisibility(View.INVISIBLE);
        cameraSourcePreview.setVisibility(View.INVISIBLE);
        
        hasDetectedStart = true;
        
        if(cameraRecorder instanceof  HeartRateCamera2Recorder) {
            startVideoRecording();
        }
        super.start();  // start the recording process

        // We need to stop the camera recorder ourselves
        mainHandler.postDelayed(() -> cameraRecorder.stop(),
                activeStep.getStepDuration() * 1000L);
    }
    
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    void startVideoRecording() {
        ((HeartRateCamera2Recorder) cameraRecorder).startVideoRecording();
    
    }

    @Override
    public void doUIAnimationPerSecond() {
        if (hasDetectedStart) {
            float progress = 1.0f - ((float)secondsLeft / (float)activeStep.getStepDuration());
            arcDrawable.setSweepAngle(ArcDrawable.FULL_SWEEPING_ANGLE * progress);
        }
    }

    @Override
    protected void recorderServiceSpokeText(String spokenText) {
        super.recorderServiceSpokeText(spokenText);
        crfMessageTextView.setText(spokenText);
    }

    @UiThread
    public void bpmUpdate(BpmHolder bpmHolder) {
        if (heartBeatAnimation == null) {
            heartBeatAnimation = new HeartBeatAnimation(bpmHolder.bpm);
            heartImageView.startAnimation(heartBeatAnimation);
        }
        heartBeatAnimation.setBpm(bpmHolder.bpm);
        bpmList.add(bpmHolder);
    }

    @Override
    public void stop() {
        super.stop();

        isFinished = true;
        if (shouldContinueOnStop) {
            onNextButtonClicked();
        }
    }

    protected void onNextButtonClicked() {
        shouldContinueOnStop = true;
        if (isFinished) {
            callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, activeStep, stepResult);
        }
    }

    protected void showCompleteUi() {
        nextButton.setVisibility(View.VISIBLE);
        heartImageView.clearAnimation();
        heartImageView.setVisibility(View.GONE);
        cameraSourcePreview.setVisibility(View.INVISIBLE);
        arcDrawableContainer.setVisibility(View.GONE);
        heartRateTextContainer.setVisibility(View.VISIBLE);

        if (!bpmList.isEmpty()) {
            int bpmSum = 0;
            for (BpmHolder bpmHolder : bpmList) {
                bpmSum += bpmHolder.bpm;
            }
            int averageBpm = bpmSum / bpmList.size();
            setBpmDifferenceResult(averageBpm);
            setBpmStartAndEnd(bpmList.get(0), bpmList.get(bpmList.size()-1));
            heartRateNumber.setText(String.format(Locale.getDefault(), "%d", averageBpm));
        } else {
            setBpmDifferenceResult(0);
            heartRateNumber.setText(String.format(Locale.getDefault(), "%d", 0));
        }
    }

    /**
     * Saves the first and last BPM readings of the step
     * @param bpmStart first BPM reading recorded
     * @param bpmEnd last BPM reading recorded
     */
    private void setBpmStartAndEnd(BpmHolder bpmStart, BpmHolder bpmEnd) {
        String startIdentifier = activeStep.getIdentifier() + BPM_START_IDENTIFIER_SUFFIX;
        stepResult.setResultForIdentifier(startIdentifier,
                getBpmStepResult(startIdentifier, bpmStart));

        String endIdentifier = activeStep.getIdentifier() + BPM_END_IDENTIFIER_SUFFIX;
        stepResult.setResultForIdentifier(endIdentifier,
                getBpmStepResult(endIdentifier, bpmEnd));
    }

    private StepResult<Integer> getBpmStepResult(String identifier, BpmHolder bpmHolder) {
        QuestionStep bpmQuestion =
                new QuestionStep(identifier, identifier, new DecimalAnswerFormat(0,300));
        StepResult<Integer> bpmResult = new StepResult<>(bpmQuestion);
        bpmResult.setResult(bpmHolder.bpm);
        bpmResult.setStartDate(new Date(bpmHolder.timestamp));
        bpmResult.setEndDate(new Date(bpmHolder.timestamp));

        return bpmResult;
    }

    /**
     * The BPM result will be stored in the TaskResult and if there are multiple
     * CrfHeartRateSteps, the difference between the BPMs will be stored in the result as well
     * @param bpm the average BPM
     */
    private void setBpmDifferenceResult(int bpm) {
        // See if we have a previous BPM, in which case we should calculate the difference
        if (previousBpm >= 0) {
            String bpmStepId = CrfCompletionStepLayout.COMPLETION_BPM_VALUE_RESULT;
            StepResult<String> bpmResult = new StepResult<>(new Step(bpmStepId));
            int bpmDifference = Math.abs(bpm - previousBpm);
            bpmResult.setResult(String.valueOf(bpmDifference));
            stepResult.setResultForIdentifier(bpmStepId, bpmResult);
        } else {
            String bpmStepId = CrfHeartRateStepLayout.AVERAGE_BPM_IDENTIFIER;
            StepResult<String> bpmResult = new StepResult<>(new Step(bpmStepId));
            bpmResult.setResult(String.valueOf(bpm));
            stepResult.setResultForIdentifier(bpmStepId, bpmResult);
        }
    }

    @Override
    public void intelligentStartUpdate(float progress, boolean ready) {
        if (ready) {
            intelligentStartDetected();
        }
    }

    @Override
    public int crfToolbarTintColor() {
        return R.color.azure;
    }

    @Override
    public int crfStatusBarColor() {
        return R.color.white;
    }

    @Override
    public void crfTaskResult(TaskResult taskResult) {
        String bpmString = StepResultHelper.findStringResult(taskResult, AVERAGE_BPM_IDENTIFIER);
        if (bpmString != null) {
            previousBpm = Integer.parseInt(bpmString);
        }
    }

    @Override
    public void onComplete(Recorder recorder, Result result) {
        stepResult.setResultForIdentifier(recorder.getIdentifier(), result);
        
        // don't do this for video recorder, wait for heart rate JSON
        if (recorder instanceof JsonArrayDataRecorder) {
            showCompleteUi();
        }
    }

    @Override
    public void onFail(Recorder recorder, Throwable error) {
        super.showOkAlertDialog(error.getMessage(), (dialogInterface, i) ->
                callbacks.onSaveStep(StepCallbacks.ACTION_END, activeStep, null));
    }

    @Nullable
    @Override
    public Context getBroadcastContext() {
        return getContext().getApplicationContext();
    }

    private class HeartBeatAnimation extends AlphaAnimation {

        void setBpm(int bpm) {
            setDuration((long)((60.0f / (float)bpm) * 1000));
        }

        HeartBeatAnimation(int bpm) {
            super(1.0f, 1.0f);
            setBpm(bpm);
            setInterpolator(new AccelerateInterpolator());
            setRepeatCount(Animation.INFINITE);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            float alpha;
            if (interpolatedTime < 0.5f) { // we are fading out
                alpha = (2 * (0.5f - interpolatedTime));
            } else {
                alpha = (2 * (interpolatedTime - 0.5f));
            }
            t.setAlpha(alpha);
        }
    }
}
