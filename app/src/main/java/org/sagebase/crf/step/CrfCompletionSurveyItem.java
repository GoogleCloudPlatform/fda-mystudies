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

import com.google.gson.annotations.SerializedName;

/**
 * Created by TheMDP on 11/5/17.
 */

public class CrfCompletionSurveyItem extends CrfInstructionSurveyItem {
    /**
     * Text that shows up above value label
     */
    @SerializedName("topText")
    public String topText;

    /**
     * Text that shows up below value label
     */
    @SerializedName("bottomText")
    public String bottomText;

    /**
     * Text that shows up as the value label, i.e. FEET or BPM
     */
    @SerializedName("valueLabelText")
    public String valueLabelText;

    /**
     * The step identifier that will contain the value for value
     * currently can be completion_bpm_result or completion_distance_result
     */
    @SerializedName("valueResultId")
    public String valueResultId;
}
