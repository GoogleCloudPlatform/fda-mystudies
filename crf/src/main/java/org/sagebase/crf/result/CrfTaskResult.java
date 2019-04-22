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

package org.sagebase.crf.result;

import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import org.threeten.bp.ZonedDateTime;

/**
 * Result from a run of a task.
 */
public class CrfTaskResult extends CrfResult {
    private static final String TYPE = "task";

    @NonNull
    private final ImmutableList<CrfResult> stepHistory;
    @NonNull
    private final ImmutableList<CrfResult> asyncResults;

    public CrfTaskResult(@NonNull String identifier, @NonNull ZonedDateTime startTime,
                         @NonNull ZonedDateTime endTime,
                         @NonNull ImmutableList<CrfResult> stepHistory,
                         @NonNull ImmutableList<CrfResult> asyncResults) {
        super(identifier, TYPE, startTime, endTime);
        this.stepHistory = stepHistory;
        this.asyncResults = asyncResults;
    }

    /**
     * @return list of step results in this run of the task
     */
    @NonNull
    public ImmutableList<CrfResult> getStepHistory() {
        return stepHistory;
    }

    /**
     * @return list of async results for this run of the task
     */
    @NonNull
    public ImmutableList<CrfResult> getAsyncResults() {
        return asyncResults;
    }
}
