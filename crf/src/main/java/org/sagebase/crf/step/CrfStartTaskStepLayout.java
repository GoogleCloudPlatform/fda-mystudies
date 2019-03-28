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
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.researchstack.backbone.ResourceManager;
import org.researchstack.backbone.factory.IntentFactory;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.researchstack.backbone.ui.ViewWebDocumentActivity;
import org.researchstack.backbone.utils.ResUtils;
import org.sagebase.crf.CrfActivityResultListener;
import org.sagebase.crf.CrfViewTaskActivity;
import org.sagebase.crf.R;
import org.sagebase.crf.researchstack.CrfResourceManager;
import org.sagebase.crf.researchstack.CrfTaskFactory;
import org.sagebase.crf.view.CrfTaskToolbarActionManipulator;
import org.sagebase.crf.view.CrfTaskToolbarIconManipulator;
import org.sagebase.crf.view.CrfTaskToolbarProgressManipulator;

import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * Created by TheMDP on 10/25/17.
 */

public class CrfStartTaskStepLayout extends CrfInstructionStepLayout implements
        CrfTaskToolbarProgressManipulator, CrfActivityResultListener {

    private static final String LOG_TAG = CrfStartTaskStepLayout.class.getCanonicalName();
    public static final int DAILY_REMINDER_REQUEST_CODE = 2398;

    private CrfStartTaskStep crfStartTaskStep;
    protected Button remindMeLaterButton;
    protected ImageView imageIcon;

    public CrfStartTaskStepLayout(Context context) {
        super(context);
    }

    public CrfStartTaskStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CrfStartTaskStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CrfStartTaskStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public int getContentResourceId() {
        return R.layout.crf_step_layout_start_task;
    }

    @Override
    public void initialize(Step step, StepResult result) {
        validateAndSetCrfStartTaskStep(step);
        super.initialize(step, result);
    }

    protected void validateAndSetCrfStartTaskStep(Step step) {
        if (!(step instanceof CrfStartTaskStep)) {
            throw new IllegalStateException("CrfStartTaskStepLayout only works with CrfStartTaskStep");
        }
        this.crfStartTaskStep = (CrfStartTaskStep) step;
    }

    @Override
    public void connectStepUi(int titleRId, int textRId, int imageRId, int detailRId) {
        super.connectStepUi(titleRId, textRId, imageRId, detailRId);
        remindMeLaterButton = findViewById(R.id.remind_me_later);
        imageIcon = findViewById(R.id.crf_needs_icon);
    }

    @Override
    public void refreshStep() {
        super.refreshStep();

        if (crfStartTaskStep.remindMeLater) {
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

        if (crfStartTaskStep.textColorRes != null) {
            int colorId = ResUtils.getColorResourceId(getContext(), crfStartTaskStep.textColorRes);
            int color = ResourcesCompat.getColor(getResources(), colorId, null);
            titleTextView.setTextColor(color);
            textTextView.setTextColor(color);
        }

        if (crfStartTaskStep.getIconImage() != null) {
            int drawableInt = ResUtils.getDrawableResourceId(getContext(), crfStartTaskStep.getIconImage());
            if (drawableInt != 0) {
                imageIcon.setImageResource(drawableInt);

            }
        }
    }

    public void remindMeLater() {
        Task task = (new CrfTaskFactory()).createTask(getContext(), CrfResourceManager.REMIND_ME_LATER_RESOURCE);
        Intent intent = IntentFactory.INSTANCE.newTaskIntent(getContext(), CrfViewTaskActivity.class, task);
        if (!(callbacks instanceof Activity)) {
            throw new IllegalStateException("Callbacks class must be an activity " +
                    "so we can start another activity from this step layout");
        }
        Activity activity = (Activity)callbacks;
        activity.startActivityForResult(intent, DAILY_REMINDER_REQUEST_CODE);
    }

    @Override
    public boolean crfToolbarShowProgress() {
        return false;
    }

//    @Override
//    public boolean crfToolbarRightIconClicked() {
//        String path = ResourceManager.getInstance().
//                generateAbsolutePath(ResourceManager.Resource.TYPE_HTML, crfStartTaskStep.infoHtmlFilename);
//        Intent intent = new Intent(getContext(), ViewWebDocumentActivity.class);
//        intent.putExtra(ViewWebDocumentActivity.KEY_DOC_PATH, path);
//        intent.putExtra(ViewWebDocumentActivity.KEY_TITLE, "");
//        getContext().startActivity(intent);
//        return true; // consumed the click
//    }
//
//    @Override
//    public int crfToolbarLeftIcon() {
//        return R.drawable.crf_ic_back;
//    }
//
//    @Override
//    public int crfToolbarRightIcon() {
//        return crfStartTaskStep.infoHtmlFilename != null ? R.drawable.crf_ic_info : NO_ICON;
//    }

    @Override
    public void onActivityFinished(int requestCode, int resultCode, Intent data) {
        if (requestCode == DAILY_REMINDER_REQUEST_CODE && resultCode == RESULT_OK) {
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
            //TODO: Return reminder time to calling container to handle -nathaniel 03/21/19

        }
    }
}
