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
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.ActiveTaskActivity;

import org.researchstack.backbone.ui.step.layout.StepLayout;
import org.sagebase.crf.step.CrfTaskToolbarManipulator;
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
    }

    public void refreshToolbar() {
        if (getCurrentStepLayout() == null) {
            return;
        }

        CrfTransparentToolbar crfToolbar = getToolbar();
        StepLayout current = getCurrentStepLayout();
        ActionBar actionBar = getSupportActionBar();

        // Allow for customization of the toolbar
        boolean showProgress = true;
        if (current instanceof CrfTaskToolbarManipulator) {
            CrfTaskToolbarManipulator toolbarManipulator = (CrfTaskToolbarManipulator)current;
            showProgress = toolbarManipulator.showProgress();
            crfToolbar.refreshToolbar(actionBar, toolbarManipulator);
        } else {
            // Default toolbar style
            crfToolbar.tintToolbar(actionBar, R.color.azure, R.drawable.crf_ic_back, R.drawable.crf_ic_close);
            crfToolbar.showProgressInToolbar(showProgress);
        }

        if (showProgress && task instanceof OrderedTask) {
            OrderedTask orderedTask = (OrderedTask)task;
            int progress = orderedTask.getSteps().indexOf(currentStep);
            int max = orderedTask.getSteps().size();
            crfToolbar.setProgress(progress, max);
        } else {
            Log.e("CrfActiveTaskActivity", "Progress Bars only work with OrderedTask");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        StepLayout current = getCurrentStepLayout();
        // Allow for customization of the toolbar
        if (current instanceof CrfTaskToolbarManipulator) {
            CrfTaskToolbarManipulator toolbarManipulator = (CrfTaskToolbarManipulator) current;
            if(item.getItemId() == org.researchstack.backbone.R.id.rsb_clear_menu_item) {
                return toolbarManipulator.rightIconClicked();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
