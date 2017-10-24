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

package org.sagebionetworks.bridge.researchstack;

import android.content.Context;

import org.researchstack.backbone.model.survey.InstructionSurveyItem;
import org.researchstack.backbone.model.survey.SurveyItem;
import org.researchstack.backbone.model.survey.factory.SurveyFactory;
import org.researchstack.backbone.step.CompletionStep;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.Step;
import org.sagebase.crf.step.CrfInstructionStep;

/**
 * Created by TheMDP on 10/24/17.
 */

public class CrfSurveyFactory extends SurveyFactory {

    public CrfSurveyFactory() {
        super();
        setupCustomStepCreator();
    }

    private void setupCustomStepCreator() {
        setCustomStepCreator(new CustomStepCreator() {
            @Override
            public Step createCustomStep(Context context, SurveyItem item, boolean isSubtaskStep, SurveyFactory factory) {
                return null;
            }
        });
    }

    @Override
    public InstructionStep createInstructionStep(InstructionSurveyItem item) {
        InstructionStep step = new CrfInstructionStep(item.identifier, item.title, item.text);



        fillInstructionStep(step, item);
        return step;
    }

    /**
     * @param item InstructionSurveyItem from JSON
     * @return valid CompletionStep matching the InstructionSurveyItem
     */
    @Override
    public CompletionStep createInstructionCompletionStep(InstructionSurveyItem item) {
        // TODO: replace with our custom steps
        return super.createInstructionCompletionStep(item);
//        CompletionStep step = new CompletionStep(item.identifier, item.title, item.text);
//        fillInstructionStep(step, item);
//        return step;
    }
}
