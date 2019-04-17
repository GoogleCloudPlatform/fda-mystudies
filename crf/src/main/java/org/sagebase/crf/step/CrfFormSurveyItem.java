/*
 *    Copyright 2019 Sage Bionetworks
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

import com.google.gson.annotations.SerializedName;

import org.researchstack.backbone.model.survey.FormSurveyItem;
import org.researchstack.backbone.model.survey.InstructionSurveyItem;


public class CrfFormSurveyItem extends FormSurveyItem {


    /**
     * Text to display as the learn more link
     */
    @SerializedName("learnMoreText")
    public String learnMoreText;

    /**
     * Html file to show when user taps learn more link
     */
    @SerializedName("learnMoreFile")
    public String learnMoreFile;

    /**
     * Title to show on learn more screen
     */
    @SerializedName("learnMoreTitle")
    public String learnMoreTitle;

}
