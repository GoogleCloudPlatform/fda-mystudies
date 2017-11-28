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
 * Created by TheMDP on 10/24/17.
 */

public enum  CrfInstructionButtonType {
    @SerializedName("default")
    DEFAULT,
    @SerializedName("defaultWhiteSalmon")
    DEFAULT_WHITE_SALMON,
    @SerializedName("defaultWhiteDeepGreen")
    DEFAULT_WHITE_DEEP_GREEN,
    @SerializedName("heart")
    HEART,
    @SerializedName("treadmill")
    TREADMILL,
    @SerializedName("stairStep")
    STAIR_STEP,
    @SerializedName("run")
    RUN
}
