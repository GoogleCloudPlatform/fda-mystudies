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

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.sagebase.crf.fitbit.FitbitManager;

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

    public CrfFitBitStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initialize(Step step, StepResult result) {
        super.initialize(step, result);
        if (fitbitManager == null) {
            fitbitManager = new FitbitManager(getContext(), null);
        }
        // We come back into this step from an activity intent, so it will be
        // re-created after the user authenticates, so we must check here too
        if (fitbitManager.isAuthenticated()) {
            super.onComplete();
        }
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
