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
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.layout.InstructionStepLayout;

import org.researchstack.backbone.utils.ResUtils;
import org.sagebionetworks.research.crf.R;

/**
 * Created by TheMDP on 10/24/17.
 */

public class CrfInstructionStepLayout extends InstructionStepLayout {

    protected CrfInstructionStep crfInstructionStep;
    protected Button nextButton;
    protected View rootInstructionLayout;

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
        validateAndSetCrfStep(step);
        super.initialize(step, result);
    }

    protected void validateAndSetCrfStep(Step step) {
        if (!(step instanceof CrfInstructionStep)) {
            throw new IllegalStateException("CrfInstructionStepLayout only works with CrfInstructionStep");
        }
        this.crfInstructionStep = (CrfInstructionStep)step;
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
    public void connectStepUi(int titleRId, int textRId, int imageRId, int detailRId) {
        super.connectStepUi(
                R.id.crf_intruction_title,
                R.id.crf_intruction_text,
                R.id.crf_image_view,
                R.id.crf_instruction_more_detail_text);

        nextButton = findViewById(R.id.button_go_forward);
        rootInstructionLayout = findViewById(R.id.crf_root_instruction_layout);
    }

    @Override
    public void refreshStep() {
        super.refreshStep();

        if (crfInstructionStep.buttonText != null) {
            nextButton.setText(crfInstructionStep.buttonText);
        }
        if (crfInstructionStep.buttonType != null) {
            switch (crfInstructionStep.buttonType) {
                case DEFAULT:
                    nextButton.setBackgroundResource(R.drawable.crf_rounded_button_salmon);
                    nextButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.rsb_white, null));
                    break;
                case DEFAULT_WHITE_SALMON:
                    nextButton.setBackgroundResource(R.drawable.crf_rounded_button_white);
                    nextButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.salmon, null));
                    break;
                case DEFAULT_WHITE_DEEP_GREEN:
                    nextButton.setBackgroundResource(R.drawable.crf_rounded_button_white);
                    nextButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.deepGreen, null));
                    break;
                case HEART:
                    // TODO: setup image button
                    break;
                case TREADMILL:
                    // TODO: setup image button
                    break;
            }
        }
        nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                goForwardClicked(view);
            }
        });
        if (crfInstructionStep.backgroundColorRes != null) {
            int colorId = ResUtils.getColorResourceId(getContext(), crfInstructionStep.backgroundColorRes);
            rootInstructionLayout.setBackgroundResource(colorId);
        }
        if (crfInstructionStep.imageBackgroundColorRes != null) {
            int colorId = ResUtils.getColorResourceId(getContext(), crfInstructionStep.imageBackgroundColorRes);
            imageView.setBackgroundResource(colorId);
        }
    }

    public void goForwardClicked(View v) {
        callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, step, null);
    }
}
