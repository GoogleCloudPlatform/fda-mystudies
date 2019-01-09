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

public class CrfSkipStep extends CrfInstructionStep
        implements NavigableOrderedTask.NavigationSkipRule {

    public String skipIdentifier;
    public String stepIdentifier;

    public CrfSkipStep(String identifier, String title) {
        super(identifier, title, null);
    }

    @Override
    public boolean shouldSkipStep(@Nullable TaskResult result,
                                  @Nullable List<TaskResult> additionalTaskResults) {
        StepResult<Boolean> res = (StepResult<Boolean>)
                result.getStepResult(this.stepIdentifier).getResultForIdentifier(skipIdentifier);

        return res.getResult();
    }

    @Override
    public Class getStepLayoutClass() {
        return CrfSkipStepLayout.class;
    }

}
