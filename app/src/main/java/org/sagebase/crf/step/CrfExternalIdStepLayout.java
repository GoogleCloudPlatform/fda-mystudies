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
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.widget.Button;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.step.layout.LoginStepLayout;
import org.sagebase.crf.view.CrfTaskToolbarIconManipulator;
import org.sagebionetworks.research.crf.R;

/**
 * Created by TheMDP on 11/17/17.
 */

public class CrfExternalIdStepLayout extends LoginStepLayout implements CrfTaskToolbarIconManipulator {

    protected CrfExternalIdStep crfExternalIdStep;
    protected Button nextButton;

    public CrfExternalIdStepLayout(Context context) {
        super(context);
    }

    public CrfExternalIdStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CrfExternalIdStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CrfExternalIdStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initialize(Step step, StepResult result) {
        super.initialize(step, result);
        validateCrfExternalIdStep(step);
        nextButton = findViewById(R.id.button_go_forward);
        nextButton.setOnClickListener(view -> onNextClicked());
    }

    protected void validateCrfExternalIdStep(Step step) {
        if (!(step instanceof CrfExternalIdStep)) {
            throw new IllegalStateException("CrfExternalIdStepLayout only works with CrfExternalIdStep");
        }
        crfExternalIdStep = (CrfExternalIdStep)step;
    }

    @Override
    public void showLoadingDialog() {
        super.showLoadingDialog();
        nextButton.setEnabled(false);
    }

    @Override
    public void hideLoadingDialog() {
        super.hideLoadingDialog();
        nextButton.setEnabled(true);
    }

    @Override
    public int getContentResourceId() {
        return R.layout.crf_exernal_id_form_step_layout;
    }

    @Override
    public @LayoutRes int getFixedSubmitBarLayoutId() {
        return R.layout.crf_external_id_container_layout;
    }

    @Override
    public int crfToolbarLeftIcon() {
        return R.drawable.crf_ic_back;
    }

    @Override
    public int crfToolbarRightIcon() {
        return NO_ICON;
    }
}
