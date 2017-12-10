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

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import org.researchstack.backbone.DataProvider;
import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.sagebionetworks.bridge.researchstack.BridgeDataProvider;
import org.sagebionetworks.bridge.researchstack.CrfDataProvider;
import org.sagebionetworks.bridge.researchstack.step.DataGroupQuestionStep;
import org.sagebionetworks.bridge.researchstack.step.layout.DataGroupQuestionStepLayout;
import org.sagebionetworks.research.crf.R;

import java.lang.ref.WeakReference;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by TheMDP on 11/19/17.
 */

public class CrfClinicDataGroupsStepLayout extends DataGroupQuestionStepLayout {
    public CrfClinicDataGroupsStepLayout(Context context) {
        super(context);
    }

    public CrfClinicDataGroupsStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CrfClinicDataGroupsStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initialize(Step step, StepResult result) {
        super.initialize(step, result);
        checkForExistingClinicAssignment();
    }

    /**
     * If we already have an existing clinic assignment, adding the other one can put the app
     * in a unrecoverable state, so make sure we avoid that
     */
    protected void checkForExistingClinicAssignment() {
        if (!(DataProvider.getInstance() instanceof BridgeDataProvider)) {
            throw new IllegalStateException("CrfClinicDataGroupsStepLayout only works with BridgeDataProvider");
        }


        /**
         *
         *
         * TODO: skip group selection if already set
         *
         *
         *
         */


        String loadingTitle = getString(R.string.crf_data_groups_loading_title);
        showLoadingDialog(loadingTitle);

        BridgeDataProvider bridgeDataProvider = (BridgeDataProvider)DataProvider.getInstance();
        final WeakReference<View> weakView = new WeakReference<>(this);
        List<String> localDataGroups = bridgeDataProvider.getLocalDataGroups();

        bridgeDataProvider.getStudyParticipant().observeOn(AndroidSchedulers.mainThread())
                .subscribe(participant -> {
            hideLoadingDialog();
            // Controls canceling an observable perform through weak reference to the view
            if (weakView.get() == null || weakView.get().getContext() == null) {
                return; // no callback
            }
            List<String> serverDataGroups = participant.getDataGroups();

            boolean shouldSkip = true;
            if (!localDataGroups.contains(CrfDataProvider.TEST_USER)) {
                if (serverDataGroups.contains(CrfDataProvider.CLINIC1)) {
                    bridgeDataProvider.addLocalDataGroup(CrfDataProvider.CLINIC1);
                } else if (serverDataGroups.contains(CrfDataProvider.CLINIC2)) {
                    bridgeDataProvider.addLocalDataGroup(CrfDataProvider.CLINIC1);
                } else {
                    // no clinic, do not skip
                    shouldSkip = false;
                }
            }

            if (shouldSkip) {
                // Skip this step, we already have the clinic assigned
                onComplete();
            }
        }, throwable -> {
            // Silent fail, allow user to try and set data groups
            hideLoadingDialog();
        });
    }

    public static class CrfDataGroupQuestionStep extends DataGroupQuestionStep {
        public static final String CUSTOM_STEP_IDENTIFIER = "clinicChoices";
        public CrfDataGroupQuestionStep(String identifier, String title, AnswerFormat format) {
            super(identifier, title, format);
        }
        @Override
        public Class getStepLayoutClass() {
            return CrfClinicDataGroupsStepLayout.class;
        }
    }
}
