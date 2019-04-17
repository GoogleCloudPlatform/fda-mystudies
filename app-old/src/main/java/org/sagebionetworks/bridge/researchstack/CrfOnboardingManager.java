package org.sagebionetworks.bridge.researchstack;

import android.content.Context;
import android.content.Intent;

import com.google.gson.GsonBuilder;

import org.researchstack.backbone.factory.IntentFactory;
import org.researchstack.backbone.model.survey.SurveyItem;
import org.researchstack.backbone.model.survey.factory.SurveyFactory;
import org.researchstack.backbone.onboarding.OnboardingManagerTask;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.NavigableOrderedTask;
import org.sagebase.old.CrfOnboardingTaskActivity;
import org.sagebase.crf.researchstack.CrfSurveyItemAdapter;
import org.sagebase.crf.researchstack.CrfTaskFactory;
import org.sagebase.old.step.CrfExternalIdStep;
import org.sagebase.crf.step.CrfInstructionSurveyItem;
import org.sagebionetworks.bridge.researchstack.onboarding.BridgeOnboardingManager;

import java.util.List;

/**
 * Created by TheMDP on 1/12/17.
 * <p>
 * This class is overridden to demonstrate to the developer how to convert
 * custom JSON SurveyItem objects in "onboarding.json" into CustomSteps used
 * in the onboarding process
 */

public class CrfOnboardingManager extends BridgeOnboardingManager {

    public static final String CRF_EXTERNAL_ID_TYPE = "crfExternalID";

    private CrfTaskFactory crfTaskFactory;

    public CrfOnboardingManager(Context context) {
        super(context);
        crfTaskFactory = new CrfTaskFactory();
    }

    /**
     * Override to register custom SurveyItemAdapters,
     * but make sure that the adapter extends from SurveyItemAdapter, and only overrides
     * the method getCustomClass()
     */
    @Override
    public void registerSurveyItemAdapter(GsonBuilder builder) {
        builder.registerTypeAdapter(SurveyItem.class, new CrfSurveyItemAdapter());
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
        if (CRF_EXTERNAL_ID_TYPE.equals(item.getCustomTypeValue())) {
            return new CrfExternalIdStep(item.identifier);
        } else if (CrfSurveyItemAdapter.CRF_FITBIT_SURVEY_ITEM_TYPE.equals(item.getCustomTypeValue())) {
            if (!(item instanceof CrfInstructionSurveyItem)) {
                throw new IllegalStateException("crf_fitbit types must be parsed as CrfInstructionSurveyItem");
            }
            return crfTaskFactory.createFitBitStep((CrfInstructionSurveyItem)item);
        }
        return super.createCustomStep(context, item, isSubtaskStep, factory);
    }
    
    @Override
    public OnboardingManagerTask createOnboardingTask(String identifier, List<Step> stepList) {
        // here we can implement our own Task, but it needs to be a NavigableOrderedTask
        return super.createOnboardingTask(identifier, stepList);
    }

    @Override
    public Intent createOnboardingTaskActivityIntent(Context context, NavigableOrderedTask task) {
        return IntentFactory.INSTANCE.newTaskIntent(context, CrfOnboardingTaskActivity.class, task);
    }
}
