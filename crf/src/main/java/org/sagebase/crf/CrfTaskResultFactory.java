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

import com.google.common.collect.ImmutableList;

import org.researchstack.backbone.result.TaskResult;
import org.sagebase.crf.result.CrfResult;
import org.sagebase.crf.result.CrfTaskResult;
import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

/**
 * Factory for creating Cardiorespiratory Fitness task results.
 */
public class CrfTaskResultFactory {
    /**
     * @param taskResult raw task result
     * @return task result in Cardiorespiratory Fitness module format
     */
    public static CrfTaskResult create(TaskResult taskResult) {
        String identifier = taskResult.getIdentifier();

        ZonedDateTime startTime = ZonedDateTime.ofInstant(
                Instant.ofEpochMilli(taskResult.getStartDate().getTime()),
                ZoneId.systemDefault());
        ZonedDateTime endTime = ZonedDateTime.ofInstant(
                Instant.ofEpochMilli(taskResult.getEndDate().getTime()),
                ZoneId.systemDefault());

        ImmutableList<CrfResult> stepHistory = ImmutableList.of();
        ImmutableList<CrfResult> asyncResults = ImmutableList.of();

        return new CrfTaskResult(identifier, startTime, endTime, stepHistory, asyncResults);
    }

    private CrfTaskResultFactory() {
    }
}
