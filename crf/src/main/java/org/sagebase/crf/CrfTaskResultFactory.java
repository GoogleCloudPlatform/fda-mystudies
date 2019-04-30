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

import android.content.Intent;
import android.support.annotation.VisibleForTesting;

import com.google.common.collect.ImmutableList;

import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.sagebase.crf.result.CrfResult;
import org.sagebase.crf.result.CrfTaskResult;

/**
 * Factory for creating Cardiorespiratory Fitness task results.
 */
public class CrfTaskResultFactory {

    /**
     * @param data data received in onActivityResult for a Cardiorespiratory Fitness task
     * @return task result in Cardiorespiratory Fitness module format
     */
    public static CrfTaskResult create(Intent data) {
//            return new CrfTaskResult("id", ZonedDateTime.now(), ZonedDateTime.now(),
//                    ImmutableList.of(), ImmutableList.of());
        return createFromTaskResult((TaskResult)data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT));
    }

    /**
     * @param taskResult raw task result
     * @return task result in Cardiorespiratory Fitness module format
     */
    @VisibleForTesting
    private static CrfTaskResult createFromTaskResult(TaskResult taskResult) {
        String identifier = taskResult.getIdentifier();

        ImmutableList<CrfResult> stepHistory = ImmutableList.of();
        ImmutableList<CrfResult> asyncResults = ImmutableList.of();

        return new CrfTaskResult(identifier, taskResult.getStartDate(), taskResult.getEndDate(), stepHistory, asyncResults);
    }

    private CrfTaskResultFactory() {
    }
}
