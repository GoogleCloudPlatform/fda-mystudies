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

import org.researchstack.backbone.model.survey.InstructionSurveyItem;
import org.sagebase.crf.view.CrfTaskStatusBarManipulator;

/**
 * Created by TheMDP on 10/24/17.
 */

public class CrfInstructionSurveyItem extends InstructionSurveyItem {
    /**
     * The type of button to show
     */
    @SerializedName("buttonType")
    public CrfInstructionButtonType buttonType;

    /**
     * When buttonType is DEFAULT, this will be used as the title on the button
     * This can also be used in conjunction with other button types
     */
    @SerializedName("buttonText")
    public String buttonText;

    /**
     * A string representation of a color resource for the background
     */
    @SerializedName("backgroundColor")
    public String backgroundColorRes;

    /**
     * A string representation of a color resource for the image background
     */
    @SerializedName("imageColor")
    public String imageColorRes;

    /**
     * A string representation of a color resource for the toolbar tint
     */
    @SerializedName("tintColor")
    public String tintColorRes;

    /**
     * A string representation of a color resource for the status bar
     */
    @SerializedName("statusBarColor")
    public String statusBarColorRes;

    /**
     * Hides the progress bar when this step is within a toolbar with progress
     */
    @SerializedName("hideProgress")
    public boolean hideProgress;

    /**
     * Puts the image behind the toolbar
     */
    @SerializedName("behindToolbar")
    public boolean behindToolbar;

    /**
     * If true, volume buttons will control media, false it will go to default
     */
    @SerializedName("mediaVolume")
    public boolean mediaVolume;
}
