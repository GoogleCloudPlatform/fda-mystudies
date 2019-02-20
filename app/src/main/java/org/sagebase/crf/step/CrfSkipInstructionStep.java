/*
 *    Copyright 2018 Sage Bionetworks
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

import android.support.annotation.Nullable;


import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.NavigableOrderedTask;

import java.util.List;

public class CrfSkipInstructionStep extends CrfInstructionStep
        implements NavigableOrderedTask.NavigationSkipRule, NavigableOrderedTask.NavigationRule {
    /**
     * Search for this identifier to determine if we should skip this step
     */
    public String skipIdentifier;

    public String stepIdentifier;

    public boolean firstTime;

    /**
     * The type of button to show
     */
    public CrfInstructionButtonType buttonType;


    public String buttonText;
    public String instruction;
    boolean continueMeasurement = false;

    /* Default constructor needed for serialization/deserialization of object */
    public CrfSkipInstructionStep() {
        super();
    }

    public CrfSkipInstructionStep(String identifier, String title) {
        super(identifier, title, null);
    }

    @Override
    public boolean shouldSkipStep(@Nullable TaskResult result,
                                  @Nullable List<TaskResult> additionalTaskResults) {

        if(stepIdentifier.equals("declining_feedback") || stepIdentifier.equals("abnormal_feedbackForm")) {
            if ((StepResult<Boolean>) result.getStepResult("camera").getResultForIdentifier(skipIdentifier) == null) {
                return true;
            }

            StepResult<Boolean> res = (StepResult<Boolean>)
                    result.getStepResult("camera").getResultForIdentifier(skipIdentifier);

            // If you are not skipping this step, the next step needs to continue measurement
            if (!res.getResult()) {
                continueMeasurement = true;
            }
            return res.getResult();
        }
        return !firstTime;
    }

    @Override
    public String nextStepIdentifier(TaskResult result, List<TaskResult> additionalTaskResults) {
        if(stepIdentifier.equals("declining_feedback")) {
            if (continueMeasurement) {
                return "instructionCamera";
            }
            return "abnormal_feedbackForm";
        }
        else if(stepIdentifier.equals("instruction_test")){
            return "camera_test";
        }
        return "abnormal_feedbackForm";
    }

    @Override
    public Class getStepLayoutClass() {
        return CrfSkipInstructionStepLayout.class;
    }

}
