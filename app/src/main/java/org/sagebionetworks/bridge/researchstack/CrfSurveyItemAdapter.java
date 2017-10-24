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

import org.researchstack.backbone.model.survey.BaseSurveyItem;
import org.researchstack.backbone.model.survey.SurveyItem;
import org.researchstack.backbone.model.survey.SurveyItemAdapter;
import org.sagebase.crf.step.CrfInstructionSurveyItem;

/**
 * Created by TheMDP on 10/24/17.
 */

public class CrfSurveyItemAdapter extends SurveyItemAdapter {

    public static final String CRF_INSTRUCTION_SURVEY_ITEM_TYPE = "crf_instruction";

    @Override
    public Class<? extends SurveyItem> getCustomClass(String customType, JsonElement json) {
        if (customType == null) {
            return BaseSurveyItem.class;
        }
        if (customType.equals(CRF_INSTRUCTION_SURVEY_ITEM_TYPE)) {
            return CrfInstructionSurveyItem.class;
        }
        return BaseSurveyItem.class;
    }
}
