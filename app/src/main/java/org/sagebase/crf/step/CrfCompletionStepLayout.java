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
import android.widget.TextView;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.utils.StepResultHelper;
import org.sagebionetworks.research.crf.R;

/**
 * Created by TheMDP on 11/5/17.
 */

public class CrfCompletionStepLayout extends CrfInstructionStepLayout implements CrfResultListener {

    public static final String COMPLETION_BPM_VALUE_RESULT = "completion_bpm_result";
    public static final String COMPLETION_DISTANCE_VALUE_RESULT = "completion_distance_result";

    private View mCompletionTextContainer;
    private TextView mCompletionTextTop;
    private TextView mCompletionValueText;
    private TextView mCompletionLabelText;
    private TextView mCompletionTextBottom;

    // This is passed in from the TaskResult
    private String mCompletionValueResult;

    private CrfCompletionStep crfCompletionStep;

    public CrfCompletionStepLayout(Context context) {
        super(context);
    }

    public CrfCompletionStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CrfCompletionStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CrfCompletionStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public int getContentResourceId() {
        return R.layout.crf_step_layout_completion;
    }

    @Override
    public void initialize(Step step, StepResult result) {
        validateAndSetCrfCompletionStep(step);
        super.initialize(step, result);
    }

    protected void validateAndSetCrfCompletionStep(Step step) {
        if (!(step instanceof CrfCompletionStep)) {
            throw new IllegalStateException("CrfCompletionStepLayout only works with CrfCompletionStep");
        }
        this.crfCompletionStep = (CrfCompletionStep)step;
    }

    @Override
    public void refreshStep() {
        super.refreshStep();

        mCompletionTextContainer = findViewById(R.id.crf_text_container);

        mCompletionTextTop = findViewById(R.id.crf_completion_text_top);
        mCompletionTextTop.setText(crfCompletionStep.topText);
        mCompletionTextTop.setVisibility((crfCompletionStep.topText == null) ? View.GONE : View.VISIBLE);

        mCompletionLabelText = findViewById(R.id.crf_completion_text_label);
        mCompletionLabelText.setText(crfCompletionStep.valueLabelText);
        mCompletionLabelText.setVisibility((crfCompletionStep.valueLabelText == null) ? View.GONE : View.VISIBLE);

        mCompletionTextBottom = findViewById(R.id.crf_completion_text_bottom);
        mCompletionTextBottom.setText(crfCompletionStep.bottomText);
        mCompletionTextBottom.setVisibility((crfCompletionStep.bottomText == null) ? View.GONE : View.VISIBLE);

        mCompletionValueText = findViewById(R.id.crf_completion_text_value);
        refreshCompletionValueLabel();
    }

    @Override
    public void crfTaskResult(TaskResult taskResult) {
        mCompletionValueResult = StepResultHelper.findStringResult(taskResult, crfCompletionStep.valueResultId);
        refreshCompletionValueLabel();
    }

    private void refreshCompletionValueLabel() {
        if (mCompletionValueText == null || mCompletionLabelText == null || mCompletionValueResult == null) {
            mCompletionTextContainer.setVisibility(View.GONE);
            return;
        }
        mCompletionTextContainer.setVisibility(View.VISIBLE);
        mCompletionValueText.setText(mCompletionValueResult);
    }
}
