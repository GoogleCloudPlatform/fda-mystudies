package org.sagebionetworks.bridge.researchstack;

import android.content.Context;
import android.content.Intent;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import org.researchstack.backbone.model.survey.SurveyItem;
import org.researchstack.backbone.model.survey.SurveyItemAdapter;
import org.researchstack.backbone.model.survey.factory.SurveyFactory;
import org.researchstack.backbone.onboarding.OnboardingManager;
import org.researchstack.backbone.onboarding.OnboardingManagerTask;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.NavigableOrderedTask;

import java.util.List;

/**
 * Created by TheMDP on 1/12/17.
 * <p>
 * This class is overridden to demonstrate to the developer how to convert
 * custom JSON SurveyItem objects in "onboarding.json" into CustomSteps used
 * in the onboarding process
 */

public class CrfOnboardingManager extends OnboardingManager {

    public CrfOnboardingManager(Context context) {
        super(context);
    }

    /**
     * Override to register custom SurveyItemAdapters,
     * but make sure that the adapter extends from SurveyItemAdapter, and only overrides
     * the method getCustomClass()
     */
    @Override
    public void registerSurveyItemAdapter(GsonBuilder builder) {
        builder.registerTypeAdapter(SurveyItem.class, new CustomSurveyItemAdapter());
    }

    /**
     * @param item      CustomSurveyItem, which will be the type returns from our
     *                  CustomSurveyItemAdapter
     * @param factory   either a SurveyFactory, or ConsentDocumentFactory subclass
     * @param isSubtaskStep true if this is within a subtask step already, false otherwise
     * @return a CustomStep object, which can be anything we want it to be
     */
    @Override
    public Step createCustomStep(Context context, SurveyItem item, boolean isSubtaskStep,
                                 SurveyFactory factory) {
        // Since we dont have any in Crf, just go with default implementation of this instance
        // of SurveyFactory
        return factory.createCustomStep(context, item, isSubtaskStep);
    }

    @Override
    public OnboardingManagerTask createOnboardingTask(String identifier, List<Step> stepList) {
        // here we can implement our own Task, but it needs to be a NavigableOrderedTask
        return super.createOnboardingTask(identifier, stepList);
    }

    @Override
    public Intent createOnboardingTaskActivityIntent(Context context, NavigableOrderedTask task) {
        // here we can show our own custom activity, but it should be a subclass of
        // OnboardingTaskActivity
        return super.createOnboardingTaskActivityIntent(context, task);
    }

    class CustomSurveyItemAdapter extends SurveyItemAdapter {
        /**
         * This can be overridden to provide custom survey item deserialization
         * the default deserialization is a CustomSurveyItem, or a
         * CustomInstructionSurveyItem if customType ends with ".instruction"
         *
         * @param customType used to map to different types of survey items
         * @return type of survey item to create from the custom class
         */
        @Override
        public Class<? extends SurveyItem> getCustomClass(String customType, JsonElement json) {
            return super.getCustomClass(customType, json);
        }
    }
}
