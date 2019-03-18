/*
 *    Copyright 2018 Sage Bionetworks
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


public class CrfSkipInstructionStepSurveyItem extends CrfInstructionSurveyItem {

    /**
     * The skip key to look for when determining whether to skip this step or not
     */
    @SerializedName("skipIdentifier")
    public String skipIdentifier;

    /**
     * The instruction feedback displayed to the user
     */
    @SerializedName("instruction")
    public String instruction;

    /**
     * Identifier for previous step
     */
    @SerializedName("previousStepIdentifier")
    public String previousStepIdentifier;

    /**
     * Identifier for next step
     */
    @SerializedName("nextStepIdentifier")
    public String nextStepIdentifier;

    /**
     * Identifier for the camera step
     */
    @SerializedName("cameraStepIdentifier")
    public String cameraStepIdentifier;
}
