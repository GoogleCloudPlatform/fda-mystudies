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

import org.joda.time.DateTime;
import org.researchstack.backbone.AppPrefs;
import org.researchstack.backbone.ResourceManager;
import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.result.FileResult;
import org.researchstack.backbone.result.Result;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.storage.NotificationHelper;
import org.sagebase.crf.step.CrfBooleanAnswerFormat;
import org.sagebase.crf.step.body.CrfChoiceAnswerFormat;
import org.sagebase.crf.step.body.CrfIntegerAnswerFormat;
import org.sagebionetworks.bridge.android.manager.BridgeManagerProvider;
import org.sagebionetworks.bridge.data.Archive;
import org.sagebionetworks.bridge.data.ArchiveFile;
import org.sagebionetworks.bridge.data.JsonArchiveFile;
import org.sagebionetworks.bridge.researchstack.factory.ArchiveFileFactory;
import org.sagebionetworks.bridge.researchstack.survey.SurveyAnswer;
import org.sagebionetworks.bridge.researchstack.wrapper.StorageAccessWrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by TheMDP on 10/20/17.
 */

public class CrfTaskHelper extends TaskHelper {

    public static final String ANSWERS_FILENAME = "answers";

    static HashMap<String, String> CRF_RESULT_CONVERSION_MAP = new HashMap<String, String>() {{
        put("HeartRateCamera_heartRate.before", "heartRate_before_recorder");
        put("HeartRateCamera_heartRate.after",  "heartRate_after_recorder");
        put("motion_stairStep",                 "stairStep_motion");
        put("location_run",                     "location");
    }};

    public CrfTaskHelper(StorageAccessWrapper storageAccess, ResourceManager resourceManager, AppPrefs appPrefs, NotificationHelper notificationHelper, BridgeManagerProvider bridgeManagerProvider) {
        super(storageAccess, resourceManager, appPrefs, notificationHelper, bridgeManagerProvider);
        setArchiveFileFactory(new CrfArchiveFileFactory());
    }

    /**
     * Can be overridden by sub-class for custom data archiving
     * @param archiveBuilder fill this builder up with files from the flattenedResultList
     * @param flattenedResultList read these and add them to the archiveBuilder
     */
    protected void addFiles(Archive.Builder archiveBuilder, List<Result> flattenedResultList, String taskResultId) {

        // Per Bridge server schema, the background survey uses the default survey packaging
//        if (CrfTaskFactory.TASK_ID_BACKGROUND_SURVEY.equals(taskResultId)) {
            super.addFiles(archiveBuilder, flattenedResultList, taskResultId);
//            return;
//        }

        // Group the question step results in a single "answers" file
        // This is behavior that the bridge server team has wanted for a long time
        // Once this is proven capable, its functionality should be moved into TaskHelper base class
        Map<String, Object> answersMap = new HashMap<>();
        for (Result result : flattenedResultList) {
            boolean addedToAnswerMap = false;
            if (result instanceof StepResult) {
                StepResult stepResult = (StepResult)result;
                addedToAnswerMap = true;
                // This is a question step result, and will be added to the answers group
                Map mapResults = stepResult.getResults();
                for (Object key : mapResults.keySet()) {
                    Object value = mapResults.get(key);
                    if (key instanceof String && !(value instanceof FileResult)) {
                        // We can only work with String keys
                        if (StepResult.DEFAULT_KEY.equals(key)) {
                            answersMap.put(stepResult.getIdentifier(), value);
                        } else {
                            answersMap.put((String)key, value);
                        }
                    }
                }
            }

            // This is the default implementation
//            if (!addedToAnswerMap) {
//                ArchiveFile archiveFile = archiveFileFactory.fromResult(result);
//                if (archiveFile != null) {
//                    archiveBuilder.addDataFile(archiveFile);
//                } else {
//                    logger.error("Failed to convert Result to BridgeDataInput " + result.toString());
//                }
//            }
        }

        if (!answersMap.isEmpty()) {
            // The answer group will not have a valid end date, if one is needed,
            // consider adding key_endDate as a key/value in answer map above
            archiveBuilder.addDataFile(new JsonArchiveFile(ANSWERS_FILENAME, DateTime.now(), answersMap));
        }
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
        protected String getFilename(String identifier) {
            if (CrfTaskHelper.CRF_RESULT_CONVERSION_MAP.containsKey(identifier)) {
                return CrfTaskHelper.CRF_RESULT_CONVERSION_MAP.get(identifier);
            }
            return identifier;
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
