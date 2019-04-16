/*
 *    Copyright 2019 Sage Bionetworks
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
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.step.layout.FormStepLayout;
import org.sagebase.crf.R;
import org.sagebase.crf.view.CrfTaskToolbarProgressManipulator;

/**
 * Created by rianhouston on 11/22/17.
 */

public class CrfFormStepLayout extends FormStepLayout implements CrfTaskToolbarProgressManipulator {

    protected CrfFormStep crfFormStep;

    protected Button crfBackButton;
    protected Button crfNextButton;
    protected Button crfSkipButton;
    protected Button learnMore;
    protected Toolbar toolbar;
    private TextView crfStepProgressTextview;

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
        setupViews();
        refreshCrfSubmitBar();
    }

    protected void setupViews() {
        crfBackButton = findViewById(R.id.crf_submit_bar_back);
        crfBackButton.setOnClickListener(this::onBackButtonClicked);

        crfNextButton = findViewById(R.id.crf_submit_bar_next);
        crfNextButton.setOnClickListener(this::onNextButtonClicked);

        crfSkipButton = findViewById(R.id.crf_submit_bar_skip);
        crfSkipButton.setOnClickListener(this::onSkipButtonClicked);

        learnMore = findViewById(R.id.learn_more);
        if (learnMore != null) {
            if (crfFormStep.learnMoreText != null && crfFormStep.learnMoreFile != null) {
                learnMore.setVisibility(View.VISIBLE);
                learnMore.setPaintFlags(learnMore.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                learnMore.setText(crfFormStep.learnMoreText);
                learnMore.setOnClickListener(view -> onLearnMoreButtonClicked());
            } else {
                learnMore.setVisibility(View.GONE);
            }
        }
    }

    protected void validateAndSetCrfFormStep(Step step) {
        if (!(step instanceof CrfFormStep)) {
            throw new IllegalStateException("CrfFormStepLayout only works with CrfFormStep");
        }
        this.crfFormStep = (CrfFormStep) step;
    }

    private void onLearnMoreButtonClicked() {
        Intent i = new Intent(getContext(), CrfTrainingInfo.class);
        i.putExtra(CrfTrainingInfoKt.EXTRA_HTML_FILENAME, crfFormStep.learnMoreFile);
        i.putExtra(CrfTrainingInfoKt.EXTRA_TITLE, crfFormStep.learnMoreTitle);
        getContext().startActivity(i);
    }

    protected void onBackButtonClicked(View v) {
        hideKeyboard();
        isBackEventConsumed();
    }

    protected void onNextButtonClicked(View v) {
        hideKeyboard();
        super.onNextClicked();
    }

    protected void onSkipButtonClicked(View v) {
        hideKeyboard();
        super.onSkipClicked();
    }

    @Override
    public int getContentResourceId() {
        return R.layout.crf_step_layout_form;
    }


    @Override
    public @LayoutRes int getFixedSubmitBarLayoutId() {
        return R.layout.crf_layout_fixed_submit_bar;
    }

    protected void refreshCrfSubmitBar() {
        crfBackButton.setText(R.string.rsb_AX_BUTTON_BACK);
        crfNextButton.setText(R.string.rsb_BUTTON_NEXT);

        String skipButtonTitle = super.skipButtonTitle();
        crfSkipButton.setText(skipButtonTitle);
        crfSkipButton.setVisibility(skipButtonTitle == null ? View.GONE : View.VISIBLE);
    }


    @Override
    public boolean crfToolbarShowProgress() {
        return !crfFormStep.hideProgress;
    }
}
