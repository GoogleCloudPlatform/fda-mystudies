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

package org.sagebase.crf;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.ActiveStep;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.ActiveTaskActivity;

import org.researchstack.backbone.ui.step.layout.ActiveStepLayout;
import org.researchstack.backbone.ui.step.layout.StepLayout;
import org.researchstack.backbone.utils.ResUtils;
import org.sagebase.crf.step.CrfResultListener;
import org.sagebase.crf.step.CrfStartTaskStep;
import org.sagebase.crf.view.CrfTaskStatusBarManipulator;
import org.sagebase.crf.view.CrfTaskToolbarActionManipulator;
import org.sagebase.crf.view.CrfTaskToolbarIconManipulator;
import org.sagebase.crf.view.CrfTaskToolbarProgressManipulator;
import org.sagebase.crf.view.CrfTaskToolbarTintManipulator;
import org.sagebase.crf.view.CrfTransparentToolbar;
import org.sagebionetworks.research.crf.R;

/**
 * Created by TheMDP on 10/24/17.
 */

public class CrfActiveTaskActivity extends ActiveTaskActivity {

    public static Intent newIntent(Context context, Task task) {
        Intent intent = new Intent(context, CrfActiveTaskActivity.class);
        intent.putExtra(EXTRA_TASK, task);
        return intent;
    }

    protected CrfTransparentToolbar getToolbar() {
        if (toolbar != null && toolbar instanceof CrfTransparentToolbar) {
            return (CrfTransparentToolbar)toolbar;
        }
        return null;
    }

    private TextView crfStepProgressTextview;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean status = super.onCreateOptionsMenu(menu);
        refreshToolbar();
        return status;
    }

    @Override
    public void onDataAuth() {
        storageAccessUnregister();
        MainApplication.mockAuthenticate(this);
        super.onDataReady();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        crfStepProgressTextview = findViewById(R.id.crf_step_progress_textview);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_active_task;
    }

    public int getViewSwitcherRootId() {
        return R.id.crf_active_container;
    }

    @Override
    public @IdRes int getToolbarResourceId() {
        return R.id.crf_task_toolbar;
    }

    @Override
    public void showStep(Step step, boolean alwaysReplaceView) {
        super.showStep(step, alwaysReplaceView);
        refreshToolbar();

        // Let steps know about the task result if it needs to
        if (getCurrentStepLayout() instanceof CrfResultListener) {
            ((CrfResultListener)getCurrentStepLayout()).crfTaskResult(taskResult);
        }
    }

    public void refreshToolbar() {
        if (getCurrentStepLayout() == null) {
            return;
        }

        CrfTransparentToolbar crfToolbar = getToolbar();
        StepLayout current = getCurrentStepLayout();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Allow for customization of the toolbar
        crfToolbar.refreshToolbar(
                actionBar,      // used to set icons
                current,        // the object that may inherit from a manipulator
                defaultToolbarTintColor(),
                R.drawable.crf_ic_close,
                CrfTaskToolbarIconManipulator.NO_ICON);

        // The text color of the step progress defaults to white,
        // but is set to a darker theme for all tint colors other than white
        @ColorRes int stepProgressTextColorRes = defaultStepProgressColor();
        if (current instanceof CrfTaskToolbarTintManipulator) {
            if (((CrfTaskToolbarTintManipulator)current).crfToolbarTintColor() != R.color.white) {
                stepProgressTextColorRes = R.color.darkGrayText;
            }
        }
        int stepProgressTextColor = ResourcesCompat.getColor(getResources(), stepProgressTextColorRes, null);
        crfStepProgressTextview.setTextColor(stepProgressTextColor);

        // Set the visibility of the step progress text to mimic the progress bar visibility
        if (!(current instanceof CrfTaskToolbarProgressManipulator)) {
            crfToolbar.showProgressInToolbar(true);
            crfStepProgressTextview.setVisibility(View.VISIBLE);
        } else if (((CrfTaskToolbarProgressManipulator)current).crfToolbarShowProgress()) {
            crfStepProgressTextview.setVisibility(View.VISIBLE);
        } else {
            crfStepProgressTextview.setVisibility(View.GONE);
        }

        // Allow for customization of the status bar
        @ColorRes int statusBarColor = R.color.colorPrimaryDark;
        if (current instanceof CrfTaskStatusBarManipulator) {
            CrfTaskStatusBarManipulator manipulator = (CrfTaskStatusBarManipulator)current;
            if (manipulator.crfStatusBarColor() != CrfTaskStatusBarManipulator.DEFAULT_COLOR) {
                statusBarColor = manipulator.crfStatusBarColor();
            }
        }
        int color = ResourcesCompat.getColor(getResources(), statusBarColor, null);
        MainApplication.setStatusBarColor(this, color);

        // Set the step progress
        if (task instanceof OrderedTask) {
            OrderedTask orderedTask = (OrderedTask)task;
            int progress = orderedTask.getSteps().indexOf(currentStep);
            int max = orderedTask.getSteps().size();
            crfToolbar.setProgress(progress, max);

            // Set up the text and styling of the step 1 of 5, 2 of 5, etc.
            String progressStr = String.valueOf(progress + 1); // array index 0 should be 1
            String maxString = String.valueOf(max);
            String stepProgressStr = String.format(getString(R.string.crf_step_progress), progressStr, maxString);

            SpannableStringBuilder str = new SpannableStringBuilder(stepProgressStr);
            str.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                    stepProgressStr.indexOf(progressStr),
                    stepProgressStr.indexOf(progressStr) + progressStr.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            crfStepProgressTextview.setText(str);
        } else {
            Log.e("CrfActiveTaskActivity", "Progress Bars only work with OrderedTask");
        }

        // Media Volume controls
        int streamType = AudioManager.USE_DEFAULT_STREAM_TYPE;
        if (current instanceof CrfTaskMediaVolumeController) {
            if (((CrfTaskMediaVolumeController)current).controlMediaVolume()) {
                streamType = AudioManager.STREAM_MUSIC;
            }
        } else if (current instanceof ActiveStepLayout) {
            // ActiveStepLayouts have verbal spoken instructions
            streamType = AudioManager.STREAM_MUSIC;
        }
        setVolumeControlStream(streamType);
    }

    protected @ColorRes int defaultToolbarTintColor() {
        return R.color.white;
    }

    protected @ColorRes int defaultStepProgressColor() {
        return R.color.white;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        StepLayout current = getCurrentStepLayout();
        // Allow for customization of the toolbar
        if (current instanceof CrfTaskToolbarActionManipulator) {
            CrfTaskToolbarActionManipulator manipulator = (CrfTaskToolbarActionManipulator) current;
            if(item.getItemId() == org.researchstack.backbone.R.id.rsb_clear_menu_item) {
                return manipulator.crfToolbarRightIconClicked();
            }
        } else if(item.getItemId() == android.R.id.home) {
            showConfirmExitDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void notifyStepOfBackPress() {
        if (getCurrentStep() instanceof CrfStartTaskStep) {
            super.notifyStepOfBackPress();
        }
    }

    public interface CrfTaskMediaVolumeController {
        /**
         * @return if true, volume buttons will control media, not ringer
         */
        boolean controlMediaVolume();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            StepLayout layout = getCurrentStepLayout();
            if(layout instanceof CrfActivityResultListener) {
                ((CrfActivityResultListener)layout).onActivityFinished(requestCode, resultCode, data);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
