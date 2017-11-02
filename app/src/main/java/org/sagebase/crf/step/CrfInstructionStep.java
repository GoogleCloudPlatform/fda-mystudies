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

import org.researchstack.backbone.step.InstructionStep;
import org.sagebase.crf.view.CrfTaskStatusBarManipulator;

/**
 * Created by TheMDP on 10/24/17.
 */

public class CrfInstructionStep extends InstructionStep {

    /**
     * The type of button to show
     */
    public CrfInstructionButtonType buttonType;

    /**
     * When buttonType is DEFAULT, this will be used as the title on the button
     * This can also be used in conjunction with other button types
     */
    public String buttonText;

    /**
     * A string representation of a color resource for the view background
     */
    public String backgroundColorRes;

    /**
     * A string representation of a color resource for the image background
     */
    public String imageBackgroundColorRes;

    /**
     * A string representation of a color resource for the toolbar tint
     */
    public String tintColorRes;

    /**
     * A string representation of a color resource for the status bar
     */
    public String statusBarColorRes;

    /**
     * Hides the progress bar when this step is within a toolbar with progress
     */
    public boolean hideProgress;

    /**
     * Puts the image behind the toolbar
     */
    public boolean behindToolbar;

    /**
     * If true, volume buttons will control media, false it will go to default
     */
    public boolean mediaVolume;

    /* Default constructor needed for serialization/deserialization of object */
    public CrfInstructionStep() {
        super();
    }

    public CrfInstructionStep(String identifier, String title, String detailText) {
        super(identifier, title, detailText);
    }

    @Override
    public Class getStepLayoutClass() {
        return CrfInstructionStepLayout.class;
    }
}
