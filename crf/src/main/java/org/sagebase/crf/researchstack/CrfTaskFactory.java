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

package org.sagebase.crf.researchstack;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

//import org.researchstack.backbone.ResourceManager;
import org.researchstack.backbone.ResourcePathManager;
import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.model.survey.ActiveStepSurveyItem;
import org.researchstack.backbone.model.survey.ChoiceQuestionSurveyItem;
import org.researchstack.backbone.model.survey.FormSurveyItem;
import org.researchstack.backbone.model.survey.IntegerRangeSurveyItem;
import org.researchstack.backbone.model.survey.QuestionSurveyItem;
import org.researchstack.backbone.model.survey.SurveyItem;
import org.researchstack.backbone.model.survey.factory.SurveyFactory;
import org.researchstack.backbone.model.taskitem.TaskItem;
import org.researchstack.backbone.model.taskitem.TaskItemAdapter;
import org.researchstack.backbone.model.taskitem.factory.TaskItemFactory;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.ActiveStep;
import org.researchstack.backbone.task.Task;
import org.sagebase.crf.CrfActiveTaskActivity;
import org.sagebase.crf.step.CrfCameraPermissionStep;
import org.sagebase.crf.step.CrfCompletionStep;
import org.sagebase.crf.step.CrfCompletionSurveyItem;
import org.sagebase.crf.step.CrfCountdownStep;
import org.sagebase.crf.step.CrfFormStep;
import org.sagebase.crf.step.CrfFormSurveyItem;
import org.sagebase.crf.step.CrfHeartRateCameraStep;
import org.sagebase.crf.step.CrfInstructionStep;
import org.sagebase.crf.step.CrfInstructionSurveyItem;
import org.sagebase.crf.step.CrfStairStep;
import org.sagebase.crf.step.CrfStartTaskStep;
import org.sagebase.crf.step.CrfStartTaskSurveyItem;
import org.sagebase.crf.step.body.CrfChoiceAnswerFormat;
import org.sagebase.crf.step.CrfHeartRateSurveyItem;
import org.sagebase.crf.step.body.CrfIntegerAnswerFormat;

import java.util.Collections;
import java.util.List;

/**
 * Created by TheMDP on 10/24/17.
 */

public class CrfTaskFactory extends TaskItemFactory {

    public static final String TASK_ID_HEART_RATE_MEASUREMENT = "Heart Rate Measurement";
    public static final String TASK_ID_CARDIO_STRESS_TEST = "Cardio Stress Test";
    public static final String TASK_ID_HEART_RATE_TRAINING = "Heart Rate Training";

    public static final String TASK_ID_CARDIO_12MT = "Cardio 12MT";
    public static final String TASK_ID_STAIR_STEP = "Cardio Stair Step";
    public static final String TASK_ID_BACKGROUND_SURVEY = "Background Survey";
    public static final String TASK_ID_SETTINGS_SCREEN = "Settings Screen";

    public static final String RESULT_ID_SETTINGS_SCREEN_FORM           = "Settings Form (Read Only)";
    public static final String RESULT_ID_SETTINGS_SCREEN_EXTERNAL_ID    = "External ID";
    public static final String RESULT_ID_SETTINGS_SCREEN_VERSION        = "Version";
    public static final String RESULT_ID_SETTINGS_SCREEN_CONTACT_INFO   = "Contact Info";
    public static final String RESULT_ID_SETTINGS_SCREEN_DATA_GROUPS    = "Data Groups";


    private Gson gson;

    private CrfResourceManager resourceManager;

    public CrfTaskFactory() {
        super();
        resourceManager = new CrfResourceManager();
        gson = createGson();
        setupCustomStepCreator();
    }

    public Task createTask(Context context, String resourceName) {
        ResourcePathManager.Resource resource = resourceManager.getResource(resourceName);
        String json = resourceManager.getResourceAsString(context,
                resourceManager.generatePath(resource.getType(), resource.getName()));
        Gson gson = createGson(); // Do not store this gson as a member variable, it has a link to Context
        TaskItem taskItem = gson.fromJson(json, TaskItem.class);
        return super.createTask(context, taskItem);
    }

    private Gson createGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(SurveyItem.class, new CrfSurveyItemAdapter());
        builder.registerTypeAdapter(TaskItem.class, new TaskItemAdapter());
        return builder.create();
    }

    private void setupCustomStepCreator() {
        setCustomStepCreator(new CustomStepCreator() {
            @Override
            public Step createCustomStep(Context context, SurveyItem item, boolean isSubtaskStep, SurveyFactory factory) {
                if (item.getCustomTypeValue() != null) {
                    switch (item.getCustomTypeValue()) {
                        case CrfSurveyItemAdapter.CRF_INSTRUCTION_SURVEY_ITEM_TYPE:
                            if (!(item instanceof CrfInstructionSurveyItem)) {
                                throw new IllegalStateException("Error in json parsing, crf_instruction types must be CrfInstructionSurveyItem");
                            }
                            return createCrfInstructionStep((CrfInstructionSurveyItem)item);
                        case CrfSurveyItemAdapter.CRF_CAMERA_PERMISSION_SURVEY_ITEM_TYPE:
                            if (!(item instanceof CrfInstructionSurveyItem)) {
                                throw new IllegalStateException("Error in json parsing, crf_instruction types must be CrfInstructionSurveyItem");
                            }
                            return createCrfCameraPermissionStep((CrfInstructionSurveyItem)item);
                        case CrfSurveyItemAdapter.CRF_START_TASK_SURVEY_ITEM_TYPE:
                            if (!(item instanceof CrfStartTaskSurveyItem)) {
                                throw new IllegalStateException("Error in json parsing, crf_start_task types must be CrfStartTaskSurveyItem");
                            }
                            return createCrfStartTaskStep((CrfStartTaskSurveyItem)item);
                        case CrfSurveyItemAdapter.CRF_HEART_RATE_CAMERA_SURVEY_ITEM_TYPE:
                            if (!(item instanceof CrfHeartRateSurveyItem)) {
                                throw new IllegalStateException("Error in json parsing, crf_heart_rate_camera_step types must be CrfHeartRateSurveyItem");
                            }
                            return createHeartRateCameraStep((CrfHeartRateSurveyItem)item);
                        case CrfSurveyItemAdapter.CRF_COUNTDOWN_SURVEY_ITEM_TYPE:
                            if (!(item instanceof ActiveStepSurveyItem)) {
                                throw new IllegalStateException("Error in json parsing, crf_countdown types must be ActiveStepSurveyItem");
                            }
                            return createCrfCountdownStep((ActiveStepSurveyItem)item);
                        case CrfSurveyItemAdapter.CRF_STAIR_STEP_SURVEY_ITEM_TYPE:
                            if (!(item instanceof ActiveStepSurveyItem)) {
                                throw new IllegalStateException("Error in json parsing, crf_step_layout_stair types must be ActiveStepSurveyItem");
                            }
                            return createCrfStairStep((ActiveStepSurveyItem)item);
                        case CrfSurveyItemAdapter.CRF_COMPLETION_SURVEY_ITEM_TYPE:
                            if (!(item instanceof CrfCompletionSurveyItem)) {
                                throw new IllegalStateException("Error in json parsing, crf_completion types must be CrfCompletionSurveyItem");
                            }
                            return createCompletionStep((CrfCompletionSurveyItem)item);
                        case CrfSurveyItemAdapter.CRF_FORM_SURVEY_ITEM_TYPE:
                            if (!(item instanceof CrfFormSurveyItem)) {
                                throw new IllegalStateException("Error in json parsing, crf_form types must be CrfFormSurveyItem");
                            }
                            return createCrfFormStep(context, (CrfFormSurveyItem)item);
                        case CrfSurveyItemAdapter.CRF_INTEGER_SURVEY_ITEM_TYPE:
//                        case CrfSurveyItemAdapter.CRF_MULTIPLE_CHOICE_SURVEY_ITEM_TYPE:
                        case CrfSurveyItemAdapter.CRF_SINGLE_CHOICE_SURVEY_ITEM_TYPE:
                            if (!(item instanceof QuestionSurveyItem)) {
                                throw new IllegalStateException("Error in json parsing " + item.getCustomTypeValue() + ", types must be QuestionSurveyItem");
                            }
                            // Even though these weren't wrapped in a form step, we are going to wrap
                            // them in a CrfFormStep so that the UI looks appropriate
                            QuestionSurveyItem questionItem = (QuestionSurveyItem)item;
                            CrfFormSurveyItem FormSurveyItem = new CrfFormSurveyItem();
                            FormSurveyItem.identifier = item.identifier + "Form";
                            FormSurveyItem.items = Collections.singletonList(item);
                            FormSurveyItem.skipIdentifier = questionItem.skipIdentifier;
                            FormSurveyItem.skipIfPassed = questionItem.skipIfPassed;
                            FormSurveyItem.expectedAnswer = questionItem.expectedAnswer;
                            return createCrfFormStep(context, FormSurveyItem);
                    }
                }
                return null;
            }
        });
    }

    public static class CrfFormSurveyItemWrapper extends FormSurveyItem {

        /* Default constructor needed for serialization/deserialization of object */
        public CrfFormSurveyItemWrapper() {
            super();
        }

        @Override
        public String getCustomTypeValue() {
            return CrfSurveyItemAdapter.CRF_FORM_SURVEY_ITEM_TYPE;
        }
    }

    @Override
    public AnswerFormat createCustomAnswerFormat(Context context, QuestionSurveyItem item) {
        switch (item.getCustomTypeValue()) {
            case CrfSurveyItemAdapter.CRF_INTEGER_SURVEY_ITEM_TYPE:
                return createCrfIntegerAnswerFormat(context, item);
            case CrfSurveyItemAdapter.CRF_MULTIPLE_CHOICE_SURVEY_ITEM_TYPE:
            case CrfSurveyItemAdapter.CRF_SINGLE_CHOICE_SURVEY_ITEM_TYPE:
                return createCrfChoiceAnswerFormat(context, item);
        }
        return super.createCustomAnswerFormat(context, item);
    }

    private CrfInstructionStep createCrfInstructionStep(CrfInstructionSurveyItem item) {
        CrfInstructionStep step = new CrfInstructionStep(
                item.identifier, item.title, item.text);
        fillCrfInstructionStep(step, item);
        return step;
    }

    private CrfInstructionStep createCrfCameraPermissionStep(CrfInstructionSurveyItem item) {
        CrfCameraPermissionStep step = new CrfCameraPermissionStep(
                item.identifier, item.title, item.text);
        fillCrfInstructionStep(step, item);
        return step;
    }

    void fillCrfInstructionStep(CrfInstructionStep step, CrfInstructionSurveyItem item) {
        fillInstructionStep(step, item);
        if (item.buttonType != null) {
            step.buttonType = item.buttonType;
        }
        if (item.buttonText != null) {
            step.buttonText = item.buttonText;
        }
        if (item.backgroundColorRes != null) {
            step.backgroundColorRes = item.backgroundColorRes;
        }
        if (item.imageColorRes != null) {
            step.imageBackgroundColorRes = item.imageColorRes;
        }
        if (item.tintColorRes != null) {
            step.tintColorRes = item.tintColorRes;
        }
        if (item.statusBarColorRes != null) {
            step.statusBarColorRes = item.statusBarColorRes;
        }
        if (item.hideProgress) {
            step.hideProgress = true;
        }
        if (item.behindToolbar) {
            step.behindToolbar = true;
        }
        if (item.mediaVolume) {
            step.mediaVolume = true;
        }
        if(item.learnMoreText != null) {
            step.learnMoreText = item.learnMoreText;
        }
        if(item.learnMoreFile != null) {
            step.learnMoreFile = item.learnMoreFile;
        }
        if(item.learnMoreTitle != null) {
            step.learnMoreTitle = item.learnMoreTitle;
        }
        if (item.remindMeLater) {
            step.remindMeLater = true;
        }
        else {
            step.remindMeLater = false;
        }

    }

    private CrfStartTaskStep createCrfStartTaskStep(CrfStartTaskSurveyItem item) {
        CrfStartTaskStep step = new CrfStartTaskStep(
                item.identifier, item.title, item.text);
        fillCrfStartTaskStep(step, item);
        return step;
    }

    private void fillCrfStartTaskStep(CrfStartTaskStep step, CrfStartTaskSurveyItem item) {
        fillCrfInstructionStep(step, item);
        step.remindMeLater = item.remindMeLater;
        if (item.infoHtmlFilename != null) {
            step.infoHtmlFilename = item.infoHtmlFilename;
        }
        if (item.textColorRes != null) {
            step.textColorRes = item.textColorRes;
        }
        if (item.iconText != null) {
            step.iconText = item.iconText;
        }
    }

    private CrfHeartRateCameraStep createHeartRateCameraStep(CrfHeartRateSurveyItem item) {
        CrfHeartRateCameraStep step = new CrfHeartRateCameraStep(item.identifier, item.title, item.text);
        fillCrfActiveStep(step, item);
        if(item.identifier != null) {
            step.stepIdentifier = item.identifier;
        }

        step.isHrRecoveryStep = item.isHrRecoveryStep;
        return step;
    }

    private CrfCountdownStep createCrfCountdownStep(ActiveStepSurveyItem item) {
        CrfCountdownStep step = new CrfCountdownStep(item.identifier);
        fillCrfActiveStep(step, item);
        return step;
    }

    private CrfStairStep createCrfStairStep(ActiveStepSurveyItem item) {
        CrfStairStep step = new CrfStairStep(item.identifier);
        fillCrfActiveStep(step, item);
        return step;
    }

    protected void fillCrfActiveStep(ActiveStep step, ActiveStepSurveyItem item) {
        fillActiveStep(step, item);
        step.setActivityClazz(CrfActiveTaskActivity.class);
    }

    private CrfCompletionStep createCompletionStep(CrfCompletionSurveyItem item) {
        CrfCompletionStep step = new CrfCompletionStep(item.identifier, item.title, item.text);
        fillCrfInstructionStep(step, item);
        if (item.topText != null) {
            step.topText = item.topText;
        }
        if (item.bottomText != null) {
            step.bottomText = item.bottomText;
        }
        if (item.valueLabelText != null) {
            step.valueLabelText = item.valueLabelText;
        }
        if (item.valueResultId != null) {
            step.valueResultId = item.valueResultId;
        }
        if (item.showRedoButton) {
            step.showRedoButton = item.showRedoButton;
        }
        return step;
    }

    private CrfFormStep createCrfFormStep(Context context, CrfFormSurveyItem item) {
        if (item.items == null || item.items.isEmpty()) {
            throw new IllegalStateException("compound surveys must have step items to proceed");
        }

        List<QuestionStep> questionSteps = super.formStepCreateQuestionSteps(context, item);
        CrfFormStep step = new CrfFormStep(item.identifier, item.title, item.text, questionSteps);
        fillNavigationFormStep(step, item);
        if(item.learnMoreText != null) {
            step.learnMoreText = item.learnMoreText;
        }
        if(item.learnMoreFile != null) {
            step.learnMoreFile = item.learnMoreFile;
        }
        if(item.learnMoreTitle != null) {
            step.learnMoreTitle = item.learnMoreTitle;
        }
        return step;
    }


    public CrfIntegerAnswerFormat createCrfIntegerAnswerFormat(Context context, QuestionSurveyItem item) {
        if (!(item instanceof IntegerRangeSurveyItem)) {
            throw new IllegalStateException("Error in json parsing, QUESTION_INTEGER types must be IntegerRangeSurveyItem");
        }
        CrfIntegerAnswerFormat format = new CrfIntegerAnswerFormat();
        fillIntegerAnswerFormat(format, (IntegerRangeSurveyItem)item);
        return format;
    }

    public CrfChoiceAnswerFormat createCrfChoiceAnswerFormat(Context context, QuestionSurveyItem item) {
        if (!(item instanceof ChoiceQuestionSurveyItem)) {
            throw new IllegalStateException("Error in json parsing, this type must be ChoiceQuestionSurveyItem");
        }
        CrfChoiceAnswerFormat format = new CrfChoiceAnswerFormat();
        fillChoiceAnswerFormat(format, (ChoiceQuestionSurveyItem)item);
        // Override setting multiple choice answer format, since it is a custom survey type
        if (item.getCustomTypeValue().equals(CrfSurveyItemAdapter.CRF_MULTIPLE_CHOICE_SURVEY_ITEM_TYPE)
                || item.getCustomTypeValue().equals(CrfSurveyItemAdapter.CRF_SKIP_MC_TYPE)) {
            format.setAnswerStyle(AnswerFormat.ChoiceAnswerStyle.MultipleChoice);
        }
        return format;
    }
}
