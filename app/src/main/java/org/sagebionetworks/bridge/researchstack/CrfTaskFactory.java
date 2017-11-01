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
import org.researchstack.backbone.model.survey.ActiveStepSurveyItem;
import org.researchstack.backbone.model.survey.SurveyItem;
import org.researchstack.backbone.model.survey.factory.SurveyFactory;
import org.researchstack.backbone.model.taskitem.TaskItem;
import org.researchstack.backbone.model.taskitem.TaskItemAdapter;
import org.researchstack.backbone.model.taskitem.factory.TaskItemFactory;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.Task;
import org.sagebase.crf.step.Crf12MinWalkingStep;
import org.sagebase.crf.step.CrfCountdownStep;
import org.sagebase.crf.step.CrfHeartRateCameraStep;
import org.sagebase.crf.step.CrfInstructionStep;
import org.sagebase.crf.step.CrfInstructionSurveyItem;
import org.sagebase.crf.step.CrfStartTaskStep;
import org.sagebase.crf.step.CrfStartTaskSurveyItem;

/**
 * Created by TheMDP on 10/24/17.
 */

public class CrfTaskFactory extends TaskItemFactory {

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

    private void fillCrfInstructionStep(CrfInstructionStep step, CrfInstructionSurveyItem item) {
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
}
