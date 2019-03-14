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

import android.support.annotation.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;

import org.researchstack.backbone.model.survey.ActiveStepSurveyItem;
import org.researchstack.backbone.model.survey.BooleanQuestionSurveyItem;
import org.researchstack.backbone.model.survey.ChoiceQuestionSurveyItem;
import org.researchstack.backbone.model.survey.FormSurveyItem;
import org.researchstack.backbone.model.survey.IntegerRangeSurveyItem;
import org.researchstack.backbone.model.survey.SurveyItem;
import org.researchstack.backbone.model.survey.TextfieldSurveyItem;
import org.sagebase.crf.step.CrfCompletionSurveyItem;
import org.sagebase.crf.step.CrfInstructionSurveyItem;
import org.sagebase.crf.step.CrfSkipInstructionStepSurveyItem;
import org.sagebase.crf.step.CrfStartTaskSurveyItem;
import org.sagebionetworks.bridge.researchstack.task.creation.BridgeSurveyItemAdapter;

import java.util.Map;

/** Subclasses BridgeSurveyItemAdapter to enable CRF-specific task step types. */
public class CrfSurveyItemAdapter extends BridgeSurveyItemAdapter {
    public static final String CRF_INSTRUCTION_SURVEY_ITEM_TYPE = "crf_instruction";
    public static final String CRF_START_TASK_SURVEY_ITEM_TYPE = "crf_start_task";
    public static final String CRF_HEART_RATE_CAMERA_SURVEY_ITEM_TYPE = "crf_heart_rate_camera_step";
    public static final String CRF_COUNTDOWN_SURVEY_ITEM_TYPE = "crf_countdown";
    public static final String CRF_12_MIN_WALK_SURVEY_ITEM_TYPE = "crf_12_min_walk";
    public static final String CRF_STAIR_STEP_SURVEY_ITEM_TYPE = "crf_stair_step";
    public static final String CRF_COMPLETION_SURVEY_ITEM_TYPE = "crf_completion";
    public static final String CRF_PHOTO_CAPTURE_SURVEY_ITEM_TYPE = "crf_photo_capture";
    public static final String CRF_FITBIT_SURVEY_ITEM_TYPE = "crf_fitbit";
    public static final String CRF_FORM_SURVEY_ITEM_TYPE = "crf_form";
    public static final String CRF_INTEGER_SURVEY_ITEM_TYPE = "crfInteger";
    public static final String CRF_SINGLE_CHOICE_SURVEY_ITEM_TYPE = "crfSingleChoice";
    public static final String CRF_MULTIPLE_CHOICE_SURVEY_ITEM_TYPE = "crfMultipleChoice";
    public static final String CRF_BOOLEAN_SURVEY_ITEM_TYPE = "crfBoolean";
    public static final String HR_PARTICIPANT_ID_SURVEY_ITEM_TYPE = "hrParticipantId";
    public static final String CRF_SKIP_INSTRUCTION_TYPE = "crf_skip_instruction";
    public static final String CRF_SKIP_MC_TYPE = "crf_skip_mc";
    public static final String CRF_SNAPSHOT_TYPE = "crf_snapshot";

    @VisibleForTesting
    static final Map<String, Class<? extends SurveyItem>> TYPE_TO_CLASS =
            ImmutableMap.<String, Class<? extends SurveyItem>>builder()
                    .put(CRF_FITBIT_SURVEY_ITEM_TYPE, CrfInstructionSurveyItem.class)
                    .put(CRF_INSTRUCTION_SURVEY_ITEM_TYPE, CrfInstructionSurveyItem.class)
                    .put(CRF_START_TASK_SURVEY_ITEM_TYPE, CrfStartTaskSurveyItem.class)
                    .put(CRF_HEART_RATE_CAMERA_SURVEY_ITEM_TYPE, ActiveStepSurveyItem.class)
                    .put(CRF_COUNTDOWN_SURVEY_ITEM_TYPE, ActiveStepSurveyItem.class)
                    .put(CRF_12_MIN_WALK_SURVEY_ITEM_TYPE, ActiveStepSurveyItem.class)
                    .put(CRF_STAIR_STEP_SURVEY_ITEM_TYPE, ActiveStepSurveyItem.class)
                    .put(CRF_COMPLETION_SURVEY_ITEM_TYPE, CrfCompletionSurveyItem.class)
                    .put(CRF_PHOTO_CAPTURE_SURVEY_ITEM_TYPE, CrfInstructionSurveyItem.class)
                    .put(CRF_FORM_SURVEY_ITEM_TYPE, FormSurveyItem.class)
                    .put(CRF_INTEGER_SURVEY_ITEM_TYPE, IntegerRangeSurveyItem.class)
                    .put(CRF_SINGLE_CHOICE_SURVEY_ITEM_TYPE, ChoiceQuestionSurveyItem.class)
                    .put(CRF_MULTIPLE_CHOICE_SURVEY_ITEM_TYPE, ChoiceQuestionSurveyItem.class)
                    .put(CRF_BOOLEAN_SURVEY_ITEM_TYPE, BooleanQuestionSurveyItem.class)
                    .put(HR_PARTICIPANT_ID_SURVEY_ITEM_TYPE, TextfieldSurveyItem.class)
                    .put(CRF_SKIP_INSTRUCTION_TYPE, CrfSkipInstructionStepSurveyItem.class)
                    .put(CRF_SKIP_MC_TYPE, ChoiceQuestionSurveyItem.class)
                    .put(CRF_SNAPSHOT_TYPE, CrfInstructionSurveyItem.class)
                    .build();

    @Override
    public Class<? extends SurveyItem> getCustomClass(String customType, JsonElement json) {
        if (customType != null && TYPE_TO_CLASS.containsKey(customType)) {
            return TYPE_TO_CLASS.get(customType);
        } else {
            return super.getCustomClass(customType, json);
        }
    }
}
