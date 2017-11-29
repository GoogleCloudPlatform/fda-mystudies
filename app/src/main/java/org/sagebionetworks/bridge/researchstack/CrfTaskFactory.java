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

package org.sagebionetworks.bridge.researchstack;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.researchstack.backbone.ResourceManager;
import org.researchstack.backbone.ResourcePathManager;
import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import org.researchstack.backbone.answerformat.IntegerAnswerFormat;
import org.researchstack.backbone.factory.IntentFactory;
import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.model.survey.ActiveStepSurveyItem;
import org.researchstack.backbone.model.survey.ChoiceQuestionSurveyItem;
import org.researchstack.backbone.model.survey.CompoundQuestionSurveyItem;
import org.researchstack.backbone.model.survey.IntegerRangeSurveyItem;
import org.researchstack.backbone.model.survey.QuestionSurveyItem;
import org.researchstack.backbone.model.survey.SurveyItem;
import org.researchstack.backbone.model.survey.SurveyItemType;
import org.researchstack.backbone.model.survey.factory.SurveyFactory;
import org.researchstack.backbone.model.taskitem.TaskItem;
import org.researchstack.backbone.model.taskitem.TaskItemAdapter;
import org.researchstack.backbone.model.taskitem.factory.TaskItemFactory;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.NavigationFormStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.Task;
import org.sagebase.crf.CrfOnboardingTaskActivity;
import org.sagebase.crf.CrfSettingsActivity;
import org.sagebase.crf.CrfSurveyTaskActivity;
import org.sagebase.crf.step.Crf12MinWalkingStep;
import org.sagebase.crf.step.CrfCompletionStep;
import org.sagebase.crf.step.CrfCompletionSurveyItem;
import org.sagebase.crf.step.CrfCountdownStep;
import org.sagebase.crf.step.CrfFitBitStepLayout;
import org.sagebase.crf.step.CrfFormStep;
import org.sagebase.crf.step.CrfHeartRateCameraStep;
import org.sagebase.crf.step.CrfInstructionStep;
import org.sagebase.crf.step.CrfInstructionSurveyItem;
import org.sagebase.crf.step.CrfStairStep;
import org.sagebase.crf.step.CrfStairSurveyItem;
import org.sagebase.crf.step.CrfPhotoCaptureStep;
import org.sagebase.crf.step.CrfStartTaskStep;
import org.sagebase.crf.step.CrfStartTaskSurveyItem;
import org.sagebase.crf.step.body.CrfChoiceAnswerFormat;
import org.sagebase.crf.step.body.CrfIntegerAnswerFormat;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import static org.sagebionetworks.bridge.researchstack.CrfSurveyItemAdapter.CRF_INTEGER_SURVEY_ITEM_TYPE;

/**
 * Created by TheMDP on 10/24/17.
 */

public class CrfTaskFactory extends TaskItemFactory {

    public static final String TASK_ID_HEART_RATE_MEASUREMENT = "HeartRate Measurement";
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

    public CrfTaskFactory() {
        super();
        gson = createGson();
        setupCustomStepCreator();
    }

    public Task createTask(Context context, String resourceName) {
        ResourcePathManager.Resource resource = ResourceManager.getInstance().getResource(resourceName);
        String json = ResourceManager.getResourceAsString(context,
                ResourceManager.getInstance().generatePath(resource.getType(), resource.getName()));
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
                        case CrfSurveyItemAdapter.CRF_START_TASK_SURVEY_ITEM_TYPE:
                            if (!(item instanceof CrfStartTaskSurveyItem)) {
                                throw new IllegalStateException("Error in json parsing, crf_start_task types must be CrfStartTaskSurveyItem");
                            }
                            return createCrfStartTaskStep((CrfStartTaskSurveyItem)item);
                        case CrfSurveyItemAdapter.CRF_HEART_RATE_CAMERA_SURVEY_ITEM_TYPE:
                            if (!(item instanceof ActiveStepSurveyItem)) {
                                throw new IllegalStateException("Error in json parsing, crf_heart_rate_camera_step types must be ActiveStepSurveyItem");
                            }
                            return createHeartRateCameraStep((ActiveStepSurveyItem)item);
                        case CrfSurveyItemAdapter.CRF_COUNTDOWN_SURVEY_ITEM_TYPE:
                            if (!(item instanceof ActiveStepSurveyItem)) {
                                throw new IllegalStateException("Error in json parsing, crf_countdown types must be ActiveStepSurveyItem");
                            }
                            return createCrfCountdownStep((ActiveStepSurveyItem)item);
                        case CrfSurveyItemAdapter.CRF_12_MIN_WALK_SURVEY_ITEM_TYPE:
                            if (!(item instanceof ActiveStepSurveyItem)) {
                                throw new IllegalStateException("Error in json parsing, crf_12_min_walk types must be ActiveStepSurveyItem");
                            }
                            return createCrf12MinWalkStep((ActiveStepSurveyItem)item);
                        case CrfSurveyItemAdapter.CRF_STAIR_STEP_SURVEY_ITEM_TYPE:
                            if (!(item instanceof CrfStairSurveyItem)) {
                                throw new IllegalStateException("Error in json parsing, crf_step_layout_stair types must be CrfStairSurveyItem");
                            }
                            return createCrfStairStep((CrfStairSurveyItem)item);
                        case CrfSurveyItemAdapter.CRF_COMPLETION_SURVEY_ITEM_TYPE:
                            if (!(item instanceof CrfCompletionSurveyItem)) {
                                throw new IllegalStateException("Error in json parsing, crf_completion types must be CrfCompletionSurveyItem");
                            }
                            return createCompletionStep((CrfCompletionSurveyItem)item);
                        case CrfSurveyItemAdapter.CRF_PHOTO_CAPTURE_SURVEY_ITEM_TYPE:
                            if (!(item instanceof CrfInstructionSurveyItem)) {
                                throw new IllegalStateException("Error in json parsing, crf_photo_capture types must be CrfPhotoCaptureSurveyItem");
                            }
                            return createCrfPhotoCaptureStep((CrfInstructionSurveyItem)item);
                        case CrfSurveyItemAdapter.CRF_FITBIT_SURVEY_ITEM_TYPE:
                            if (!(item instanceof CrfInstructionSurveyItem)) {
                                throw new IllegalStateException("crf_fitbit types must be parsed as CrfInstructionSurveyItem");
                            }
                            return createFitBitStep((CrfInstructionSurveyItem)item);
                        case CrfSurveyItemAdapter.CRF_FORM_SURVEY_ITEM_TYPE:
                            if (!(item instanceof CompoundQuestionSurveyItem)) {
                                throw new IllegalStateException("Error in json parsing, crf_form types must be CrfFormSurveyItem");
                            }
                            return createCrfFormStep(context, (CompoundQuestionSurveyItem)item);

                    }
                }
                return null;
            }
        });
    }

    private CrfInstructionStep createCrfInstructionStep(CrfInstructionSurveyItem item) {
        CrfInstructionStep step = new CrfInstructionStep(
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
    }

    private CrfHeartRateCameraStep createHeartRateCameraStep(ActiveStepSurveyItem item) {
        CrfHeartRateCameraStep step = new CrfHeartRateCameraStep(item.identifier, item.title, item.text);
        fillActiveStep(step, item);
        return step;
    }

    private CrfCountdownStep createCrfCountdownStep(ActiveStepSurveyItem item) {
        CrfCountdownStep step = new CrfCountdownStep(item.identifier);
        fillActiveStep(step, item);
        return step;
    }

    private Crf12MinWalkingStep createCrf12MinWalkStep(ActiveStepSurveyItem item) {
        Crf12MinWalkingStep step = new Crf12MinWalkingStep(item.identifier);
        fillActiveStep(step, item);
        return step;
    }

    private CrfStairStep createCrfStairStep(CrfStairSurveyItem item) {
        CrfStairStep step = new CrfStairStep(item.identifier);
        fillActiveStep(step, item);
        if (item.stairInterval > 0) {
            step.stairInterval = item.stairInterval;
        }
        return step;
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
        return step;
    }

    private CrfPhotoCaptureStep createCrfPhotoCaptureStep(CrfInstructionSurveyItem item) {
        CrfPhotoCaptureStep step = new CrfPhotoCaptureStep(
                item.identifier, item.title, item.text);
        fillCrfInstructionStep(step, item);
        return step;
    }

    CrfFitBitStepLayout.CrfFitBitStep createFitBitStep(CrfInstructionSurveyItem item) {
        CrfFitBitStepLayout.CrfFitBitStep step = new CrfFitBitStepLayout.CrfFitBitStep(
                item.identifier, item.title, item.text);
        fillCrfInstructionStep(step, item);
        return step;
    }

    /**
     * @param externalID the external ID for the user
     * @param version the app version string
     * @param contactInfo the contact info (email) for the study
     * @return the task for the settings screen
     */
    public void startSettingsScreen(Context context, String externalID, String version, String contactInfo, String dataGroups) {

        TaskResult taskResult = new TaskResult(TASK_ID_SETTINGS_SCREEN);
        StepResult<StepResult> formResult = new StepResult<>(new Step(RESULT_ID_SETTINGS_SCREEN_FORM));

        {   // External ID
            StepResult<String> result = new StepResult<>(new Step(RESULT_ID_SETTINGS_SCREEN_EXTERNAL_ID));
            result.setResult(externalID);
            formResult.setResultForIdentifier(result.getIdentifier(), result);
        }

        {   // Version
            StepResult<String> result = new StepResult<>(new Step(RESULT_ID_SETTINGS_SCREEN_VERSION));
            result.setResult(version);
            formResult.setResultForIdentifier(result.getIdentifier(), result);
        }

        {   // Contact Info
            StepResult<String> result = new StepResult<>(new Step(RESULT_ID_SETTINGS_SCREEN_CONTACT_INFO));
            result.setResult(contactInfo);
            formResult.setResultForIdentifier(result.getIdentifier(), result);
        }

        {   // Data Groups
            StepResult<String> result = new StepResult<>(new Step(RESULT_ID_SETTINGS_SCREEN_DATA_GROUPS));
            result.setResult(dataGroups);
            formResult.setResultForIdentifier(result.getIdentifier(), result);
        }

        Map<String, StepResult> taskResultMap = new LinkedHashMap<>();
        taskResultMap.put(formResult.getIdentifier(), formResult);
        taskResult.setResults(taskResultMap);

        Task task = createTask(context, CrfResourceManager.SETTINGS_SCREEN_RESOURCE);
        context.startActivity(IntentFactory.INSTANCE.newTaskIntent(
                context, CrfSettingsActivity.class, task, taskResult));
    }

    private CrfFormStep createCrfFormStep(Context context, CompoundQuestionSurveyItem item) {
        if (item.items == null || item.items.isEmpty()) {
            throw new IllegalStateException("compound surveys must have step items to proceed");
        }

        List<QuestionStep> questionSteps = new ArrayList<>();
        for (SurveyItem subItem : item.items) {
            if (subItem.getCustomTypeValue() != null &&
                    subItem.getCustomTypeValue().equals(CrfSurveyItemAdapter.CRF_INTEGER_SURVEY_ITEM_TYPE)) {
                QuestionStep step = createCustomIntegerBody(context, (QuestionSurveyItem) subItem);
                questionSteps.add(step);
            } else if (subItem.getCustomTypeValue() != null &&
                    subItem.getCustomTypeValue().equals(CrfSurveyItemAdapter.CRF_SINGLE_CHOICE_SURVEY_ITEM_TYPE)) {
                QuestionStep step = createCustomSingleChoiceBody(context, (QuestionSurveyItem) subItem);
                questionSteps.add(step);
            } else if (subItem instanceof QuestionSurveyItem) {
                QuestionStep step = createQuestionStep(context, (QuestionSurveyItem)subItem);
                questionSteps.add(step);
            }
        }

        CrfFormStep step = new CrfFormStep(item.identifier, item.title, item.text, questionSteps);
        step.setAutoFocusFirstEditText(true);

        step.setSkipIfPassed(item.skipIfPassed);
        step.setSkipToStepIdentifier(item.skipIdentifier);


        fillQuestionStep(item, step);
        if (item.expectedAnswer != null) {
            step.setExpectedAnswer(item.expectedAnswer);
        }
        if (item.skipTitle != null) {
            step.setSkipTitle(item.skipTitle);
            // we can assume that if we set the skip title, we want to show the skip button
            step.setOptional(true);
        }

        return step;
    }

    public QuestionStep createCustomIntegerBody(Context context, QuestionSurveyItem item) {
        AnswerFormat format = null;
        if (!(item instanceof IntegerRangeSurveyItem)) {
            throw new IllegalStateException("Error in json parsing, QUESTION_INTEGER types must be IntegerRangeSurveyItem");
        }
        IntegerRangeSurveyItem intItem = (IntegerRangeSurveyItem)item;
        int min = (intItem.min == null) ? 0 : intItem.min;
        int max = (intItem.max == null) ? 0 : intItem.max;
        format = new CrfIntegerAnswerFormat(min, max);

        QuestionStep step = new QuestionStep(item.identifier, item.title, format);
        return step;
    }

    public QuestionStep createCustomSingleChoiceBody(Context context, QuestionSurveyItem item) {
        AnswerFormat format = null;
        if (!(item instanceof ChoiceQuestionSurveyItem)) {
            throw new IllegalStateException("Error in json parsing, this type must be ChoiceQuestionSurveyItem");
        }
        ChoiceQuestionSurveyItem singleItem = (ChoiceQuestionSurveyItem)item;
        if (singleItem.items == null || singleItem.items.isEmpty()) {
            throw new IllegalStateException("ChoiceQuestionSurveyItem must have choices");
        }
        AnswerFormat.ChoiceAnswerStyle answerStyle = AnswerFormat.ChoiceAnswerStyle.SingleChoice;
        Choice[] choices = singleItem.items.toArray(new Choice[singleItem.items.size()]);
        format = new CrfChoiceAnswerFormat(answerStyle, choices);

        QuestionStep step = new QuestionStep(item.identifier, item.title, format);
        return step;
    }
}
