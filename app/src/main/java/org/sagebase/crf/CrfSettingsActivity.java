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

package org.sagebase.crf;

import android.app.Activity;
import android.view.View;

import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.step.layout.StepLayout;
import org.researchstack.backbone.ui.views.FixedSubmitBarLayout;

/**
 * Created by TheMDP on 11/21/17.
 *
 * Re-using CrfOnboardingTaskActivity because it has the correct style of Toolbar
 * We can switch this to another subclass if we copy in the toolbar code from CrfOnboardingTaskActivity
 */

public class CrfSettingsActivity extends CrfOnboardingTaskActivity {
    @Override
    protected StepLayout getLayoutForStep(Step step) {
        StepLayout stepLayout = super.getLayoutForStep(step);
        setActionBarTitle("Crf Settings");
        if (stepLayout instanceof FixedSubmitBarLayout) {
            ((FixedSubmitBarLayout) stepLayout).getSubmitBar().setVisibility(View.GONE);
        }
        return stepLayout;
    }

    @Override
    protected void showPreviousStep() {
        // just finish. OnboardTaskActivity also signs us out
        finish();
    }
}