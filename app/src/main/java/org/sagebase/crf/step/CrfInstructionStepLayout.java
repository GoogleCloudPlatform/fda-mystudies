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
import org.researchstack.backbone.ui.step.layout.InstructionStepLayout;
import org.sagebionetworks.research.crf.R;

/**
 * Created by TheMDP on 10/24/17.
 */

public class CrfInstructionStepLayout extends InstructionStepLayout {

    private CrfInstructionStep crfStep;

    public CrfInstructionStepLayout(Context context) {
        super(context);
    }

    public CrfInstructionStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CrfInstructionStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CrfInstructionStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public int getContentResourceId() {
        return R.layout.crf_step_layout_instruction;
    }

    @Override
    public void initialize(Step step, StepResult result) {
        super.initialize(step, result);
        validateAndSetStep(step);

        // Hide submit bar for this view, we use a custom next button
        getSubmitBar().setVisibility(View.GONE);
    }

    protected void validateAndSetStep(Step step) {
        if (!(step instanceof CrfInstructionStep)) {
            throw new IllegalStateException("CrfInstructionStepLayout only works with CrfInstructionStep");
        }
        this.crfStep = (CrfInstructionStep)step;
    }

    public void goForwardClicked(View v) {
        super.onComplete();
    }
}
