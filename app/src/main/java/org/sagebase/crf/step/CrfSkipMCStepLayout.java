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
import android.widget.TextView;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.sagebionetworks.research.crf.R;

public class CrfSkipMCStepLayout extends CrfFormStepLayout {

    protected CrfSkipMCStep step;
    protected TextView crf_instruction;

    public CrfSkipMCStepLayout(Context context) {
        super(context);
    }

    @Override
    public void initialize(Step step, StepResult result) {
        validateAndSetCrfFormStep(step);
        super.initialize(step, result);
        setupViews();
        refreshCrfSubmitBar();
        //this.crf_instruction = findViewById(R.id.rsb_form_step_title);
        this.step = (CrfSkipMCStep) step;
    }

    @Override
    public int getContentResourceId() {
        return R.layout.crf_step_layout_skip_mc;
    }

    @Override
    public void setupViews() {
        super.setupViews();
        //crf_instruction.setText("Hello World"); // not sure if this is the right title to display

    }
}
