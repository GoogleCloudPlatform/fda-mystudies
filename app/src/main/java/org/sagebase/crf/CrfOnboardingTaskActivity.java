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
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.ViewGroup;

import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.OnboardingTaskActivity;
import org.researchstack.backbone.ui.step.layout.StepLayout;
import org.sagebase.crf.fitbit.FitbitManager;
import org.sagebase.crf.fitbit.OAuthDAO;
import org.sagebase.crf.step.CrfExternalIdStep;
import org.sagebase.crf.step.CrfFitBitStepLayout;
import org.sagebase.crf.view.CrfTaskToolbarIconManipulator;
import org.sagebase.crf.view.CrfTransparentToolbar;
import org.sagebionetworks.bridge.android.manager.BridgeManagerProvider;
import org.sagebionetworks.research.crf.R;


/**
 * Created by TheMDP on 11/18/17.
 */

public class CrfOnboardingTaskActivity extends OnboardingTaskActivity {

    protected ViewGroup crfContainer;

    protected CrfTransparentToolbar getToolbar() {
        if (toolbar != null && toolbar instanceof CrfTransparentToolbar) {
            return (CrfTransparentToolbar)toolbar;
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        crfContainer = findViewById(R.id.crf_activity_onboarding_container);
    }

    @Override
    public int getContentViewId() {
        return R.layout.crf_activity_onboarding;
    }

    public int getViewSwitcherRootId() {
        return R.id.crf_active_container;
    }

    @Override
    public @IdRes
    int getToolbarResourceId() {
        return R.id.crf_task_toolbar;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean status = super.onCreateOptionsMenu(menu);
        refreshToolbar();
        return status;
    }

    @Override
    protected StepLayout getLayoutForStep(Step step) {

        if (step instanceof CrfExternalIdStep) {
            crfContainer.setBackgroundResource(R.color.deepGreen);
            getToolbar().setBackgroundResource(R.drawable.crf_toolbar_background);
        } else {
            crfContainer.setBackgroundResource(R.color.white);
            getToolbar().setBackgroundResource(R.color.deepGreen);
        }

        return super.getLayoutForStep(step);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CrfFitBitStepLayout.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Context applicationContext = this.getApplicationContext();
                new FitbitManager(applicationContext, new OAuthDAO(applicationContext,
                        BridgeManagerProvider.getInstance().getAuthenticationManager()))
                        .handleResponse(data, (FitbitManager.ErrorHandler)
                        getCurrentStepLayout());
            }
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
                R.color.white,
                R.drawable.crf_ic_close,
                CrfTaskToolbarIconManipulator.NO_ICON);

        crfToolbar.showProgressInToolbar(false);
    }
}
