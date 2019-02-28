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

    /**
     * Current step identifier
     */
    public String stepIdentifier;

    /**
     * Previous step identifier
     */
    public String previousStepIdentifier;

    /**
     * Measurement camera step identifier
     */
    public String cameraStepIdentifier;

    /**
     * Next step identifier
     */
    public String nextStepIdentifier;

    /**
     * The type of button to show
     */
    public CrfInstructionButtonType buttonType;

    /**
     * The text displayed on the button
     */
    public String buttonText;

    /**
     * Instruction displayed to user
     */
    public String instruction;

    /**
     * Boolean value indicating what the next step should be
     */
    boolean continueMeasurement = false;


    public CrfSkipInstructionStep(String identifier, String title) {
        super(identifier, title, null);
    }

    @Override
    public boolean shouldSkipStep(@Nullable TaskResult result,
                                  @Nullable List<TaskResult> additionalTaskResults) {

        if((StepResult<Boolean>) result.getStepResult(cameraStepIdentifier).getResultForIdentifier(skipIdentifier) == null) {
            return true;
        }
        StepResult<Boolean> res = (StepResult<Boolean>)
                result.getStepResult(cameraStepIdentifier).getResultForIdentifier(skipIdentifier);

        // If you are not skipping this step, the next step needs to continue measurement
        if (!res.getResult()) {
            continueMeasurement = true;
        }
        return res.getResult();

    }

    @Override
    public String nextStepIdentifier(TaskResult result, List<TaskResult> additionalTaskResults) {
        if (continueMeasurement) {
            return previousStepIdentifier;
        }
        return nextStepIdentifier;
    }

    @Override
    public Class getStepLayoutClass() {
        return CrfSkipInstructionStepLayout.class;
    }

}
