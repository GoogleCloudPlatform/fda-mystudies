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

public class CrfSnapshotInstructionStepLayout extends CrfInstructionStepLayout {

    protected CrfSnapshotInstructionStep crfSnapshotInstructionStep;
    protected TextView instructionViewTop;
    protected TextView instructionViewBottom;

    public CrfSnapshotInstructionStepLayout(Context context) {
        super(context);
    }


    @Override
    public int getContentResourceId() {
        return R.layout.crf_step_layout_skip_instruction;
    }

    @Override
    public void initialize(Step step, StepResult result) {
        validateAndSetCrfSnapshotStep(step);
        super.initialize(step, result);
    }

    protected void validateAndSetCrfSnapshotStep(Step step) {
        if (!(step instanceof CrfInstructionStep)) {
            throw new IllegalStateException("CrfInstructionStepLayout only works with CrfInstructionStep");
        }
        this.crfSnapshotInstructionStep = (CrfSnapshotInstructionStep)step;
        this.instructionViewTop = findViewById(R.id.crf_instruction_text_top);
        this.instructionViewBottom = findViewById(R.id.crf_instruction_text_bottom);
    }

    @Override
    public void refreshStep() {
        super.refreshStep();

        // Display the instruction
        if(this.instructionViewTop != null) {
            instructionViewTop.setText(crfSnapshotInstructionStep.instruction);
            instructionViewTop.setVisibility(VISIBLE);
        }

        // Display the detail text
        if(this.instructionViewBottom != null) {
            instructionViewBottom.setText(crfSnapshotInstructionStep.getMoreDetailText());
            instructionViewBottom.setVisibility(VISIBLE);
        }
    }
}
