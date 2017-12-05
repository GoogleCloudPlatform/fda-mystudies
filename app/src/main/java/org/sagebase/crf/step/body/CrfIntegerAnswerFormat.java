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

package org.sagebase.crf.step.body;

import org.researchstack.backbone.answerformat.IntegerAnswerFormat;


/**
 * Created by rianhouston on 11/27/17.
 */

public class CrfIntegerAnswerFormat extends IntegerAnswerFormat {

    public CrfIntegerAnswerFormat() {
        super();
    }

    public CrfIntegerAnswerFormat(int minValue, int maxValue) {
        super(minValue, maxValue);
    }

    public QuestionType getQuestionType() {
        // For this to work, we must also provide custom code in CrfTaskHelper for this format
        return () -> CrfIntegerQuestionBody.class;
    }
}
