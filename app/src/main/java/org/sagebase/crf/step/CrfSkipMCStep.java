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

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.task.NavigableOrderedTask;

import java.util.List;

public class CrfSkipMCStep extends CrfFormStep implements NavigableOrderedTask.NavigationSkipRule{

    /**
     * Search for this identifier to determine if we should skip this step
     */
    public String skipIdentifier;

    /**
     * When true, displays the remindMeLater button
     */
    public boolean remindMeLater = true;

    public String buttonType = "GRAY";

    /* Default constructor needed for serialization/deserialization of object */
    public CrfSkipMCStep() {
        super();
    }

    public CrfSkipMCStep(String identifier, String title, String detailText) {
        super(identifier, title, detailText);
    }

    public CrfSkipMCStep(String identifier, String title, String text, List<QuestionStep> steps) {
        this(identifier, title, text);
        setFormSteps(steps);
    }


    @Override
    public boolean shouldSkipStep(TaskResult result, List<TaskResult> additionalTaskResults) {
        if ((StepResult<Boolean>) result.getStepResult("camera").getResultForIdentifier(skipIdentifier) == null) {
            return true;
        }

        StepResult<Boolean> res = (StepResult<Boolean>)
                result.getStepResult("camera").getResultForIdentifier(skipIdentifier);

        return res.getResult();
    }

    @Override
    public Class getStepLayoutClass() {
        return CrfSkipMCStepLayout.class;
    }

}
