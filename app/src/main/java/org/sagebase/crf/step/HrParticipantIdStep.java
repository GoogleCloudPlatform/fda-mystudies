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

import android.support.annotation.LayoutRes;
import android.text.InputType;

import org.researchstack.backbone.answerformat.TextAnswerFormat;
import org.researchstack.backbone.model.ProfileInfoOption;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.FormStep;
import org.researchstack.backbone.step.LoginStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.step.body.TextQuestionBody;
import org.sagebionetworks.bridge.researchstack.CrfPrefs;
import org.sagebionetworks.research.crf.R;

import java.util.Collections;


public class HrParticipantIdStep extends FormStep {

    static final String PARTICIPANT_ID_RESULT_IDENTIFIER = "hr_participant_id_question";

    /* Default constructor needed for serialization/deserialization of object */
    protected HrParticipantIdStep() {
        super();
        setAutoFocusFirstEditText(true);
    }

    public HrParticipantIdStep(String identifier) {
        super(identifier, null, null,
                Collections.singletonList(new HrParticipantIdQuestionStep()));
        setAutoFocusFirstEditText(true);
        setAnswerFormat(new HrParticipantIdAnswerFormat());
    }

    @Override
    public int getStepTitle() {
        return R.string.crf_external_empty;
    }

    @Override
    public Class getStepLayoutClass() {
        return HrParticipantIdStepLayout.class;
    }

    protected static class HrParticipantIdQuestionBody extends TextQuestionBody {
        public HrParticipantIdQuestionBody(Step step, StepResult result) {
            super(step, result);
        }

        @Override
        public @LayoutRes
        int getBodyViewRes() {
            return R.layout.crf_external_id_question_body;
        }
    }

    protected static class HrParticipantIdAnswerFormat extends TextAnswerFormat {

        private static final String TEST_PARTICIPANT_ID = "00";

        protected HrParticipantIdAnswerFormat() {
            super(UNLIMITED_LENGTH);
        }
        protected HrParticipantIdAnswerFormat(int maximumLength) {
            super(maximumLength);
        }

        public QuestionType getQuestionType() {
            return () -> HrParticipantIdQuestionBody.class;
        }

        @Override
        public boolean isAnswerValid(String text) {
            boolean isValid = super.isAnswerValid(text);
            if (isValid) {
                if (TEST_PARTICIPANT_ID.equals(text) || !CrfPrefs.getInstance().getHrValidationParticipantIds().contains(text)) {
                    isValid = true;
                } else {
                    isValid = false;
                }
            }
            return isValid;
        }
    }

    protected static class HrParticipantIdQuestionStep extends QuestionStep {
        protected HrParticipantIdQuestionStep() {
            super(PARTICIPANT_ID_RESULT_IDENTIFIER);
            HrParticipantIdAnswerFormat format = new HrParticipantIdAnswerFormat();
            format.setInputType(InputType.TYPE_CLASS_NUMBER);
            setAnswerFormat(format);
            setOptional(false);
            setPlaceholder("");
        }
    }
}
