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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.researchstack.backbone.factory.IntentFactory;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.sagebase.crf.CrfActivityResultListener;
import org.sagebase.crf.CrfSurveyTaskActivity;
import org.sagebase.crf.reminder.CrfReminderManager;
import org.sagebionetworks.bridge.researchstack.CrfResourceManager;
import org.sagebionetworks.bridge.researchstack.CrfTaskFactory;
import org.sagebionetworks.research.crf.R;

import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class CrfSkipInstructionStepLayout extends CrfInstructionStepLayout implements CrfActivityResultListener {

    protected CrfSkipInstructionStep crfSkipInstructionStep;
    protected TextView instructionViewTop;
    protected TextView instructionViewBottom;
    private static final String LOG_TAG = CrfSkipInstructionStepLayout.class.getCanonicalName();


    public CrfSkipInstructionStepLayout(Context context) {
        super(context);
    }


    @Override
    public int getContentResourceId() {
        return R.layout.crf_step_layout_skip_instruction;
    }

    @Override
    public void initialize(Step step, StepResult result) {
        validateAndSetCrfSkipStep(step);
        super.initialize(step, result);
    }

    protected void validateAndSetCrfSkipStep(Step step) {
        if (!(step instanceof CrfSkipInstructionStep)) {
            throw new IllegalStateException("CrfSkipInstructionStepLayout only works with CrfSkipInstructionStep");
        }
        this.crfSkipInstructionStep = (CrfSkipInstructionStep)step;
        this.instructionViewTop = findViewById(R.id.crf_instruction_text_top);
        this.instructionViewBottom = findViewById(R.id.crf_instruction_text_bottom);
    }

    @Override
    public void refreshStep() {
        super.refreshStep();

        // Display the instruction
        if(this.instructionViewTop != null) {
            instructionViewTop.setText(crfSkipInstructionStep.instruction);
            instructionViewTop.setVisibility(VISIBLE);
        }

        // Display the detail text
        if(this.instructionViewBottom != null) {
            instructionViewBottom.setText(crfSkipInstructionStep.getMoreDetailText());
            instructionViewBottom.setVisibility(VISIBLE);
        }

        if (remindMeLaterButton != null) {
            if (crfInstructionStep.remindMeLater) {
                remindMeLaterButton.setVisibility(View.VISIBLE);
                remindMeLaterButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        remindMeLater();
                    }
                });
            } else {
                remindMeLaterButton.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public boolean crfToolbarShowProgress() {
        return false;
    }


    @Override
    public void onActivityFinished(int requestCode, int resultCode, Intent data) {
        // From CrfStartTaskStepLayout
        if (requestCode == CrfReminderManager.DAILY_REMINDER_REQUEST_CODE && resultCode == RESULT_OK) {
            TaskResult taskResult = (TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT);
            if (taskResult == null || taskResult.getResults().values().isEmpty()) {
                Log.e(LOG_TAG, "Reminder time result empty");
                return;
            }
            StepResult reminderTimeResult = taskResult.getStepResult(CrfResourceManager.REMIND_ME_LATER_RESOURCE);
            if (!(reminderTimeResult.getResult() instanceof Long)) {
                Log.e(LOG_TAG, "Reminder time result must be a Long time");
                return;
            }
            Date reminderTime = new Date((Long)reminderTimeResult.getResult());
            CrfReminderManager.setReminderTimeHourAndMinute(getContext(), reminderTime);

            onComplete();
        }
    }

    public void remindMeLater() {
        Task task = (new CrfTaskFactory()).createTask(getContext(), CrfResourceManager.REMIND_ME_LATER_RESOURCE);
        Intent intent = IntentFactory.INSTANCE.newTaskIntent(getContext(), CrfSurveyTaskActivity.class, task);
        if (!(callbacks instanceof Activity)) {
            throw new IllegalStateException("Callbacks class must be an activity " +
                    "so we can start another activity from this step layout");
        }
        Activity activity = (Activity)callbacks;
        activity.startActivityForResult(intent, CrfReminderManager.DAILY_REMINDER_REQUEST_CODE);
        if(((CrfSkipInstructionStep)step).continueMeasurement) {
            ((CrfSkipInstructionStep) step).continueMeasurement = false;
        }
        ((CrfSkipInstructionStep)step).continueMeasurement = false;

    }
}
