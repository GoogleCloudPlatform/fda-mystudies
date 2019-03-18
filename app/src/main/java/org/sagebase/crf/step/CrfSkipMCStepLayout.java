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
import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.researchstack.backbone.factory.IntentFactory;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.FormStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.sagebase.crf.CrfActivityResultListener;
import org.sagebase.crf.CrfSurveyTaskActivity;
import org.sagebase.crf.reminder.CrfReminderManager;
import org.sagebase.crf.researchstack.CrfResourceManager;
import org.sagebase.crf.researchstack.CrfTaskFactory;
import org.sagebionetworks.research.crf.R;

import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class CrfSkipMCStepLayout extends CrfFormStepLayout implements CrfActivityResultListener {

    protected CrfSkipMCStep step;
    protected TextView crf_title;
    protected TextView crf_text;
    protected Button remindMeLaterButton;
    private static final String LOG_TAG = CrfSkipMCStepLayout.class.getCanonicalName();

    public CrfSkipMCStepLayout(Context context) {
        super(context);
    }

    @Override
    public void initialize(Step step, StepResult result) {
        validateAndSetCrfFormStep(step);
        super.initialize(step, result);
        setupViews();
        refreshCrfSubmitBar();

        this.step = (CrfSkipMCStep) step;
    }

    @Override
    public int getContentResourceId() {
        return R.layout.crf_step_layout_skip_mc;
    }

    @Override
    public void setupViews() {
        super.setupViews();

        this.crf_title = findViewById(R.id.rsb_survey_title);
        this.crf_text = findViewById(R.id.rsb_survey_text);
        this.remindMeLaterButton = findViewById(R.id.crf_submit_bar_skip);

    }

    @Override
    protected void initStepLayout(FormStep step)
    {
        super.initStepLayout(step);
        container.setBackgroundColor(getResources().getColor(R.color.magenta));

    }

    @Override
    protected void refreshCrfSubmitBar() {
        super.refreshCrfSubmitBar();

        crfNextButton.setBackgroundResource(R.drawable.crf_rounded_button_gray);
        crfNextButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.rsb_white, null));
        crfNextButton.setText("Done");
        crfBackButton.setVisibility(GONE);

        crf_title.setTextColor(Color.WHITE);
        crf_title.setVisibility(VISIBLE);
        crf_text.setTextColor(Color.WHITE);
        crf_text.setVisibility(VISIBLE);

        remindMeLaterButton.setText("Remind me later");
        remindMeLaterButton.setVisibility(View.VISIBLE);
        remindMeLaterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                remindMeLater();
            }
        });
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

        onComplete();
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

        }
    }
}
