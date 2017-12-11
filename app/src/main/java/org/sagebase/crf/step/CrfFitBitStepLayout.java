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
import android.support.annotation.VisibleForTesting;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.google.common.collect.Sets;

import org.researchstack.backbone.DataProvider;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.views.SubmitBar;
import org.sagebase.crf.fitbit.FitbitManager;
import org.sagebionetworks.bridge.android.manager.BridgeManagerProvider;
import org.sagebionetworks.bridge.researchstack.BridgeDataProvider;
import org.sagebionetworks.bridge.researchstack.CrfDataProvider;
import org.sagebionetworks.research.crf.R;

import java.util.Set;

/**
 * Created by TheMDP on 11/28/17.
 */

public class CrfFitBitStepLayout extends CrfInstructionStepLayout {

    private FitbitManager fitbitManager;

    public CrfFitBitStepLayout(Context context) {
        super(context);
    }

    public CrfFitBitStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CrfFitBitStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CrfFitBitStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int
            defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initialize(Step step, StepResult result) {

        if (!(DataProvider.getInstance() instanceof BridgeDataProvider)) {
            throw new IllegalStateException("CrfClinicDataGroupsStepLayout only works with BridgeDataProvider");
        }

        BridgeDataProvider bridgeDataProvider = (BridgeDataProvider)DataProvider.getInstance();

        Set<String> dataGroups = Sets.newHashSet(
                bridgeDataProvider.getLocalDataGroups()
        );

        if(shouldAllowSkip(dataGroups)) {
            step.setOptional(true);
        }

        super.initialize(step, result);
        addSubmitBarForSkipFunctionality();

        if (fitbitManager == null) {
            fitbitManager = new FitbitManager(getContext(), null);
        }
        // We come back into this step from an activity intent, so it will be
        // re-created after the user authenticates, so we must check here too
        if (fitbitManager.isAuthenticated()) {
            super.onComplete();
        }
    }

    protected void addSubmitBarForSkipFunctionality() {
        FrameLayout nextButtonContainer = findViewById(R.id.crf_next_button_container);
        LayoutParams layoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        submitBar = new SubmitBar(getContext());
        nextButtonContainer.addView(submitBar, layoutParams);
        refreshStep();  // needs to reset submit bar content
        nextButton.setVisibility(View.GONE);
    }

    @VisibleForTesting
    static boolean shouldAllowSkip(Set<String> dataGroups) {
        return !Sets.intersection(CrfDataProvider.TEST_DATA_GROUPS, dataGroups).isEmpty();
    }

    @Override
    public void goForwardClicked(View v) {
        onComplete();
    }

    @Override
    protected void onComplete() {
        if (fitbitManager.isAuthenticated()) {
            super.onComplete();
        } else {
            fitbitManager.authenticate();
        }
    }

    public static class CrfFitBitStep extends CrfInstructionStep {

        /* Default constructor needed for serialization/deserialization of object */
        public CrfFitBitStep() {
            super();
        }

        public CrfFitBitStep(String identifier, String title, String detailText) {
            super(identifier, title, detailText);
        }

        @Override
        public Class getStepLayoutClass() {
            return CrfFitBitStepLayout.class;
        }
    }
}
