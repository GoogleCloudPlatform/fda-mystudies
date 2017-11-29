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
import android.widget.LinearLayout;
import android.widget.TextView;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.step.layout.FormStepLayout;
import org.sagebionetworks.research.crf.R;

/**
 * Created by rianhouston on 11/22/17.
 */

public class CrfFormStepLayout extends FormStepLayout {

    private CrfFormStep crfFormStep;

    public CrfFormStepLayout(Context context) {
    super(context);
  }

    public CrfFormStepLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

    public CrfFormStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CrfFormStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initialize(Step step, StepResult result) {
        validateAndSetCrfFormStep(step);
        super.initialize(step, result);
        refreshSubmitBar();
    }

    protected void validateAndSetCrfFormStep(Step step) {
        if (!(step instanceof CrfFormStep)) {
            throw new IllegalStateException("CrfFormStepLayout only works with CrfFormStep");
        }
        this.crfFormStep = (CrfFormStep) step;
    }

    @Override
    public int getContentResourceId() {
        return org.researchstack.backbone.R.layout.rsb_form_step_layout;
    }

    @Override
    public void refreshSubmitBar() {
        if (submitBar == null) {
            return;  // custom layouts may not have a submit bar
        }
        submitBar.setOrientation(LinearLayout.VERTICAL);
        submitBar.setPositiveAction(v -> onNextClicked());

        TextView skip = (TextView)submitBar.findViewById(R.id.bar_submit_skip);
        if (formStep.getSkipTitle() == null) {
            submitBar.setNegativeTitle(org.researchstack.backbone.R.string.rsb_step_skip);
            skip.setVisibility(View.GONE);
        } else {
            skip.setText(formStep.getSkipTitle());
            skip.setVisibility(View.VISIBLE);
            skip.setOnClickListener(view -> onSkipClicked());
        }
        submitBar.setNegativeAction(v -> isBackEventConsumed());

        submitBar.getNegativeActionView().setVisibility(View.VISIBLE);
        submitBar.setNegativeTitle("Back");
        if (!formStep.isOptional()) {
            // If form isnt optional, check and see if the question steps are
            for (FormStepData stepData : subQuestionStepData) {
                if (!stepData.step.isOptional()) {
                    //submitBar.getNegativeActionView().setVisibility(View.GONE);
                }
            }
        }
    }
}
