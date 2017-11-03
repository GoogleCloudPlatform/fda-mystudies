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

import com.google.gson.JsonElement;

import org.researchstack.backbone.model.survey.ActiveStepSurveyItem;
import org.researchstack.backbone.model.survey.BaseSurveyItem;
import org.researchstack.backbone.model.survey.SurveyItem;
import org.researchstack.backbone.model.survey.SurveyItemAdapter;
import org.sagebase.crf.step.CrfInstructionSurveyItem;
import org.sagebase.crf.step.CrfStairSurveyItem;
import org.sagebase.crf.step.CrfPhotoCaptureSurveyItem;
import org.sagebase.crf.step.CrfStartTaskSurveyItem;

/**
 * Created by TheMDP on 10/24/17.
 */

public class CrfSurveyItemAdapter extends SurveyItemAdapter {

    public static final String CRF_INSTRUCTION_SURVEY_ITEM_TYPE = "crf_instruction";
    public static final String CRF_START_TASK_SURVEY_ITEM_TYPE = "crf_start_task";
    public static final String CRF_HEART_RATE_CAMERA_SURVEY_ITEM_TYPE = "crf_heart_rate_camera_step";
    public static final String CRF_COUNTDOWN_SURVEY_ITEM_TYPE = "crf_countdown";
    public static final String CRF_12_MIN_WALK_SURVEY_ITEM_TYPE = "crf_12_min_walk";
    public static final String CRF_STAIR_STEP_SURVEY_ITEM_TYPE = "crf_stair_step";
    public static final String CRF_COMPLETION_SURVEY_ITEM_TYPE = "crf_completion";
    public static final String CRF_PHOTO_CAPTURE_SURVEY_ITEM_TYPE = "crf_photo_capture";

    @Override
    public Class<? extends SurveyItem> getCustomClass(String customType, JsonElement json) {
        if (customType == null) {
            return BaseSurveyItem.class;
        }
        if (customType.equals(CRF_INSTRUCTION_SURVEY_ITEM_TYPE)) {
            return CrfInstructionSurveyItem.class;
        } else if (customType.equals(CRF_START_TASK_SURVEY_ITEM_TYPE)) {
            return CrfStartTaskSurveyItem.class;
        } else if (customType.equals(CRF_HEART_RATE_CAMERA_SURVEY_ITEM_TYPE)) {
            return ActiveStepSurveyItem.class;
        } else if (customType.equals(CRF_COUNTDOWN_SURVEY_ITEM_TYPE)) {
            return ActiveStepSurveyItem.class;
        } else if (customType.equals(CRF_12_MIN_WALK_SURVEY_ITEM_TYPE)) {
            return ActiveStepSurveyItem.class;
        } else if (customType.equals(CRF_STAIR_STEP_SURVEY_ITEM_TYPE)) {
            return CrfStairSurveyItem.class;
        } else if (customType.equals(CRF_COMPLETION_SURVEY_ITEM_TYPE)) {
            return CrfInstructionSurveyItem.class;
        } else if (customType.equals(CRF_PHOTO_CAPTURE_SURVEY_ITEM_TYPE)) {
            return CrfPhotoCaptureSurveyItem.class;
        }
        return BaseSurveyItem.class;
    }
}
