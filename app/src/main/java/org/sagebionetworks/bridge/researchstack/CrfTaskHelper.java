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

import org.researchstack.backbone.ResourceManager;
import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.storage.NotificationHelper;
import org.researchstack.skin.AppPrefs;
import org.sagebase.crf.step.CrfBooleanAnswerFormat;
import org.sagebase.crf.step.body.CrfChoiceAnswerFormat;
import org.sagebase.crf.step.body.CrfIntegerAnswerFormat;
import org.sagebionetworks.bridge.android.manager.BridgeManagerProvider;
import org.sagebionetworks.bridge.researchstack.factory.ArchiveFileFactory;
import org.sagebionetworks.bridge.researchstack.survey.SurveyAnswer;
import org.sagebionetworks.bridge.researchstack.wrapper.StorageAccessWrapper;

import java.util.HashMap;

/**
 * Created by TheMDP on 10/20/17.
 */

public class CrfTaskHelper extends TaskHelper {

    private HashMap<String, String> mCrfResultMap = new HashMap<String, String>() {{
        put("HeartRateCamera_camera","camera_cameraHeartRate_heartRate");
    }};

    public CrfTaskHelper(StorageAccessWrapper storageAccess, ResourceManager resourceManager, AppPrefs appPrefs, NotificationHelper notificationHelper, BridgeManagerProvider bridgeManagerProvider) {
        super(storageAccess, resourceManager, appPrefs, notificationHelper, bridgeManagerProvider);
        setArchiveFileFactory(new CrfArchiveFileFactory());
    }

    /**
     * @param identifier identifier for the result
     * @return the filename to use for the bridge result
     */
    @Override
    public String bridgifyIdentifier(String identifier) {
        String trueIdentifier = identifier;
        if (mCrfResultMap.containsKey(identifier)) {
            trueIdentifier = mCrfResultMap.get(trueIdentifier);
        }
        return super.bridgifyIdentifier(trueIdentifier);
    }

    /**
     * There is currently an architecture issue in Bridge SDK,
     * Where the survey answer type is coupled to a non-extendable enum,
     * So we need to specifically specify how the these custom answer will become SurveyAnswers
     * We can get rid of this after we fix this https://sagebionetworks.jira.com/browse/AA-91
     */
    public static class CrfArchiveFileFactory extends ArchiveFileFactory {
        protected CrfArchiveFileFactory() {
            super();
        }

        @Override
        public SurveyAnswer customSurveyAnswer(StepResult stepResult, AnswerFormat format) {
            if (format instanceof CrfBooleanAnswerFormat) {
                SurveyAnswer surveyAnswer = new SurveyAnswer.BooleanSurveyAnswer(stepResult);
                surveyAnswer.questionType = AnswerFormat.Type.Boolean.ordinal();
                surveyAnswer.questionTypeName = AnswerFormat.Type.Boolean.name();
                return surveyAnswer;
            } else if (format instanceof CrfChoiceAnswerFormat) {
                SurveyAnswer surveyAnswer = new SurveyAnswer.ChoiceSurveyAnswer<>(stepResult);
                if (((CrfChoiceAnswerFormat)format).getAnswerStyle() == AnswerFormat.ChoiceAnswerStyle.SingleChoice) {
                    surveyAnswer.questionType = AnswerFormat.Type.SingleChoice.ordinal();
                    surveyAnswer.questionTypeName = AnswerFormat.Type.SingleChoice.name();
                } else {
                    surveyAnswer.questionType = AnswerFormat.Type.MultipleChoice.ordinal();
                    surveyAnswer.questionTypeName = AnswerFormat.Type.MultipleChoice.name();
                }
                return surveyAnswer;
            } else if (format instanceof CrfIntegerAnswerFormat) {
                SurveyAnswer surveyAnswer = new SurveyAnswer.NumericSurveyAnswer(stepResult);
                surveyAnswer.questionType = AnswerFormat.Type.Integer.ordinal();
                surveyAnswer.questionTypeName = AnswerFormat.Type.Integer.name();
                return surveyAnswer;
            }
            return null;
        }
    }
}
