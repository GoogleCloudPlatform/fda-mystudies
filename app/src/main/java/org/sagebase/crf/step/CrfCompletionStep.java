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

import org.researchstack.backbone.utils.ResUtils;

/**
 * Created by TheMDP on 10/31/17.
 */

public class CrfCompletionStep extends CrfInstructionStep {
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
        buttonType = CrfInstructionButtonType.DEFAULT_WHITE_DEEP_GREEN;
        setImage(ResUtils.ANIMATED_CHECK_MARK_DELAYED);
        setIsImageAnimated(true);
        buttonText = "Done";
        backgroundColorRes = "deepGreen";
        hideProgress = true;
    }
}
