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

import org.researchstack.backbone.step.FormStep;
import org.researchstack.backbone.step.NavigationFormStep;
import org.researchstack.backbone.step.QuestionStep;

import java.util.List;

/**
 * Created by rianhouston on 11/22/17.
 */

public class CrfFormStep extends NavigationFormStep {

    /* Default constructor needed for serialization/deserialization of object */
    public CrfFormStep() {
        super();
    }

    public CrfFormStep(String identifier, String title, String detailText) {
        super(identifier, title, detailText);
    }

    public CrfFormStep(String identifier, String title, String text, List<QuestionStep> steps) {
        this(identifier, title, text);
        setFormSteps(steps);
    }

    @Override
    public Class getStepLayoutClass() {
        return CrfFormStepLayout.class;
    }
}
