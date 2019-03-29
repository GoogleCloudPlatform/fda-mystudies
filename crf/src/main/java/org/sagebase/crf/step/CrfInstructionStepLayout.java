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
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.layout.InstructionStepLayout;
import org.researchstack.backbone.utils.ResUtils;
import org.sagebase.crf.CrfActiveTaskActivity;
import org.sagebase.crf.R;
import org.sagebase.crf.view.CrfTaskStatusBarManipulator;
import org.sagebase.crf.view.CrfTaskToolbarProgressManipulator;
import org.sagebase.crf.view.CrfTaskToolbarTintManipulator;

/**
 * Created by TheMDP on 10/24/17.
 */

public class CrfInstructionStepLayout extends InstructionStepLayout implements
        CrfTaskToolbarTintManipulator, CrfTaskStatusBarManipulator, CrfTaskToolbarProgressManipulator,
        CrfActiveTaskActivity.CrfTaskMediaVolumeController {

    protected CrfInstructionStep crfInstructionStep;
    protected Button nextButton;
    protected View rootInstructionLayout;
    protected ImageButton customButton;
    protected TextView customButtonText;
    //protected Button remindMeLaterButton;
    protected Button learnMore;
    protected TextView instructionViewTop;
    protected TextView instructionViewBottom;
    protected ImageButton exitButton;

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
                R.id.crf_instruction_title,
                R.id.crf_instruction_text,
                R.id.crf_image_view,
                R.id.crf_instruction_more_detail_text);

        nextButton = findViewById(R.id.button_go_forward);
        nextButton.setEnabled(true);
        rootInstructionLayout = findViewById(R.id.crf_root_instruction_layout);

        //remindMeLaterButton = findViewById(R.id.remind_me_later);
        learnMore = findViewById(R.id.learn_more);
        this.instructionViewTop = findViewById(R.id.crf_instruction_text);
        this.instructionViewBottom = findViewById(R.id.crf_instruction_more_detail_text);

        this.exitButton = findViewById(R.id.x_button);
    }

    @Override
    public void refreshStep() {
        super.refreshStep();

        if (crfInstructionStep.buttonText != null) {
            nextButton.setText(crfInstructionStep.buttonText);
            if (customButtonText != null) {
                customButtonText.setText(crfInstructionStep.buttonText);
            }
        }
//
//        if (crfInstructionStep.buttonType != null) {
//            switch (crfInstructionStep.buttonType) {
//                case DEFAULT:
//                    nextButton.setBackgroundResource(R.drawable.crf_rounded_button_secondary_color);
//                    nextButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.rsb_white, null));
//                    break;
//                case DEFAULT_WHITE_SALMON:
//                    nextButton.setBackgroundResource(R.drawable.crf_rounded_button_white);
//                    nextButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.salmon, null));
//                    break;
//                case DEFAULT_WHITE_DEEP_GREEN:
//                    nextButton.setBackgroundResource(R.drawable.crf_rounded_button_white);
//                    nextButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.deepGreen, null));
//                    break;
//                case GRAY_STICKY:
//                    nextButton.setBackgroundResource(R.drawable.crf_rounded_button_gray_sticky);
//                    //nextButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.rsb_white, null));
//                    break;
//                case GRAY:
//                    nextButton.setBackgroundResource(R.drawable.crf_rounded_button_gray);
//                    nextButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.rsb_white, null));
//                    break;
//                case HEART:
//                    nextButton.setVisibility(View.GONE);
//                    customButton.setImageResource(R.drawable.crf_heart_capture_button);
//                    customButton.setVisibility(View.VISIBLE);
//                    customButtonText.setVisibility(View.VISIBLE);
//                    break;
////                case TREADMILL:
////                    nextButton.setVisibility(View.GONE);
////                    customButton.setImageResource(R.drawable.crf_treadmill_button);
////                    customButton.setVisibility(View.VISIBLE);
////                    customButtonText.setVisibility(View.VISIBLE);
////                    break;
//                case STAIR_STEP:
//                    nextButton.setVisibility(View.GONE);
//                    customButton.setImageResource(R.drawable.crf_stair_step_button);
//                    customButton.setVisibility(View.VISIBLE);
//                    customButtonText.setVisibility(View.VISIBLE);
//                    break;
////                case RUN:
////                    nextButton.setVisibility(View.GONE);
////                    customButton.setImageResource(R.drawable.crf_12_min_walking_button);
////                    customButton.setVisibility(View.VISIBLE);
////                    customButtonText.setVisibility(View.VISIBLE);
////                    break;
//            }
//        }
        nextButton.setOnClickListener(this::goForwardClicked);
        if (customButton != null) {
            customButton.setOnClickListener(this::goForwardClicked);
        }
        if (crfInstructionStep.backgroundColorRes != null) {
            int colorId = ResUtils.getColorResourceId(getContext(), crfInstructionStep.backgroundColorRes);
            rootInstructionLayout.setBackgroundResource(colorId);
        }
        if (crfInstructionStep.imageBackgroundColorRes != null) {
            int colorId = ResUtils.getColorResourceId(getContext(), crfInstructionStep.imageBackgroundColorRes);
            imageView.setBackgroundResource(colorId);
        }
        if (crfInstructionStep.behindToolbar) {
            imageView.setPadding(imageView.getPaddingLeft(), 0,
                    imageView.getPaddingRight(), imageView.getPaddingBottom());
        }
        if (learnMore != null) {
            if (crfInstructionStep.learnMoreText != null && crfInstructionStep.learnMoreFile != null) {
                learnMore.setVisibility(View.VISIBLE);
                learnMore.setPaintFlags(learnMore.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                learnMore.setText(crfInstructionStep.learnMoreText);
                learnMore.setOnClickListener(view -> setLearnMore());
            } else {
                learnMore.setVisibility(View.GONE);
            }
        }
        // Display the instruction
        if(this.instructionViewTop != null) {
            instructionViewTop.setText(crfInstructionStep.getText());
            instructionViewTop.setVisibility(VISIBLE);
        }

        // Display the detail text
        if(this.instructionViewBottom != null) {
            instructionViewBottom.setText(crfInstructionStep.getMoreDetailText());
            instructionViewBottom.setVisibility(VISIBLE);
        }

        if(this.exitButton != null) {
            exitButton.setImageResource(R.drawable.ic_close_white_24dp);
            exitButton.setVisibility(View.VISIBLE);
            exitButton.setOnClickListener(this::goBackClicked);
        }

    }


    public static final String EXTRA_HTML_FILENAME = "EXTRA_FILENAME";

    private void setLearnMore() {
        Intent i = new Intent(getContext(), CrfTrainingInfo.class);
        i.putExtra(CrfTrainingInfoKt.EXTRA_HTML_FILENAME, crfInstructionStep.learnMoreFile);
        i.putExtra(CrfTrainingInfoKt.EXTRA_TITLE, crfInstructionStep.learnMoreTitle);
        getContext().startActivity(i);
    }

    public void goForwardClicked(View v) {
        nextButton.setEnabled(false);
        onComplete();
    }

    public void goBackClicked(View v) {
        callbacks.onSaveStep(StepCallbacks.ACTION_PREV, step, null);
    }

    @Override
    public int crfToolbarTintColor() {
        if (crfInstructionStep.tintColorRes == null) {
            return R.color.white;
        }
        return ResUtils.getColorResourceId(getContext(), crfInstructionStep.tintColorRes);
    }

    @Override
    public int crfStatusBarColor() {
        if (crfInstructionStep.statusBarColorRes != null) {
            return ResUtils.getColorResourceId(getContext(), crfInstructionStep.statusBarColorRes);
        } else if (crfInstructionStep.backgroundColorRes != null) {
            return ResUtils.getColorResourceId(getContext(), crfInstructionStep.backgroundColorRes);
        } else if (crfInstructionStep.imageBackgroundColorRes != null) {
            return ResUtils.getColorResourceId(getContext(), crfInstructionStep.imageBackgroundColorRes);
        }
        return CrfTaskStatusBarManipulator.DEFAULT_COLOR;
    }

    @Override
    public boolean crfToolbarShowProgress() {
        return !crfInstructionStep.hideProgress;
    }

    @Override
    public boolean controlMediaVolume() {
        return crfInstructionStep.mediaVolume;
    }


}
