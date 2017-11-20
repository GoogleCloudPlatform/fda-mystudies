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

import org.researchstack.backbone.answerformat.TextAnswerFormat;
import org.researchstack.backbone.model.ProfileInfoOption;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.LoginStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.step.body.TextQuestionBody;
import org.sagebionetworks.research.crf.R;

import java.util.Collections;

/**
 * Created by TheMDP on 11/17/17.
 */

public class CrfExternalIdStep extends LoginStep {
    /* Default constructor needed for serialization/deserialization of object */
    protected CrfExternalIdStep() {
        super();
    }

    public CrfExternalIdStep(String identifier) {
        super(identifier, null, null,
                Collections.singletonList(ProfileInfoOption.EXTERNAL_ID),
                Collections.singletonList(new CrfExternalIdQuestionStep()));
    }

    @Override
    public int getStepTitle() {
        return R.string.crf_external_empty;
    }

    @Override
    public Class getStepLayoutClass() {
        return CrfExternalIdStepLayout.class;
    }

    protected static class CrfExternalIdQuestionBody extends TextQuestionBody {
        public CrfExternalIdQuestionBody(Step step, StepResult result) {
            super(step, result);
        }

        @Override
        public @LayoutRes
        int getBodyViewRes() {
            return R.layout.crf_external_id_question_body;
        }
    }

    protected static class CrfExternalIdAnswerFormat extends TextAnswerFormat {
        protected CrfExternalIdAnswerFormat() {
            super(UNLIMITED_LENGTH);
        }
        protected CrfExternalIdAnswerFormat(int maximumLength) {
            super(maximumLength);
        }

        public QuestionType getQuestionType() {
            return () -> CrfExternalIdQuestionBody.class;
        }
    }

    protected static class CrfExternalIdQuestionStep extends QuestionStep {
        protected CrfExternalIdQuestionStep() {
            super(ProfileInfoOption.EXTERNAL_ID.getIdentifier());
            CrfExternalIdAnswerFormat format = new CrfExternalIdAnswerFormat();
            // TODO: before release add this back in
            //format.setInputType(InputType.TYPE_CLASS_NUMBER);
            setAnswerFormat(new CrfExternalIdAnswerFormat());
            setOptional(false);
            setPlaceholder("");
        }
    }
}
