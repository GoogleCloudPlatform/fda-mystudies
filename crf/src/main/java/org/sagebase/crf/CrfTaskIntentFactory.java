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

package org.sagebase.crf;

import android.content.Context;
import android.content.Intent;

import org.researchstack.backbone.factory.IntentFactory;
import org.sagebase.crf.researchstack.CrfResourceManager;

public class CrfTaskIntentFactory {
    private static final org.sagebase.crf.researchstack.CrfTaskFactory taskFactory =
            new org.sagebase.crf.researchstack.CrfTaskFactory();

    private static final IntentFactory intentFactory = IntentFactory.INSTANCE;

    public static Intent getHeartRateTrainingTaskIntent(Context context) {
        return intentFactory.newTaskIntent(
                context,
                CrfActiveTaskActivity.class,
                taskFactory.createTask(
                        context,
                        CrfResourceManager.HEART_RATE_TRAINING_TEST_RESOURCE));
    }

    public static Intent getHeartRateMeasurementTaskIntent(Context context) {
        return intentFactory.newTaskIntent(
                context,
                CrfActiveTaskActivity.class,
                taskFactory.createTask(
                        context,
                        CrfResourceManager.HEART_RATE_MEASUREMENT_TEST_RESOURCE));
    }

    public static Intent getStairStepTaskIntent(Context context) {
        return intentFactory.newTaskIntent(
                context,
                CrfActiveTaskActivity.class,
                taskFactory.createTask(
                        context,
                        CrfResourceManager.STAIR_STEP_RESOURCE));
    }

    private CrfTaskIntentFactory() {
    }
}
