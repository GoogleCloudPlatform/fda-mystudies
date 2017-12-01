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

import org.researchstack.backbone.answerformat.BooleanAnswerFormat;
import org.sagebase.crf.step.body.CrfChoiceQuestionBody;

/**
 * Created by TheMDP on 11/30/17.
 */

public class CrfBooleanAnswerFormat extends BooleanAnswerFormat {
    /* Default constructor needed for serilization/deserialization of object */
    public CrfBooleanAnswerFormat()
    {
        super();
    }

    public CrfBooleanAnswerFormat(String trueString, String falseString) {
        super(trueString, falseString);
    }

    @Override
    public QuestionType getQuestionType() {
        // For this to work, we must also provide custom code in CrfTaskHelper for this format
        return () -> CrfChoiceQuestionBody.class;
    }
}
