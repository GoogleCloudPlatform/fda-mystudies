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

import android.os.Build;

import com.google.gson.annotations.SerializedName;

import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.NavigableOrderedTask;
import org.researchstack.backbone.utils.ResUtils;
import org.researchstack.backbone.utils.StepResultHelper;

import java.util.List;

/**
 * Created by TheMDP on 10/31/17.
 */

public class CrfCompletionStep extends CrfInstructionStep implements NavigableOrderedTask.NavigationSkipRule {

    /**
     * Text that shows up above value label
     */
    public String topText;

    /**
     * Text that shows up below value label
     */
    public String bottomText;

    /**
     * Text that shows up as the value label, i.e. FEET or BPM
     */
    public String valueLabelText;

    /**
     * The step identifier that will contain the value
     */
    public String valueResultId;

    /**
     * The step identifier that will contain the secondary value to display
     */
    public String secondaryValueResultId;

    /**
     * Hide or show the redo measurement button
     */
    public boolean showRedoButton;

    /* Default constructor needed for serialization/deserialization of object */
    public CrfCompletionStep() {
        super();
        commonInit();
    }

    public CrfCompletionStep(String identifier, String title, String detailText) {
        super(identifier, title, detailText);
        commonInit();
    }

    private void commonInit() {
        setImage("crf_completed_icon");
        setIsImageAnimated(false);
        buttonText = "Done";
        backgroundColorRes = "white";
        hideProgress = true;
    }

    @Override
    public Class getStepLayoutClass() {
        return CrfCompletionStepLayout.class;
    }

    @Override
    public boolean shouldSkipStep(TaskResult result, List<TaskResult> additionalTaskResults) {
        String status = StepResultHelper.findStringResult(result, CrfHeartRateStepLayout.RESULT_STATUS);
        if (CrfHeartRateStepLayout.STATUS_FAILED.equals(status)) {
            return true;
        }
        return false;
    }
}
