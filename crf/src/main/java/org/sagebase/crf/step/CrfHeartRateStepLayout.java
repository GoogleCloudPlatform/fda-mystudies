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
import android.content.Intent;
import android.graphics.Path;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import org.sagebase.crf.R;
import org.sagebase.crf.camera.CameraSourcePreview;
import org.sagebase.crf.step.active.BpmRecorder;
import org.sagebase.crf.step.active.HeartRateCamera2Recorder;
import org.sagebase.crf.step.active.HeartRateCameraRecorder;
import org.sagebase.crf.step.active.HeartRateCameraRecorderConfig;
import org.sagebase.crf.view.CrfTaskStatusBarManipulator;
import org.sagebase.crf.view.CrfTaskToolbarProgressManipulator;
import org.sagebase.crf.view.CrfTaskToolbarTintManipulator;
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
        BpmRecorder.CameraCoveredListener,
        BpmRecorder.PressureListener,
        BpmRecorder.DeclineHRListener,
        BpmRecorder.AbnormalHRListener,
        RecorderListener,
        CrfTaskToolbarProgressManipulator,
        CrfResultListener {
    private static final Logger LOG = LoggerFactory.getLogger(CrfHeartRateStepLayout.class);

    protected CrfHeartRateCameraStep step;
//    private static final String AVERAGE_BPM_IDENTIFIER = "AVERAGE_BPM_IDENTIFIER";
    private static final String BPM_START_IDENTIFIER_SUFFIX = ".heartRate_start";
    private static final String BPM_END_IDENTIFIER_SUFFIX = ".heartRate_end";

    public static final String RESTING_BPM_VALUE_RESULT = "resting";
    public static final String PEAK_BPM_VALUE_RESULT = "peak";

    private CameraSourcePreview cameraSourcePreview;
    private TextureView cameraPreview;
    public TextureView getCameraPreview() {
        return cameraPreview;
    }
    protected TextView crfMessageTextView;
    protected TextView crfOops;

    protected View heartRateTextContainer;
    protected TextView heartRateNumber;

    protected TextView currentHeartRate;
    protected TextView calculateSuccess;
    protected TextView bpmText;

    protected View heartContainer;

    protected View arcDrawableContainer;
    protected View arcDrawableView;
    protected ArcDrawable arcDrawable;
    protected RelativeLayout layout;

    protected ConstraintLayout buttonContainer;
    protected Button nextButton;
    protected Button redoButton;

    protected ImageView heartImageView;
    protected HeartBeatAnimation heartBeatAnimation;

    protected ImageView crfCompletionIcon;
    protected TextView crfPractice;
    protected TextView coverFlash;
    protected TextView yourHRis;
    protected TextView finalBpm;
    protected TextView finalBpmText;

    private boolean hasDetectedStart = false;
    private List<BpmHolder> bpmList;

    protected  Recorder cameraRecorder;
    protected boolean shouldContinueOnStop = false;
    protected boolean isFinished = false;
    private boolean shouldShowFinishUi = false;

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
        this.step = (CrfHeartRateCameraStep) step;
        super.initialize(step, result);
    }

    @Override
    public void setupActiveViews() {
        super.setupActiveViews();

        shouldShowFinishUi = getResources().getBoolean(R.bool.heart_rate_show_finish_ui);

        cameraPreview = findViewById(R.id.crf_camera_texture_view);

        crfOops = findViewById(R.id.crf_oops);
        crfOops.setText("Oops!");
        crfOops.setVisibility(View.INVISIBLE);

        bpmText = findViewById(R.id.crf_heart_rate_bpm);

        crfMessageTextView = findViewById(R.id.crf_heart_rate_title);
        speakText(getContext().getString(R.string.crf_camera_cover));
        crfMessageTextView.setText(R.string.crf_camera_cover);
        if (shouldShowFinishUi) {
            //Remove the padding at the top for the progress bar, that is not shown in this case
            crfMessageTextView.setPadding(crfMessageTextView.getPaddingLeft(), 0, crfMessageTextView.getPaddingRight(), crfMessageTextView.getPaddingBottom());
        }

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
        currentHeartRate = findViewById(R.id.crf_current_bpm);

        arcDrawableContainer = findViewById(R.id.crf_arc_drawable_container);
        arcDrawableView = findViewById(R.id.crf_arc_drawable);
        arcDrawable = new ArcDrawable();
        arcDrawable.setColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
        arcDrawable.setArchWidth(getResources().getDimensionPixelOffset(R.dimen.crf_ard_drawable_width));
        arcDrawable.setDirection(Path.Direction.CW);
        arcDrawable.setIncludeFullCirclePreview(true);
        arcDrawable.setFullCirclePreviewColor(ResourcesCompat.getColor(getResources(), R.color.silver, null));
        arcDrawableView.setBackground(arcDrawable);

        layout = findViewById(R.id.crf_root_instruction_layout);

        heartContainer = findViewById(R.id.crf_heart_container);
        heartContainer.setBackgroundColor(getResources().getColor(R.color.white));

        nextButton = findViewById(R.id.button_go_forward);
        nextButton.setVisibility(View.GONE);
        nextButton.setOnClickListener(view -> onNextButtonClicked());

        calculateSuccess = findViewById(R.id.crf_calculate);
        calculateSuccess.setVisibility(View.INVISIBLE);

        redoButton = findViewById(R.id.crf_redo_button);
        redoButton.setVisibility(View.GONE);
        redoButton.setOnClickListener(view -> onNextButtonClicked());

        heartImageView = findViewById(R.id.crf_heart_icon);
        heartImageView.setVisibility(View.GONE);

        buttonContainer = findViewById(R.id.crf_next_button_container);

        crfCompletionIcon = findViewById(R.id.crf_completion_icon);
        crfPractice = findViewById(R.id.crf_practice);
        coverFlash = findViewById(R.id.crf_later_tests);
        yourHRis = findViewById(R.id.crf_your_hr_is);
        finalBpm = findViewById(R.id.crf_final_bpm);
        finalBpmText = findViewById(R.id.crf_bpm_text);

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
                new HeartRateCameraRecorderConfig(step.stepIdentifier);
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
        // Testing
        heartImageView.setVisibility(View.VISIBLE);
        arcDrawableContainer.setVisibility(View.VISIBLE);
        arcDrawable.setSweepAngle(0.0f);
        cameraPreview.setVisibility(View.GONE);
        cameraSourcePreview.setVisibility(View.GONE);

        hasDetectedStart = true;

        if(cameraRecorder instanceof  HeartRateCamera2Recorder) {
            //Due to privacy concerns, and not currently having a scientific use for the raw video
            //disable video recording
//            startVideoRecording();
        }
        super.start();  // start the recording process

        // We need to stop the camera recorder ourselves
        mainHandler.postDelayed(() -> cameraRecorder.stop(),
                activeStep.getStepDuration() * 1000L);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    void startVideoRecording() {
        //HACK for Samsung Galaxy J7 Neo that records 0 bpm when recording video
        String device = Build.MANUFACTURER + Build.MODEL;
        if ("samsungSM-J701M".equalsIgnoreCase(device)) {
            //TODO: Figure out a better solution if there are other devices that can't record video and heart rate at same time
            // -Nathaniel 12/18/18
            return;
        }

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
            heartImageView.setVisibility(VISIBLE);
        }
        currentHeartRate.setText(bpmHolder.bpm + " " + getContext().getString(R.string.crf_bpm));
        arcDrawableContainer.setVisibility(VISIBLE);
        currentHeartRate.setVisibility(VISIBLE);
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
        if (shouldShowFinishUi) {
            showFinishUi();
        } else {
            shouldContinueOnStop = true;
            if (isFinished) {
                callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, activeStep, stepResult);
            }
        }
    }

    protected void onDoneButtonClicked() {
        callbacks.onSaveStep(StepCallbacks.ACTION_END, activeStep, stepResult);
    }

    public void onRedoButtonClicked() {
        pauseActiveStepLayout();
        forceStop();
        callbacks.onSaveStep(StepCallbacks.ACTION_PREV, activeStep, null);
    }

    protected void showFailureUi() {
        crfOops.setText(R.string.crf_sorry);
        crfOops.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);
        //heartRateTextContainer.setVisibility(View.VISIBLE);
        calculateSuccess.setVisibility(View.VISIBLE);
        arcDrawableContainer.setVisibility(View.VISIBLE);
        arcDrawable.setSweepAngle(0.0f);

        String troubleString = getResources().getString(R.string.crf_having_trouble);
        int startIndex = troubleString.indexOf("Tips");
        int endIndex = troubleString.length();
        SpannableString troubleSpannable = new SpannableString(troubleString);
        troubleSpannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent i = new Intent(getContext(), CrfTrainingInfo.class);
                i.putExtra(CrfTrainingInfoKt.EXTRA_HTML_FILENAME, "crf_tips_resting_hr.html");
                i.putExtra(CrfTrainingInfoKt.EXTRA_TITLE, getResources().getString(R.string.crf_tips_for_measuring));
                getContext().startActivity(i);
            }
        }, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        calculateSuccess.setText(troubleSpannable);
        calculateSuccess.setMovementMethod(LinkMovementMethod.getInstance());

        if (step.isHrRecoveryStep) {
            nextButton.setOnClickListener(view -> onDoneButtonClicked());
            nextButton.setText(R.string.crf_done);
        } else {
            nextButton.setOnClickListener(view -> onRedoButtonClicked());
            nextButton.setText(R.string.crf_redo);
        }

        findViewById(R.id.crf_error_icon_view).setVisibility(View.VISIBLE);


        heartImageView.clearAnimation();
        heartImageView.setVisibility(View.GONE);
        cameraSourcePreview.setVisibility(View.GONE);
        cameraPreview.setVisibility(View.GONE);
        currentHeartRate.setVisibility(View.GONE);
        crfMessageTextView.setVisibility(View.INVISIBLE);

    }

    protected void showCompleteUi() {
        crfOops.setText(R.string.crf_nicely_done);
        crfOops.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);
        heartRateTextContainer.setVisibility(View.VISIBLE);
        calculateSuccess.setVisibility(View.VISIBLE);
        arcDrawableContainer.setVisibility(View.VISIBLE);


        heartImageView.clearAnimation();
        heartImageView.setVisibility(View.GONE);
        cameraSourcePreview.setVisibility(View.GONE);
        cameraPreview.setVisibility(View.GONE);
        currentHeartRate.setVisibility(View.GONE);
        crfMessageTextView.setVisibility(View.INVISIBLE);


        if (!bpmList.isEmpty()) {
            int bpmSum = 0;
            for (BpmHolder bpmHolder : bpmList) {
                bpmSum += bpmHolder.bpm;
            }
            int averageBpm = bpmSum / bpmList.size();

            BpmHolder bestHolder = findBestHr();
            int bestHr = bestHolder.bpm;

            setBpmResult(bestHr);
            setBpmStartAndEnd(bpmList.get(0), bpmList.get(bpmList.size()-1));
            heartRateNumber.setText(String.format(Locale.getDefault(), "%d", bestHr));
            finalBpm.setText(String.format(Locale.getDefault(), "%d", bestHr));
        } else {
            setBpmResult(0);
            heartRateNumber.setText(String.format(Locale.getDefault(), "%d", 0));
            finalBpm.setText(String.format(Locale.getDefault(), "%d", 0));
        }
    }

    private boolean haveValidHr() {
        if (!bpmList.isEmpty()) {
            for (BpmHolder bpmHolder : bpmList) {
                if (bpmHolder.confidence > 0.5) {
                    return true;
                }
            }
        }
        return false;

    }

    private BpmHolder findBestHr() {
        BpmHolder bestHr = null;

        if (!bpmList.isEmpty()) {
            bestHr = bpmList.get(0);
            for (BpmHolder bpmHolder : bpmList) {
                if (bpmHolder.confidence > bestHr.confidence) {
                    bestHr = bpmHolder;
                }
            }
        }
        return bestHr;
    }

    private void showFinishUi() {
        shouldShowFinishUi = false;
        layout.setBackgroundColor(getResources().getColor(R.color.completion_background_end));
        buttonContainer.setBackgroundColor(getResources().getColor(R.color.white));
        yourHRis.setVisibility(View.VISIBLE);
        finalBpm.setVisibility(View.VISIBLE);
        finalBpmText.setVisibility(View.VISIBLE);

        crfCompletionIcon.setVisibility(View.VISIBLE);
        crfPractice.setVisibility(View.VISIBLE);
        coverFlash.setVisibility(View.VISIBLE);

        crfOops.setVisibility(View.INVISIBLE);
        calculateSuccess.setVisibility(View.INVISIBLE);
        heartContainer.setVisibility(View.GONE);



        nextButton.setText("Done");
        nextButton.setOnClickListener(view -> onNextButtonClicked());

        redoButton.setVisibility(View.VISIBLE);
        redoButton.setOnClickListener(view -> onRedoButtonClicked());

        arcDrawableContainer.setVisibility(View.GONE);

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


    private void setBpmResult(int bpm) {
        String bpmStepId = CrfHeartRateStepLayout.RESTING_BPM_VALUE_RESULT;
        StepResult<String> bpmResult = new StepResult<>(new Step(bpmStepId));
        bpmResult.setResult(String.valueOf(bpm));
        stepResult.setResultForIdentifier(bpmStepId, bpmResult);
    }

    @Override
    public void intelligentStartUpdate(float progress, boolean ready) {
        if (ready) {
            intelligentStartDetected();
        }
    }

    @Override
    public void crfTaskResult(TaskResult taskResult) {

    }

    @Override
    public void onComplete(Recorder recorder, Result result) {
        stepResult.setResultForIdentifier(recorder.getIdentifier(), result);

        // don't do this for video recorder, wait for heart rate JSON
        if (recorder instanceof JsonArrayDataRecorder) {
            if (haveValidHr()) {
                showCompleteUi();
            } else {
                showFailureUi();
            }
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

    @Override
    public void pressureUpdate(PressureHolder pressure) {

    }

    @Override
    public void cameraUpdate(CameraCoveredHolder camera) {
        if(!camera.cameraCovered) {
            LOG.warn("Camera isn't covered");
            crfMessageTextView.setText(R.string.crf_move_finger_back);
            crfOops.setVisibility(View.VISIBLE);
            currentHeartRate.setText("--");

            heartImageView.setVisibility(View.INVISIBLE);
            arcDrawableContainer.setVisibility(View.INVISIBLE);
            arcDrawable.setSweepAngle(0.0f);
            cameraPreview.setVisibility(View.VISIBLE);
        } else {
            crfOops.setVisibility(View.INVISIBLE);
            crfMessageTextView.setText(R.string.crf_camera_cover);
            currentHeartRate.setText("Capturing...");

            heartImageView.setVisibility(View.VISIBLE);
            arcDrawableContainer.setVisibility(View.VISIBLE);
            cameraPreview.setVisibility(View.GONE);

        }
    }



    @Override
    public void abnormalHRUpdate(AbnormalHRHolder abnormal) {
        if(abnormal.abnormal) {
            StepResult<Boolean> abnormalHRResult = new StepResult<>(new Step("displaySurvey"));
            abnormalHRResult.setResult(false);
            stepResult.setResultForIdentifier("skipAbnormalStep",
                    abnormalHRResult);
        }
        else {
            StepResult<Boolean> abnormalHRResult = new StepResult<>(new Step("displaySurvey"));
            abnormalHRResult.setResult(true);
            stepResult.setResultForIdentifier("skipAbnormalStep",
                    abnormalHRResult);
        }
    }

    @Override
    public void declineHRUpdate(DeclineHRHolder decline) {
        if(decline.declining) {
            StepResult<Boolean> decliningHRResult = new StepResult<>(new Step("displayDecliningHR"));
            decliningHRResult.setResult(false);
            stepResult.setResultForIdentifier("skipDeclineStep",
                    decliningHRResult);
        }
        else {
            StepResult<Boolean> decliningHRResult = new StepResult<>(new Step("displayDecliningHR"));
            decliningHRResult.setResult(true);
            stepResult.setResultForIdentifier("skipDeclineStep",
                    decliningHRResult);
        }
    }

    @Override
    public boolean crfToolbarShowProgress() {
        return false;
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